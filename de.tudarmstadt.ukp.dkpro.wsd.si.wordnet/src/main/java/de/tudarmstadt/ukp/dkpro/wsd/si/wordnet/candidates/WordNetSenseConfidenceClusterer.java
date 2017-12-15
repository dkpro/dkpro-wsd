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

package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.candidates;

import java.io.IOException;

import org.apache.commons.collections15.Transformer;
import org.apache.uima.fit.descriptor.ExternalResource;

import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseClusterer;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseConfidenceClusterer;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetSenseInventoryBase;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Collapses the confidence scores of senses in {@link WSDResult}s (representing
 * WordNet synsets) according to the provided clustering of WordNet sense keys
 */
public class WordNetSenseConfidenceClusterer
    extends SenseConfidenceClusterer
{
    public final static String SENSE_INVENTORY_RESOURCE = "SenseInventory";
    @ExternalResource(key = SENSE_INVENTORY_RESOURCE)
    protected SenseInventory inventory;

    @Override
    protected void initializeSenseClusterer()
        throws IOException
    {
        Transformer<String, String> senseMap = new Transformer<String, String>() {

            @Override
            public String transform(String input)
            {
                try {
                    return ((WordNetSenseInventoryBase) inventory).senseKeyToSynsetOffsetAndPos(input);
                }
                catch (SenseInventoryException e) {
                    throw new IllegalArgumentException(e);
                }
            } };
        senseClusterer = new SenseClusterer(clusterUrl, delimiterRegex, senseMap);
    }
}
