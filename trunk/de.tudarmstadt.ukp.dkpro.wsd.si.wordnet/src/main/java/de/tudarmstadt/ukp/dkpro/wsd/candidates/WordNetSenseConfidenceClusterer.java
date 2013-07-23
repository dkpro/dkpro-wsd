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

package de.tudarmstadt.ukp.dkpro.wsd.candidates;

import java.io.IOException;
import java.util.Map;

import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Collapses the confidence scores of senses in {@link WSDResult}s (representing
 * WordNet synsets) according to the provided clustering of WordNet sense keys
 */
public class WordNetSenseConfidenceClusterer
    extends SenseConfidenceClusterer
{
    public static final String PARAM_INDEX_SENSE_FILE = "IndexSenseFile";
    @ConfigurationParameter(name = PARAM_INDEX_SENSE_FILE, mandatory = true, description = "The location of the WordNet index.sense file")
    private String indexSenseFile;

    @Override
    protected void initializeSenseClusterer()
        throws IOException
    {
        Map<String, String> senseMap = WordNetSenseKeyToSynset
                .getSenseMap(ResourceUtils.resolveLocation(indexSenseFile,
                        this, this.getContext()));
        senseClusterer = new SenseClusterer(clusterUrl, delimiterRegex, senseMap);
    }
}
