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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class PairedOverlap
	implements OverlapStrategy
{
    private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Computes the number of one-to-one overlaps between two arrays of
	 * objects
	 *
	 * @param o1	the first array of objects to be compared
	 * @param o2	the second array of objects to be compared
     * @return	the number of one-to-one overlaps between the two arrays
	 */
	@Override
	public double overlap(List<String> o1, List<String> o2)
	{
		int overlap = 0;
		boolean[] paired = new boolean[o2.size()];
		List<String> intersection = null;

		if (logger.isTraceEnabled()) {
		    intersection = new ArrayList<String>();
		}

		for (int i = 0; i < o1.size(); i++) {
			for (int j = 0; j < o2.size(); j++) {
				if (o1.get(i).equals(o2.get(j)) && paired[j] == false) {
			        if (logger.isTraceEnabled()) {
			            intersection.add(o1.get(i));
			        }
					overlap++;
					paired[j] = true;
					break;
				}
			}
		}

        if (logger.isTraceEnabled()) {
//            logger.trace("Tokenized sense description: " + o1.toString());
//            logger.trace("Tokenized context: " + o2.toString());
            logger.trace("Multiset intersection: " + intersection.toString());
        }

        return overlap;
	}

}
