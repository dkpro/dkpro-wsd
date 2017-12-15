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
package de.tudarmstadt.ukp.dkpro.wsd.supervised.twsi.resource;

import java.util.Map;

import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceDocumentBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.twsi.TwsiSenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.supervised.twsi.TwsiWsdDisambiguator;

/**
 * A resource wrapping {@link TwsiWsdDisambiguator}
 *
 * @author <a href="mailto:baer@ukp.informatik.tu-darmstadt.de">Daniel Bär</a>
 *
 */
public class TwsiWsdDisambiguatorResource
    extends WSDResourceDocumentBasic
{
    public static final String PARAM_SENSE_INVENTORY = "senseInventory";
    @ExternalResource(key = PARAM_SENSE_INVENTORY)
    private TwsiSenseInventory senseInventory;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        return true;
    }

    @Override
    public void afterResourcesInitialized() throws ResourceInitializationException
    {
        super.afterResourcesInitialized();

        wsdAlgorithm = new TwsiWsdDisambiguator(senseInventory);
    }
}
