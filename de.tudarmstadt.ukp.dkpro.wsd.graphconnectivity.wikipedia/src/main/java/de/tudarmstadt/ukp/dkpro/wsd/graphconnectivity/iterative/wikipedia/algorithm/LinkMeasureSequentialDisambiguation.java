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

import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.algorithm.SequentialGraphDisambiguation;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.wikipedia.measures.WikiLinkComparator;

/**
 * A class for sequential disambiguation using Wikipedia links as weights between senses
 *
 * @author nico.erbs@gmail.com
 *
 */
public class LinkMeasureSequentialDisambiguation extends
		SequentialGraphDisambiguation {

    private WikiLinkComparator wlc;

	public LinkMeasureSequentialDisambiguation(SenseInventory inventory) {
		super(inventory);
	}

	public void setupWikipedia()
		throws WikiApiException
	{
    	Wikipedia wiki = null;
		DatabaseConfiguration db = new DatabaseConfiguration();
		db.setHost("localhost");
        db.setDatabase("wikipedia_en_20111008");
        db.setUser("root");
        db.setPassword("");
        db.setLanguage(Language.english);
        try {
            wiki = new Wikipedia(db);
        } catch (WikiInitializationException e) {
            throw new WikiApiException(e);
        }
        wlc = new WikiLinkComparator(wiki, false);
    }



	@Override
	protected double getSenseSimilarity(String baseSense, String targetSense) throws SimilarityException {
		if(baseSense.equals(targetSense)){
			return 1;
		}
		//lazy initialization
		if(wlc==null){
			try {
				setupWikipedia();
			} catch (WikiApiException e) {
				throw new SimilarityException(e);
			}
		}

		double similarity = wlc.getSimilarity(baseSense, targetSense);
		System.out.println("Similarity (" + baseSense + " - " + targetSense + "):\t" + similarity);

		return similarity;
	}

}
