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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.Pair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Scores all WSDResults against that of a given gold standard. The criteria are
 * defined by Agirre &amp; Edmonds, pp. 78-79. It is assumed that sense weights
 * given by the test algorithm are normalized.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class MultipleExactMatchEvaluator
    extends AbstractWSDEvaluator
{
    private Map<Pair<String, POS>, Integer> testAnnotatedInstances,
            bothAnnotatedInstances;
    private Map<Pair<String, POS>, Double> totalScore;
    private Map<POS, Integer> goldAnnotatedInstances;
    private int totalGoldAnnotatedInstances = 0;
    private Set<String> testAlgorithms;
    private final Logger logger = Logger.getLogger(getClass());

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        testAnnotatedInstances = new HashMap<Pair<String, POS>, Integer>();
        bothAnnotatedInstances = new HashMap<Pair<String, POS>, Integer>();
        goldAnnotatedInstances = new HashMap<POS, Integer>();
        testAlgorithms = new HashSet<String>();
        totalScore = new HashMap<Pair<String, POS>, Double>();
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        super.process(aJCas);

        // Score each WSDItem
        for (WSDItem wsdItem : JCasUtil.select(aJCas, WSDItem.class)) {
            POS pos = wsdItem.getPos() == null ? null : POS.valueOf(wsdItem
                    .getPos());
            WSDResult goldResult = null;
            List<WSDResult> wsdResults = getWSDResults(aJCas, wsdItem);

            // Extract the result from the gold standard algorithm
            for (WSDResult r : wsdResults) {
                if (r.getDisambiguationMethod().equals(goldStandardAlgorithm)) {
                    if (ignoreResult(r)) {
                        logger.info(goldStandardAlgorithm
                                + " result for " + wsdItem.getId()
                                + " matches the ignore pattern");
                    }
                    else if (goldResult == null) {
                        Integer count = goldAnnotatedInstances.get(pos);
                        if (count == null) {
                            goldAnnotatedInstances.put(pos, Integer.valueOf(1));
                        }
                        else {
                            goldAnnotatedInstances.put(pos,
                                    Integer.valueOf(count + 1));
                        }
                        totalGoldAnnotatedInstances++;
                        goldResult = r;
                    }
                    else {
                        // There should be only one gold standard annotation
                        throw new AnalysisEngineProcessException();
                    }
                }
            }

            // Extract the results from the test algorithms
            for (WSDResult testResult : wsdResults) {
                if (testResult.getDisambiguationMethod().equals(
                        goldStandardAlgorithm)) {
                    continue;
                }

                String testAlgorithm = testResult.getDisambiguationMethod();
                testAlgorithms.add(testAlgorithm);
                Pair<String, POS> testAlgorithmPOS = new Pair<String, POS>(
                        testAlgorithm, pos);
                Integer testCount = testAnnotatedInstances
                        .get(testAlgorithmPOS);
                if (testCount == null) {
                    testAnnotatedInstances.put(testAlgorithmPOS,
                            Integer.valueOf(1));
                    bothAnnotatedInstances.put(testAlgorithmPOS,
                            Integer.valueOf(0));
                    totalScore.put(testAlgorithmPOS, Double.valueOf(0.0));
                }
                else {
                    testAnnotatedInstances.put(testAlgorithmPOS,
                            Integer.valueOf(testCount + 1));
                }

                // We have no result to compare against
                if (goldResult == null) {
                    continue;
                }

                // Both results exist
                bothAnnotatedInstances.put(testAlgorithmPOS,
                        Integer.valueOf(bothAnnotatedInstances
                                .get(testAlgorithmPOS) + 1));
                for (int i = 0; i < goldResult.getSenses().size(); i++) {
                    for (int j = 0; j < testResult.getSenses().size(); j++) {
                        if (goldResult.getSenses(i).getId()
                                .equals(testResult.getSenses(j).getId())) {
                            totalScore.put(testAlgorithmPOS,
                                    totalScore.get(testAlgorithmPOS)
                                            + testResult.getSenses(j)
                                                    .getConfidence());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * A helper class for computing statistics (precision, recall, etc.)
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    class WSDStats
    {
        Map<String, String> map = null;
        final double precision, recall, coverage, f1, totalScore;
        final int testAnnotatedInstances, bothAnnotatedInstances,
                goldAnnotatedInstances;

        public WSDStats(Integer testAnnotatedInstances,
                Integer bothAnnotatedInstances, Integer goldAnnotatedInstances,
                Double totalScore)
        {

            this.goldAnnotatedInstances = goldAnnotatedInstances == null ? 0
                    : goldAnnotatedInstances;
            this.testAnnotatedInstances = testAnnotatedInstances == null ? 0
                    : testAnnotatedInstances;
            this.bothAnnotatedInstances = bothAnnotatedInstances == null ? 0
                    : bothAnnotatedInstances;
            this.totalScore = totalScore == null ? 0.0 : totalScore;

            if (this.bothAnnotatedInstances > 0) {
                precision = this.totalScore / bothAnnotatedInstances;
            }
            else {
                precision = 0.0;
            }

            if (goldAnnotatedInstances != null && goldAnnotatedInstances > 0) {
                recall = this.totalScore / goldAnnotatedInstances;
            }
            else {
                recall = 0.0;
            }

            if (goldAnnotatedInstances != null && goldAnnotatedInstances > 0) {
                coverage = (double) this.bothAnnotatedInstances
                        / goldAnnotatedInstances;
            }
            else {
                coverage = 0.0;
            }

            if (precision + recall > 0.0) {
                f1 = 2 * (precision * recall) / (precision + recall);
            }
            else {
                f1 = 0.0;
            }
        }
    }

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        System.out.format("%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%s\n",
                "POS", "test", "gold", "both", "p", "r", "cover", "F1",
                "algorithm");
        System.out.format("%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%s\n",
                "-------", "-------", "-------", "-------", "-------",
                "-------", "-------", "-------", "---------");

        for (String algorithm : testAlgorithms) {

            int totalTestAnnotatedInstances = 0;
            int totalBothAnnotatedInstances = 0;
            double totalTotalScore = 0.0;

            for (POS pos : POS.values()) {
                Pair<String, POS> algorithmPOS = new Pair<String, POS>(
                        algorithm, pos);

                WSDStats wsdStats = new WSDStats(
                        testAnnotatedInstances.get(algorithmPOS),
                        bothAnnotatedInstances.get(algorithmPOS),
                        goldAnnotatedInstances.get(pos),
                        totalScore.get(algorithmPOS));
                totalTestAnnotatedInstances += wsdStats.testAnnotatedInstances;
                totalBothAnnotatedInstances += wsdStats.bothAnnotatedInstances;
                totalTotalScore += wsdStats.totalScore;
                putWSDStats(algorithm, pos.toString(), wsdStats);
            }

            Pair<String, POS> algorithmPOS = new Pair<String, POS>(algorithm,
                    null);
            if (testAnnotatedInstances.containsKey(algorithmPOS)) {
                totalTestAnnotatedInstances += testAnnotatedInstances
                        .get(algorithmPOS);
            }
            if (bothAnnotatedInstances.containsKey(algorithmPOS)) {
                totalBothAnnotatedInstances += bothAnnotatedInstances
                        .get(algorithmPOS);
            }
            if (totalScore.containsKey(algorithmPOS)) {
                totalTotalScore += totalScore.get(algorithmPOS);
            }

            WSDStats wsdStats = new WSDStats(totalTestAnnotatedInstances,
                    totalBothAnnotatedInstances, totalGoldAnnotatedInstances,
                    totalTotalScore);
            putWSDStats(algorithm, "ALL", wsdStats);

        }
    }

    /**
     * Add the statistics for this algorithm/POS combination to the given map,
     * and also print them to stdout
     *
     * @param algorithm
     *            The name of the disambiguation method
     * @param pos
     *            The part of speech
     * @param wsdStats
     *            A @link{WSDStats} object containing the statistics
     * @param propertiesMap
     *            A map for eventual consumption by DKPro Lab
     */
    private void putWSDStats(String algorithm, String pos, WSDStats wsdStats)
    {
        // POS
        System.out.format("%7s\t", pos);

        // Number of test WSD results
        System.out.format("%7d\t", wsdStats.testAnnotatedInstances);

        // Number of gold WSD results
        System.out.format("%7d\t", wsdStats.goldAnnotatedInstances);

        // Number of WSD results where there is also a gold standard
        System.out.format("%7d\t", wsdStats.bothAnnotatedInstances);

        // Precision
        System.out.format("%1.5f\t", wsdStats.precision);

        // Recall
        System.out.format("%1.5f\t", wsdStats.recall);

        // Coverage
        System.out.format("%1.5f\t", wsdStats.coverage);

        // F1 score
        System.out.format("%1.5f\t", wsdStats.f1);

        System.out.println(algorithm);
    }

}
