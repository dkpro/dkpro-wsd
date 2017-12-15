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

import java.util.Collection;

import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;

import de.tudarmstadt.ukp.dkpro.wsd.WSDException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;
import de.tudarmstadt.ukp.dkpro.wsd.wsi.algorithm.SenseInductionAlgorithm;

public class ExternalSenseInductionResource
    extends Resource_ImplBase
    implements SenseInductionAlgorithm
{
    public static final String SENSE_INVENTORY = "SenseInventory";

    @ExternalResource(key = SENSE_INVENTORY)
    SenseInventoryResourceBase senseInventory;

    @Override
    public SenseInventory getSenseInventory()
    {
        return senseInventory;
    }

    @Override
    public SenseInventory induce(Collection<String> targetWords)
    {
        // todo: Webservice-/external tool
        return senseInventory;
    }

    @Override
    public void induceSenses(String term)
        throws WSDException
    {
        if (senseInventory.getSenses(term).size() == 0)
            throw new WSDException("Term " + term + " is not in static sense inventory");

    }

}
