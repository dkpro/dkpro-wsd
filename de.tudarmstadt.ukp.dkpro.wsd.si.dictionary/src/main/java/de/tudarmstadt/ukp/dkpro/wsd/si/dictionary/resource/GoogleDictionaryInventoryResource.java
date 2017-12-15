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

package de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.GoogleDictionaryInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.IGoogleDictionary;
import de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.util.GoogleDictionary;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;

public class GoogleDictionaryInventoryResource
    extends SenseInventoryResourceBase
    implements IGoogleDictionary
{
    public static final String PARAM_INVENTORY_PATH = "InventoryPath";
    @ConfigurationParameter(name = PARAM_INVENTORY_PATH, mandatory = true)
    protected String inventoryPath;

    public static final String PARAM_INVENTORY_SERIALIZABLE = "InventorySerializable";
    @ConfigurationParameter(name = PARAM_INVENTORY_SERIALIZABLE, mandatory = true)
    protected String inventorySerializable;

    public static final String PARAM_NEEDED_MENTIONS_PATH = "NeededMentionsPath";
    @ConfigurationParameter(name = PARAM_NEEDED_MENTIONS_PATH, mandatory = true)
    protected String neededMentionsPath;

    /**
     * Returns the underlying {@link GoogleDictionary} object.
     *
     * @return the underlying {@link GoogleDictionary} object
     */
    public GoogleDictionary getUnderlyingResource() {
        return ((GoogleDictionaryInventory)inventory).getUnderlyingResource();
    }

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map<String, Object> additionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, additionalParams)) {
            return false;
        }

        try {
            inventory = new GoogleDictionaryInventory(inventoryPath,
                    inventorySerializable, neededMentionsPath);
        }
        catch (FileNotFoundException e) {
            throw new ResourceInitializationException();
        }
        catch (IOException e) {
            throw new ResourceInitializationException();
        }

        return true;
    }

    @Override
    public Map<String, Double> getAlternativeWikipediaWeightedSenses(String sod)
        throws SenseInventoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Double> getEnglishlanguageWeightedSenses(String sod)
        throws SenseInventoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Double> getInterlanguageWeightedSenses(String sod)
        throws SenseInventoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Double> getWeightedSenses(String sod)
        throws SenseInventoryException
    {
        return ((GoogleDictionaryInventory) inventory).getWeightedSenses(sod);
    }

    @Override
    public Map<String, Double> getWikipediaWeightedSenses(String sod)
        throws SenseInventoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
