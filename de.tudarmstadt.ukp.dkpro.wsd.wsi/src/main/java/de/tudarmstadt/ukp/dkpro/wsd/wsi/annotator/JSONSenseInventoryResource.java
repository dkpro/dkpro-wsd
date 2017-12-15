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
package de.tudarmstadt.ukp.dkpro.wsd.wsi.annotator;

import java.util.List;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.WSDException;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;
import de.tudarmstadt.ukp.dkpro.wsd.wsi.si.InducedSenseInventory;

public class JSONSenseInventoryResource
    extends SenseInventoryResourceBase

{
    public final static String PARAM_FILENAME = "InventoryFile";
    @ConfigurationParameter(name = PARAM_FILENAME, mandatory = true, defaultValue = "clusters.json")
    protected String fileName;

    protected InducedSenseInventory inventory;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {

        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }
        inventory = new InducedSenseInventory();
        try {
            inventory.loadInventory(fileName);
        }
        catch (WSDException e) {
            throw new ResourceInitializationException(e);
        }
        return true;
    }

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        return inventory.getSenseInventory();
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {

        return inventory.getSenses(sod);
    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {

        return inventory.getSenses(sod, pos);
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return inventory.getMostFrequentSense(sod);
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return inventory.getMostFrequentSense(sod, pos);
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {

        return inventory.getSenseDescription(senseId);
    }

    @Override
    public String getSenseInventoryName()
    {
        return inventory.getSenseInventoryName();
    }

    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return inventory.toString();
    }
}