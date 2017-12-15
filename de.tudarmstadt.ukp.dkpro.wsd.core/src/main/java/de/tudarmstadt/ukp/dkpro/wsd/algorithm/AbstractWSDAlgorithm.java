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

import java.util.Map;
import java.util.TreeMap;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;

/**
 * An abstract class for word sense disambiguation algorithms.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class AbstractWSDAlgorithm
	implements WSDAlgorithm
{

	protected SenseInventory inventory;

    @SuppressWarnings("unused")
    private AbstractWSDAlgorithm() {};

    public AbstractWSDAlgorithm(SenseInventory inventory) {
        setSenseInventory(inventory);
    }

	@Override
	public void setSenseInventory(SenseInventory inventory)
	{
		this.inventory = inventory;
	}

	@Override
	public SenseInventory getSenseInventory()
	{
		return inventory;
	}

	@Override
	public String getDisambiguationMethod()
	{
		return this.getClass().getName();
	}

	protected Map<String, Double> getSortedDisambiguation(
			Map<String, Double> disambiguation)
	{
		@SuppressWarnings("unchecked")
		Map<String, Double> sortedDisambiguation = new TreeMap<String, Double>(
				new DescendingMapByValueComparator(disambiguation));
		sortedDisambiguation.putAll(disambiguation);
		return sortedDisambiguation;
	}

	protected final Map<String, Double> getDisambiguationMap(String[] senses,
			double[] values)
	{
		Map<String, Double> disambiguationMap = new TreeMap<String, Double>();
		for (int i = 0; i < senses.length; i++) {
			disambiguationMap.put(senses[i], values[i]);
		}
		return disambiguationMap;
	}

	@SuppressWarnings("unchecked")
	class DescendingMapByValueComparator
		implements java.util.Comparator
	{
		Map map;

		public DescendingMapByValueComparator(Map map)
		{
			this.map = map;
		}

		@Override
		public int compare(Object o1, Object o2)
		{
			return (-1) * ((Comparable) map.get(o1)).compareTo(map.get(o2));
		}
	}
}