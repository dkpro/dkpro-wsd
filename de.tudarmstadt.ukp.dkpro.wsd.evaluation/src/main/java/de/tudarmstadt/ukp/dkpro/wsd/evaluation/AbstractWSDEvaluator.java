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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * An abstract class for annotators which evaluate WSD results in some way
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class AbstractWSDEvaluator
    extends JCasAnnotator_ImplBase
{
    public static final String PARAM_MAXIMUM_ITEMS_TO_ATTEMPT = "maxItemsAttempted";
    @ConfigurationParameter(name = PARAM_MAXIMUM_ITEMS_TO_ATTEMPT, mandatory = false, description = "The maximum number of items to attempt to evaluate.  Set to a negative number to attempt all items.", defaultValue = "-1")
    protected int maxItemsAttempted;

    public static final String PARAM_GOLD_STANDARD_ALGORITHM = "goldStandardAlgorithm";
    @ConfigurationParameter(name = PARAM_GOLD_STANDARD_ALGORITHM, mandatory = true, description = "The gold standard algorithm")
    protected String goldStandardAlgorithm;

    public static final String PARAM_IGNORE_ANY_GOLD = "ignoreAnyGold";
    @ConfigurationParameter(name = PARAM_IGNORE_ANY_GOLD, mandatory = false, description = "Instances where any of the gold standard senses match this regular expression will be ignored")
    protected String ignoreAnyGold;
    Pattern ignoreAnyGoldPattern;

    public static final String PARAM_IGNORE_ALL_GOLD = "ignoreAllGold";
    @ConfigurationParameter(name = PARAM_IGNORE_ALL_GOLD, mandatory = false, description = "Instances where all of the gold standard senses match this regular expression will be ignored")
    protected String ignoreAllGold;
    Pattern ignoreAllGoldPattern;

    protected HashMap<WSDItem, List<WSDResult>> wsdItemIndex;
    protected int numItemsAttempted = 0;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        if (ignoreAllGold != null) {
            ignoreAllGoldPattern = Pattern.compile(ignoreAllGold);
        }
        if (ignoreAnyGold != null) {
            ignoreAnyGoldPattern = Pattern.compile(ignoreAnyGold);
        }
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        wsdItemIndex = null;
    }

    protected void createWSDItemIndex(JCas jcas)
    {
        if (wsdItemIndex != null) {
            return;
        }

        wsdItemIndex = new HashMap<WSDItem, List<WSDResult>>();

        // Put all wsdItems into the index
        for (WSDItem wsdItem : JCasUtil.select(jcas, WSDItem.class)) {
            wsdItemIndex.put(wsdItem, new ArrayList<WSDResult>());
        }

        // Put all wsdResults in the index
        for (WSDResult wsdResult : JCasUtil.select(jcas, WSDResult.class)) {
            wsdItemIndex.get(wsdResult.getWsdItem()).add(wsdResult);
        }
    }

    protected List<WSDResult> getWSDResults(JCas jcas, WSDItem wsdItem)
    {
        createWSDItemIndex(jcas);
        return wsdItemIndex.get(wsdItem);
    }

    /**
     * Returns the senses with the highest non-zero confidence score.
     *
     * @param wsdResult
     * @return
     */
    protected Set<Sense> getBestSenses(WSDResult wsdResult)
    {
        return getBestSenses(wsdResult.getSenses());
    }

    /**
     * Searches through the sense IDs of the given array of senses, and returns
     * true if any of them match the given pattern.
     *
     * @param senseArray
     * @return
     */
    protected boolean anySenseMatches(FSArray senseArray, Pattern pattern)
    {
        if (pattern == null || senseArray == null) {
            return false;
        }

        for (Sense sense : JCasUtil.select(senseArray, Sense.class)) {
            if (pattern.matcher(sense.getId()).matches()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Searches through the sense IDs of the given array of senses, and returns
     * true if all of them match the given pattern.
     *
     * @param senseArray
     * @return
     */
    protected boolean allSensesMatch(FSArray senseArray, Pattern pattern)
    {
        if (pattern == null || senseArray == null) {
            return false;
        }

        for (Sense sense : JCasUtil.select(senseArray, Sense.class)) {
            if (!pattern.matcher(sense.getId()).matches()) {
                return false;
            }
        }

        return true;
    }

    protected boolean ignoreResult(WSDResult r)
    {
        return allSensesMatch(r.getSenses(), ignoreAllGoldPattern)
                || anySenseMatches(r.getSenses(), ignoreAnyGoldPattern);
    }

    /**
     * Returns the senses with the highest non-zero confidence score.
     */
    protected Set<Sense> getBestSenses(FSArray senseArray)
    {
        Set<Sense> bestSenses = new HashSet<Sense>();
        if (senseArray == null) {
            return bestSenses;
        }
        double bestConfidence = Double.MIN_VALUE;
        for (Sense sense : JCasUtil.select(senseArray, Sense.class)) {
            if (sense.getConfidence() > bestConfidence && sense.getConfidence() > 0) {
                bestSenses.clear();
                bestSenses.add(sense);
                bestConfidence = sense.getConfidence();
            }
            else if (sense.getConfidence() == bestConfidence) {
                bestSenses.add(sense);
            }
        }
        return bestSenses;
    }
}
