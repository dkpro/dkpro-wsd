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

package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.AbstractWSDAlgorithm;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmCollectiveSequentialBasic;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util.DisambiguationEdge;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util.DisambiguationEdgeTransformer;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util.DisambiguationVertex;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util.DisambiguationVertexTransformer;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.wikipedia.util.IncomingLinksCache;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.wikipedia.util.SimilarityCache;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseWeightedInventory;
import dkpro.similarity.algorithms.api.SimilarityException;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Base for all methods doing sequential disambiguation, i.e. iterating over lists of sods and taking previous disambiguations into account
 *
 * @author nico.erbs@gmail.com
 *
 */
public abstract class SequentialGraphDisambiguation
extends AbstractWSDAlgorithm
implements WSDAlgorithmCollectiveSequentialBasic {


	//Dimensions
	private double pagerankAlpha = 0.1;

	private double dampingFactor;

	private final Map<String, Double> previousEntites;

	private final static Logger logger = Logger
			.getLogger(SequentialGraphDisambiguation.class.getName());

	public SequentialGraphDisambiguation(SenseInventory inventory) {
		super(inventory);

		previousEntites = new HashMap<String, Double>();
		SimilarityCache.initialize();
	}

	/**
	 * First check if the similarity between a pair has already been computed
	 * @param baseSense first sense
	 * @param targetSense second sense
	 * @return a similarity value
	 * @throws SimilarityException
	 */
	protected double getSimilarity(String baseSense, String targetSense) throws SimilarityException{
		if(SimilarityCache.contains(baseSense, targetSense)){
			return SimilarityCache.get(baseSense, targetSense);
		}
		else{
			double similarity = getSenseSimilarity(baseSense, targetSense);
			SimilarityCache.put(baseSense, targetSense, similarity);
			return similarity;
		}
	}

	/**
	 * Compute the similarity between two senses
	 * @param baseSense first sense
	 * @param targetSense second sense
	 * @return a similarity value
	 * @throws SimilarityException
	 */
	abstract protected double getSenseSimilarity(String baseSense, String targetSense) throws SimilarityException;

	@Override
	public List<Map<String, Map<String, Double>>> getDisambiguation(
			Collection<Collection<String>> sods) throws SenseInventoryException {
		List<Map<String, Map<String, Double>>> disambiguation = new LinkedList<Map<String, Map<String, Double>>>();

		Map<String, Map<String, Double>> sequenceDisambiguation;
		for(Collection<String> sequence : sods){

			sequenceDisambiguation = getDisambiguatedSequence(sequence, previousEntites);
			reduceWeights(previousEntites);

			//add disambiguated entities to map of previous entities
			for(Map<String, Double> disambiguations : sequenceDisambiguation.values()){
				if(disambiguations.size() != 0){
					previousEntites.put(getTopRankedEntity(disambiguations), 1d);
				}
			}

			disambiguation.add(sequenceDisambiguation);
		}
		return disambiguation;
	}

	/**
	 * Get disambiguations from graph
	 * @param sods the sods to be disambiguated
	 * @param previousEntites senses for sods that are already disambiguated
	 * @return the disambiguation of sods
	 * @throws SenseInventoryException
	 */
	private Map<String, Map<String, Double>> getDisambiguatedSequence(
			Collection<String> sods,
			Map<String, Double> previousEntites) throws SenseInventoryException{

		//Construct graph
		Graph<DisambiguationVertex, DisambiguationEdge> graph = new DirectedSparseGraph<DisambiguationVertex, DisambiguationEdge>();

		//Add nodes from sentence to graph
		Map<String, Double> weightedSenses;
		for(String sod : sods){
			weightedSenses = ((SenseWeightedInventory)inventory).getWeightedSenses(sod);
			for(String sense : weightedSenses.keySet()){
				logger.debug("Added vertex to graph: " + sod +"\t"+ sense +"\t"+ weightedSenses.get(sense));
				graph.addVertex(new DisambiguationVertex(sod, sense, weightedSenses.get(sense)));
			}
		}

		//Add nodes from sentence to graph
		for(String sense : previousEntites.keySet()){
			graph.addVertex(new DisambiguationVertex("PREVIOUS", sense, previousEntites.get(sense)));
		}

		//Add edges to graph
		double similarity;
		for(DisambiguationVertex source : graph.getVertices()){
			for(DisambiguationVertex target : graph.getVertices()){
				if(source != target){
					//					System.out.println(getSenseSimilarity(source.getSense(), target.getSense()) +"\t"+
					//					source.getSense() +"\t"+
					//					target.getSense());
					try {
						similarity = getSimilarity(source.getSense(), target.getSense());
						if(similarity > 0){
						graph.addEdge(
								new DisambiguationEdge(similarity),
								source,
								target);
						}
					} catch (SimilarityException e) {
						throw new SenseInventoryException(e);
					}
				}
			}
		}

		//Run graph algorithm
//		BetweennessCentrality<DisambiguationVertex, DisambiguationEdge> graphAlgorithm =
//				new BetweennessCentrality<DisambiguationVertex, DisambiguationEdge>(
//						graph,
//						new DisambiguationEdgeTransformer());

		PageRankWithPriors<DisambiguationVertex, DisambiguationEdge> graphAlgorithm =
				new PageRankWithPriors<DisambiguationVertex, DisambiguationEdge>(
						graph,
						new DisambiguationEdgeTransformer(),
						new DisambiguationVertexTransformer(),
						pagerankAlpha);
		graphAlgorithm.initialize();
		graphAlgorithm.evaluate();


		//Fill disambiguation results
		Map<String, Map<String, Double>> disambiguation = new HashMap<String, Map<String, Double>>();
		for(DisambiguationVertex vertex : graph.getVertices()){
			if(!disambiguation.containsKey(vertex.getSod())){
				disambiguation.put(vertex.getSod(), new HashMap<String, Double>());
			}
			disambiguation.get(vertex.getSod()).put(vertex.getSense(), graphAlgorithm.getVertexScore(vertex));
			logger.debug(vertex.getSod() +"\t"+ vertex.getSense()  +"\t"+ graphAlgorithm.getVertexScore(vertex));
		}

		return disambiguation;

//		for(String sod : sods){
//			disambiguation.put(sod, ((SenseWeightedInventory)inventory).getWeightedSenses(sod));
//		}
	}

	/**
	 * reducing weights of previous disambiguations the older they are because they are less important
	 * @param previousEntites
	 */
	private void reduceWeights(Map<String,Double> previousEntites){
		List<String> toDelete = new ArrayList<String>();
		for(String key : previousEntites.keySet()){

			previousEntites.put(key, previousEntites.get(key)*dampingFactor);

			if (previousEntites.get(key) < 0.000001){
				toDelete.add(key);
			}

		}
		for(String key : toDelete){
			previousEntites.remove(key);
		}
	}

	/**
	 * Utility method to get the top-ranked sense
	 * @param disambiguations map with disambiguations
	 * @return
	 */
	private String getTopRankedEntity(Map<String, Double> disambiguations) {
		String topRankedEntity = null;
		double weight = -1;
		for(String entity : disambiguations.keySet()){
			if(disambiguations.get(entity) > weight){
				topRankedEntity = entity;
				weight = disambiguations.get(entity);
			}
		}
		return topRankedEntity;
	}

	@Override
	public void collectionProcessComplete() {
		SimilarityCache.serialize();
		IncomingLinksCache.serialize();
	}

	@Override
	public void setDampingFactor(double dampingFactor) {
		this.dampingFactor = dampingFactor;
	}

	@Override
	public void setPagerankAlpha(double pagerankAlpha) {
		this.pagerankAlpha = pagerankAlpha;
	}

}
