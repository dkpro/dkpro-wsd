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

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Given two test algorithms and a gold standard, outputs a confusion matrix for
 * the test algorithms. Note that the two test algorithms must assign senses
 * with a probability of 1.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class ConfusionMatrix
    extends AbstractWSDEvaluator
{
    public static final String PARAM_TEST_ALGORITHM1 = "testAlgorithm1";
    @ConfigurationParameter(name = PARAM_TEST_ALGORITHM1, mandatory = true, description = "The first test algorithm to be evaluated")
    protected String testAlgorithm1;

    public static final String PARAM_TEST_ALGORITHM2 = "testAlgorithm2";
    @ConfigurationParameter(name = PARAM_TEST_ALGORITHM2, mandatory = true, description = "The second test algorithm to be evaluated")
    protected String testAlgorithm2;

    public static final String PARAM_MCNEMAR_CORRECTION = "mcnemarCorrection";
    @ConfigurationParameter(name = PARAM_MCNEMAR_CORRECTION, mandatory = false, description = "The correction to use for McNemar's test", defaultValue = "0.0")
    protected double mcnemarCorrection;

    protected double[][] agreement;

    private final Logger logger = Logger.getLogger(getClass());

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        agreement = new double[2][2];
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        super.process(aJCas);

        // Score each WSDItem
        for (WSDItem wsdItem : JCasUtil.select(aJCas, WSDItem.class)) {
            if (maxItemsAttempted >= 0
                    && numItemsAttempted++ >= maxItemsAttempted) {
                break;
            }
            WSDResult goldResult = null;
            WSDResult test1Result = null;
            WSDResult test2Result = null;
            double test1Score, test2Score;
            List<WSDResult> wsdResults = getWSDResults(aJCas, wsdItem);

            // Make a first pass through the list of WSDResults for this
            // WSDItem to extract the gold, test, and backoff algorithm
            // results.
            for (WSDResult r : wsdResults) {
                if (r.getDisambiguationMethod().equals(goldStandardAlgorithm)) {
                    if (ignoreResult(r)) {
                        logger.info(goldStandardAlgorithm + " result for "
                                + wsdItem.getId()
                                + " matches the ignore pattern");
                    }
                    else if (goldResult != null) {
                        // There should be only one gold standard annotation
                        logger.error("Multiple gold standard annotations found");
                        throw new AnalysisEngineProcessException();
                    }
                    else {
                        goldResult = r;
                    }
                }
                else if (r.getDisambiguationMethod().equals(testAlgorithm1)) {
                    if (test1Result != null) {
                        // There should be only one first test algorithm
                        // annotation
                        logger.error("Multiple first test algorithm annotations found");
                        throw new AnalysisEngineProcessException();
                    }
                    test1Result = r;
                }
                else if (r.getDisambiguationMethod().equals(testAlgorithm2)) {
                    if (test2Result != null) {
                        // There should be only one second test algorithm
                        // annotation
                        logger.error("Multiple second test algorithm annotations found");
                        throw new AnalysisEngineProcessException();
                    }
                    test2Result = r;
                }
            }

            // We have no result to compare against
            if (goldResult == null) {
                logger.info(wsdItem.getId() + ": no gold standard annotation");
                continue;
            }

            test1Score = getMatchingScore(test1Result, goldResult);
            test2Score = getMatchingScore(test2Result, goldResult);

            // Print gold standard annotations
            for (int i = 0; i < goldResult.getSenses().size(); i++) {
                logger.info(goldResult.getWsdItem().getId() + ": "
                        + goldResult.getDisambiguationMethod() + "="
                        + goldResult.getSenseInventory() + "/"
                        + goldResult.getSenses(i).getId());
            }

            logger.info(goldResult.getWsdItem().getId()
                    + ": T1="
                    + test1Score
                    + " T2="
                    + test2Score
                    + "; T1="
                    + (test1Result == null ? "null" : test1Result.getSenses(0)
                            .getId())
                    + " T2="
                    + (test2Result == null ? "null" : test2Result.getSenses(0)
                            .getId()));
            agreement[1][1] += test1Score * test2Score;
            agreement[1][0] += test1Score * (1.0 - test2Score);
            agreement[0][1] += (1.0 - test1Score) * test2Score;
            agreement[0][0] += (1.0 - test1Score) * (1.0 - test2Score);
        }
    }

    /**
     * Given a test and gold standard set of disambiguation results, loop
     * through them to find the common senses, and return the sum of the test
     * results' confidence values.
     *
     * @param testResult
     * @param goldResult
     * @return
     */
    protected double getMatchingScore(WSDResult testResult, WSDResult goldResult)
    {
        if (testResult == null) {
            return 0.0;
        }
        double confidence = 0.0;
        for (int i = 0; i < goldResult.getSenses().size(); i++) {
            for (int j = 0; j < testResult.getSenses().size(); j++) {
                if (goldResult.getSenses(i).getId()
                        .equals(testResult.getSenses(j).getId())) {
                    double testConfidence = testResult.getSenses(j)
                            .getConfidence();
                    logger.info(testResult.getWsdItem().getId() + ": "
                            + testResult.getDisambiguationMethod()
                            + " annotation " + testResult.getSenseInventory()
                            + "/" + testResult.getSenses(j).getId()
                            + " score: " + testConfidence);
                    confidence += testConfidence;
                }
            }
        }

        logger.info(testResult.getWsdItem().getId() + ": "
                + testResult.getDisambiguationMethod() + " total score: "
                + confidence);

        return confidence;
    }

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        System.out.println("AGREEMENT MATRIX");
        System.out.println("================\n");

        System.out.println("Test algorithm 1 (T1)  : " + testAlgorithm1);
        System.out.println("Test algorithm 2 (T2)  : " + testAlgorithm2);
        System.out.println("Gold standard algorithm: " + goldStandardAlgorithm);

        System.out.println("\n            \tT2 incorrect\tT2   correct");
        System.out.format("T1 incorrect\t%12f\t%12f\n", agreement[0][0],
                agreement[0][1]);
        System.out.format("T1   correct\t%12f\t%12f\n", agreement[1][0],
                agreement[1][1]);
        System.out.format("\nMcNemar's test (with correction %f): %f", mcnemarCorrection, mcnemar(agreement, mcnemarCorrection));
    }

    public static double mcnemar(double[][] agreement, double mcnemarCorrection)
    {
        double x = Math.abs(agreement[0][1] - agreement[1][0]) - mcnemarCorrection;
        return (x * x) / (agreement[0][1] + agreement[1][0]);
    }
}
