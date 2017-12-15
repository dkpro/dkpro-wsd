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
package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.candidates;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.annotator.AnnotatorConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.wsd.WSDUtils;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseConverter;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.resource.WordNetSenseInventoryResourceBase;

/**
 * Converts WordNet sense keys to synset offset+POS
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WordNetSenseKeyToSynset
    extends SenseConverter
{
    public static final String PARAM_INDEX_SENSE_FILE = "IndexSenseFile";
    @ConfigurationParameter(name = PARAM_INDEX_SENSE_FILE, mandatory = false, description = "The location of the WordNet index.sense file.  This parameter can be used only if a WordNet sense inventory resource is not specified.")
    private String indexSenseFile;

    private static final Pattern senseKeyPattern = Pattern
            .compile(".*%([12345]):.*");

    protected Map<String, String> senseMap;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        if (indexSenseFile != null && sourceInventory != null) {
            throw new ResourceInitializationException(
                    new AnnotatorConfigurationException(
                            AnnotatorConfigurationException.MUTUALLY_EXCLUSIVE_PARAMS,
                            new String[] { PARAM_INDEX_SENSE_FILE + ", "
                                    + SOURCE_SENSE_INVENTORY_RESOURCE }));
        }

        if (indexSenseFile != null) {
            try {
                senseMap = getSenseMap(ResourceUtils.resolveLocation(
                        indexSenseFile, this, context));
            }
            catch (Exception e) {
                throw new ResourceInitializationException(e);
            }
        }
    }

    /**
     * Returns a mapping of WordNet sense keys to synset offsets
     *
     * @param indexSenseFile
     *            The URL of the WordNet index.sense file
     * @return A mapping of WordNet sense keys to synset offsets
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static Map<String, String> getSenseMap(URL indexSenseFile)
        throws IllegalArgumentException, IOException
    {
        Map<String, String> senseMap;
        senseMap = WSDUtils.readMap(indexSenseFile, 1, String.class, 2,
                String.class, "[\\t ]+");
        for (String senseKey : senseMap.keySet()) {
            Matcher m = senseKeyPattern.matcher(senseKey);
            if (m.matches() == false) {
                throw new IllegalArgumentException();
            }
            char pos;
            switch (m.group(1).charAt(0)) {
            case '1':
                pos = 'n';
                break;
            case '2':
                pos = 'v';
                break;
            case '5':
            case '3':
                pos = 'a';
                break;
            case '4':
                pos = 'r';
                break;
            default:
                throw new IllegalArgumentException();
            }
            senseMap.put(senseKey, senseMap.get(senseKey) + pos);
        }
        return senseMap;
    }

    @Override
    public String convert(String senseId)
        throws SenseInventoryException
    {
        if (senseMap != null) {
            return senseMap.get(senseId);
        }
        else {
            return ((WordNetSenseInventoryResourceBase) sourceInventory)
                    .getWordNetSynsetOffsetAndPos(senseId);
        }
    }

}
