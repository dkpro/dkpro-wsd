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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.impl.PropertiesAdapter;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.TaskContextProvider;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

//TODO: Rewrite with Chunk

/**
 * Scores a set of WSDResults from a given algorithm against that of a given
 * gold standard. The criteria are defined by Agirre &amp; Edmonds, pp. 78-79.
 * It is assumed that sense weights given by the test algorithm are normalized.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a> Andriy
 *         Nadolskyy
 */
public abstract class AbstractSingleExactMatchEvaluator
    extends AbstractWSDEvaluator
{
    public static final String PROPERTIES_KEY_BACKOFF = "backoff";
    public static final String PROPERTIES_KEY_WITH_BACKOFF = "+backoff";
    public static final String PROPERTIES_KEY_WITHOUT_BACKOFF = "-backoff";

    public static final String PARAM_TEST_ALGORITHM = "testAlgorithm";
    @ConfigurationParameter(name = PARAM_TEST_ALGORITHM, mandatory = true, description = "The test algorithm to be evaluated")
    protected String testAlgorithm;

    public static final String PARAM_BACKOFF_ALGORITHM = "backoffAlgorithm";
    @ConfigurationParameter(name = PARAM_BACKOFF_ALGORITHM, mandatory = false, description = "The backoff algorithm to use when the test algorithm is unable to make a sense assignment")
    protected String backoffAlgorithm;

    public static final String PARAM_BACKOFF_ALGORITHMS = "backoffAlgorithms";
    @ConfigurationParameter(name = PARAM_BACKOFF_ALGORITHMS, mandatory = false, description = "The backoff algorithms to use when the test algorithm is unable to make a sense assignment")
    protected String backoffAlgorithms[];

    public static final String PARAM_PROPERTIES_OUTPUT = "output";
    @ConfigurationParameter(name = PARAM_PROPERTIES_OUTPUT, mandatory = false, description = "An output location for the properties file for use with DKPro Lab")
    protected String outputProperties;

    public static final String PARAM_OUTPUT_FILE = "outputFilename";
    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = false, description = "The output file.  If unset, output goes to standard output.")
    protected String outputFilename;
    protected BufferedWriter output;

    @ExternalResource(api = TaskContextProvider.class, mandatory = false)
    protected TaskContext ctx;

    private final Logger logger = Logger.getLogger(getClass());
    protected Map<POS, Integer> testAnnotatedInstances;
    protected Map<POS, Integer> bothAnnotatedInstances;
    protected Map<POS, Double> totalScore;
    protected Map<POS, Double> backoffScore;
    protected Map<POS, Integer> goldAnnotatedInstances;
    protected Map<POS, Integer> backoffAnnotatedInstances;
    protected int totalGoldAnnotatedInstances = 0;
    protected Map<String, Integer> backoffAlgorithmToNumber;

    protected abstract void beginFile(String fileTitle)
        throws IOException;

    protected abstract void endFile()
        throws IOException;

    protected abstract void beginTableRow()
        throws IOException;

    protected abstract void endTableRow()
        throws IOException;

    protected abstract void endTable()
        throws IOException;

    protected abstract void beginTable()
        throws IOException;

    protected abstract void beginDocument(String documentTitle)
        throws IOException;

    protected abstract void endDocument()
        throws IOException;

    protected abstract void tableHeader(String cellContents)
        throws IOException;

    protected abstract void tableCell(String cellContents)
        throws IOException;

    protected abstract void paragraph(String text)
            throws IOException;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        if (backoffAlgorithms != null && backoffAlgorithms.length == 0) {
            throw new CASRuntimeException(CASRuntimeException.ILLEGAL_ARRAY_SIZE);
        }

        if (backoffAlgorithm != null) {
            if (backoffAlgorithms != null) {
                throw new ResourceInitializationException(
                        org.apache.uima.UIMAException.STANDARD_MESSAGE_CATALOG,
                        "annotator_mutually_exclusive_params",
                        new Object[] { PARAM_BACKOFF_ALGORITHM + ", "
                                + PARAM_BACKOFF_ALGORITHMS });
                }
            backoffAlgorithms = new String[] { backoffAlgorithm };
            backoffAlgorithm = null;
        }

        testAnnotatedInstances = new HashMap<POS, Integer>();
        bothAnnotatedInstances = new HashMap<POS, Integer>();
        backoffAnnotatedInstances = new HashMap<POS, Integer>();
        goldAnnotatedInstances = new HashMap<POS, Integer>();
        totalScore = new HashMap<POS, Double>();
        backoffScore = new HashMap<POS, Double>();
        for (POS pos : POS.values()) {
            testAnnotatedInstances.put(pos, Integer.valueOf(0));
            goldAnnotatedInstances.put(pos, Integer.valueOf(0));
            bothAnnotatedInstances.put(pos, Integer.valueOf(0));
            backoffAnnotatedInstances.put(pos, Integer.valueOf(0));
            totalScore.put(pos, Double.valueOf(0.0));
            backoffScore.put(pos, Double.valueOf(0.0));
        }
        backoffAlgorithmToNumber = new HashMap<String, Integer>();
        if (backoffAlgorithms != null) {
            for (int i = 0; i < backoffAlgorithms.length; i++) {
                backoffAlgorithmToNumber.put(backoffAlgorithms[i], i);
            }
        }
        if (outputFilename != null) {
            try {
                output = new BufferedWriter(new FileWriter(outputFilename));
            }
            catch (IOException e) {
                throw new ResourceInitializationException(e);
            }
        }
        else {
            output = new BufferedWriter(new OutputStreamWriter(System.out));
        }
        try {
            beginFile(this.getClass().getSimpleName());
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    // TODO: Split this method up into various submethods. It will make it
    // easier to override behaviour in subclasses.
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
            POS pos = wsdItem.getPos() == null ? null : POS.valueOf(wsdItem
                    .getPos());
            WSDResult goldResult = null;
            WSDResult backoffResult = null;
            WSDResult testResult = null;
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
                        logger.error("Multiple gold standard annotations found for "
                                + r.getWsdItem().getId());
                        throw new AnalysisEngineProcessException();
                    }
                    else {
                        goldResult = r;
                        goldAnnotatedInstances.put(pos, Integer
                                .valueOf(goldAnnotatedInstances.get(pos) + 1));
                        totalGoldAnnotatedInstances++;
                    }
                }
                else if (backoffAlgorithms != null && backoffAlgorithms.length > 0
                        && backoffAlgorithmToNumber.containsKey(r
                                .getDisambiguationMethod())) {
                    // look if the current backoff algorithm a "higher priority"
                    // than saved one has
                    if (backoffResult != null) {
                        String savedBackoffResultMethod = backoffResult
                                .getDisambiguationMethod();
                        String currentBackoffResultMethod = r
                                .getDisambiguationMethod();
                        if (backoffAlgorithmToNumber
                                .get(savedBackoffResultMethod) > backoffAlgorithmToNumber
                                .get(currentBackoffResultMethod)) {
                            backoffResult = r;
                        }
                    }
                    else {
                        backoffResult = r;
                    }
                }
                else if (r.getDisambiguationMethod().equals(testAlgorithm)) {
                    if (testResult != null) {
                        // There should be only one test algorithm annotation
                        logger.error("Multiple test algorithm annotations found for "
                                + r.getWsdItem().getId());
                        throw new AnalysisEngineProcessException();
                    }
                    testResult = r;
                    testAnnotatedInstances.put(pos, Integer
                            .valueOf(testAnnotatedInstances.get(pos) + 1));
                }
            }

            // We have no result to compare against
            if (goldResult == null) {
                logger.info(wsdItem.getId() + ": no gold standard annotation");
                continue;
            }

            // Print gold standard annotations
            for (int i = 0; i < goldResult.getSenses().size(); i++) {
                logger.info(goldResult.getWsdItem().getId() + ": "
                        + goldResult.getDisambiguationMethod() + " annotation "
                        + goldResult.getSenseInventory() + "/"
                        + goldResult.getSenses(i).getId() + " score: "
                        + goldResult.getSenses(i).getConfidence());
            }

            // We have a test result and a gold result
            if (testResult != null) {
                bothAnnotatedInstances.put(pos,
                        Integer.valueOf(bothAnnotatedInstances.get(pos) + 1));
                totalScore.put(
                        pos,
                        Double.valueOf(totalScore.get(pos)
                                + getMatchingScore(testResult, goldResult)));
                continue;
            }

            logger.info(goldResult.getWsdItem().getId() + ": " + testAlgorithm
                    + " total score: 0.0");

            // We have a gold result but no test result, so we use the backoff
            if (backoffResult != null) {
                backoffAnnotatedInstances
                        .put(pos, Integer.valueOf(backoffAnnotatedInstances
                                .get(pos) + 1));
                backoffScore.put(
                        pos,
                        Double.valueOf(backoffScore.get(pos)
                                + getMatchingScore(backoffResult, goldResult)));
                continue;
            }

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

    /**
     * A helper class for computing statistics (precision, recall, etc.)
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    class WSDStats
    {
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
                f1 = 2.0 * (precision * recall) / (precision + recall);
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

        try {
            beginDocument("Document");
            beginTable();
            beginTableRow();
            tableHeader("POS");
            tableHeader("test");
            tableHeader("gold");
            tableHeader("both");
            tableHeader("score");
            tableHeader("p");
            tableHeader("r");
            tableHeader("cover");
            tableHeader("F1");
            tableHeader("backoff");
            endTableRow();
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

        int totalTestAnnotatedInstances = 0;
        int totalBothAnnotatedInstances = 0;
        int totalBackoffAnnotatedInstances = 0;
        double totalTotalScore = 0.0;
        double totalBackoffScore = 0.0;

        Map<String, String> propertiesMap = null;
        if (ctx != null && outputProperties != null) {
            propertiesMap = new HashMap<String, String>();
            if (backoffAlgorithms[0] != null) {
                propertiesMap.put(PROPERTIES_KEY_BACKOFF, backoffAlgorithms[0]);
            }
            propertiesMap.put("algorithm", testAlgorithm);
        }

        // Print results for each part of speech
        for (POS pos : POS.values()) {
            WSDStats wsdStats = new WSDStats(testAnnotatedInstances.get(pos),
                    bothAnnotatedInstances.get(pos),
                    goldAnnotatedInstances.get(pos), totalScore.get(pos));
            totalTestAnnotatedInstances += wsdStats.testAnnotatedInstances;
            totalBothAnnotatedInstances += wsdStats.bothAnnotatedInstances;
            totalTotalScore += wsdStats.totalScore;

            try {
                putWSDStats(pos.toString(), wsdStats, false);
            }
            catch (IOException e) {
                throw new AnalysisEngineProcessException(e);
            }
            putWSDStatsToPropertiesMap(pos.toString(), wsdStats, false,
                    propertiesMap);
            if (backoffAlgorithms != null) {
                wsdStats = new WSDStats(testAnnotatedInstances.get(pos)
                        + backoffAnnotatedInstances.get(pos),
                        bothAnnotatedInstances.get(pos)
                                + backoffAnnotatedInstances.get(pos),
                        goldAnnotatedInstances.get(pos), totalScore.get(pos)
                                + backoffScore.get(pos));
                totalBackoffAnnotatedInstances += backoffAnnotatedInstances
                        .get(pos);
                totalBackoffScore += backoffScore.get(pos);
                try {
                    putWSDStats(pos.toString(), wsdStats, true);
                }
                catch (IOException e) {
                    throw new AnalysisEngineProcessException(e);
                }
                putWSDStatsToPropertiesMap(pos.toString(), wsdStats, true,
                        propertiesMap);
            }
        }

        // Print results for all POS combined
        WSDStats wsdStats = new WSDStats(totalTestAnnotatedInstances,
                totalBothAnnotatedInstances, totalGoldAnnotatedInstances,
                totalTotalScore);
        try {
            putWSDStats("all", wsdStats, false);
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
        putWSDStatsToPropertiesMap("all", wsdStats, false, propertiesMap);
        if (backoffAlgorithms != null) {
            wsdStats = new WSDStats(totalTestAnnotatedInstances
                    + totalBackoffAnnotatedInstances,
                    totalBothAnnotatedInstances
                            + totalBackoffAnnotatedInstances,
                    totalGoldAnnotatedInstances, totalTotalScore
                            + totalBackoffScore);
            try {
                putWSDStats("all", wsdStats, true);
            }
            catch (IOException e) {
                throw new AnalysisEngineProcessException(e);
            }
            putWSDStatsToPropertiesMap("all", wsdStats, true, propertiesMap);
        }

        if (propertiesMap != null && ctx != null) {
            ctx.storeBinary(outputProperties, new PropertiesAdapter(
                    propertiesMap));
        }

        try {
            endTable();
            endDocument();
            endFile();
            output.close();
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    /**
     * Print the statistics for this algorithm/POS combination.
     *
     * @param pos
     *            The part of speech
     * @param wsdStats
     *            A @link{WSDStats} object containing the statistics
     */
    protected void putWSDStats(String pos, WSDStats wsdStats, boolean backoff)
        throws IOException
    {
        beginTableRow();

        // POS
        tableCell(String.format("%7s", pos));

        // Number of test WSD results
        tableCell(String.format("%7d", wsdStats.testAnnotatedInstances));

        // Number of gold WSD results
        tableCell(String.format("%7d", wsdStats.goldAnnotatedInstances));

        // Number of WSD results where there is also a gold standard
        tableCell(String.format("%7d", wsdStats.bothAnnotatedInstances));

        // Score
        tableCell(String.format("%7.2f", wsdStats.totalScore));

        // Precision
        tableCell(String.format("%1.5f", wsdStats.precision));

        // Recall
        tableCell(String.format("%1.5f", wsdStats.recall));

        // Coverage
        tableCell(String.format("%1.5f", wsdStats.coverage));

        // F1 score
        tableCell(String.format("%1.5f", wsdStats.f1));

        // backoff
        tableCell(String.format("%7s", backoff));

        endTableRow();
    }

    /**
     * Add the statistics for this algorithm/POS combination to the given map.
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
    private void putWSDStatsToPropertiesMap(String pos, WSDStats wsdStats,
            boolean backoff, Map<String, String> propertiesMap)
    {
        if (propertiesMap == null) {
            return;
        }
        String parameters = "_" + pos;
        if (backoff) {
            parameters += PROPERTIES_KEY_WITH_BACKOFF;
        }
        else {
            parameters += PROPERTIES_KEY_WITHOUT_BACKOFF;
        }
        propertiesMap.put("coverage" + parameters,
                String.valueOf(wsdStats.coverage));
        propertiesMap.put("precision" + parameters,
                String.valueOf(wsdStats.precision));
        propertiesMap.put("recall" + parameters,
                String.valueOf(wsdStats.recall));
        propertiesMap.put("f1" + parameters, String.valueOf(wsdStats.f1));
    }
}
