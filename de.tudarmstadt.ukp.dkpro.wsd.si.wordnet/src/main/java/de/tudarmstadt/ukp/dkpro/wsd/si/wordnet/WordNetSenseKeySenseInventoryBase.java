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

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.PointerTarget;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseDictionary;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Graphs;

/**
 * Abstract class for WordNet-like sense inventories, where sense keys are used
 * as sense IDs.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class WordNetSenseKeySenseInventoryBase
    extends WordNetSenseInventoryBase
    implements SenseDictionary, SenseTaxonomy
{

    private final Log logger = LogFactory.getLog(getClass());

    private final Map<String, CachedSense> senses = new HashMap<String, CachedSense>();

    // It's expensive to find sense keys corresponding to a given lemma+POS, so
    // we cache them in a map
    private final Map<String, List<String>> senseKeyMap = new HashMap<String, List<String>>();

    /**
     * A class for the textual information associated with a WordNet sense
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    private class CachedSense
        extends CachedWordNetSense
    {
        Word word;

        public CachedSense(String senseId)
            throws SenseInventoryException
        {
            super(senseId);
            try {
                word = wn.getWordBySenseKey(senseId);
                synset = word.getSynset();
                pos = wordNetPosToSiPos.transform(word.getPOS());
                useCount = word.getUseCount();
            }
            catch (JWNLException e) {
                throw new SenseInventoryException(e);
            }
        }

        @Override
        public Set<String> getSynonyms()
        {
            if (synonyms != null) {
                return synonyms;
            }

            synonyms = new HashSet<String>(synset.getWords().size());
            for (Word w : synset.getWords()) {
                synonyms.add(w.getLemma());
            }
            return synonyms;
        }

        @Override
        protected void setDefinitionAndExamples()
            throws SenseInventoryException
        {
            examples = new HashSet<String>();
            String gloss;
            try {
                gloss = synset.getGloss();
            }
            catch (IllegalArgumentException e) {
                throw new SenseInventoryException(e);
            }
            if (gloss == null) {
                if (senseDescriptionFormat.matches(".*%[de].*")) {
                    logger.warn("Sense " + id + " has no gloss");
                }
                definition = "";
                return;
            }
            Matcher glossMatcher = glossPattern.matcher(gloss);

            if (glossMatcher.matches()) {
                definition = glossMatcher.group(1);
                Scanner scanner = new Scanner(glossMatcher.group(2));
                scanner.useDelimiter("(\"$)|(\"?; \")");
                while (scanner.hasNext()) {
                    examples.add(scanner.next());
                }
                scanner.close();
            }
            else {
                definition = gloss;
            }
        }

        @Override
        public Set<String> getNeighbours()
            throws SenseInventoryException
        {
            if (neighbours != null) {
                return neighbours;
            }

            neighbours = new HashSet<String>();

            try {
                // Add sense keys for all words in a lexical relationship
                for (Pointer p : word.getPointers()) {
                    PointerTarget t = p.getTarget();
                    if (t instanceof Word) {
                        neighbours.add(((Word) t).getSenseKey());
                    }
                }

                // Add sense keys for all words of all synsets in a semantic
                // relationship
                for (Pointer p : synset.getPointers()) {
                    PointerTarget t = p.getTarget();
                    if (t instanceof Synset) {
                        for (Word w : ((Synset) t).getWords()) {
                            neighbours.add(w.getSenseKey());
                        }
                    }
                }
            }
            catch (JWNLException e) {
                throw new SenseInventoryException(e);
            }

            return neighbours;
        }
    }

    @Override
    protected List<String> getSenses(String sod, net.sf.extjwnl.data.POS pos)
        throws SenseInventoryException
    {
        String lemmaKey = sod + "/" + pos.getKey();
        List<String> senses = senseKeyMap.get(lemmaKey);
        if (senses != null) {
            return senses;
        }
        try {
            IndexWord indexWord = wn.getIndexWord(pos, sod);
            if (indexWord == null) {
                senses = new ArrayList<String>(0);
                senseKeyMap.put(lemmaKey, senses);
                return senses;
            }
            List<Synset> synsets = indexWord.getSenses();
            senses = new ArrayList<String>(synsets.size());
            for (Synset s : synsets) {
                for (Word w : s.getWords()) {
                    if (w.getLemma().equalsIgnoreCase(indexWord.getLemma())) {
                        senses.add(w.getSenseKey());
                    }
                }
            }
            senseKeyMap.put(lemmaKey, senses);
            return senses;
        }
        catch (JWNLException e) {
            throw new SenseInventoryException(e);
        }
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        List<String> senses = getSenses(sod, pos);
        if (senses.isEmpty()) {
            return null;
        }
        return senses.get(0);
    }

    /**
     * Retrieves a sense from the cache, or creates one and adds it to the cache
     * if it's not already there
     *
     * @param senseId
     * @return
     * @throws SenseInventoryException
     */
    @Override
    protected CachedWordNetSense getSense(String senseId)
        throws SenseInventoryException
    {
        CachedSense s = senses.get(senseId);
        if (s == null) {
            s = new CachedSense(senseId);
            senses.put(senseId, s);
        }
        return s;
    }

    /**
     * Read WordNet into a graph
     *
     * @throws SenseInventoryException
     */
    @Override
    public UndirectedGraph<String, UnorderedPair<String>> getUndirectedGraph()
        throws SenseInventoryException
    {
        if (undirectedWNGraph != null) {
            return undirectedWNGraph;
        }

        undirectedWNGraph = new UndirectedSparseGraph<String, UnorderedPair<String>>();
        int senseKeyCount = 0, pointerCount = 0;

        // For each POS
        for (Object pos : net.sf.extjwnl.data.POS.getAllPOS()) {
            logger.info("Adding synsets for " + pos);

            Iterator<Synset> i;
            try {
                i = wn.getSynsetIterator((net.sf.extjwnl.data.POS) pos);
            }
            catch (JWNLException e) {
                throw new SenseInventoryException(e);
            }

            // For each Synset
            while (i.hasNext()) {
                Synset s = i.next();

                // For each Word
                for (Word w : s.getWords()) {
                    senseKeyCount++;
                    Set<String> targetSenseKeys = new HashSet<String>();

                    try {
                        // Create list of lexical relation targets
                        for (Pointer p : w.getPointers()) {
                            PointerTarget t = p.getTarget();
                            if (t instanceof Word) {
                                pointerCount++;
                                targetSenseKeys.add(((Word) t).getSenseKey());
                            }
                        }

                        // Create list of semantic relation targets
                        for (Pointer p : s.getPointers()) {
                            PointerTarget t = p.getTarget();
                            if (t instanceof Synset) {
                                pointerCount++;
                                for (Word targetWord : ((Synset) t).getWords()) {
                                    targetSenseKeys.add(targetWord
                                            .getSenseKey());
                                }
                            }
                        }

                        // Add vertices and edges to graph
                        undirectedWNGraph.addVertex(w.getSenseKey());
                        for (String targetSenseKey : targetSenseKeys) {
                            UnorderedPair<String> e = new UnorderedPair<String>(
                                    w.getSenseKey(), targetSenseKey);
                            if (!undirectedWNGraph.containsEdge(e)) {
                                undirectedWNGraph.addEdge(e, w.getSenseKey(),
                                        targetSenseKey);
                            }
                        }
                    }
                    catch (JWNLException e) {
                        throw new SenseInventoryException(e);
                    }
                }

            }
            logger.info("# vertices = " + undirectedWNGraph.getVertexCount()
                    + "; # senseKeys = " + senseKeyCount + "; # edges = "
                    + undirectedWNGraph.getEdgeCount() + "; # pointers = "
                    + pointerCount);
        }

        undirectedWNGraph = Graphs
                .unmodifiableUndirectedGraph(undirectedWNGraph);
        return undirectedWNGraph;

    }

    @Override
    public String getWordNetSenseKey(String senseId, String lemma)
        throws SenseInventoryException
    {
        return senseId;
    }
}
