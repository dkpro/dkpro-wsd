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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetOverlap
	implements OverlapStrategy
{

	/**
	 * Computes the number of unique objects two arrays have in common
	 *
	 * @param o1	the first array of objects to be compared
	 * @param o2	the second array of objects to be compared
     * @return	the number of unique objects the two arrays have in common
	 */
	@Override
	public double overlap(List<String> o1, List<String> o2)
	{
		Set<Object> set1 = new HashSet<Object>(o1);
		Set<Object> set2 = new HashSet<Object>(o2);

		set2.retainAll(set1);
		return set2.size();
	}

}
