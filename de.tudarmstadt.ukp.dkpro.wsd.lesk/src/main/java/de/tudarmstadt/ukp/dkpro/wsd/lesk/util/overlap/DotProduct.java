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

package de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap;

import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.Lesk;


public class DotProduct
	implements OverlapStrategy
{

	/**
	 * Takes two arrays of objects and returns the dot product of their
	 * frequency vectors
	 *
	 * @param o1	the first array of objects
	 * @param o2	the second array of objects
     * @return	the dot product of the two arrays
	 */
	@Override
	public double overlap(List<String> o1, List<String> o2)
	{
		// Create a cooccurrence matrix
		Map<Object, Integer[]> cooccurrence = Lesk.frequencyMatrix(new Object[][] {o1.toArray(), o2.toArray()});

		// Compute dot product
		int dotProduct = 0;
		for (Integer[] weights : cooccurrence.values()) {
			dotProduct += weights[0] * weights[1];
		}
		return dotProduct;
	}

}
