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

import java.util.List;

public class NoNormalization
	implements NormalizationStrategy
{

	/**
	 * Returns 1.0.  This method can be passed to the Lesk algorithm to
	 * avoid normalizing the overlap measure.
	 *
	 * @param o1	not used
	 * @param o2	not used
     * @return	1.0
	 */
	@Override
    public double normalizer(List<String> o1, List<String> o2)
	{
		return 1.0;
	}

}
