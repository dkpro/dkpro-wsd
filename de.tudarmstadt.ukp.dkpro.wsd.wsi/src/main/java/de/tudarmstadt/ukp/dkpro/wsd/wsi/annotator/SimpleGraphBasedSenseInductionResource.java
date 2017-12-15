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
package de.tudarmstadt.ukp.dkpro.wsd.wsi.annotator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.wsd.wsi.algorithm.SimpleGraphClusteringInductionAlgorithm;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasure;

/**
 * @author zorn
 *
 */
public class SimpleGraphBasedSenseInductionResource
    extends SenseInductionResourceBase
{
    final static String KEY_LSR = "LexicalResource";
    @ExternalResource(key = KEY_LSR)
    LexicalSemanticResource thesaurus;

    final static String KEY_TERM_SIMILARITY1 = "TermSimilarity1";
    @ExternalResource
    TermSimilarityMeasure termMeasure1;

    final static String KEY_TERM_SIMILARITY2 = "TermSimilarity2";
    @ExternalResource(mandatory = false)
    TermSimilarityMeasure termMeasure2;

    final static String KEY_TERM_SIMILARITY3 = "TermSimilarity3";
    @ExternalResource(mandatory = false)
    TermSimilarityMeasure termMeasure3;

    private final List<TermSimilarityMeasure> measures = new LinkedList<TermSimilarityMeasure>();

    final static String PARAM_THRESHOLD = "Threshold";
    @ConfigurationParameter(name = PARAM_THRESHOLD, mandatory = true, defaultValue = "0.2")
    Double sim_thres;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {

        super.initialize(aSpecifier, aAdditionalParams);
        try {
            measures.add(termMeasure1);
            if (termMeasure2 != null) {
                measures.add(termMeasure2);
            }
            wsiAlgorithm = new SimpleGraphClusteringInductionAlgorithm(thesaurus, sim_thres,
                    measures);
        }
        catch (SimilarityException e) {
            throw new ResourceInitializationException(e);
        }
        return true;
    }

}
