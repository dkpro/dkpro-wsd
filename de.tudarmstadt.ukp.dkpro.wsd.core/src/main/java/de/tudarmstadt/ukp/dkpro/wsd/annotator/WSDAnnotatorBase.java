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

package de.tudarmstadt.ukp.dkpro.wsd.annotator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * An abstract class for annotators which call a WSD algorithm in order to
 * disambiguate one or more {@link WSDItem}s in a document.
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public abstract class WSDAnnotatorBase
    extends JCasAnnotator_ImplBase
{
    private Random generator = null;
    protected int numItemsAttempted = 0;

    public static enum TieStrategy
    {
        FAIL, SPLIT_SCORES, CHOOSE_RANDOMLY
    };

    public static final String PARAM_MAXIMUM_ITEMS_TO_ATTEMPT = "maxItemsAttempted";
    @ConfigurationParameter(name = PARAM_MAXIMUM_ITEMS_TO_ATTEMPT, mandatory = false, description = "The maximum number of items to attempt to disambiguate.  Set to a negative number to attempt all items.", defaultValue = "-1")
    protected int maxItemsAttempted;

    public static final String PARAM_DISAMBIGUATION_METHOD_NAME = "disambiguationMethodName";
    @ConfigurationParameter(name = PARAM_DISAMBIGUATION_METHOD_NAME, mandatory = false, description = "An identifier which will be attached to the disambiguation results produced by this annotator.  If blank, the algorithm's default identifier will be used.")
    protected String disambiguationMethodName;

    public static final String PARAM_NORMALIZE_CONFIDENCE = "normalizeConfidence";
    @ConfigurationParameter(name = PARAM_NORMALIZE_CONFIDENCE, mandatory = false, description = "Whether to normalize the disambiguation confidence values", defaultValue = "true")
    protected boolean normalizeConfidence;

    public static final String PARAM_BEST_ONLY = "bestOnly";
    @ConfigurationParameter(name = PARAM_BEST_ONLY, mandatory = false, description = "Whether to retain only those disambiguation results with the highest confidence", defaultValue = "true")
    protected boolean bestOnly;

    public static final String PARAM_TIE_STRATEGY = "tieStrategy";
    @ConfigurationParameter(name = PARAM_TIE_STRATEGY, mandatory = false, description = "What to do in the event that there is a tie for the highest-scoring disambiguation", defaultValue = "FAIL")
    protected TieStrategy tieStrategy;

    public static final String PARAM_ALLOW_EMPTY_WSD_RESULTS = "allowEmptyWsdResults";
    @ConfigurationParameter(name = PARAM_ALLOW_EMPTY_WSD_RESULTS, mandatory = false, description = "Should the algorithm generate a WSDResult if there are no entries", defaultValue = "false")
    protected boolean allowEmptyWsdResults;

    public static final String PARAM_SET_SENSE_DESCRIPTIONS = "setSenseDescriptions";
    @ConfigurationParameter(name = PARAM_SET_SENSE_DESCRIPTIONS, mandatory = false, description = "Whether to include sense descriptions in the Sense table of the WSDResult", defaultValue = "true")
    protected boolean setSenseDescriptions;

    protected SenseInventory inventory;
    private final Logger logger = Logger.getLogger(getClass());

    protected abstract String getDisambiguationMethod()
        throws SenseInventoryException;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        // TODO: Allow user to specify a seed via a configuration parameter
        super.initialize(context);
        generator = new Random();
    }

    /**
     * Given a {@link WSDItem} and a mapping of disambiguation results,
     * postprocess the results (by normalization, etc.), and then associate the
     * WSDItem with it and add it to the CAS.
     *
     * @param aJCas
     *            The CAS to modify
     * @param wsdItem
     *            The subject of disambiguation
     * @param disambiguationResult
     *            A mapping of sense IDs to confidence values
     * @throws AnalysisEngineProcessException
     */
    public void setWSDItem(JCas aJCas, WSDItem wsdItem,
            Map<String, Double> disambiguationResult)
        throws AnalysisEngineProcessException
    {

        try {
            if (disambiguationResult == null || disambiguationResult.isEmpty()) {
                if (allowEmptyWsdResults) {
                    disambiguationResult = new HashMap<String, Double>();
                }
                else {
                    logger.info(getDisambiguationMethod()
                            + " failed to disambiguate item " + wsdItem.getId()
                            + ": no sense assignment attempted");
                    return;
                }
            }

            WSDResult wsdResult = new WSDResult(aJCas);
            wsdResult.setSenseInventory(inventory.getSenseInventoryName());
            wsdResult.setDisambiguationMethod(getDisambiguationMethod());
            wsdResult.setWsdItem(wsdItem);

            if (bestOnly == true) {
                discardAllButHighestConfidence(disambiguationResult);
                if (tieStrategy == TieStrategy.FAIL
                        && disambiguationResult.size() != 1) {
                    logger.info(getDisambiguationMethod()
                            + " failed to disambiguate item " + wsdItem.getId()
                            + ": " + disambiguationResult.size()
                            + "-way tie between senses");
                    return;
                }
                else if (tieStrategy == TieStrategy.CHOOSE_RANDOMLY
                        && disambiguationResult.size() != 1) {
                    logger.info(getDisambiguationMethod()
                            + " disambiguating item " + wsdItem.getId()
                            + " by randomly choosing from a "
                            + disambiguationResult.size()
                            + "-way tie between senses");
                    saveOneRandomly(disambiguationResult);
                }
            }

            if (normalizeConfidence == true) {
                normalize(disambiguationResult);
            }

            int resultSize = disambiguationResult.size();
            FSArray senseArray = new FSArray(aJCas, resultSize);
            int i = 0;
            for (Map.Entry<String, Double> entry : disambiguationResult
                    .entrySet()) {
                logger.info(getDisambiguationMethod() + " disambiguated item "
                        + wsdItem.getId() + " to "
                        + inventory.getSenseInventoryName() + "/"
                        + entry.getKey() + " with confidence "
                        + entry.getValue());
                Sense sense = new Sense(aJCas);
                sense.setId(entry.getKey());
                sense.setConfidence(entry.getValue());
                if (setSenseDescriptions == true) {
                    sense.setDescription(inventory.getSenseDescription(entry
                            .getKey()));
                }
                // TODO: Is it really necessary to add senses to the indexes?
                sense.addToIndexes();

                senseArray.set(i, sense);

                i++;
            }

            wsdResult.setSenses(senseArray);
            wsdResult.addToIndexes();
        }
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    // private Map<String, Double> discardTies(
    // Map<String, Double> disambiguationResult)
    // {
    // Collection<Double> valuesCollection = disambiguationResult.values();
    // Set<Double> valuesSet = new HashSet<Double>(valuesCollection);
    // if (valuesCollection.size() == valuesSet.size()) {
    // return disambiguationResult;
    // }
    //
    // Map<String, Double> noTies = new TreeMap<String, Double>();
    // for (String sense : disambiguationResult.keySet()) {
    // Double confidence = disambiguationResult.get(sense);
    // int count = 0;
    // for (String otherSense : disambiguationResult.keySet()) {
    // if (disambiguationResult.get(otherSense).equals(confidence)) {
    // count++;
    // }
    // }
    // if (count == 1) {
    // noTies.put(sense, confidence);
    // }
    // }
    // return noTies;
    // }

    private void saveOneRandomly(Map<String, Double> disambiguationResult)
    {
        List<String> keys = new ArrayList<String>(disambiguationResult.keySet());
        String randomKey = keys.get(generator.nextInt(keys.size()));
        disambiguationResult.clear();
        disambiguationResult.put(randomKey, Double.valueOf(1.0));
    }

    private void normalize(Map<String, Double> disambiguationResult)
    {
        double weight = 0.0;
        for (Double confidence : disambiguationResult.values()) {
            weight += confidence;
        }
        if (weight == 0.0) {
            for (String sense : disambiguationResult.keySet()) {
                logger.trace("Normalizing " + sense + " from "
                        + disambiguationResult.get(sense) + " to 1.0");
                disambiguationResult.put(sense,
                        Double.valueOf(1.0 / disambiguationResult.size()));
            }
        }
        else if (weight != 1.0) {
            for (String sense : disambiguationResult.keySet()) {
                Double newConfidence = Double.valueOf(disambiguationResult
                        .get(sense) / weight);
                logger.trace("Normalizing " + sense + " from "
                        + disambiguationResult.get(sense) + " to "
                        + newConfidence);
                disambiguationResult.put(sense, newConfidence);
            }
        }
    }

    protected void discardAllButHighestConfidence(
            Map<String, Double> disambiguationResult)
    {
        if (disambiguationResult.size() == 1) {
            return;
        }

        Double highestConfidence = Double.valueOf(0.0);

        // Find the highest confidence value
        for (Double confidence : disambiguationResult.values()) {
            if (confidence > highestConfidence) {
                highestConfidence = confidence;
            }
        }

        // Remove entries which do not have the highest confidence value
        for (String sense : new HashSet<String>(disambiguationResult.keySet())) {
            if (disambiguationResult.get(sense) < highestConfidence) {
                logger.trace("Discarding " + sense);
                disambiguationResult.remove(sense);
            }
            else {
                logger.trace("Retaining " + sense);
            }
        }
    }
}
