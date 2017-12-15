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

package de.tudarmstadt.ukp.dkpro.wsd.evaluation;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

public class ConfusionMatrixTest
{

    private final String method1 = "Method1";
    private final String method2 = "Method2";
    private final String gold = "Gold";
    private final String inventory = "myInventory";

    @Test
    public void testProcessJCasTokens()
        throws AnalysisEngineProcessException, ResourceInitializationException
    {
        AnalysisEngine engine = AnalysisEngineFactory.createEngine(
                ConfusionMatrix.class,
                ConfusionMatrix.PARAM_GOLD_STANDARD_ALGORITHM, gold,
                ConfusionMatrix.PARAM_TEST_ALGORITHM1, method1,
                ConfusionMatrix.PARAM_TEST_ALGORITHM2, method2,
                ConfusionMatrix.PARAM_MCNEMAR_CORRECTION, 0.5f);

        JCas jcas = engine.newJCas();

        WSDItem[] wsdItems = new WSDItem[6];
        WSDResult[] goldResults = new WSDResult[6];
        WSDResult[] test1Results = new WSDResult[6];
        WSDResult[] test2Results = new WSDResult[6];

        FSArray correctSenseArray = new FSArray(jcas, 1);
        Sense sense = new Sense(jcas);
        sense.setId("correctSense");
        sense.setConfidence(1.0);
        sense.setDescription("some description");
        sense.addToIndexes();
        correctSenseArray.set(0, sense);
        correctSenseArray.addToIndexes(); // required?

        FSArray incorrectSenseArray = new FSArray(jcas, 1);
        sense = new Sense(jcas);
        sense.setId("incorrectSense");
        sense.setConfidence(1.0);
        sense.setDescription("some description");
        sense.addToIndexes();
        incorrectSenseArray.set(0, sense);
        incorrectSenseArray.addToIndexes(); // required?

        for (int i = 0; i < 6; i++) {
            wsdItems[i] = new WSDItem(jcas);
            wsdItems[i].setId("wsdItem" + i);
            wsdItems[i].addToIndexes();

            goldResults[i] = new WSDResult(jcas);
            goldResults[i].setWsdItem(wsdItems[i]);
            goldResults[i].setDisambiguationMethod(gold);
            goldResults[i].setSenseInventory(inventory);
            goldResults[i].setSenses(correctSenseArray);
            goldResults[i].addToIndexes();

            test1Results[i] = new WSDResult(jcas);
            test1Results[i].setWsdItem(wsdItems[i]);
            test1Results[i].setDisambiguationMethod(method1);
            test1Results[i].setSenseInventory(inventory);
            if (i < 5) {
                test1Results[i].setSenses(correctSenseArray);
            }
            else {
                test1Results[i].setSenses(incorrectSenseArray);
            }
            test1Results[i].addToIndexes();

            test2Results[i] = new WSDResult(jcas);
            test2Results[i].setWsdItem(wsdItems[i]);
            test2Results[i].setDisambiguationMethod(method2);
            test2Results[i].setSenseInventory(inventory);
            if (i > 1) {
                test2Results[i].setSenses(correctSenseArray);
            }
            else {
                test2Results[i].setSenses(incorrectSenseArray);
            }
            test2Results[i].addToIndexes();
        }

        engine.process(jcas);
        engine.collectionProcessComplete();
    }

}
