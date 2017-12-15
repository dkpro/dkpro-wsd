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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Produces a precision-recall graph of WSD results
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class PrecisionRecallGraph
    extends AbstractWSDEvaluator
{
    public static final String PROPERTIES_KEY_BACKOFF = "backoff";
    public static final String PROPERTIES_KEY_WITH_BACKOFF = "+backoff";
    public static final String PROPERTIES_KEY_WITHOUT_BACKOFF = "-backoff";

    public static final String PARAM_TEST_ALGORITHM = "testAlgorithm";
    @ConfigurationParameter(name = PARAM_TEST_ALGORITHM, mandatory = true, description = "The test algorithm to be evaluated")
    protected String testAlgorithm;

    // TODO: Change this parameter so that it accepts a list of backoff
    // algorithms to try in succession
    public static final String PARAM_BACKOFF_ALGORITHM = "backoffAlgorithm";
    @ConfigurationParameter(name = PARAM_BACKOFF_ALGORITHM, mandatory = false, description = "The backoff algorithm to use when the test algorithm is unable to make a sense assignment")
    protected String backoffAlgorithm;

    // TODO: It's a bit of a hack to require the user to pass the name of an
    // algorithm which annotates only monosemous senses. There must be some
    // better way of doing this…?
    public static final String PARAM_MONOSEMOUS_ALGORITHM = "monosemousAlgorithm";
    @ConfigurationParameter(name = PARAM_MONOSEMOUS_ALGORITHM, mandatory = false, description = "The monosemous baseline only algorithm", defaultValue = "de.tudarmstadt.ukp.dkpro.wsd.algorithms.MonosemousOnlyBaseline")
    protected String monosemousOnlyAlgorithm;

    public static final String PARAM_OUTPUT_FILE = "outputFilename";
    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = false, description = "The output file.  If unset, output goes to standard output.")
    protected String outputFilename;
    protected BufferedWriter output;

    public static final String PARAM_OPEN_IN_BROWSER = "openInBrowser";
    @ConfigurationParameter(name = PARAM_OPEN_IN_BROWSER, mandatory = false, description = "Whether to open the output file in the system Web browser", defaultValue = "false")
    protected boolean openInBrowser;

    private final Logger logger = Logger.getLogger(getClass());
    protected int totalGoldAnnotatedInstances = 0;

    class InstanceResult
        implements Comparable<InstanceResult>
    {
        String id;
        // TODO: Should confidenceTop be the *sum* of the top confidences?
        // Probably not, but need to think about it.
        double confidenceTop;
        double score;

        @Override
        public int compareTo(InstanceResult arg0)
        {
            if (confidenceTop == arg0.confidenceTop) {
                return id.compareTo(arg0.id);
            }
            if (confidenceTop < arg0.confidenceTop) {
                return 1;
            }
            return -1;
        }

        @SuppressWarnings("unused")
        private InstanceResult()
        {
        }

        public InstanceResult(WSDItem wsdItem, WSDResult testResult,
                WSDResult goldResult, boolean monosemous)
        {
            id = wsdItem.getId();
            if (monosemous) {
                confidenceTop = Double.MAX_VALUE;
                score = getMatchingScore(testResult, goldResult);
            }
            else {
                confidenceTop = testResult.getBestSense().getConfidence();
                score = getMatchingScore(testResult, goldResult);
            }
        }
    }

    private final Set<InstanceResult> testResults = new TreeSet<InstanceResult>();
    private final Set<InstanceResult> backoffResults = new TreeSet<InstanceResult>();
    private final Set<InstanceResult> testWithBackoffResults = new TreeSet<InstanceResult>();

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
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
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        super.process(aJCas);

        // Score each WSDItem
        for (WSDItem wsdItem : JCasUtil.select(aJCas, WSDItem.class)) {
            if (maxItemsAttempted >= 0 && numItemsAttempted++ >= maxItemsAttempted) {
                break;
            }
            WSDResult goldResult = null;
            WSDResult backoffResult = null;
            WSDResult testResult = null;
            List<WSDResult> wsdResults = getWSDResults(aJCas, wsdItem);
            boolean monosemous = false;

            // Make a first pass through the list of WSDResults for this
            // WSDItem to extract the gold, test, and backoff algorithm
            // results.
            for (WSDResult r : wsdResults) {
                if (r.getDisambiguationMethod().equals(monosemousOnlyAlgorithm)
                        && r.getSenses().size() > 0) {
                    monosemous = true;
                }
                else if (r.getDisambiguationMethod().equals(
                        goldStandardAlgorithm)) {
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
                        totalGoldAnnotatedInstances++;
                    }
                }
                else if (backoffAlgorithm != null
                        && r.getDisambiguationMethod().equals(backoffAlgorithm)) {
                    if (backoffResult != null) {
                        // There should be only one backoff algorithm annotation
                        logger.error("Multiple backoff algorithm annotations found for "
                                + r.getWsdItem().getId());
                        throw new AnalysisEngineProcessException();
                    }
                    backoffResult = r;
                }
                else if (r.getDisambiguationMethod().equals(testAlgorithm)) {
                    if (testResult != null) {
                        // There should be only one test algorithm annotation
                        logger.error("Multiple test algorithm annotations found for "
                                + r.getWsdItem().getId());
                        throw new AnalysisEngineProcessException();
                    }
                    testResult = r;
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

            InstanceResult i;

            // We have a test result and a gold result
            if (testResult != null) {
                i = new InstanceResult(wsdItem, testResult, goldResult,
                        monosemous);
                testResults.add(i);
                testWithBackoffResults.add(i);
            }

            // We have a test result and a backoff result
            if (backoffResult != null) {
                i = new InstanceResult(wsdItem, backoffResult, goldResult,
                        monosemous);
                backoffResults.add(i);
                if (testResult == null) {
                    testWithBackoffResults.add(i);
                }
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
    double getMatchingScore(WSDResult testResult, WSDResult goldResult)
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

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {

        int testGuesses = 0, backoffGuesses = 0, testWithBackoffGuesses = 0;
        double testScore = 0.0, backoffScore = 0.0, testWithBackoffScore = 0.0;
        try {
            output.write("# pCombined\trCombined\tpBackoff\trBackoff\tpTest\trTest");
            output.newLine();
            InstanceResult[] testArray = testResults
                    .toArray(new InstanceResult[0]);
            InstanceResult[] backoffArray = backoffResults
                    .toArray(new InstanceResult[0]);
            InstanceResult[] testWithBackoffArray = testWithBackoffResults
                    .toArray(new InstanceResult[0]);
            for (int i = 0; i < totalGoldAnnotatedInstances; i++) {
                if (i < testWithBackoffArray.length) {
                    testWithBackoffScore += testWithBackoffArray[i].score;
                    testWithBackoffGuesses++;
                    output.write(String.format("%f\t%f\t", testWithBackoffScore
                            / testWithBackoffGuesses, testWithBackoffScore
                            / totalGoldAnnotatedInstances));
                }
                else {
                    output.write("\t\t");
                }
                if (i < backoffArray.length) {
                    backoffScore += backoffArray[i].score;
                    backoffGuesses++;
                    output.write(String.format("%f\t%f\t", backoffScore
                            / backoffGuesses, backoffScore
                            / totalGoldAnnotatedInstances));
                }
                else {
                    output.write("\t\t");
                }
                if (i < testArray.length) {
                    testScore += testArray[i].score;
                    testGuesses++;
                    output.write(String.format("%f\t%f", testScore
                            / testGuesses, testScore
                            / totalGoldAnnotatedInstances));
                }
                else {
                    output.write("\t");
                }
                output.newLine();
            }
            output.close();
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

        if (openInBrowser == true) {
            try {
                java.awt.Desktop.getDesktop().browse(
                        java.net.URI.create("file://" + outputFilename));
            }
            catch (IOException e) {
                throw new AnalysisEngineProcessException(e);
            }
        }
    }

}
