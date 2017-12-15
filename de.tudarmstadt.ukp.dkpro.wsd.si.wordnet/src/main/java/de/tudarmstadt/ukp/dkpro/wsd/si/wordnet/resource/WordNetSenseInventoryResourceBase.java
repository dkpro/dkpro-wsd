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

import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetSenseInventoryBase;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan
 *         Miller</a>
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
     * Returns the underlying {@link Dictionary} object.
     *
     * @return the underlying {@link Dictionary} object
     */
    public Dictionary getUnderlyingResource()
    {
        return ((WordNetSenseInventoryBase) inventory).getUnderlyingResource();
    }

    /**
     * Given a lemma and a string representing a synset + part of speech,
     * returns a corresponding sense key.
     *
     * @param senseId
     * @param lemma
     * @return the corresponding sense key
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
     * @param senseKey
     * @return the synset offset and part of speech
     * @throws SenseInventoryException
     */
    public String getWordNetSynsetOffsetAndPos(String senseKey)
        throws SenseInventoryException
    {
        return ((WordNetSenseInventoryBase) inventory)
                .senseKeyToSynsetOffsetAndPos(senseKey);
    }

    /**
     * Returns true if the given String corresponds to a valid sense key
     *
     * @param senseKey
     * @return true if the given String corresponds to a valid sense key
     * @throws SenseInventoryException
     */
    public boolean isSenseKey(String senseKey)
        throws SenseInventoryException
    {
        return ((WordNetSenseInventoryBase) inventory).isSenseKey(senseKey);
    }

    /**
     * Returns true if the given String corresponds to a valid synset offset +
     * POS
     *
     * @param synset
     * @return true if the given String corresponds to a valid synset offset +
     *         POS
     * @throws SenseInventoryException
     */
    public boolean isSynset(String synset)
        throws SenseInventoryException
    {
        return ((WordNetSenseInventoryBase) inventory).isSynset(synset);
    }

}
