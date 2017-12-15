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

import java.util.List;

/**
 * A dictionary is a kind of sense inventory with mentions/sods/strings and a list of targets for each mention
 * 
 * @author nico.erbs@gmail.com
 *
 */
public interface Dictionary {

	public abstract List<String> getTargets(String mention);

	public abstract List<String[]> getTargetValuePairs(String mention);

	public abstract String getMostFrequentTarget(String mention);

	public abstract boolean isStringAsValueInDictionary(String candidate,
			boolean onlyFirst);

	public abstract boolean isStringAsValueForMentionInDictionary(
			String mention, String candidate, boolean onlyFirst);

	/**
	 * The format of the dictionary is always lowercased and underscores instead of blanks
	 * @param key the key to search for in dictionary
	 * @return the value for the lowercased and blank-replaced key
	 */
	public abstract boolean containsKey(String key);

	public abstract Integer getTargetPopularity(String target);

	public abstract int getMentionFrequency(String mention);

	public abstract int getMentionSize();

	public abstract int getTargetSize();

	public abstract long getNumberOfMentionEntityPairs();
	
	public abstract boolean containsTarget(String senseId);

}