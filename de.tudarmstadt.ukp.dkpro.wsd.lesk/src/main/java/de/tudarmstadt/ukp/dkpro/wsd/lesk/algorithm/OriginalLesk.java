package de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm;
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

// TODO: Rewrite

//package de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.normalization.NormalizationStrategy;
//import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.overlap.OverlapStrategy;
//import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.tokenization.TokenizationStrategy;
//import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
//import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
//import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
//
///**
// * Disambiguates words using the original Lesk algorithm -- i.e., the
// * definitions of the subject of disambiguation are compared against the
// * definitions of all the words in its context
// *
// * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
// */
//public class OriginalLesk
//    extends Lesk
//{
//    public OriginalLesk(SenseInventory inventory,
//            OverlapStrategy overlapStrategy,
//            NormalizationStrategy normalizationStrategy,
//            TokenizationStrategy tokenizationStrategy)
//    {
//        super(inventory, overlapStrategy, normalizationStrategy,
//                tokenizationStrategy);
//    }
//
//    /**
//     * Takes a subject of disambiguation <b>sod</b> and its part of speech, plus
//     * one or more context words and their parts of speech. For each context
//     * word, all its sense descriptions are compared pairwise with the sense
//     * descriptions of <b>sod</b>, and the best overall sense of <b>sod</b> is
//     * returned. It is assumed that the sense description comparisons are
//     * independent.
//     *
//     * @param sod
//     *            the subject of disambiguation
//     * @param sodPOS
//     *            the subject of disambiguation's part of speech
//     * @param contextWords
//     *            an array of context words to compare against
//     * @param contextWordsPos
//     *            an array of parts of speech for the context words
//     * @return a sense map containing the sense of the subject of disambiguation
//     *         which best matches the contexts, or null if no such sense could
//     *         be found
//     */
//    @SuppressWarnings("unchecked")
//    public Map<String, Double> getDisambiguation(String sod, POS sodPos,
//            String[] contextWords, POS[] contextWordsPos)
//        throws SenseInventoryException
//    {
//        if (contextWords.length < 1) {
//            return null;
//        }
//        if (contextWordsPos != null
//                && contextWords.length != contextWordsPos.length) {
//            throw new IllegalArgumentException("context array size mismatch");
//        }
//
//        // Get the sense IDs for the subject of disambiguation
//        List<String> sodSenses = getSenses(sod, sodPos);
//
//        // If the subject of disambiguation was not found in the sense
//        // inventory,
//        // Lesk cannot disambiguate it
//        if (sodSenses.isEmpty()) {
//            return null;
//        }
//
//        // If the subject of disambiguation has only one sense, we can avoid
//        // computing its similarity to the contexts
//        if (sodSenses.size() == 1) {
//            return getDisambiguationMap(new String[] { sodSenses.get(0) },
//                    new double[] { 1.0 });
//        }
//
//        // Get the sense IDs for the context words
//        List<?>[] contextSenses = new List<?>[contextWords.length];
//        boolean foundSenses = false;
//        for (int i = 0; i < contextWords.length; i++) {
//            contextSenses[i] = getSenses(contextWords[i],
//                    contextWordsPos == null ? null : contextWordsPos[i]);
//            if (!contextSenses[i].isEmpty()) {
//                foundSenses = true;
//            }
//        }
//
//        // If none of the contexts was found in the sense inventory either,
//        // there is nothing Lesk can do to disambiguate the subject of
//        // disambiguation.
//        if (foundSenses == false) {
//            return null;
//        }
//
//        // Get the descriptions for the senses
//        List<String> sodSenseDescriptions = getSenseDescriptions(sodSenses);
//        List<List<String>> contextSenseDescriptions = new ArrayList<List<String>>(contextWords.length);
//        for (int i = 0; i < contextWords.length; i++) {
//            contextSenseDescriptions.add(getSenseDescriptions((List<String>) contextSenses[i]));
//        }
//
//        // Use the Lesk algorithm to find the index of the best sense
//        int i = lesk(sodSenseDescriptions, contextSenseDescriptions);
//
//        if (i < 0) {
//            return null;
//        }
//        else {
//            return getDisambiguationMap(new String[] { sodSenses.get(i) },
//                    new double[] { 1.0 });
//        }
//    }
//
//    /**
//     * Takes a subject of disambiguation <b>sod</b> and one or more context
//     * words. For each context word, all its sense descriptions are compared
//     * pairwise with the sense descriptions of <b>sod</b>, and the best overall
//     * sense of <b>sod</b> is returned. It is assumed that the sense description
//     * comparisons are independent.
//     *
//     * @param sod
//     *            the subject of disambiguation
//     * @param contextWords
//     *            an array of context words to compare against
//     * @return a sense map containing the sense of the subject of disambiguation
//     *         which best matches the contexts, or null if no such sense could
//     *         be found
//     */
//    public Map<String, Double> getDisambiguation(String sod,
//            String[] contextWords)
//        throws SenseInventoryException
//    {
//        return getDisambiguation(sod, null, contextWords, null);
//    }
//
//}
