/**
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import de.tuebingen.uni.sfs.germanet.api.GermaNet;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
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

    /**
     * Returns the underlying {@link GermaNet} object.
     *
     * @return the underlying {@link GermaNet} object
     */
    public GermaNet getUnderlyingResource() {
        return ((GermaNetLexUnitSenseInventory)inventory).getUnderlyingResource();
    }

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
