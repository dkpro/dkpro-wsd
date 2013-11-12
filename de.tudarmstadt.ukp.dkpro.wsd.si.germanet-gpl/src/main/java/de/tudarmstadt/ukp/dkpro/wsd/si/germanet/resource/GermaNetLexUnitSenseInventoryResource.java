/*******************************************************************************
 * Copyright 2013
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

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.si.germanet.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.wsd.si.germanet.GermaNetLexUnitSenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;

/**
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class GermaNetLexUnitSenseInventoryResource
    extends SenseInventoryResourceBase
{
    public static final String PARAM_GERMANET_DIRECTORY = "germaNetDirectory";
    @ConfigurationParameter(name = PARAM_GERMANET_DIRECTORY, description = "The directory containing GermaNet", mandatory = true)
    protected String germaNetDirectory;

    public static final String PARAM_SENSE_DESCRIPTION_FORMAT = "senseDescriptionFormat";
    @ConfigurationParameter(name = PARAM_SENSE_DESCRIPTION_FORMAT, description = "A format string specifying how sense descriptions should be printed", mandatory = false)
    protected String senseDescriptionFormat;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        try {
            inventory = new GermaNetLexUnitSenseInventory(germaNetDirectory);
        }
        catch (FileNotFoundException e) {
            throw new ResourceInitializationException(e);
        }
        catch (XMLStreamException e) {
            throw new ResourceInitializationException(e);
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }

        if (senseDescriptionFormat != null) {
            ((GermaNetLexUnitSenseInventory) inventory)
                    .setSenseDescriptionFormat(senseDescriptionFormat);
        }
        return true;
    }

}
