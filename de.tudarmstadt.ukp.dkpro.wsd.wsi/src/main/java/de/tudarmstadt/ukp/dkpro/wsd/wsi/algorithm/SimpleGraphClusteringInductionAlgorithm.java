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
package de.tudarmstadt.ukp.dkpro.wsd.wsi.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import de.tudarmstadt.ukp.dkpro.wsd.WSDException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * @author zorn
 *
 */
public class SimpleGraphClusteringInductionAlgorithm
    extends WSIAlgorithmBase
    implements SenseInductionAlgorithm
{

    List<TermSimilarityMeasure> measures;
    private final LexicalSemanticResource thesaurus;

    private double sim_thres;

    private static HashMap<String, Double> scoreCache = new HashMap<String, Double>();
    HashMap<String, List<String>> clusterCache = new HashMap<String, List<String>>();

    public SimpleGraphClusteringInductionAlgorithm(LexicalSemanticResource thesaurus,
            double sim_thres, List<TermSimilarityMeasure> measures2)
        throws SimilarityException
    {

        this.thesaurus = thesaurus;
        this.sim_thres = sim_thres;
        this.measures = measures2;

    }

    /**
     * take the original feature vector for each new tag_n: mask the features that are zero in all
     * feature-vectors of the cluster-members
     *
     * @param term
     * @param similars
     * @param clusters
     * @throws SimilarityException
     */
    public void createNewSenses(String term, Set<Entity> similars, List<List<String>> clusters)
        throws SimilarityException
    {
    }

    /**
     * Clusters a set of terms given a similarity function.
     *
     * @param term
     * @param similarEntities
     *
     * @return clusters of terms
     */
    public List<List<String>> cluster(String term, Set<Entity> similarEntities)
    {
        /*
         * Graph creation step. Different strategies can apply.
         */

        Graph<String, Integer> localGraph = null;
        localGraph = new UndirectedSparseGraph<String, Integer>();
        Collection<String> similars = new LinkedList<String>();
        for (final Entity entity : similarEntities) {
            localGraph.addVertex(entity.getFirstLexeme());
            similars.add(entity.getFirstLexeme());
        }
        double mod_sim_thres = this.sim_thres;
        System.out.println("simthres " + this.sim_thres);
        this.sim_thres = 0.20;
        mod_sim_thres = 0.20;
        final double min_thres = 0.01;
        final WeakComponentClusterer<String, Integer> weakComponentClusterer = new WeakComponentClusterer<String, Integer>();
        Collection<Graph<String, Integer>> connectedComponents;
        /*
         * Current implementation adds edges until the number of connected components falls below a
         * threshold
         */
        // final GraphMLWriter<String, Integer> writer = new GraphMLWriter<String, Integer>();
        int iteration = 0;
        do {

            createGraph(similars, localGraph, mod_sim_thres);

            connectedComponents = FilterUtils.createAllInducedSubgraphs(
                    weakComponentClusterer.transform(localGraph), localGraph);
            mod_sim_thres = (mod_sim_thres) - (mod_sim_thres / 10);
            System.out.println("got " + connectedComponents.size()
                    + " components reducing sim_thres to " + mod_sim_thres);

            // HEURISTICS WARNING
        }
        while (connectedComponents.size() > 70 && mod_sim_thres > min_thres);

        System.out.println("final connected components " + connectedComponents.size());

        /*
         * We assume that each connected component represents one sense If the components are too
         * large (needs to be defined), then an more sophisticated clustering is being run.
         */
        int maxSize = 0;
        final List<List<String>> result = new LinkedList<List<String>>();
        final Graph<String, Integer> outliersGraph = new UndirectedSparseGraph<String, Integer>();
        for (final Graph<String, Integer> subGraph : connectedComponents) {
            if (subGraph.getVertexCount() > 1) {
                System.out.println("found connected component !:");

                for (final String i1 : subGraph.getVertices()) {
                    System.out.print(i1 + ",");
                }
                System.out.println();

                if (subGraph.getVertexCount() > 20) {
                    System.out.println("large cluster found, running community detection");
                    final Transformer<Graph<String, Integer>, Set<Set<String>>> fc = new EdgeBetweennessClusterer<String, Integer>(
                            subGraph.getEdgeCount() / 10);

                    final Collection<Graph<String, Integer>> subSubGraph = FilterUtils
                            .createAllInducedSubgraphs(fc.transform(subGraph), subGraph);
                    System.out.println("done, found " + subSubGraph.size() + " clusters");
                    iteration = 0;
                    for (final Graph<String, Integer> g2 : subSubGraph) {
                        final List<String> list = new LinkedList<String>();
                        for (final String vertex : g2.getVertices()) {
                            list.add(vertex);
                        }
                        if (list.size() > 1) {
                            result.add(list);

                        }
                        else {
                            outliersGraph.addVertex(list.get(0));
                        }
                    }
                }
                else {
                    final List<String> list = new LinkedList<String>();
                    for (final String vertex : subGraph.getVertices()) {
                        list.add(vertex);
                    }
                    result.add(list);
                }

            }
            else {
                outliersGraph.addVertex(subGraph.getVertices().iterator().next());
            }
            if (subGraph.getVertexCount() >= maxSize) {
                maxSize = subGraph.getVertexCount();
                localGraph = subGraph;
            }

        }
        createGraph(outliersGraph.getVertices(), outliersGraph, 0.000001);
        System.out.println("size of outliersgraph " + outliersGraph.getVertexCount());

        connectedComponents = FilterUtils.createAllInducedSubgraphs(
                weakComponentClusterer.transform(outliersGraph), outliersGraph);
        System.out.println("connected components in outliersgraph " + connectedComponents.size());
        for (final Graph<String, Integer> subGraph : connectedComponents) {
            // System.out.println("component with "+g.getVertexCount()+" verticex");
            if (subGraph.getVertexCount() > 1) {
                System.out.println("found connected component !:");

                for (final String i1 : subGraph.getVertices()) {
                    System.out.print(i1 + ",");
                }
                System.out.println();
                final List<String> list = new LinkedList<String>();
                for (final String vertex : subGraph.getVertices()) {
                    list.add(vertex);
                }
                result.add(list);
            }
        }
        return result;

    }

    /**
     * create a local graph from a term neighborhood
     *
     * @param collection
     * @param localGraph
     * @param mod_sim_thres
     */
    public void createGraph(Collection<String> collection, Graph<String, Integer> localGraph,
            double mod_sim_thres)
    {
        final ArrayList<String> vertexlist2 = new ArrayList<String>(collection);
        final int i = 0;
        for (final String v : collection) {
            // vertexlist2.remove(v);
            for (final String w : vertexlist2) {
                if (!w.equals(v)) {

                    double similarity;
                    /*
                     * Edge ids are calculated by XORing the hashcodes of the corresponding nodes.
                     */
                    if (!localGraph.containsEdge(v.hashCode() ^ w.hashCode())) {
                        try {
                            similarity = getSimilarity(v, w);
                            if (similarity > mod_sim_thres) {
                                localGraph.addEdge(v.hashCode() ^ w.hashCode(), v, w);
                            }
                        }
                        catch (final Exception e) {

                            e.printStackTrace();
                        }
                    }

                }
            }

        }
    }

    private double getSimilarity(String v, String w)
    {
        final String key = v + ":" + w;
        if (SimpleGraphClusteringInductionAlgorithm.scoreCache.containsKey(key)) {
            return SimpleGraphClusteringInductionAlgorithm.scoreCache.get(key);
        }
        double cosine = TermSimilarityMeasure.NOT_FOUND;

        for (final TermSimilarityMeasure measure : this.measures) {
            try {
                cosine = measure.getSimilarity(v, w);
            }
            catch (final SimilarityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (cosine != TermSimilarityMeasure.NOT_FOUND) {
                break;
            }
        }
        SimpleGraphClusteringInductionAlgorithm.scoreCache.put(key, cosine);
        return cosine;
    }

    @Override
    public SenseInventory induce(Collection<String> targetWords)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void induceSenses(String term)
        throws WSDException
    {
        Set<Entity> similars;
        try {
            similars = this.thesaurus.getRelatedEntities(new Entity(term), null);

            System.out.println(term + ":" + similars);
            if (similars.size() == 0) {
                return;
            }

            final List<List<String>> clusters = cluster(term, similars);
            // create a new tag tag_n for each cluster
            // List<Vector> features = getNeighborFeatures(term);
            int clid = 0;
            // list of clusters of size 1
            final List<String> smallcluster = new LinkedList<String>();
            int clustercnt = 0;
            for (final List<String> cluster : clusters) {
                if (cluster.size() == 1) {

                    smallcluster.addAll(cluster);

                }
                else {
                    clustercnt++;
                }
            }

            for (final List<String> cluster : clusters) {
                senseInventory.addSense(term, term + "_" + clid++, cluster);
            }
        }
        catch (LexicalSemanticResourceException e) {
            throw new WSDException(e);
        }

    }

}
