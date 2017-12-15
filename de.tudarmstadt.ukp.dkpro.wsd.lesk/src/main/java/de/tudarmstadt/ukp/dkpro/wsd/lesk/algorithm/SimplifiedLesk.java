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

package de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmContextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmContextPOS;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NormalizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.OverlapStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.TokenizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * Disambiguates words using the simplified Lesk algorithm -- i.e., the
 * definitions of the subject of disambiguation are compared against its context
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class SimplifiedLesk
    extends Lesk
    implements WSDAlgorithmContextBasic, WSDAlgorithmContextPOS
{

    private final Logger logger = Logger.getLogger(getClass());

    protected boolean prefilterSod = false;
    protected boolean postfilterSod = false;

    public SimplifiedLesk(SenseInventory inventory,
            OverlapStrategy overlapStrategy,
            NormalizationStrategy normalizationStrategy,
            TokenizationStrategy senseTokenizationStrategy,
            TokenizationStrategy contextTokenizationStrategy)
    {
        super(inventory, overlapStrategy, normalizationStrategy,
                senseTokenizationStrategy, contextTokenizationStrategy);
    }

    /**
     * Determines whether to postfilter the subject of disambiguation from the
     * tokenized contexts and sense descriptions
     *
     * @param postfilterSod
     *            If true, filter the subject of disambiguation from the
     *            tokenized contexts and sense descriptions
     */
    public void setPostfilterSod(boolean postfilterSod)
    {
        this.postfilterSod = postfilterSod;
    }

    /**
     * Determines whether to prefilter the subject of disambiguation from the
     * untokenized contexts and sense descriptions
     *
     * @param prefilterSod
     *            If true, filter the subject of disambiguation from the
     *            untokenized contexts and sense descriptions
     */
    public void setPrefilterSod(boolean prefilterSod)
    {
        this.prefilterSod = prefilterSod;
    }

    /**
     * Takes a subject of disambiguation <b>sod</b> and its part of speech, plus
     * a string of context words <b>context</b>. Each sense description of
     * <b>sod</b> is compared pairwise with <b>context</b>, and a mapping of
     * sense IDs to overlap scores is returned. By definition a monosemous
     * subject of disambiguation gets an overlap score of 1.
     *
     * @param sod
     *            the subject of disambiguation
     * @param context
     *            a context to tokenize and compare against
     * @return a (possibly empty) sense map containing the senses of the subject
     *         of disambiguation and their nonzero overlap scores with the
     *         context, or null if the sense inventory contains no senses for
     *         the subject of disambiguation
     */
    @Override
    public Map<String, Double> getDisambiguation(String sod, String context)
        throws SenseInventoryException
    {
        return getDisambiguation(sod, null, context);
    }

    /**
     * Takes a subject of disambiguation <b>sod</b> and its part of speech, plus
     * a string of context words <b>context</b>. Each sense description of
     * <b>sod</b> is compared pairwise with <b>context</b>, and a mapping of
     * sense IDs to overlap scores is returned. By definition a monosemous
     * subject of disambiguation gets an overlap score of 1.
     *
     * @param sod
     *            the subject of disambiguation
     * @param sodPos
     *            the subject of disambiguation's part of speech
     * @param context
     *            a context to tokenize and compare against
     * @return a (possibly empty) sense map containing the senses of the subject
     *         of disambiguation and their nonzero overlap scores with the
     *         context, or null if the sense inventory contains no senses for
     *         the subject of disambiguation
     */
    @Override
    public Map<String, Double> getDisambiguation(String sod, POS sodPos,
            String context)
        throws SenseInventoryException
    {
        String tokenPostfilter = postfilterSod ? sod : null;
        String tokenPrefilter = prefilterSod ? sod : null;

        // Get the sense IDs for the subject of disambiguation
        List<String> sodSenses = getSenses(sod, sodPos);

        // If the subject of disambiguation was not found in the sense
        // inventory, Lesk cannot disambiguate it
        if (sodSenses.isEmpty()) {
            return null;
        }

        // If the subject of disambiguation has only one sense, we can avoid
        // computing its similarity to the contexts
        if (sodSenses.size() == 1) {
            return getDisambiguationMap(new String[] { sodSenses.get(0) },
                    new double[] { 1.0 });
        }

        // Get the tokenized context and sense descriptions
        Map<String, List<String>> tokenizedSenseDescriptions = getTokenizedSenseDescriptions(
                sodSenses, tokenPrefilter, tokenPostfilter);
        List<String> tokenizedContext = getTokenizedContext(context,
                tokenPrefilter, tokenPostfilter);
        if (logger.isDebugEnabled()) {
            logger.debug("Comparing " + tokenizedSenseDescriptions.size()
                    + " senses of " + sod + "/" + sodPos + " against context");
        }

        // Use the Lesk algorithm to find the index of the best sense
        return simplifiedLesk(tokenizedSenseDescriptions, tokenizedContext);
    }

    /**
     * This function takes a mapping of sense IDs to tokenized sense
     * descriptions and a tokenized context. It computes the "overlap" between
     * the context and each sense description, and returns a mapping of sense
     * IDs to nonzero overlap scores.
     *
     * @param tokenizedCandidates
     *            a mapping of sense IDs to tokenized sense descriptions
     * @param tokenizedContext
     *            a list of tokens constituting the context
     * @return a mapping of sense IDs to nonzero overlap scores
     */
    protected Map<String, Double> simplifiedLesk(
            Map<String, List<String>> tokenizedCandidates,
            List<String> tokenizedContext)
    {
        Map<String, Double> disambiguationMap = new TreeMap<String, Double>();

        // Avoid expensive computation for monosemous terms
        if (tokenizedCandidates.size() == 1) {
            String sense = tokenizedCandidates.keySet().iterator().next();
            disambiguationMap.put(sense, 1.0);
            logger.debug("Sense " + sense + " has overlap 1.0 / 1.0 = 1.0");
            return disambiguationMap;
        }

        // Compute overlap for each of the disambiguation candidates
        for (String sense : tokenizedCandidates.keySet()) {
            double overlap = overlapStrategy.overlap(
                    tokenizedCandidates.get(sense), tokenizedContext);
            if (overlap > 0.0) {
                double normalization = normalizationStrategy.normalizer(
                        tokenizedCandidates.get(sense), tokenizedContext);
                double normalizedOverlap = overlap / normalization;
                logger.debug("Sense " + sense + " has overlap " + overlap
                        + " / " + normalization + " = " + normalizedOverlap);
                disambiguationMap.put(sense, Double.valueOf(normalizedOverlap));
            }
            else {
                logger.debug("Sense " + sense + " has overlap 0.0 / 1.0 = 0.0");
            }
        }

        return disambiguationMap;
    }

}
