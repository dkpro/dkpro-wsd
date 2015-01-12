/*******************************************************************************
 * Copyright 2015
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

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetSenseInventoryBase;

/**
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class WordNetSenseInventoryResourceBase
    extends SenseInventoryResourceBase
{
    public static final String PARAM_WORDNET_PROPERTIES_URL = "wordNetPropertiesURL";
    @ConfigurationParameter(name = PARAM_WORDNET_PROPERTIES_URL, description = "The URL of the WordNet properties file", mandatory = true)
    protected String wordNetPropertiesURL;

    public static final String PARAM_SENSE_DESCRIPTION_FORMAT = "senseDescriptionFormat";
    @ConfigurationParameter(name = PARAM_SENSE_DESCRIPTION_FORMAT, description = "A format string specifying how sense descriptions should be printed", mandatory = false)
    protected String senseDescriptionFormat;

    /**
     * Given a lemma and a string representing a synset + part of speech,
     * returns a corresponding sense key.
     *
     * @param senseId
     * @param lemma
     * @return
     * @throws SenseInventoryException
     */
    public String getWordNetSenseKey(String senseId, String lemma)
        throws SenseInventoryException
    {
        return ((WordNetSenseInventoryBase) inventory)
                .synsetOffsetAndPosToSenseKey(senseId, lemma);
    }

    /**
     * Given a WordNet sense key, return a synset offset + POS
     *
     * @param senseId
     * @return
     * @throws SenseInventoryException
     */
    public String getWordNetSynsetOffsetAndPos(String senseKey)
        throws SenseInventoryException
    {
        return ((WordNetSenseInventoryBase) inventory)
                .senseKeyToSynsetOffsetAndPos(senseKey);
    }
}
