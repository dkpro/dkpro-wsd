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

import org.apache.commons.collections15.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseDictionary;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Graphs;

/**
 * Abstract class for WordNet-like sense inventories, where synset IDs are used
 * as sense IDs.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class WordNetSynsetSenseInventoryBase
    extends WordNetSenseInventoryBase
    implements SenseDictionary, SenseTaxonomy
{

    private final Log logger = LogFactory.getLog(getClass());

    // Transformers
    private final PointerToUnorderedPair pointerToUnorderedPair = new PointerToUnorderedPair();

    // Variables and cache for sense descriptions
    private final Map<String, CachedSense> senses = new HashMap<String, CachedSense>();

    @Override
    public String getWordNetSenseKey(String senseId, String lemma)
        throws SenseInventoryException
    {
        return synsetOffsetAndPosToSenseKey(senseId, lemma);
    }

    @Override
    protected List<String> getSenses(String sod, net.sf.extjwnl.data.POS pos)
        throws SenseInventoryException
    {
        try {
            IndexWord indexWord = wn.getIndexWord(pos, sod);
            List<String> senses = new ArrayList<String>();
            if (indexWord == null) {
                return senses;
            }
            for (Synset s : indexWord.getSenses()) {
                senses.add(synsetToString.transform(s));
            }
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
        try {
            IndexWord indexWord = wn.getIndexWord(
                    siPosToWordNetPos.transform(pos), sod);
            if (indexWord == null) {
                return null;
            }
            return synsetToString.transform(indexWord.getSenses().get(0));
        }
        catch (JWNLException e) {
            throw new SenseInventoryException(e);
        }
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
     * A class for the textual information associated with a WordNet synset
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    private class CachedSense
        extends CachedWordNetSense
    {

        public CachedSense(String senseId)
            throws SenseInventoryException
        {
            super(senseId);
            synset = stringToSynset.transform(id);
            pos = wordNetPosToSiPos.transform(synset.getPOS());
            useCount = 0;
            for (Word word : synset.getWords()) {
                useCount += word.getUseCount();
            }
        }

        @Override
        public Set<String> getSynonyms()
        {
            if (synonyms != null) {
                return synonyms;
            }

            synonyms = new HashSet<String>();
            for (Word word : synset.getWords()) {
                synonyms.add(word.getLemma().replace('_', ' '));
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

            // Add neighbours
            for (Pointer pointer : synset.getPointers()) {
                try {
                    neighbours.add(synsetToString.transform(pointer
                            .getTargetSynset()));
                }
                catch (JWNLException e) {
                    throw new SenseInventoryException(e);
                }
            }

            return neighbours;
        }
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
        int synsetCount = 0, pointerCount = 0;

        for (Object pos : net.sf.extjwnl.data.POS.getAllPOS()) {
            logger.info("Adding synsets for " + pos);
            try {
                for (Iterator<?> i = wn
                        .getSynsetIterator((net.sf.extjwnl.data.POS) pos); i
                        .hasNext();) {
                    Synset s = (Synset) i.next();
                    String s_name = synsetToString.transform(s);
                    undirectedWNGraph.addVertex(s_name);
                    synsetCount++;
                    for (Pointer p : s.getPointers()) {
                        pointerCount++;
                        UnorderedPair<String> e = pointerToUnorderedPair
                                .transform(p);
                        if (!undirectedWNGraph.containsEdge(e)) {
                            undirectedWNGraph.addEdge(e, s_name, synsetToString
                                    .transform(p.getTargetSynset()));
                        }
                    }
                }
            }
            catch (JWNLException e) {
                throw new SenseInventoryException(e);
            }
            logger.info("# vertices = " + undirectedWNGraph.getVertexCount()
                    + "; # synsets = " + synsetCount + "; # edges = "
                    + undirectedWNGraph.getEdgeCount() + "; # pointers = "
                    + pointerCount);
        }

        undirectedWNGraph = Graphs
                .unmodifiableUndirectedGraph(undirectedWNGraph);
        return undirectedWNGraph;

    }

    /**
     * Compact string representation of a <Synset, Pointer> graph
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    @SuppressWarnings("unused")
    private class GraphToString
        implements Transformer<Graph<Synset, Pointer>, String>
    {
        SynsetToString sts = new SynsetToString();
        PointerToString pts = new PointerToString();

        @Override
        public String transform(Graph<Synset, Pointer> g)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Vertices (" + g.getVertexCount() + "):\n");
            for (Synset s : g.getVertices()) {
                sb.append("\t" + sts.transform(s) + "\n");
            }
            sb.append("Edges (" + g.getEdgeCount() + "):\n");
            for (Pointer p : g.getEdges()) {
                sb.append("\t" + pts.transform(p) + "\n");
            }
            return sb.toString();
        }
    }

    /**
     * Transforms a WordNet pointer to a String representation indicating only
     * its source and target synsets. (Note that the output does not uniquely
     * identify the pointer, since type information is not included.)
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    private class PointerToString
        implements Transformer<Pointer, String>
    {
        @Override
        public String transform(Pointer p)
        {
            Synset source;
            PointerTarget pt = p.getSource();
            if (pt instanceof Word) {
                source = ((Word) pt).getSynset();
            }
            else {
                source = (Synset) pt;
            }
            try {
                return synsetToString.transform(source) + " -> "
                        + synsetToString.transform(p.getTargetSynset());
            }
            catch (JWNLException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Transforms a WordNet pointer to an unordered pair of Strings indicating
     * only the two synsets in the relation. (Note that the output does not
     * uniquely identify the pointer, since type information is not included,
     * and because the directionality of the pointer is not preserved.)
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    private class PointerToUnorderedPair
        implements Transformer<Pointer, UnorderedPair<String>>
    {
        @Override
        public UnorderedPair<String> transform(Pointer p)
        {
            Synset source;
            PointerTarget pt = p.getSource();
            if (pt instanceof Word) {
                source = ((Word) pt).getSynset();
            }
            else {
                source = (Synset) pt;
            }
            try {
                return new UnorderedPair<String>(
                        synsetToString.transform(source),
                        synsetToString.transform(p.getTargetSynset()));
            }
            catch (JWNLException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

}
