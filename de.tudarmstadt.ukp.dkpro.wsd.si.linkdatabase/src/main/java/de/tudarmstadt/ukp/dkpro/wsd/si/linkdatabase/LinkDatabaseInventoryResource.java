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

package de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase;

import java.util.List;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;

/**
 * A SenseInventoryResoucre for a LinkDatabase
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class LinkDatabaseInventoryResource
extends SenseInventoryResourceBase
implements LinkDatabase
{
	public static final String PARAM_RESOURCE_HOST = "resourceHost";
	@ConfigurationParameter(name = PARAM_RESOURCE_HOST, mandatory = true)
	private String resourceHost;

	public static final String PARAM_RESOURCE_DATABASE = "resourceDatabase";
	@ConfigurationParameter(name = PARAM_RESOURCE_DATABASE, mandatory = true)
	private String resourceDatabase;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map additionalParams)
	throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, additionalParams)) {
			return false;
		}

		inventory = new LinkDatabaseInventory(resourceHost, resourceDatabase);

		return true;
	}

	@Override
	public List<String> getIncomingLinks(String target)
			throws SenseInventoryException, UnsupportedOperationException {
		return ((LinkDatabaseInventory) inventory).getIncomingLinks(target);
	}

	@Override
	public int getNumberOfSenses() {
		return ((LinkDatabaseInventory) inventory).getNumberOfSenses();
	}

	@Override
	public Map<String, Double> getWeightedSenses(String sod)
			throws SenseInventoryException {
		return ((LinkDatabaseInventory) inventory).getWeightedSenses(sod);
	}
}
