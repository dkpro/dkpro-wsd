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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Graphs;

/**
 * A sense inventory for WordNet++, based on extJWNL.  Synset IDs are used as
 * sense IDs.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WordNetPlusPlusSenseInventory
    extends WordNetSynsetSenseInventory
{
    private final UndirectedGraph<String, UnorderedPair<String>> undirectedWNPPGraph;
    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public UndirectedGraph<String, UnorderedPair<String>> getUndirectedGraph()
        throws SenseInventoryException
    {
        return undirectedWNPPGraph;
    }

    public WordNetPlusPlusSenseInventory(InputStream propertiesStream,
            InputStream wordNetPlusPlusStream, double similarityThreshold)
        throws Exception
    {
        super(propertiesStream);
        undirectedWNPPGraph = buildWordNetPlusPlusGraph(wordNetPlusPlusStream,
                similarityThreshold);
    }

    public WordNetPlusPlusSenseInventory(URL propertiesURL,
            URL wordNetPlusPlusRelationsURL, double similarityThreshold)
        throws Exception
    {
        super(propertiesURL);
        undirectedWNPPGraph = buildWordNetPlusPlusGraph(
                wordNetPlusPlusRelationsURL.openStream(), similarityThreshold);
    }

    private UndirectedGraph<String, UnorderedPair<String>> buildWordNetPlusPlusGraph(
            InputStream wordNetPlusPlusStream, double similarityThreshold)
        throws NumberFormatException, IOException, Exception
    {
        logger.info("Getting WordNet graph");
        Graph<String, UnorderedPair<String>> undirectedWNGraph = super
                .getUndirectedGraph();
        logger.info("Copying WordNet graph");
        UndirectedGraph<String, UnorderedPair<String>> undirectedWNPPGraph = new UndirectedSparseGraph<String, UnorderedPair<String>>();
        for (String vertex : undirectedWNGraph.getVertices()) {
            undirectedWNPPGraph.addVertex(vertex);
        }
        for (UnorderedPair<String> edge : undirectedWNGraph.getEdges()) {
            undirectedWNPPGraph.addEdge(edge,
                    undirectedWNGraph.getEndpoints(edge));
        }

        int addedRelations = 0, totalRelations = 0, duplicateRelations = 0, dissimilarRelations = 0;

        // Read the WordNet++ semantic relations
        logger.info("Reading WordNet++ semantic relations");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                wordNetPlusPlusStream, "UTF-8"));

        String line;

        while ((line = br.readLine()) != null) {
            String[] lineParts = line.split("[ \\t]+");
            if (lineParts.length != 4) {
                throw new Exception("Invalid file format");
            }

            totalRelations++;

            double similarity = Double.parseDouble(lineParts[0]);
            if (similarity < similarityThreshold) {
                dissimilarRelations++;
                continue;
            }

            String source = String
                    .format("%08dn", Long.parseLong(lineParts[2]));
            String target = String
                    .format("%08dn", Long.parseLong(lineParts[3]));
            UnorderedPair<String> edge = new UnorderedPair<String>(source,
                    target);
            if (undirectedWNPPGraph.containsEdge(edge)) {
                duplicateRelations++;
                continue;
            }
            addedRelations++;
            undirectedWNPPGraph.addEdge(edge, source, target);
        }

        logger.info("Added " + addedRelations + " of " + totalRelations
                + " semantic relations (" + dissimilarRelations
                + " lower than similarity threshold; " + duplicateRelations
                + " already in WordNet graph)");
        logger.info("Old graph contains " + undirectedWNGraph.getVertexCount()
                + " vertices and " + undirectedWNGraph.getEdgeCount()
                + " edges");
        logger.info("New graph contains "
                + undirectedWNPPGraph.getVertexCount() + " vertices and "
                + undirectedWNPPGraph.getEdgeCount() + " edges");

        if (undirectedWNPPGraph.getVertexCount() > undirectedWNGraph
                .getVertexCount()) {
            logger.error("WordNet++ semantic relations reference nonexistent WordNet synsets -- probable WordNet version mismatch");
            throw new SenseInventoryException("WordNet++ semantic relations reference nonexistent WordNet synsets");
        }

        return Graphs.unmodifiableUndirectedGraph(undirectedWNPPGraph);
    }

    @Override
    public Set<String> getSenseNeighbours(String senseId)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return new HashSet<String>(undirectedWNPPGraph.getNeighbors(senseId));
    }

}
