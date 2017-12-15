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

package de.tudarmstadt.ukp.dkpro.wsd.si.dictionary;

import java.util.Map;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseWeightedInventory;

/**
 * A SenseWeightedInventory with special information from inter-language and Google links
 * 
 * @author nico.erbs@gmail.com
 *
 */
public interface IGoogleDictionary extends SenseWeightedInventory {
	
	public Map<String, Double> getInterlanguageWeightedSenses(String sod) throws SenseInventoryException;

	public Map<String, Double> getEnglishlanguageWeightedSenses(String sod) throws SenseInventoryException;

	public Map<String, Double> getWikipediaWeightedSenses(String sod) throws SenseInventoryException;

	public Map<String, Double> getAlternativeWikipediaWeightedSenses(String sod) throws SenseInventoryException;

}
