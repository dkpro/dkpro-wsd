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
 * An extension to the SenseWeightedInventory to return prior popularity of sense 
 * 
 * @author nico.erbs@gmail.com
 *
 */
public interface IUkbDictionary extends SenseWeightedInventory {
	
	public Map<String, Double> getPopularityWeightedSenses(String sod)	throws SenseInventoryException;

	public Map<String, Double> getRandomlyWeightedSenses(String sod)	throws SenseInventoryException;

}
