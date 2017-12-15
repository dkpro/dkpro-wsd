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

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.evaluation.AbstractWSDEvaluator;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;
import de.tudarmstadt.ukp.dkpro.wsd.wsi.type.WSITopic;

public class Semeval2013Task11Evaluator
    extends AbstractWSDEvaluator
{

    private final String queryDataset = "src/main/resources/datasets/2013test";

    private final String wsioutput = "myclustering.txt";

    private FileWriter outputFile;
    public static final String PROPERTIES_KEY_BACKOFF = "backoff";
    public static final String PROPERTIES_KEY_WITH_BACKOFF = "+backoff";
    public static final String PROPERTIES_KEY_WITHOUT_BACKOFF = "-backoff";

    public static final String PARAM_TEST_ALGORITHM = "testAlgorithm";
    @ConfigurationParameter(name = PARAM_TEST_ALGORITHM, mandatory = true, description = "The test algorithm to be evaluated")
    protected String testAlgorithm;

    // algorithms to try in succession
    public static final String PARAM_BACKOFF_ALGORITHM = "backoffAlgorithm";
    @ConfigurationParameter(name = PARAM_BACKOFF_ALGORITHM, mandatory = false, description = "The backoff algorithm to use when the test algorithm is unable to make a sense assignment")
    protected String backoffAlgorithm;

    public static final String PARAM_PROPERTIES_OUTPUT = "output";
    @ConfigurationParameter(name = PARAM_PROPERTIES_OUTPUT, mandatory = false, description = "An output location for the properties file for use with DKPro Lab")
    protected String output;

    private final Logger logger = Logger.getLogger(getClass());

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {

        super.initialize(context);
        try {
            outputFile = new FileWriter(wsioutput);
            outputFile.write("subTopicID\tresultID\n");
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        super.process(aJCas);

        // typeSystemInit(aJCas.getTypeSystem());
        final DocumentMetaData documentMetaData = DocumentMetaData.get(aJCas);
        final String docId = documentMetaData.getDocumentId();
        final String collectionId = documentMetaData.getCollectionId();
        try {
            final WSITopic wsiTopic = JCasUtil.selectSingle(aJCas,
                    WSITopic.class);
            final String term = wsiTopic.getSubjectOfDisambiguation()
                    .toLowerCase();
            String[] terms = term.split(" ");
            // outputFile.append();
            SortedMap<String, Integer> results = new TreeMap<String, Integer>();
            // Score each WSDItem
            for (WSDItem wsdItem : JCasUtil.select(aJCas, WSDItem.class)) {
                POS pos = wsdItem.getPos() == null ? null : POS.valueOf(wsdItem
                        .getPos());
                // WSDResult goldResult = null;
                WSDResult backoffResult = null;
                WSDResult testResult = null;
                List<WSDResult> wsdResults = getWSDResults(aJCas, wsdItem);

                // Make a first pass through the list of WSDResults for this
                // WSDItem to extract the gold, test, and backoff algorithm
                // results.
                for (WSDResult r : wsdResults) {
                    if (backoffAlgorithm != null
                            && r.getDisambiguationMethod().equals(
                                    backoffAlgorithm)) {
                        if (backoffResult != null) {
                            // There should be only one backoff algorithm
                            // annotation
                            logger.error("Multiple backoff algorithm annotations found for "
                                    + r.getWsdItem().getId());
                            throw new AnalysisEngineProcessException();
                        }
                        backoffResult = r;
                    }
                    else if (r.getDisambiguationMethod().equals(testAlgorithm)) {
                        if (testResult != null) {
                            // There should be only one test algorithm
                            // annotation
                            logger.error("Multiple test algorithm annotations found for "
                                    + r.getWsdItem().getId());
                            throw new AnalysisEngineProcessException();
                        }
                        testResult = r;
                        // testAnnotatedInstances.put(pos,
                        // Integer.valueOf(testAnnotatedInstances.get(pos) +
                        // 1));
                    }
                }

                // We have no result to compare against

                // We have a test result and a gold result
                if (testResult != null) {
                    Sense bestSense = testResult.getBestSense();
                    String id = bestSense.getId();
                    String subtopic = id.split("_")[1];
                    // replaceAll(" ", "_");
                    String id2 = wsiTopic.getId();
                    subtopic = id2.split("\\.")[0] + "." + subtopic;
                    if (!results.containsKey(subtopic)) {
                        results.put(subtopic, 1);
                    }
                    else {
                        results.put(subtopic, results.get(subtopic) + 1);
                    }

                    continue;
                }
                else

                // We have a gold result but no test result, so we use the
                // backoff
                if (backoffResult != null) {
                    Sense bestSense = backoffResult.getBestSense();
                    String id = bestSense.getId();
                    String subtopic = id.split("_")[1];
                    // replaceAll(" ", "_");
                    String id2 = wsiTopic.getId();
                    subtopic = id2.split("\\.")[0] + "." + subtopic;
                    if (!results.containsKey(subtopic)) {
                        results.put(subtopic, 1);
                    }
                    else {
                        results.put(subtopic, results.get(subtopic) + 1);
                    }

                    // outputFile.append(backoffResult.getBestSense().getId().replaceAll(" ",
                    // "_")
                    // + "\t" + documentMetaData.getDocumentId() + "\n");
                    continue;
                }

            }
            try {
                String id2 = wsiTopic.getId();
                String subtopic = id2.split("\\.")[0] + ".1";
                int max = 0;
                for (Entry<String, Integer> cand : results.entrySet()) {
                    if (cand.getValue() >= max) {
                        max = cand.getValue();
                        subtopic = cand.getKey();
                    }

                }
                outputFile.append(subtopic + "\t"
                        + documentMetaData.getDocumentId() + "\n");
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            System.err.println("query " + documentMetaData.getDocumentId()
                    + " has no WSITopic annotation");
        }
    }

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        String[] args = { queryDataset, wsioutput };
        try {
            outputFile.close();
            // call the WSI-Evaluator here
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

    }

}
