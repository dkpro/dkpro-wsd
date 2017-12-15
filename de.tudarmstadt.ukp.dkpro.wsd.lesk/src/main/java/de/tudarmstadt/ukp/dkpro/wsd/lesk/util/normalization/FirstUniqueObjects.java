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

package de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization;

import java.util.HashSet;
import java.util.List;

public class FirstUniqueObjects
	implements NormalizationStrategy
{

	/**
	 * Takes two arrays of objects and returns the total number of unique
	 * objects in the first array
	 *
	 * @param o1	the first array of objects
	 * @param o2	not used
     * @return	the number of unique objects in the first array
	 */
	@Override
    public double normalizer(List<String> o1, List<String> o2)
	{
		return new HashSet<Object>(o1).size();
	}

}
