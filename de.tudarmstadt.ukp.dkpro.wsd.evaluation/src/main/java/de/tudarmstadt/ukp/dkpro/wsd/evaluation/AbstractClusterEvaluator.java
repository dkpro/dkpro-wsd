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

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseClusterer;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * This class evaluates a clustering of word senses against a computed random
 * clustering of the same granularity, as described in R. Snow, S. Prakash, D.
 * Jurafsky, and A. Y. Ng, "Learning to Merge Word Senses" (2007).
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public abstract class AbstractClusterEvaluator
    extends AbstractSingleExactMatchEvaluator
{
    public final static String SENSE_INVENTORY_RESOURCE = "SenseInventory";
    @ExternalResource(key = SENSE_INVENTORY_RESOURCE)
    protected SenseInventory inventory;

    public static final String PARAM_CLUSTER_URL = "clusterUrl";
    @ConfigurationParameter(name = PARAM_CLUSTER_URL, mandatory = true, description = "The URL of a delimited text file containing sense clusters")
    protected String clusterUrl;

    public static final String PARAM_DELIMITER_REGEX = "delimiterRegex";
    @ConfigurationParameter(name = PARAM_DELIMITER_REGEX, mandatory = false, description = "A regular expression matching the delimiter between clusters", defaultValue = "[\\t ]+")
    protected String delimiterRegex;

    public final static String PARAM_SHOW_IMPROVED_SODS = "showImprovedSods";
    @ConfigurationParameter(name = PARAM_SHOW_IMPROVED_SODS, mandatory = false, description = "Whether to show the subjects of disambiguation with improved scores", defaultValue = "false")
    protected boolean showImprovedSods;

    public final static String PARAM_SHOW_IMPROVED_INSTANCES = "showImprovedInstances";
    @ConfigurationParameter(name = PARAM_SHOW_IMPROVED_INSTANCES, mandatory = false, description = "Whether to show the instances with improved scores", defaultValue = "false")
    protected boolean showImprovedInstances;

    private final Logger logger = Logger.getLogger(getClass());
    protected SenseClusterer senseClusterer;
    protected Map<POS, Double> clusteredScore;
    protected Map<POS, Double> randomClusteredScore;
    protected Map<String, Double> cachedRandomWordScore;
    protected int numberOfClusteredLexicalItems;
    protected int numberOfClusteredInstances;
    protected int improvedInstanceCount;
    protected List<String> improvedInstances;
    protected Set<String> improvedSods;
    protected Map<String, Double> clusteredScoreByLemma;
    protected Map<String, Double> randomClusteredScoreByLemma;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        if (backoffAlgorithm != null) {
            // TODO: Backoff support not implemented yet
            throw new ResourceInitializationException();
        }

        improvedInstances = new ArrayList<String>();
        improvedSods = new HashSet<String>();
        numberOfClusteredLexicalItems = 0;
        numberOfClusteredInstances = 0;
        improvedInstanceCount = 0;
        cachedRandomWordScore = new HashMap<String, Double>();
        clusteredScore = new HashMap<POS, Double>();
        randomClusteredScore = new HashMap<POS, Double>();
        clusteredScoreByLemma = new HashMap<String, Double>();
        randomClusteredScoreByLemma = new HashMap<String, Double>();
        for (POS pos : POS.values()) {
            clusteredScore.put(pos, Double.valueOf(0.0));
            randomClusteredScore.put(pos, Double.valueOf(0.0));
        }
        try {
            senseClusterer = new SenseClusterer(clusterUrl, delimiterRegex);
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    private static class Results
    {
        WSDResult testResult;
        WSDResult goldResult;
    }

    protected Results getTestAndGoldResults(JCas aJCas, WSDItem wsdItem)
        throws AnalysisEngineProcessException
    {
        Results results = new Results();
        POS pos = wsdItem.getPos() == null ? null : POS.valueOf(wsdItem
                .getPos());

        // Search through the list of WSDResults for this
        // WSDItem to extract the gold and test algorithm results.
        for (WSDResult r : getWSDResults(aJCas, wsdItem)) {
            if (r.getDisambiguationMethod().equals(goldStandardAlgorithm)) {
                if (ignoreResult(r)) {
                    logger.info(goldStandardAlgorithm + " result for "
                            + wsdItem.getId() + " matches the ignore pattern");
                }
                else if (results.goldResult != null) {
                    // There should be only one gold standard annotation
                    logger.error("Multiple gold standard annotations found for "
                            + r.getWsdItem().getId());
                    throw new AnalysisEngineProcessException();
                }
                else {
                    results.goldResult = r;
                    goldAnnotatedInstances.put(pos, Integer
                            .valueOf(goldAnnotatedInstances.get(pos) + 1));
                    totalGoldAnnotatedInstances++;
                }
            }
            else if (r.getDisambiguationMethod().equals(testAlgorithm)) {
                if (results.testResult != null) {
                    // There should be only one test algorithm annotation
                    logger.error("Multiple test algorithm annotations found for "
                            + r.getWsdItem().getId());
                    throw new AnalysisEngineProcessException();
                }
                results.testResult = r;
                testAnnotatedInstances.put(pos,
                        Integer.valueOf(testAnnotatedInstances.get(pos) + 1));
            }
        }
        return results;
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        wsdItemIndex = null;

        for (WSDItem wsdItem : JCasUtil.select(aJCas, WSDItem.class)) {
            if (maxItemsAttempted >= 0
                    && numItemsAttempted++ >= maxItemsAttempted) {
                break;
            }
            POS pos = wsdItem.getPos() == null ? null : POS.valueOf(wsdItem
                    .getPos());

            // Get the gold standard and test results
            Results results = getTestAndGoldResults(aJCas, wsdItem);
            WSDResult goldResult = results.goldResult;
            WSDResult testResult = results.testResult;
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

            if (testResult == null) {
                logger.info(wsdItem.getId() + ": no test annotation");
                logger.info(wsdItem.getId() + ": " + testAlgorithm
                        + " total score: 0.0");
                continue;
            }

            bothAnnotatedInstances.put(pos,
                    Integer.valueOf(bothAnnotatedInstances.get(pos) + 1));

            try {
                getScores(wsdItem, testResult, goldResult);
            }
            catch (SenseInventoryException e) {
                throw new AnalysisEngineProcessException(e);
            }
        }
    }

    protected void getScores(WSDItem wsdItem, WSDResult testResult,
            WSDResult goldResult)
        throws SenseInventoryException
    {
        String sod = wsdItem.getSubjectOfDisambiguation();
        POS pos = wsdItem.getPos() == null ? null : POS.valueOf(wsdItem
                .getPos());
        String sodPos = sod + "/" + pos;
        double scoreWithoutClustering;
        double scoreWithClustering;
        double scoreWithRandomClustering;

        // Calculate unclustered, clustered, and random-clustered scores
        scoreWithoutClustering = getMatchingScore(testResult, goldResult);
        scoreWithClustering = getMatchingClusteredScore(testResult, goldResult);
        scoreWithRandomClustering = (scoreWithoutClustering > 0.0) ? scoreWithoutClustering
                : getRandomScore(wsdItem, pos);
        assert (scoreWithoutClustering <= scoreWithClustering);

        // Store unclustered, clustered, and random-clustered scores
        if (scoreWithoutClustering > 0.0) {
            totalScore.put(pos, Double.valueOf(totalScore.get(pos)
                    + scoreWithoutClustering));
        }
        if (scoreWithClustering > 0.0) {
            clusteredScore.put(
                    pos,
                    Double.valueOf(clusteredScore.get(pos)
                            + scoreWithClustering));
            if (showImprovedSods) {
                Double oldLemmaScore = clusteredScoreByLemma.get(sodPos);
                if (oldLemmaScore == null) {
                    oldLemmaScore = Double.valueOf(0.0);
                }
                clusteredScoreByLemma.put(sodPos,
                        Double.valueOf(oldLemmaScore + scoreWithClustering));
            }
        }
        if (scoreWithRandomClustering > 0.0) {
            randomClusteredScore.put(
                    pos,
                    Double.valueOf(randomClusteredScore.get(pos)
                            + scoreWithRandomClustering));
            if (showImprovedSods) {
                Double oldLemmaScore = randomClusteredScoreByLemma.get(sodPos);
                if (oldLemmaScore == null) {
                    oldLemmaScore = Double.valueOf(0.0);
                }
                randomClusteredScoreByLemma.put(
                        sodPos,
                        Double.valueOf(oldLemmaScore
                                + scoreWithRandomClustering));
            }
        }

        // Make a note of any SoDs and Instances where clustering helped
        if (scoreWithClustering > scoreWithoutClustering
                && scoreWithClustering > scoreWithRandomClustering) {
            improvedSods.add(sodPos);
            improvedInstanceCount++;
            if (showImprovedInstances) {
                improvedInstances.add(wsdItem.getId());
            }
        }

        // Print all three scores
        logger.info(wsdItem.getId() + "/" + sodPos + " unclustered score: "
                + scoreWithoutClustering + " clustered score: "
                + scoreWithClustering + " random score: "
                + scoreWithRandomClustering);
    }

    /**
     * Returns the expected score for a random clustering of the same
     * granularity as the given clustering. That is, given a subject of
     * disambiguation, we look up its senses and observe the number and size of
     * the clusters which contain these senses. We then assume that the senses
     * are randomly shuffled among these clusters, and return the probability
     * that an incorrectly chosen sense and the actual correct sense would be
     * found in the same cluster. Note that this method assumes that there is a
     * <i>single</i> correct sense.
     *
     * @param subjectOfDisambiguation
     * @return
     * @throws SenseInventoryException
     */
    protected Double getRandomScore(WSDItem wsdItem, POS pos)
        throws SenseInventoryException
    {
        String sod = wsdItem.getSubjectOfDisambiguation();
        String sodPos = sod + '/' + pos;

        // Return cached score for this sod if it exists
        Double cachedScore = cachedRandomWordScore.get(sodPos);
        if (cachedScore == null) {

            // Compute score from random clustering
            List<String> senses = inventory.getSenses(sod, pos);
            if (senses.isEmpty()) {
                logger.warn("No random clustering score for '" + sodPos
                        + "'; not in sense inventory");
                return Double.valueOf(0.0);
            }

            // Snow et al. set numberOfSenses to senses.size(), though this
            // assumes that all senses in a cluster share the same lexical item.
            // Instead we compute the number of senses based on the cardinality
            // of the actual clusters.
            int numberOfSenses = 0;
            Set<Set<String>> clusters = new HashSet<Set<String>>();
            double score = 0.0;
            int numberOfClusters = 0;
            List<Integer> clusterSizes = new ArrayList<Integer>();
            for (String sense : senses) {
                Set<String> cluster = senseClusterer.getCluster(sense);
                if (clusters.add(cluster)) {
                    numberOfClusters++;
                    clusterSizes.add(Integer.valueOf(cluster.size()));
                    numberOfSenses += cluster.size();
                    score += cluster.size() * (cluster.size() - 1);
                }
            }
            if (numberOfSenses == 1) {
                logger.warn("getRandomScore() unexpectedly called when there is only one cluster. Probably the answer key for "
                        + wsdItem.getId() + "/" + sodPos + " is wrong.");
                score = 0.0;
            }
            else {
                score /= numberOfSenses * (numberOfSenses - 1);
            }
            cachedScore = Double.valueOf(score);
            cachedRandomWordScore.put(sodPos, cachedScore);
            logger.info(wsdItem.getId() + '/' + sodPos + " has "
                    + numberOfClusters + " clusters over " + numberOfSenses
                    + " senses with sizes " + clusterSizes);
            if (cachedScore > 0.0) {
                numberOfClusteredLexicalItems++;
            }
        }

        if (cachedScore > 0.0) {
            numberOfClusteredInstances++;
        }

        logger.info(wsdItem.getId() + ": random cluster score: " + cachedScore);
        return cachedScore;
    }

    /**
     * Given a test and gold standard set of disambiguation results, loop
     * through them to find the common senses (where two senses are common if
     * they are in the same cluster), and return the sum of the test results'
     * confidence values.
     *
     * @param testResult
     * @param goldResult
     * @return
     */
    double getMatchingClusteredScore(WSDResult testResult, WSDResult goldResult)
    {
        double confidence = 0.0;
        for (int i = 0; i < goldResult.getSenses().size(); i++) {
            for (int j = 0; j < testResult.getSenses().size(); j++) {
                if (senseClusterer.inSameCluster(goldResult.getSenses(i)
                        .getId(), testResult.getSenses(j).getId())) {
                    double testConfidence = testResult.getSenses(j)
                            .getConfidence();
                    logger.info(testResult.getWsdItem().getId() + ": "
                            + testResult.getDisambiguationMethod()
                            + " annotation " + testResult.getSenseInventory()
                            + "/" + testResult.getSenses(j).getId()
                            + " cluster score: " + testConfidence);
                    confidence += testConfidence;
                }
            }
        }

        logger.info(testResult.getWsdItem().getId() + ": "
                + testResult.getDisambiguationMethod()
                + " total cluster score: " + confidence);

        return confidence;
    }

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {

        try {
            beginDocument("Document");
            beginTable();
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

        int totalTestAnnotatedInstances = 0;
        int totalBothAnnotatedInstances = 0;
        double totalUnclusteredScore = 0.0;
        double totalClusteredScore = 0.0;
        double totalRandomClusteredScore = 0.0;

        // Print results for each part of speech
        for (POS pos : POS.values()) {
            WSDStats unclusteredWsdStats = new WSDStats(
                    testAnnotatedInstances.get(pos),
                    bothAnnotatedInstances.get(pos),
                    goldAnnotatedInstances.get(pos), totalScore.get(pos));
            totalTestAnnotatedInstances += unclusteredWsdStats.testAnnotatedInstances;
            totalBothAnnotatedInstances += unclusteredWsdStats.bothAnnotatedInstances;
            totalUnclusteredScore += unclusteredWsdStats.totalScore;

            WSDStats clusteredWsdStats = new WSDStats(
                    testAnnotatedInstances.get(pos),
                    bothAnnotatedInstances.get(pos),
                    goldAnnotatedInstances.get(pos), clusteredScore.get(pos));
            totalClusteredScore += clusteredWsdStats.totalScore;

            WSDStats randomClusteredWsdStats = new WSDStats(
                    testAnnotatedInstances.get(pos),
                    bothAnnotatedInstances.get(pos),
                    goldAnnotatedInstances.get(pos),
                    randomClusteredScore.get(pos));
            totalRandomClusteredScore += randomClusteredWsdStats.totalScore;

            try {
                putWSDStats(pos.toString(), unclusteredWsdStats, "no", false);
                putWSDStats(pos.toString(), clusteredWsdStats, "yes",
                        clusteredWsdStats.f1 > randomClusteredWsdStats.f1);
                putWSDStats(pos.toString(), randomClusteredWsdStats, "random",
                        false);
            }
            catch (IOException e) {
                throw new AnalysisEngineProcessException(e);
            }
        }

        // Print results for all POS combined
        WSDStats unclusteredWsdStats = new WSDStats(
                totalTestAnnotatedInstances, totalBothAnnotatedInstances,
                totalGoldAnnotatedInstances, totalUnclusteredScore);
        WSDStats clusteredWsdStats = new WSDStats(totalTestAnnotatedInstances,
                totalBothAnnotatedInstances, totalGoldAnnotatedInstances,
                totalClusteredScore);
        WSDStats randomClusteredWsdStats = new WSDStats(
                totalTestAnnotatedInstances, totalBothAnnotatedInstances,
                totalGoldAnnotatedInstances, totalRandomClusteredScore);
        try {
            putWSDStats("all", unclusteredWsdStats, "no", false);
            putWSDStats("all", clusteredWsdStats, "yes",
                    clusteredWsdStats.f1 > randomClusteredWsdStats.f1);
            putWSDStats("all", randomClusteredWsdStats, "random", false);
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
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
     * @param algorithm
     *            The name of the disambiguation method
     * @param pos
     *            The part of speech
     * @param wsdStats
     *            A @link{WSDStats} object containing the statistics
     * @param mark
     *            Whether to mark this entry
     * @throws IOException
     */
    protected void putWSDStats(String pos, WSDStats wsdStats,
            String clustering, boolean mark)
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
        tableCell(String.format("%7.1f", wsdStats.totalScore));

        // Precision
        tableCell(String.format("%1.5f", wsdStats.precision));

        // Recall
        tableCell(String.format("%1.5f", wsdStats.recall));

        // Coverage
        tableCell(String.format("%1.5f", wsdStats.coverage));

        // F1 score
        tableCell(String.format("%1.5f", wsdStats.f1));

        // clustering
        tableCell(String.format("%7s", clustering + (mark ? "*" : "")));

        endTableRow();
    }

}
