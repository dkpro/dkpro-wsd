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

package de.tudarmstadt.ukp.dkpro.wsd.linkbased.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.AbstractWSDAlgorithm;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmCollectiveBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.LinkDatabaseInventoryResource;

/**
 * An disambiguation algorithm based on the relatedness of two links, which is based on the formula by Milne &amp; Witten
 *
 * @author nico.erbs@gmail.com
 *
 */
public class WikipediaRelatednessMethod extends AbstractWSDAlgorithm implements WSDAlgorithmCollectiveBasic {

	public WikipediaRelatednessMethod(SenseInventory inventory)
    {
        super(inventory);
    }

    private final static Logger logger = Logger
			.getLogger(WikipediaRelatednessMethod.class.getName());

	private final Map<String,List<String>> incomingLinksList = new HashMap<String,List<String>>();

	private Set<String> possibleCandidates;

	@Override
	public Map<String, Map<String, Double>> getDisambiguation(Collection<String> sods)
	throws SenseInventoryException {
		Map<String, Map<String, Double>> disambiguationResults = new HashMap<String, Map<String, Double>>();

		logger.info("Get all possible senses");
		possibleCandidates = new HashSet<String>();
		List<String> senses;
		HashMap<String,Double> disambiguations;
		for(String sod : sods){
			senses = inventory.getSenses(sod);

			possibleCandidates.addAll(senses);

			disambiguations = new HashMap<String,Double>();
			for(String sense : senses){
				disambiguations.put(sense, 0d);
			}
			disambiguationResults.put(sod, disambiguations);
		}

		logger.info("Computing incoming links");

		for(String candidate : possibleCandidates){
			logger.info("Compute incoming links for " + candidate);
			if(!incomingLinksList.containsKey(candidate)){
				incomingLinksList.put(candidate, ((LinkDatabaseInventoryResource)inventory).getIncomingLinks(candidate));
			}
		}

		//create list with disambiguation results
		for(String sod : disambiguationResults.keySet()){
			for(String candidate : disambiguationResults.get(sod).keySet()){
				disambiguationResults.get(sod).put(candidate, computeWikipediaLinkMeasure(candidate));
			}
		}

		return disambiguationResults;
	}

	private Integer countIncomingSharedLinks(String candidate0,
			String candidate1) {

		return ListUtils.intersection(
				incomingLinksList.get(candidate0),
				incomingLinksList.get(candidate1))
				.size();
	}

	private Double computeWikipediaLinkMeasure(String candidate) {
		logger.info("Computing score for " + candidate);

		double relatedness = 0;
		//start with -1 because a sense own value will be zero
		int counter = -1;
		int maxIncomingLinks;
		int minIncomingLinks;
		int numberOfSenses = ((LinkDatabaseInventoryResource)inventory).getNumberOfSenses();
		for(String otherCandidate : possibleCandidates){
			maxIncomingLinks = Math.max(incomingLinksList.get(candidate).size(), incomingLinksList.get(otherCandidate).size());
			minIncomingLinks = Math.min(incomingLinksList.get(candidate).size(), incomingLinksList.get(otherCandidate).size());
			counter++;
			relatedness +=
				(Math.log(maxIncomingLinks) - Math.log(countIncomingSharedLinks(candidate, otherCandidate))) /
				(Math.log(numberOfSenses) - Math.log(minIncomingLinks)) ;
		}

		//Formula by Milne & Witten
		//		relatedness (a, b ) =
		//			log(max ( A , B )) − log( A ∩ B )
		//			log( W ) − log(min( A , B ))

		return relatedness/counter;
	}

}
