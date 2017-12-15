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
 * The UKB Sense inventory 
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 */
public class UkbDictionary extends AbstractDictionary{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public UkbDictionary(String path, String neededMentionsPath) throws FileNotFoundException, IOException{

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
		List<String[]> entities;
//		Timer timer = new Timer(9615853);
		while((line = reader.readLine()) != null){

			lineArray = line.split(" ");

			if(neededMentions.contains(lineArray[0].toLowerCase())){
				entities = new LinkedList<String[]>();
				for(int i=1;i<lineArray.length;i++){

					String target = lineArray[i].substring(0, lineArray[i].lastIndexOf(":"));
					String frequency = lineArray[i].substring(lineArray[i].lastIndexOf(":")+1,lineArray[i].length());

					//add targets to mentionMap
					entities.add(new String[]{target,frequency});

					//add target to targetMap
					if(targetMap.containsKey(target)){
						targetMap.put(target, targetMap.get(target) + Integer.valueOf(frequency));
					}
					else{
						targetMap.put(target, Integer.valueOf(frequency));
					}
				}
				mentionMap.put(lineArray[0].toLowerCase(), entities);
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
