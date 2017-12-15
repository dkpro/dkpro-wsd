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
import de.tudarmstadt.ukp.dkpro.wsd.wsi.algorithm.SenseInductionAlgorithm;
import de.tudarmstadt.ukp.dkpro.wsd.wsi.si.InducedSenseInventory;

public abstract class SenseInductionResourceBase
    extends Resource_ImplBase
    implements SenseInductionAlgorithm
{
    public final static String SENSE_INVENTORY_RESOURCE = "SenseInventory";
    @ExternalResource(key = SENSE_INVENTORY_RESOURCE)
    protected InducedSenseInventory inventory;

    protected SenseInductionAlgorithm wsiAlgorithm;

    @Override
    public SenseInventory getSenseInventory()
    {
        return wsiAlgorithm.getSenseInventory();
    }

    @Override
    public SenseInventory induce(Collection<String> targetWords)
    {
        return wsiAlgorithm.induce(targetWords);
    }

    @Override
    public void induceSenses(String term)
        throws WSDException
    {
        wsiAlgorithm.induceSenses(term);

    }

    public void writeInventory(String file)
    {
        inventory.writeInventory(file);
    }

}