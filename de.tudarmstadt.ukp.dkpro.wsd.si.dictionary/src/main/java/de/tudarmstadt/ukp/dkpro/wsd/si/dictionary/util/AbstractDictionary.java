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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Abstract dictionary with utility methods for dealing with dictionary-specific problems
 * 
 * @author nico.erbs@gmail.com
 *
 */
public abstract class AbstractDictionary implements Serializable, Dictionary{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Map<String,List<String[]>> mentionMap;
	protected Map<String,Integer> targetMap;

	protected long mentionEntityPairs = 0;
	
	public Map<String, List<String[]>> getSenseInventory(){
		return mentionMap;
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#getTargets(java.lang.String)
	 */
	public List<String> getTargets(String mention){		
		List<String> targets = new LinkedList<String>();
		for(String[] targetValuePair : getTargetValuePairs(mention)){
			targets.add(targetValuePair[0]);
		}
		return targets;
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#getTargetValuePairs(java.lang.String)
	 */
	public List<String[]> getTargetValuePairs(String mention){
		List<String[]> targetValuePairs = mentionMap.get(convertKey(mention));
		if(targetValuePairs != null){
			return targetValuePairs;
		}
		else{
			return new LinkedList<String[]>();
		}
	}

	public Map<String, Double> getWeightedSenses(String sod) {
		Map<String, Double> weightedSenses = new HashMap<String, Double>();
		double sum = 0;
		double max = 0;
		for(String[] sense : getTargetValuePairs(sod)){
			sum += Double.valueOf(sense[1]);
			if(Double.valueOf(sense[1]) > max){
				max  = Double.valueOf(sense[1]);
			}
		}

		for(String[] sense : getTargetValuePairs(sod)){
			if(weightedSenses.containsKey(sense[0])){
				weightedSenses.put(sense[0], Double.valueOf(sense[1])/sum+weightedSenses.get(sense[0]));
			}
			else{
				if(Double.valueOf(sense[1])/max > 0.01){
					weightedSenses.put(sense[0], Double.valueOf(sense[1])/sum);
				}
			}
		}

		return weightedSenses;
	}

	public Map<String, Double> getPopularityWeightedSenses(String sod) {
		Map<String, Double> weightedSenses = new HashMap<String, Double>();
		for(String[] sense : getTargetValuePairs(sod)){
			weightedSenses.put(sense[0], (double) getTargetPopularity(sense[0]));
		}
		return weightedSenses;
	}

	public Map<String, Double> getRandomnylWeightedSenses(String sod) {
		Random random = new Random();
		Map<String, Double> weightedSenses = new HashMap<String, Double>();
		for(String[] sense : getTargetValuePairs(sod)){
			weightedSenses.put(sense[0], random.nextDouble());
		}
		return weightedSenses;
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#getMostFrequentTarget(java.lang.String)
	 */
	public String getMostFrequentTarget(String mention){
		if(getTargetValuePairs(mention) == null){
			return null;
		}
		return getTargetValuePairs(mention).get(0)[0];
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#isStringAsValueInDictionary(java.lang.String, boolean)
	 */
	public boolean isStringAsValueInDictionary(
			String candidate,
			boolean onlyFirst) {

		if(onlyFirst){
			for(String key : mentionMap.keySet()){
				if(matches(candidate,getTargets(key).get(0))){
					return true;
				}
			}
			return false;
		}
		else{
			return targetMap.containsKey(candidate);
		}
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#isStringAsValueForMentionInDictionary(java.lang.String, java.lang.String, boolean)
	 */
	public boolean isStringAsValueForMentionInDictionary(
			String mention,
			String candidate,
			boolean onlyFirst) {

		List<String> targets = getTargets(mention);

		if(onlyFirst &&
				targets != null &&
				targets.size() != 0){
			if(matches(candidate,targets.get(0))){
				return true;
			}
		}
		else if(targets != null){
			for(String toCheck : targets){
				if(matches(candidate,toCheck)){
					return true;
				}

			}
		}
		return false;
	}

	public static boolean matches(String key0, String key1) {
		key0 = AbstractDictionary.convertKey(key0.toLowerCase().trim().replaceAll(" ", "_"));
		key1 = AbstractDictionary.convertKey(key1.toLowerCase().trim().replaceAll(" ", "_"));
		return key0.equalsIgnoreCase(key1);
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key){
		return mentionMap.containsKey(convertKey(key));
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

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#getTargetPopularity(java.lang.String)
	 */
	public Integer getTargetPopularity(String target) {
		return targetMap.get(target);
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#getMentionFrequency(java.lang.String)
	 */
	public int getMentionFrequency(String mention) {
		int mentionFrequency = 0;
		for(String[] pair : getTargetValuePairs(mention)){
			mentionFrequency += Integer.valueOf(pair[1]);
		}
		return mentionFrequency;
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#getMentionSize()
	 */
	public int getMentionSize() {
		return mentionMap.size();
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#getTargetSize()
	 */
	public int getTargetSize() {
		return targetMap.size();
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.experiments.nico.wsd.ixa.reader.dictionary.Dictionary#getNumberOfMentionEntityPairs()
	 */
	public long getNumberOfMentionEntityPairs() {
		if(mentionEntityPairs == 0){
			for(int value : targetMap.values()){
				mentionEntityPairs += value;
			}
		}
		return mentionEntityPairs;
	}
}
