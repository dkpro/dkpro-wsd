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

import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithm;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;

/**
 * A base class for WSD resources
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class WSDResourceBase
    extends Resource_ImplBase
    implements WSDAlgorithm
{
    public final static String SENSE_INVENTORY_RESOURCE = "SenseInventory";
    @ExternalResource(key = SENSE_INVENTORY_RESOURCE)
    protected SenseInventory inventory;

    protected WSDAlgorithm wsdAlgorithm;

    @Override
    public String getDisambiguationMethod()
    {
        return wsdAlgorithm.getDisambiguationMethod();
    }

    @Override
    public SenseInventory getSenseInventory()
    {
        return wsdAlgorithm.getSenseInventory();
    }

    @Override
    public void setSenseInventory(SenseInventory senseInventory)
    {
        wsdAlgorithm.setSenseInventory(senseInventory);
    }
}
