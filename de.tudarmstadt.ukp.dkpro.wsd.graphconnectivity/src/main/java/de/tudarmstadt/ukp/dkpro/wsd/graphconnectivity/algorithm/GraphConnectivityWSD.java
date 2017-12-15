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

package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.algorithm;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.Pair;
import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.AbstractWSDAlgorithm;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmCollectivePOS;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * An implementation of the unsupervised graph connectivity WSD from Navigli &amp;
 * Lapata (2010).
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class GraphConnectivityWSD
    extends AbstractWSDAlgorithm
    implements WSDAlgorithmCollectivePOS
{
    protected SenseTaxonomy inventory;

    private int dfsCount = 0;

    private final static Logger logger = Logger
            .getLogger(GraphConnectivityWSD.class.getName());
    GraphVisualizer graphVisualizer = null;
    private int searchDepth = 3;

    public GraphConnectivityWSD(SenseTaxonomy inventory)
    {
        super(inventory);
        this.inventory = inventory;
    }

    /**
     * Sets the maximum recursion depth for the depth-first search.
     *
     * @param depth
     *            The maximum recursion depth for the depth-first search
     */
    public void setSearchDepth(int depth)
    {
        searchDepth = depth;
    }

    /**
     * Set the graph visualizer
     *
     * @param graphVisualizer
     *            The GraphVisualizer to use
     */
    public void setGraphVisualizer(GraphVisualizer graphVisualizer)
    {
        this.graphVisualizer = graphVisualizer;
    }

    /**
     * Determines how vertex (i.e., sense) labels will be visualized
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    class VertexToolTipTransformer
        implements Transformer<String, String>
    {

        @Override
        public String transform(String s)
        {
            try {
                return inventory.getSenseDescription(s);
            }
            catch (SenseInventoryException e) {
                return e.toString();
            }
        }
    }

    @Override
    public Map<Pair<String, POS>, Map<String, Double>> getDisambiguation(
            Collection<Pair<String, POS>> sods)
        throws SenseInventoryException
    {
        Graph<String, UnorderedPair<String>> siGraph = inventory
                .getUndirectedGraph();
        Graph<String, UnorderedPair<String>> dGraph = new UndirectedSparseGraph<String, UnorderedPair<String>>();
        int sodCount = 0;

        if (graphVisualizer != null) {
            graphVisualizer.initializeColorMap(sods.size());
            graphVisualizer.setVertexToolTipTransformer(new VertexToolTipTransformer());
        }

        // Find the senses for the lemmas and add them to a graph
        for (Pair<String, POS> wsdItem : sods) {
            List<String> senses = inventory.getSenses(wsdItem.getFirst(),
                    wsdItem.getSecond());
            if (senses.isEmpty()) {
                logger.warn("unknown subject of disambiguation " + wsdItem);
                // throw new SenseInventoryException(
                // "unknown subject of disambiguation " + wsdItem);
            }

            for (String sense : senses) {
                if (graphVisualizer != null) {
                    graphVisualizer.setColor(sense, sodCount);
                }
                dGraph.addVertex(sense);
            }

            sodCount++;
        }
        logger.debug(dGraph.toString());

        if (graphVisualizer != null) {
            graphVisualizer.initialize(dGraph);
        }

        // For each synset v in s, perform a depth-first search on siGraph.
        // Every time we encounter a synset v' from s along a path of length
        // ≤ l, we add to dGraph all intermediate nodes and edges on the path
        // from v to v'.

        // Run DFS on each synset in s
        Collection<String> s = new HashSet<String>(dGraph.getVertices());
        for (String v : s) {
            logger.debug("Beginning DFS from " + v);
            Collection<String> t = new HashSet<String>(s);
            t.remove(v);
            Stack<String> synsetPath = new Stack<String>();
            synsetPath.push(v);
            dfs(v, t, siGraph, dGraph, synsetPath,
                    new Stack<UnorderedPair<String>>(), searchDepth);
        }

        logger.debug(dGraph.toString());

        // Find the best synsets for each word in the sentence
        final Map<Pair<String, POS>, Map<String, Double>> solutions = getDisambiguation(
                sods, dGraph);

        // Repaint the frame to show the disambiguated senses
        if (graphVisualizer != null) {
            graphVisualizer.refresh();
        }

        return solutions;
    }

    /**
     * Beginning at startVertex, conduct a depth-first search of the graph
     * siGraph looking for any of the vertices in goalSynsets. If any are found
     * within maxDepth iterations, add the path to dGraph.
     *
     * @param startVertex
     *            The vertex at which to begin the search
     * @param goalVertices
     *            A collection of vertices at which to stop the search
     *            successfully
     * @param siGraph
     *            The full ontology graph to search
     * @param dGraph
     *            The disambiguation graph to construct
     * @param vertexPath
     *            A stack of vertices visited so far
     * @param edgePath
     *            A stack of edges visited so far
     * @param maxDepth
     *            The maximum depth to recurse to
     * @return true if and only if a goal was found before maxDepth iterations
     */
    protected boolean dfs(final String startVertex,
            final Collection<String> goalVertices,
            final Graph<String, UnorderedPair<String>> siGraph,
            final Graph<String, UnorderedPair<String>> dGraph,
            final Stack<String> vertexPath,
            final Stack<UnorderedPair<String>> edgePath, final int maxDepth)
    {
        // TODO: This algorithm could probably be optimized further

        logger.debug("count=" + dfsCount++ + " depth="
                + (searchDepth - maxDepth) + " synset=" + startVertex);

        // We have found a goal
        if (goalVertices.contains(startVertex)) {
            logger.debug("Found goal at " + startVertex);
            for (UnorderedPair<String> p : edgePath) {
                logger.debug(p.toString());
            }
            return true;
        }

        // We have reached the maximum depth
        if (maxDepth == 0) {
            logger.debug("Reached maximum depth at " + startVertex);
            return false;
        }

        // Visit all neighbours of this vertex
        for (UnorderedPair<String> edge : siGraph.getOutEdges(startVertex)) {
            String neighbour = siGraph.getOpposite(startVertex, edge);
            if (vertexPath.contains(neighbour)) {
                // We have encountered a loop
                logger.debug("Encountered loop at " + neighbour);
                continue;
            }
            if (dGraph.containsEdge(edge)) {
                // This path is already in the disambiguation graph
                logger.debug("Path already in graph at " + edge);
                continue;
            }
            edgePath.push(edge);
            vertexPath.push(neighbour);
            logger.debug("Recursing to " + edge);
            if (dfs(neighbour, goalVertices, siGraph, dGraph, vertexPath,
                    edgePath, maxDepth - 1) == true) {
                logger.debug("Adding " + edge);
                addPath(dGraph, edgePath);
            }
            else {
                logger.debug("Not adding " + edge);
            }
            edgePath.pop();
            vertexPath.pop();
        }

        // We have reached a dead end
        logger.debug("Reached dead end at " + startVertex);
        return false;
    }

    /**
     * Adds stack of edges to a graph
     *
     * @param graph
     *            The graph to modify
     * @param edgeStack
     *            The stack of edges to add
     */
    protected void addPath(Graph<String, UnorderedPair<String>> graph,
            Stack<UnorderedPair<String>> edgeStack)
    {
        for (UnorderedPair<String> edge : edgeStack) {
            if (graph.containsEdge(edge) == false) {
                graph.addEdge(edge, edge.getFirst(), edge.getSecond());
                if (graphVisualizer != null) {
                    graphVisualizer.animate(graph, edge, edge.getFirst(),
                            edge.getSecond());
                }
            }
        }
    }

    protected abstract Map<Pair<String, POS>, Map<String, Double>> getDisambiguation(
            Collection<Pair<String, POS>> sods,
            Graph<String, UnorderedPair<String>> dGraph)
        throws SenseInventoryException;

}
