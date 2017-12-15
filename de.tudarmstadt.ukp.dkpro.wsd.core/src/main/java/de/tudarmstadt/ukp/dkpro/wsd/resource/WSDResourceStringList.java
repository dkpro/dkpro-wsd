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

package de.tudarmstadt.ukp.dkpro.wsd.resource;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.fit.component.Resource_ImplBase;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmCollectiveBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * Base class for algorithms that disambiguate all sods collectively
 * 
 * @author nico.erbs@gmail.com
 *
 */
public abstract class WSDResourceStringList
	extends Resource_ImplBase
	implements WSDAlgorithmCollectiveBasic
{	
	protected WSDAlgorithmCollectiveBasic wsd;

	@Override
	public Map<String, Map<String, Double>> getDisambiguation(Collection<String> sods)
		throws SenseInventoryException
	{
		return wsd.getDisambiguation(sods);
	}

	@Override
	public String getDisambiguationMethod()
	{
		return wsd.getDisambiguationMethod();
	}

	@Override
	public SenseInventory getSenseInventory()
	{
		return wsd.getSenseInventory();
	}

	@Override
	public void setSenseInventory(SenseInventory senseInventory)
	{
		wsd.setSenseInventory(senseInventory);
	}

}
