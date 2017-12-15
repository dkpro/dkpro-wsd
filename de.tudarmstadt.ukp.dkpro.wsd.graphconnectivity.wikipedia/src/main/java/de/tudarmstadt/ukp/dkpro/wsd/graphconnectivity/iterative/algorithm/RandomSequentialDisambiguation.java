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

package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.algorithm;

import java.util.Random;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;

/**
 * A class for sequential disambiguation using random weights between senses 
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class RandomSequentialDisambiguation extends
		SequentialGraphDisambiguation {

	public RandomSequentialDisambiguation(SenseInventory inventory) {
		super(inventory);
	}

	@Override
	protected double getSenseSimilarity(String baseSense, String targetSense) {
		if(baseSense.equals(targetSense)){
			return 1;
		}
		return new Random().nextDouble();
	}

}
