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
package de.tudarmstadt.ukp.dkpro.wsd.si.twsi.resource;

import java.io.File;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;
import de.tudarmstadt.ukp.dkpro.wsd.si.twsi.TwsiSenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.twsi.TwsiSenseInventoryBase;

/**
 * A resource wrapping {@link TwsiSenseInventory}
 *
 * @author <a href="mailto:baer@ukp.informatik.tu-darmstadt.de">Daniel Bär</a>
 *
 */
public class TwsiSenseInventoryResource
    extends SenseInventoryResourceBase
    implements TwsiSenseInventory
{
    public static final String PARAM_TWSI_CONFIG_URL = "twsiConfigUrl";
    @ConfigurationParameter(name = PARAM_TWSI_CONFIG_URL, description = "The location of the TWSI configuration file", mandatory = true)
    protected String twsiConfigUrl;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        try {
            inventory = new TwsiSenseInventoryBase(ResourceUtils
                    .resolveLocation(twsiConfigUrl, this, null).getFile());
        }
        catch (Exception e) {
            throw new ResourceInitializationException(e);
        }

        return true;
    }

    @Override
    public File getConfigFile()
    {
        return ((TwsiSenseInventory) inventory).getConfigFile();
    }
}
