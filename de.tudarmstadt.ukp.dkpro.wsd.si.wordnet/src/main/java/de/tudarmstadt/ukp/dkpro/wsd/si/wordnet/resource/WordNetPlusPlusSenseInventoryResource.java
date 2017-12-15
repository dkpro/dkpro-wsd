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

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import net.sf.extjwnl.JWNLException;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetPlusPlusSenseInventory;

/**
 * A resource wrapping {@link WordNetPlusPlusSenseInventory}
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WordNetPlusPlusSenseInventoryResource
    extends WordNetSynsetSenseInventoryResource
{
    public static final String PARAM_WORDNETPLUSPLUS_URL = "wordNetPlusPlusURL";
    @ConfigurationParameter(name = PARAM_WORDNETPLUSPLUS_URL, description = "The URL of the WordNet++ semantic relations file", mandatory = true)
    private String wordNetPlusPlusURL;

    public static final String PARAM_SIMILARITY_THRESHOLD = "similarityThreshold";
    @ConfigurationParameter(name = PARAM_SIMILARITY_THRESHOLD, description = "Only those WordNet++ semantic relations meeting this similarity threshold will be added to the resource", mandatory = false, defaultValue = "0.0")
    private String similarityThreshold;

    @SuppressWarnings("rawtypes")
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        try {
            inventory = new WordNetPlusPlusSenseInventory(
                    ResourceUtils.resolveLocation(wordNetPropertiesURL, this,
                            null), ResourceUtils.resolveLocation(
                            wordNetPlusPlusURL, this, null),
                    Double.valueOf(similarityThreshold));
        }
        catch (MalformedURLException e) {
            throw new ResourceInitializationException(e);
        }
        catch (JWNLException e) {
            throw new ResourceInitializationException(e);
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        catch (Exception e) {
            throw new ResourceInitializationException(e);
        }

        if (senseDescriptionFormat != null) {
            ((WordNetPlusPlusSenseInventory) inventory)
                    .setSenseDescriptionFormat(senseDescriptionFormat);
        }

        return true;
    }

}
