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

package de.tudarmstadt.ukp.dkpro.wsd.candidates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorBase.TieStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Collapses the confidence scores of senses in {@link WSDResult}s according to
 * the provided clustering of senses
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan
 *         Miller</a>
 */
public class SenseConfidenceClusterer
    extends JCasAnnotator_ImplBase
{

    // TODO: Merge common parts with WSDAnnotatorBase

    private final Logger logger = Logger.getLogger(getClass());
    private Random generator = null;

    public static final String PARAM_NORMALIZE_CONFIDENCE = "normalizeConfidence";
    @ConfigurationParameter(name = PARAM_NORMALIZE_CONFIDENCE, mandatory = false, description = "Whether to normalize the disambiguation confidence values", defaultValue = "true")
    protected boolean normalizeConfidence;

    public static final String PARAM_TIE_STRATEGY = "tieStrategy";
    @ConfigurationParameter(name = PARAM_TIE_STRATEGY, mandatory = false, description = "What to do in the event that there is a tie for the highest-scoring disambiguation", defaultValue = "FAIL")
    protected TieStrategy tieStrategy;

    public static final String PARAM_BEST_ONLY = "bestOnly";
    @ConfigurationParameter(name = PARAM_BEST_ONLY, mandatory = false, description = "Whether to retain only those disambiguation results with the highest confidence", defaultValue = "true")
    protected boolean bestOnly;

    public static final String PARAM_DISAMBIGUATION_ALGORITHM = "disambiguationAlgorithm";
    @ConfigurationParameter(name = PARAM_DISAMBIGUATION_ALGORITHM, mandatory = false, description = "The disambiguation algorithm whose results to cluster, or null to cluster results from all algorithms")
    protected String disambiguationAlgorithm;

    public static final String PARAM_CLUSTER_URL = "clusterUrl";
    @ConfigurationParameter(name = PARAM_CLUSTER_URL, mandatory = true, description = "The URL of a delimited text file containing sense clusters")
    protected String clusterUrl;

    public static final String PARAM_DELIMITER_REGEX = "delimiterRegex";
    @ConfigurationParameter(name = PARAM_DELIMITER_REGEX, mandatory = false, description = "A regular expression matching the delimiter between clusters", defaultValue = "[\\t ]+")
    protected String delimiterRegex;

    protected SenseClusterer senseClusterer;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        generator = new Random();

        try {
            initializeSenseClusterer();
        }
        catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
    }

    protected void initializeSenseClusterer()
        throws IOException
    {
        senseClusterer = new SenseClusterer(clusterUrl, delimiterRegex);
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        Set<WSDResult> wsdResultsToRemove = new HashSet<WSDResult>();
        for (WSDResult r : JCasUtil.select(aJCas, WSDResult.class)) {
            if (disambiguationAlgorithm != null
                    && !disambiguationAlgorithm.equals(r
                            .getDisambiguationMethod())) {
                continue;
            }
            clusterSenses(r, aJCas);
            if (r.getSenses() == null) {
                wsdResultsToRemove.add(r);
            }
        }

        for (WSDResult r : wsdResultsToRemove) {
            r.removeFromIndexes(aJCas);
        }
    }

    // TODO: Instead of destroying the old sense array, create a new
    // WSDResult for the clustered senses.
    /**
     * Merges senses belonging to the same cluster. Confidence values are
     * summed.
     *
     * @param senseArray
     * @param aJCas
     */
    void clusterSenses(WSDResult wsdResult, JCas aJCas)
    {
        FSArray senseArray = wsdResult.getSenses();
        Map<Set<String>, Double> clusterDisambiguationMap = new HashMap<Set<String>, Double>();
        for (int i = 0; i < senseArray.size(); i++) {
            Sense sense = (Sense) senseArray.get(i);
            Set<String> cluster = senseClusterer.getCluster(sense.getId());
            Double clusterScore = clusterDisambiguationMap.get(cluster);
            logger.debug(sense.getId() + " has cluster " + cluster);
            if (clusterScore == null) {
                clusterScore = Double.valueOf(sense.getConfidence());
            }
            else {
                clusterScore = Double.valueOf(sense.getConfidence()
                        + clusterScore);
            }
            clusterDisambiguationMap.put(cluster, clusterScore);
            sense.removeFromIndexes(aJCas);
        }
        senseArray.removeFromIndexes(aJCas);

        logger.info(wsdResult.getDisambiguationMethod() + " "
                + wsdResult.getWsdItem().getId() + ": collapsed "
                + senseArray.size() + " senses to "
                + clusterDisambiguationMap.size() + " clusters");

        FSArray newSenseArray;

        if (bestOnly == true) {
            discardAllButHighestConfidence(clusterDisambiguationMap);
            if (tieStrategy == TieStrategy.FAIL
                    && clusterDisambiguationMap.size() != 1) {
                wsdResult.setSenses(null);
                logger.info(wsdResult.getDisambiguationMethod()
                        + " failed to disambiguate item "
                        + wsdResult.getWsdItem().getId() + ": "
                        + clusterDisambiguationMap.size()
                        + "-way tie between senses");
                return;
            }
            else if (tieStrategy == TieStrategy.CHOOSE_RANDOMLY
                    && clusterDisambiguationMap.size() != 1) {
                logger.info(wsdResult.getDisambiguationMethod()
                        + " disambiguating item "
                        + wsdResult.getWsdItem().getId()
                        + " by randomly choosing from a "
                        + clusterDisambiguationMap.size()
                        + "-way tie between senses");
                saveOneRandomly(clusterDisambiguationMap);
            }
        }
        if (normalizeConfidence == true) {
            normalize(clusterDisambiguationMap);
        }

        // Add all sense clusters to the new array
        newSenseArray = new FSArray(aJCas, clusterDisambiguationMap.size());
        int i = 0;
        for (Set<String> cluster : clusterDisambiguationMap.keySet()) {
            Sense sense = new Sense(aJCas);
            sense.setId(cluster.toString());
            sense.setConfidence(clusterDisambiguationMap.get(cluster));
            // TODO: Is it really necessary to add senses to the index?
            sense.addToIndexes(aJCas);
            newSenseArray.set(i++, sense);
        }

        // TODO: Is it really necessary to add sense arrays to the index?
        newSenseArray.addToIndexes(aJCas);
        wsdResult.setSenses(newSenseArray);
        logger.info(wsdResult.getDisambiguationMethod() + " "
                + wsdResult.getWsdItem().getId() + ": retained "
                + newSenseArray.size() + " senses");
    }

    private void saveOneRandomly(
            Map<Set<String>, Double> clusterDisambiguationMap)
    {
        List<Set<String>> keys = new ArrayList<Set<String>>(
                clusterDisambiguationMap.keySet());
        Set<String> randomKey = keys.get(generator.nextInt(keys.size()));
        clusterDisambiguationMap.clear();
        clusterDisambiguationMap.put(randomKey, Double.valueOf(1.0));
    }

    protected void discardAllButHighestConfidence(
            Map<Set<String>, Double> clusterDisambiguationMap)
    {
        if (clusterDisambiguationMap.size() == 1) {
            return;
        }

        Double highestConfidence = Double.valueOf(0.0);

        // Find the highest confidence value
        for (Double confidence : clusterDisambiguationMap.values()) {
            if (confidence > highestConfidence) {
                highestConfidence = confidence;
            }
        }

        // Remove entries which do not have the highest confidence value
        for (Set<String> sense : new HashSet<Set<String>>(
                clusterDisambiguationMap.keySet())) {
            if (clusterDisambiguationMap.get(sense) < highestConfidence) {
                logger.trace("Discarding " + sense);
                clusterDisambiguationMap.remove(sense);
            }
            else {
                logger.trace("Retaining " + sense);
            }
        }
    }

    private void normalize(Map<Set<String>, Double> clusterDisambiguationMap)
    {
        double weight = 0.0;
        for (Double confidence : clusterDisambiguationMap.values()) {
            weight += confidence;
        }
        if (weight == 0.0) {
            for (Set<String> sense : clusterDisambiguationMap.keySet()) {
                logger.trace("Normalizing " + sense + " from "
                        + clusterDisambiguationMap.get(sense) + " to 1.0");
                clusterDisambiguationMap.put(sense,
                        Double.valueOf(1.0 / clusterDisambiguationMap.size()));
            }
        }
        else if (weight != 1.0) {
            for (Set<String> sense : clusterDisambiguationMap.keySet()) {
                Double newConfidence = Double.valueOf(clusterDisambiguationMap
                        .get(sense) / weight);
                logger.trace("Normalizing " + sense + " from "
                        + clusterDisambiguationMap.get(sense) + " to "
                        + newConfidence);
                clusterDisambiguationMap.put(sense, newConfidence);
            }
        }
    }

}
