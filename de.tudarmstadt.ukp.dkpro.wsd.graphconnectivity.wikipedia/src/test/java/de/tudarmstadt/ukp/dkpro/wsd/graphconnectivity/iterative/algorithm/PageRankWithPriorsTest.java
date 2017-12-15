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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util.DisambiguationEdge;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util.DisambiguationEdgeTransformer;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util.DisambiguationVertex;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util.DisambiguationVertexTransformer;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class PageRankWithPriorsTest {

	@Test
	public void test() {

		//Construct graph
		Graph<DisambiguationVertex, DisambiguationEdge> graph = new DirectedSparseGraph<DisambiguationVertex, DisambiguationEdge>();

		//Add nodes from sentence to graph
		DisambiguationVertex vertex0 = new DisambiguationVertex("Sod0", "Sense0", 0.6);
		DisambiguationVertex vertex1 = new DisambiguationVertex("Sod0", "Sense1", 0.4);
		graph.addVertex(vertex0);
		graph.addVertex(vertex1);
		graph.addVertex(new DisambiguationVertex("Sod1", "Sense2", 1));
		graph.addVertex(new DisambiguationVertex("Sod2", "Sense3", 1));
		graph.addVertex(new DisambiguationVertex("Sod3", "Sense4", 0.5));
		graph.addVertex(new DisambiguationVertex("Sod3", "Sense5", 0.5));
		graph.addVertex(new DisambiguationVertex("Sod4", "Sense6", 0.5));
		graph.addVertex(new DisambiguationVertex("Sod5", "Sense7", 0.5));

		//Add edges to graph
		double similarity;
		for(DisambiguationVertex source : graph.getVertices()){
			for(DisambiguationVertex target : graph.getVertices()){
				if(source != target){
					//					System.out.println(getSenseSimilarity(source.getSense(), target.getSense()) +"\t"+
					//					source.getSense() +"\t"+
					//					target.getSense());
					similarity = getSimilarity(source.getSense(), target.getSense());
					if(similarity > 0){
						graph.addEdge(
								new DisambiguationEdge(similarity),
								source,
								target);
					}
				}
			}
		}

		//Run graph algorithm
		PageRankWithPriors<DisambiguationVertex, DisambiguationEdge> pageRankAlgorithm =
				new PageRankWithPriors<DisambiguationVertex, DisambiguationEdge>(
						graph,
						new DisambiguationEdgeTransformer(),
						new DisambiguationVertexTransformer(),
						0.95);
		pageRankAlgorithm.initialize();
		pageRankAlgorithm.evaluate();

		assertTrue(pageRankAlgorithm.getVertexScore(vertex1) > pageRankAlgorithm.getVertexScore(vertex0));

		//get vertex weights
//		System.out.println(vertex0.getSod() +"\t"+ vertex0.getSense()  +"\t"+ pageRankAlgorithm.getVertexScore(vertex0));
//		System.out.println(vertex1.getSod() +"\t"+ vertex1.getSense()  +"\t"+ pageRankAlgorithm.getVertexScore(vertex1));

	}

	private double getSimilarity(String sense0, String sense1) {
		if(sense0.equals("Sense0") || sense1.equals("Sense0")){
			return 0.01;
		}
		else{
			return 0.9;
		}
	}

}
