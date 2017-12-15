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

package de.tudarmstadt.ukp.dkpro.wsd.si.resource;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.wsd.si.FixedSenseInventory;

/**
 * A resource wrapping sense inventories of type {@link FixedSenseInventory}
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class FixedSenseInventoryResource
    extends SenseInventoryResourceBase
{

    public final static String PARAM_FIXED_SENSE_ID = "fixedSenseId";
    @ConfigurationParameter(name = PARAM_FIXED_SENSE_ID, mandatory = true, description = "The sense ID to apply to all sods")
    protected String fixedSenseId;

    public final static String PARAM_FIXED_SENSE_DESCRIPTION = "fixedSenseDescription";
    @ConfigurationParameter(name = PARAM_FIXED_SENSE_DESCRIPTION, mandatory = false, description = "The sense description to apply to all sods")
    protected String fixedSenseDescription;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        inventory = new FixedSenseInventory(fixedSenseId, fixedSenseDescription);

        return true;
    }
}