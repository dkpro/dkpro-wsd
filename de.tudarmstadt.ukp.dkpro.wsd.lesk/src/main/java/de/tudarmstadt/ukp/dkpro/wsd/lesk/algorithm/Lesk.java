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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.WSDUtils;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.AbstractWSDAlgorithm;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NormalizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.OverlapStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.TokenizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * Base class for algorithms which disambiguate words using Lesk-like algorithms
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public abstract class Lesk
    extends AbstractWSDAlgorithm
{

    private final static Logger logger = Logger.getLogger(Lesk.class.getName());

    protected static final int CONTEXT_TRUNCATION_LENGTH = 80;

    protected OverlapStrategy overlapStrategy;
    protected NormalizationStrategy normalizationStrategy;
    protected TokenizationStrategy senseTokenizationStrategy;
    protected TokenizationStrategy contextTokenizationStrategy;
    protected Map<String, List<String>> tokenizedSenseDescriptionCache;
    protected Map<String, List<String>> tokenizedContextCache;

    public Lesk(SenseInventory inventory, OverlapStrategy overlapStrategy,
            NormalizationStrategy normalizationStrategy,
            TokenizationStrategy senseTokenizationStrategy,
            TokenizationStrategy contextTokenizationStrategy)
    {
        super(inventory);
        this.overlapStrategy = overlapStrategy;
        this.normalizationStrategy = normalizationStrategy;
        this.senseTokenizationStrategy = senseTokenizationStrategy;
        this.contextTokenizationStrategy = contextTokenizationStrategy;
        tokenizedSenseDescriptionCache = new HashMap<String, List<String>>();
        tokenizedContextCache = new HashMap<String, List<String>>();
    }

    @Override
    public String getDisambiguationMethod()
    {
        return this.getClass().getName();
    }

    /**
     * Flush the cache of tokenized sense descriptions and contexts
     */
    public void flushCache()
    {
        tokenizedSenseDescriptionCache.clear();
        tokenizedContextCache.clear();
    }

    /**
     * Takes a list of sense IDs and returns a mapping to their sense
     * descriptions, represented by a list of tokens
     *
     * @param senses
     *            a list of sense IDs
     * @param tokenPrefilter
     *            all occurrences of this String, if not null, will be removed
     *            from the descriptions before tokenization
     * @param tokenPostfilter
     *            all occurrences of this String, if not null, will be removed
     *            from the descriptions after tokenization
     * @return a mapping of sense IDs to lists of tokens
     */
    protected Map<String, List<String>> getTokenizedSenseDescriptions(
            List<String> senses, String tokenPrefilter, String tokenPostfilter)
        throws SenseInventoryException
    {
        Map<String, List<String>> tokenizedSenseDescriptions = new LinkedHashMap<String, List<String>>(
                senses.size());
        for (String sense : senses) {
            List<String> senseDescription;

            // If we prefilter, we can't cache the sense description
            if (tokenPrefilter != null) {
                String untokenizedSenseDescription = getSenseDescription(sense);
                senseDescription = senseTokenizationStrategy.tokenize(filter(
                        untokenizedSenseDescription, tokenPrefilter));
                logger.trace("Untokenized sense description for " + sense
                        + ": " + untokenizedSenseDescription);
                logger.trace("Tokenized sense description for " + sense
                        + "  : " + senseDescription);
            }
            else {
                senseDescription = tokenizedSenseDescriptionCache.get(sense);
                if (senseDescription == null) {
                    String untokenizedSenseDescription = getSenseDescription(sense);
                    senseDescription = senseTokenizationStrategy
                            .tokenize(untokenizedSenseDescription);
                    tokenizedSenseDescriptionCache.put(sense, senseDescription);
                    logger.trace("Untokenized sense description for " + sense
                            + ": " + untokenizedSenseDescription);
                    logger.trace("Tokenized sense description for " + sense
                            + "  : " + senseDescription);
                    logger.trace("Caching tokenized sense description for "
                            + sense + "; cache size = "
                            + tokenizedSenseDescriptionCache.size());
                }
            }
            tokenizedSenseDescriptions.put(sense,
                    filter(senseDescription, tokenPostfilter));
        }
        return tokenizedSenseDescriptions;
    }

    /**
     * Returns the sense description.
     *
     * @param sense
     * @return
     * @throws SenseInventoryException
     */
    protected String getSenseDescription(String sense)
        throws SenseInventoryException
    {
        return inventory.getSenseDescription(sense);
    }

    /**
     * Filters all occurrences of <b>word</b> from <b>string</b>
     *
     * @param string
     *            The input string
     * @param word
     *            The word to filter from the input string
     * @return The filtered string
     */
    protected String filter(String string, String word)
    {
        if (word == null || word.isEmpty()) {
            return string;
        }
        else {
            return string.replaceAll("\\b(?i)" + Pattern.quote(word) + "\\b",
                    "");
        }
    }

    /**
     * Takes a context string and returns its tokenized representation
     *
     * @param untokenizedContext
     *            a context string
     * @param tokenPrefilter
     *            all occurrences of this String, if not null, will be removed
     *            from the context before tokenization
     * @param tokenPostfilter
     *            all occurrences of this String, if not null, will be removed
     *            from the context after tokenization
     * @return a list of objects representing the tokenized context
     */
    public List<String> getTokenizedContext(String untokenizedContext,
            String tokenPrefilter, String tokenPostfilter)
    {
        List<String> tokenizedContext;

        // If we prefilter, we can't cache the context
        if (tokenPrefilter != null) {
            tokenizedContext = contextTokenizationStrategy.tokenize(filter(
                    untokenizedContext, tokenPrefilter));
            logger.trace("Untokenized context: " + untokenizedContext);
            logger.trace("Tokenized context  : " + tokenizedContext.toString());
        }
        else {
            tokenizedContext = tokenizedContextCache.get(untokenizedContext);

            if (tokenizedContext == null) {
                tokenizedContext = contextTokenizationStrategy
                        .tokenize(untokenizedContext);
                tokenizedContextCache.put(untokenizedContext, tokenizedContext);
                if (logger.isDebugEnabled()) {
                    logger.trace("Untokenized context: " + untokenizedContext);
                    logger.trace("Tokenized context  : "
                            + tokenizedContext.toString());
                    logger.trace("Caching tokenized context for "
                            + WSDUtils.truncate(untokenizedContext,
                                    CONTEXT_TRUNCATION_LENGTH)
                            + "; cache size = " + tokenizedContextCache.size());
                }
            }
        }

        return filter(tokenizedContext, tokenPostfilter);
    }

    /**
     * Returns a copy of a list filtered to remove the given elements.
     *
     * @param list
     *            The list to filter
     * @param filter
     *            The element which should not appear in the filtered list
     * @return The filtered list
     */
    protected <T> List<T> filter(List<T> list, T filter)
    {
        if (filter == null || list == null || list.isEmpty()) {
            return list;
        }
        List<T> filteredContext = new ArrayList<T>(list.size());
        for (T token : list) {
            if (token.equals(filter) == false) {
                filteredContext.add(token);
            }
        }
        return filteredContext;
    }

    /**
     * Returns the senses for a given subject of disambiguation, passing the
     * part of speech if given
     *
     * @param sod
     *            the subject of disambiguation
     * @param pos
     *            the subject of disambiguation's part of speech, or null if not
     *            applicable
     * @return the output of the underlying sense inventory's getSenses()
     */
    protected List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException
    {
        if (pos == null) {
            return inventory.getSenses(sod);
        }
        else {
            return inventory.getSenses(sod, pos);
        }
    }

    /**
     * Takes arrays of arrays of objects and returns a frequency (co-occurrence)
     * matrix
     *
     * @param arraysOfObjects
     *            an array of array of objects
     * @return a map of unique objects to arrays of frequency
     */
    public static Map<Object, Integer[]> frequencyMatrix(
            Object[][] arraysOfObjects)
    {
        Map<Object, Integer[]> matrix = new HashMap<Object, Integer[]>();
        for (int i = 0; i < arraysOfObjects.length; i++) {
            for (int j = 0; j < arraysOfObjects[i].length; j++) {
                Integer[] frequency = matrix.get(arraysOfObjects[i][j]);
                if (frequency == null) {
                    frequency = new Integer[arraysOfObjects.length];
                    Arrays.fill(frequency, Integer.valueOf(0));
                }
                frequency[i]++;
                matrix.put(arraysOfObjects[i][j], frequency);
            }
        }
        return matrix;
    }

    /**
     * Takes a list of strings and returns the magnitude of its frequency vector
     *
     * @param o
     *            the array of objects
     * @return the magnitude of o's frequency vector
     */
    public static double magnitude(List<String> o)
    {
        Map<Object, Integer[]> vector = frequencyMatrix(new Object[][] { o
                .toArray() });
        int total = 0;
        for (Integer[] weights : vector.values()) {
            total += weights[0] * weights[0];
        }
        return Math.sqrt(total);
    }

    // TODO: The following methods are left over from when this class used
    // arrays instead of collections. Some or all of them still need to be
    // converted
    // for use with the OriginalLesk class.

    // /**
    // * Takes a list of senses a list of their sense descriptions
    // *
    // * @param senses
    // * a list of senses
    // * @return a list of sense descriptions
    // */
    // protected List<String> getSenseDescriptions(List<String> senses)
    // throws SenseInventoryException
    // {
    // List<String> senseDescriptions = new ArrayList<String>(senses.size());
    // for (String sense : senses) {
    // senseDescriptions.add(inventory.getSenseDescription(sense));
    // }
    // return senseDescriptions;
    // }
    //
    // /**
    // * Compares each string in <b>untokenizedCandidates</b> to all strings in
    // * each array specified by <b>untokenizedContexts</b>, and returns the
    // index
    // * of the string of of <b>untokenizedCandidates</b> which best matches.
    // All
    // * strings are tokenized before comparison. It is assumed that the
    // * comparisons are independent.
    // *
    // * @param untokenizedCandidates
    // * an array of candidate arrays to select from
    // * @param untokenizedContexts
    // * an array of context arrays to compare against
    // * @return the index of the element of <b>candidates</b> which best
    // matches,
    // * or -1 if no match was found
    // */
    // public int lesk(List<String> untokenizedCandidates,
    // List<List<String>> untokenizedContextsList)
    // {
    // // Convert the strings into arrays of objects
    // List<List<String>> tokenizedCandidates = new
    // ArrayList<List<String>>(untokenizedCandidates.size());
    // for (String untokenizedCandidate : untokenizedCandidates) {
    // tokenizedCandidates.add(tokenizationStrategy
    // .tokenize(untokenizedCandidate));
    // }
    //
    // List<List<List<String>>> tokenizedContextsList = new
    // ArrayList<List<List<String>>>(untokenizedContextsList.size());
    // for (List<String> untokenizedContexts : untokenizedContextsList) {
    // List<List<String>> tokenizedContexts = new
    // ArrayList<List<String>>(untokenizedContexts.size());
    // for (String untokenizedContext : untokenizedContexts) {
    // tokenizedContexts.add(getTokenizedContext(untokenizedContext));
    // }
    // }
    //
    // return lesk(tokenizedCandidates, tokenizedContexts);
    // }
    //
    // /**
    // * Compares each string in <b>tokenizedCandidates</b> to all strings in
    // each
    // * array specified by <b>untokenizedContexts</b>, and returns the index of
    // * the string of of <b>tokenizedCandidates</b> which best matches. All
    // * strings in <b>untokenizedCandidates</b> are tokenized before
    // comparison.
    // * It is assumed that the comparisons are independent.
    // *
    // * @param tokenizedCandidates
    // * an array of tokenized candidate arrays to select from
    // * @param untokenizedContexts
    // * an array of context arrays to compare against
    // * @return the index of the element of <b>candidates</b> which best
    // matches,
    // * or -1 if no match was found
    // */
    // public int lesk(Object[][] tokenizedCandidates,
    // String[][] untokenizedContexts)
    // {
    // Object[][][] tokenizedContexts = new
    // Object[untokenizedContexts.length][][];
    // for (int i = 0; i < untokenizedContexts.length; i++) {
    // tokenizedContexts[i] = new Object[untokenizedContexts[i].length][];
    // for (int j = 0; j < untokenizedContexts[i].length; j++) {
    // tokenizedContexts[i][j] = getTokenizedContext(untokenizedContexts[i][j]);
    // }
    // }
    //
    // return lesk(tokenizedCandidates, tokenizedContexts);
    // }
    //
    // /**
    // * Compares each array in <b>tokenizedCandidates</b> to all arrays in each
    // * array specified by <b>tokenizedContexts</b>, and returns the index of
    // the
    // * array of <b>tokenizedCandidates</b> which best matches. It is assumed
    // * that the comparisons are independent.
    // *
    // * @param tokenizedCandidates
    // * an array of candidate arrays to select from
    // * @param tokenizedContexts
    // * an array of context arrays to compare against
    // * @return the index of the element of <b>candidates</b> which best
    // matches,
    // * or -1 if no match was found
    // */
    // public int lesk(List<List<String>> tokenizedCandidates,
    // List<List<List<String>>> tokenizedContexts)
    // {
    // double bestOverlap = 0.0;
    // int bestSodIndex = -1;
    //
    // logger.debug("Comparing " + tokenizedCandidates.length
    // + " candidate(s) against " + tokenizedContexts.length
    // + " context(s)");
    //
    // // Avoid expensive computation for monosemous terms
    // if (tokenizedCandidates.length == 1) {
    // return 0;
    // }
    //
    // // For each of the disambiguation candidates
    // for (int i = 0; i < tokenizedCandidates.length; i++) {
    // double overlap = 0.0;
    //
    // // For each of the comparator items
    // for (Object[][] context : tokenizedContexts) {
    // overlap += maxOverlap(tokenizedCandidates[i], context,
    // overlapStrategy, normalizationStrategy);
    // }
    //
    // if (overlap > bestOverlap) {
    // bestOverlap = overlap;
    // bestSodIndex = i;
    // }
    // }
    // return bestSodIndex;
    // }
    //
    // /**
    // * Compares <b>o</b> against all the elements of <b>comparands</b> using
    // the
    // * given comparator, and returns the highest result
    // *
    // * @param o
    // * an array of objects to be compared
    // * @param comparands
    // * an array of an array of objects to compare against <b>o</b>
    // * @return the highest result of all comparisons
    // */
    // protected double maxOverlap(Object[] o, Object[][] comparands,
    // OverlapStrategy os, NormalizationStrategy ns)
    // {
    // if (logger.isDebugEnabled()) {
    // StringBuffer sb = new StringBuffer();
    // for (Object token : o) {
    // sb.append(token + " ");
    // }
    // logger.debug("Tokenized sense description: " + sb);
    // }
    //
    // double bestOverlap = 0.0;
    // for (Object[] comparand : comparands) {
    // int overlap = os.overlap(o, comparand);
    // double normalization = ns.normalizer(o, comparand);
    // double normalizedOverlap = overlap / normalization;
    // bestOverlap = Math.max(bestOverlap, normalizedOverlap);
    //
    // if (logger.isDebugEnabled()) {
    // StringBuffer sb = new StringBuffer();
    // for (Object token : comparand) {
    // sb.append(token + " ");
    // }
    // logger.debug("Tokenized context: " + WSDUtils.truncate(sb.toString(),
    // CONTEXT_TRUNCATION_LENGTH));
    // logger.debug("Overlap: " + overlap + " / " + normalization
    // + " = " + normalizedOverlap);
    // }
    // }
    // logger.debug("Best overlap: " + bestOverlap);
    // return bestOverlap;
    // }

}
