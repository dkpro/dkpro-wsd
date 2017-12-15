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

package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.wikipedia.algorithm;

import java.util.List;

import org.apache.commons.collections15.ListUtils;
import org.junit.Before;

import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.algorithm.SequentialGraphDisambiguation;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.wikipedia.util.IncomingLinksCache;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.PerformanceLinkInformationReader;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * A class for sequential disambiguation using Wikipedia links from a special database as weights between senses
 *
 * @author nico.erbs@gmail.com
 *
 */
public class LinkInformationSequentialDisambiguation extends
SequentialGraphDisambiguation {

	private PerformanceLinkInformationReader linkInformationReader;

	public LinkInformationSequentialDisambiguation(SenseInventory inventory) {
		super(inventory);
	}

	@Before
	public void setupWikipedia()
			throws WikiApiException
			{
		linkInformationReader = new PerformanceLinkInformationReader(
				"localhost",
				"linkdatabase_wikipedia_en_20100615",
				"root",
				"");
		IncomingLinksCache.initialize();
			}



	@Override
	protected double getSenseSimilarity(String baseSense, String targetSense) throws SimilarityException {
		if(baseSense.equals(targetSense)){
			return 1;
		}
		//lazy initialization
		if(linkInformationReader==null){
			try {
				setupWikipedia();
			} catch (WikiApiException e) {
				throw new SimilarityException(e);
			}
		}

		double similarity = computeLinkMeasureSimilarity(baseSense.replaceAll("_", " "), targetSense.replaceAll("_", " "));
		//		if(similarity > 0){
		//			System.out.println("Similarity (" + baseSense + " - " + targetSense + "):\t" + similarity);
		//		}
		return similarity;
	}

	private double computeLinkMeasureSimilarity(String target0, String target1) throws SimilarityException{
		if (target0.equals(target1)) {
			return 1.0;
		}


		List<String> linksA;
		List<String> linksB;
		try {
			linksA = getIncomingLinks(target0);
			linksB = getIncomingLinks(target1);
		} catch (Exception e) {
			throw new SimilarityException();
		}

		int linksBoth = ListUtils.intersection(linksA, linksB).size();

		double a = Math.log(linksA.size()) ;
		double b = Math.log(linksB.size()) ;
		double ab = Math.log(linksBoth) ;
		double m = Math.log(linkInformationReader.getNumberOfSenses()) ;

		double sr = (Math.max(a, b) -ab) / (m - Math.min(a, b)) ;

		if (Double.isNaN(sr) || Double.isInfinite(sr) || sr > 1) {
			sr = 1 ;
		}

		sr = 1-sr ;

		return sr ;

	}

	private List<String> getIncomingLinks(String article) throws Exception {
		if(IncomingLinksCache.contains(article)){
			return IncomingLinksCache.getSources(article);
		}
		List<String> sources = linkInformationReader.getLinkSources(null, null, article, 0);
		IncomingLinksCache.add(article, sources);
		return sources;
	}



}
