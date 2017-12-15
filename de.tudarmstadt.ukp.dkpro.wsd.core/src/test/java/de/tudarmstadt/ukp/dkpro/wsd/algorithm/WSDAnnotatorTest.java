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

package de.tudarmstadt.ukp.dkpro.wsd.algorithm;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.MostFrequentSenseBaseline;
import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorIndividualBasic;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.WSDItemAnnotator;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceIndividualBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.TestSenseInventoryResource;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

public class WSDAnnotatorTest {

    @Test
    public void wsdAnnotatorTest() throws Exception {

        ExternalResourceDescription testResource = createExternalResourceDescription(
                TestSenseInventoryResource.class
        );

        ExternalResourceDescription mfsBaselineResource = createExternalResourceDescription(
                WSDResourceIndividualBasic.class,
                WSDResourceIndividualBasic.SENSE_INVENTORY_RESOURCE, testResource,
                WSDResourceIndividualBasic.DISAMBIGUATION_METHOD, MostFrequentSenseBaseline.class.getName()
        );

        AnalysisEngineDescription mfsBaseline = createEngineDescription(
                WSDAnnotatorIndividualBasic.class,
                WSDAnnotatorIndividualBasic.WSD_ALGORITHM_RESOURCE, mfsBaselineResource
        );

        AnalysisEngineDescription itemAnno = createEngineDescription(
                WSDItemAnnotator.class,
                WSDItemAnnotator.PARAM_FEATURE_PATH, Token.class.getName()
        );

        // Bind external resource to the aggregate
        AnalysisEngineDescription aggregate = createEngineDescription(
                createEngineDescription(BreakIteratorSegmenter.class),
                itemAnno,
                mfsBaseline
        );

        // Check the external resource was injected
        AnalysisEngine engine = createEngine(aggregate);
        JCas jcas = engine.newJCas();
        jcas.setDocumentText("bank bat test");

        engine.process(jcas);

        int i=0;
        for (WSDResult wsdResult : JCasUtil.select(jcas, WSDResult.class)) {
            System.out.println(wsdResult);
            FSArray senses = wsdResult.getSenses();

            for (int j = 0; j < senses.size(); j++) {
                System.out.println(senses.get(j));
            }

            i++;
        }

        assertEquals(3, i);
    }
}
