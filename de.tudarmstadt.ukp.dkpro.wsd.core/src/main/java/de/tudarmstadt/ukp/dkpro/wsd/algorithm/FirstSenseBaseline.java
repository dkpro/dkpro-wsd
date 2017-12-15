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

package de.tudarmstadt.ukp.dkpro.wsd.algorithm;

import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * A word sense disambiguation algorithm which, given a subject of
 * disambiguation, returns the first associated sense from the sense inventory.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class FirstSenseBaseline
	extends AbstractWSDAlgorithm
	implements WSDAlgorithmIndividualBasic, WSDAlgorithmIndividualPOS
{

	public FirstSenseBaseline(SenseInventory inventory)
	{
		super(inventory);
	}

	@Override
	public Map<String, Double> getDisambiguation(String sod)
		throws SenseInventoryException
	{
		List<String> senses = inventory.getSenses(sod);
		if (!senses.isEmpty()) {
			return getDisambiguationMap(new String[] { senses.get(0) },
					new double[] { 1.0 });
		}

		return null;
	}

	@Override
	public Map<String, Double> getDisambiguation(String sod,
			POS pos)
		throws SenseInventoryException
	{
		List<String> senses = inventory.getSenses(sod, pos);
		if (!senses.isEmpty()) {
			return getDisambiguationMap(new String[] { senses.get(0) },
					new double[] { 1.0 });
		}

		return null;
	}
}