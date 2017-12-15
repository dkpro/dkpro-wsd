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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * A specialized HashMap for dictionary entries wich consist of an anchor and a list of targets
 * @author nico.erbs@gmail.com
 *
 */
public class DictionaryWithoutFrequencies extends HashMap<String,List<String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DictionaryWithoutFrequencies(String path) throws FileNotFoundException, IOException{
		super();

		BufferedReader reader =
			new BufferedReader(
					new InputStreamReader(
							new BZip2CompressorInputStream(
									new FileInputStream(path))));

		String line;
		String[] lineArray;
		List<String> entities;
		while((line = reader.readLine()) != null){

			lineArray = line.split(" ");
			entities = new LinkedList<String>();
			for(int i=1;i<lineArray.length;i++){
				entities.add(lineArray[i].split(":")[0]);
			}
			this.put(lineArray[0].toLowerCase(), entities);

		}
		reader.close();
	}

	public List<String> getTargets(String mention){
		return this.get(mention);
	}

	public String getMostFrequentTarget(String mention){
		if(this.get(mention) == null){
			return null;
		}
		return this.get(mention).get(0);
	}

	public boolean isStringAsValueInDictionary(
			String candidate,
			boolean onlyFirst) {

		for(String key : this.keySet()){
			if(onlyFirst && this.get(key).size() != 0){
				if(matches(candidate,this.get(key).get(0))){
					return true;
				}
			}
			else{
				for(String toCheck : this.get(key)){
					if(matches(candidate,toCheck)){
						return true;
					}

				}
			}
		}
		return false;
	}

	public boolean isStringAsValueForMentionInDictionary(
			String mention,
			String candidate,
			boolean onlyFirst) {

		if(onlyFirst &&
				this.get(mention.toLowerCase()) != null &&
				this.get(mention.toLowerCase()).size() != 0){
			if(matches(candidate,this.get(mention).get(0))){
				return true;
			}
		}
		else if(this.get(mention.toLowerCase()) != null){
			for(String toCheck : this.get(mention)){
				if(matches(candidate,toCheck)){
					return true;
				}

			}
		}
		return false;
	}

	public static boolean matches(String key0, String key1) {
		key0 = DictionaryWithoutFrequencies.convertKey(key0.toLowerCase().trim().replaceAll(" ", "_"));
		key1 = DictionaryWithoutFrequencies.convertKey(key1.toLowerCase().trim().replaceAll(" ", "_"));
		return key0.equalsIgnoreCase(key1);
	}

	/**
	 * The format of the dictionary is always lowercased and underscores instead of blanks
	 * @param key the key to search for in dictionary
	 * @return the value for the lowercased and blank-replaced key
	 */
	public List<String> get(String key){
		return super.get(convertKey(key));

	}

	/**
	 * The format of the dictionary is always lowercased and underscores instead of blanks
	 * @param key the key to search for in dictionary
	 * @return the value for the lowercased and blank-replaced key
	 */
	public boolean containsKey(String key){
		return super.containsKey(convertKey(key));
	}

	public static String convertKey(String key){
		//		key = key.replaceAll("\\u002d", "-");
		//		key = key.replaceAll("\\u00df", "ß");
		//		key = key.replaceAll("\\u002c", ",");
		//		key = key.replaceAll("\\u00fc", "ü");
		//		key = key.replaceAll("\\u00f6", "ö");
		//		key = key.replaceAll("\\u0028", "(");
		//		key = key.replaceAll("\\u0029", ")");
		//		key = key.replaceAll("\\u00fd", "ý");
		//		key = key.replaceAll("\\u00e9", "é");
		//		key = key.replaceAll("\\u0027", "'");

		try {
			key = StringEscapeUtils.unescapeJava(key);
			byte[] utf8Bytes = key.getBytes();
			key = new String(utf8Bytes, "UTF8");
		}
		catch (UnsupportedEncodingException e) {
		}
		//		key = key.replaceAll("", "");
		//		key = key.replaceAll("", "");
		//		key = key.replaceAll("", "");
		//		key = key.replaceAll("", "");
		key = key.toLowerCase().replaceAll(" ", "_");
		return key;		
	}




}
