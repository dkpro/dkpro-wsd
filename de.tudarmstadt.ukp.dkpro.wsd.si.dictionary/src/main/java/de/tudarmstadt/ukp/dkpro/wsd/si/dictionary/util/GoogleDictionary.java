/*******************************************************************************
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FileUtils;

/**
 * The Google Sense inventory (Valentin I. Spitkovsky and Angel X. Chang. 2012.
 * A Cross-Lingual Dictionary for English Wikipedia Concepts.
 * In Proceedings of the Eighth International Conference on Language Resources and Evaluation (LREC 2012).)
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 */
public class GoogleDictionary extends AbstractDictionary {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public GoogleDictionary(String path, String neededMentionsPath) throws FileNotFoundException, IOException{

		HashSet<String> neededMentions = new HashSet<String>(FileUtils.readLines(new File(neededMentionsPath)));

		mentionMap = new HashMap<String,List<String[]>>();
		targetMap = new HashMap<String,Integer>();

		BufferedReader reader =
				new BufferedReader(
						new InputStreamReader(
								new BZip2CompressorInputStream(
										new FileInputStream(path))));
		String line;
		String[] lineArray;
		String anchor;
		String target;
		String score;
		boolean inKB;
		boolean isTranslation;
		//		Timer timer = new Timer(297073139);
		while((line = reader.readLine()) != null){
			lineArray = line.split("\t");

			anchor = lineArray[0];
			if(neededMentions.contains(anchor)){
				anchor = anchor.replaceAll("\"", "").trim().replaceAll(" ", "_").toLowerCase();
				score = lineArray[1].split(" ")[0].trim();
				target = lineArray[1].split(" ")[1].trim();

				inKB = false;
				isTranslation = false;
				for(String flag :
					lineArray[1].substring(lineArray[1].indexOf(target) + target.length()).split(" ")){
					if(flag.equals("KB")){
						inKB = true;
					}
					if(flag.equals("x")){
						isTranslation = true;
					}
				}
				//				if(inKB && !isTranslation)
				if(!isTranslation)
				{
					//add target to targetMap
					if(targetMap.containsKey(target)){
						targetMap.put(target, targetMap.get(target) + 1);
					}
					else{
						targetMap.put(target, 1);
					}

					//add targets to mentionMap
					if(!mentionMap.containsKey(anchor)){
						mentionMap.put(anchor, new LinkedList<String[]>());
					}
					mentionMap.get(anchor).add(new String[]{target,score});
				}

			}
		}

		reader.close();
	}

    @Override
    public boolean containsTarget(String senseId)
    {
        return targetMap.containsKey(senseId);
    }
}