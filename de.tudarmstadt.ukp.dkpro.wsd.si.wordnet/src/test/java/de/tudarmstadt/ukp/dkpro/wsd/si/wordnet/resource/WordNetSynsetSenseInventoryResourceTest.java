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

package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.resource;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.bindResource;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.resource.WordNetSynsetSenseInventoryResource;

public class WordNetSynsetSenseInventoryResourceTest
{

    public static class Annotator extends JCasAnnotator_ImplBase {
        final static String MODEL_KEY = "SenseInventory";
        @ExternalResource(key = MODEL_KEY)
        private SenseInventory inventory;

        @Override
        public void process(JCas aJCas)
            throws AnalysisEngineProcessException
        {
//            try {
                System.out.println(inventory.getSenseInventoryName());
//                for (Map.Entry<String, List<String>> entry : inventory.getSenseInventory().entrySet()) {
//                    System.out.println(entry.getKey() + " / " + entry.getValue());
//                }
//            } catch (SenseInventoryException e) {
//                throw new AnalysisEngineProcessException(e);
//            }
        }
    }

    @Ignore
    @Test
    public void configureAggregatedExample() throws Exception {
        AnalysisEngineDescription desc = createEngineDescription(Annotator.class);

        bindResource(
                desc,
                Annotator.MODEL_KEY,
                WordNetSynsetSenseInventoryResource.class,
                WordNetSynsetSenseInventoryResource.PARAM_WORDNET_PROPERTIES_URL, "/home/miller/share/WordNet/WordNet-2.1/wordnet_properties.xml"
        );

        // Check the external resource was injected
        AnalysisEngine ae = createEngine(desc);
        ae.process(ae.newJCas());
    }

}
