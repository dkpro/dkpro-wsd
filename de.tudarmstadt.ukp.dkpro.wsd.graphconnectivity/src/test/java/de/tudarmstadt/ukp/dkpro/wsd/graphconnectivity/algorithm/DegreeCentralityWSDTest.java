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
package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.extjwnl.JWNLException;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.Pair;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.algorithm.DegreeCentralityWSD;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.algorithm.JungGraphVisualizer;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetSynsetSenseInventory;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class DegreeCentralityWSDTest
{
	@Test
    @Ignore
	public void testDegreeCentrality()
		throws MalformedURLException, JWNLException, IOException,
		SenseInventoryException, InterruptedException
	{
	    WordNetSynsetSenseInventory inventory = new WordNetSynsetSenseInventory(
                new FileInputStream(
                        "/home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));
		DegreeCentralityWSD wsdAlgorithm = new DegreeCentralityWSD(inventory);

		JungGraphVisualizer g = new JungGraphVisualizer();
		inventory.setSenseDescriptionFormat("<html><b>%w</b><br />%d</html>");
		g.setAnimationDimensions(1000, 500);
		g.setAnimationDelay(0);
		g.setInteractive(true);

		wsdAlgorithm.setGraphVisualizer(g);

		// Disambiguate a sentence
		List<Pair<String, POS>> sentence = new ArrayList<Pair<String, POS>>();
		sentence.add(new Pair<String, POS>("drink", POS.VERB));
        sentence.add(new Pair<String, POS>("milk", POS.NOUN));
        sentence.add(new Pair<String, POS>("straw", POS.NOUN));
		wsdAlgorithm.setSearchDepth(4);
		Map<Pair<String,POS>, Map<String, Double>> dabMap = wsdAlgorithm
				.getDisambiguation(sentence);

		Thread.sleep(5000);

		assertNotNull(dabMap);
		assertEquals(2, dabMap.size());
		Map<String, Double> wordMap;

		wordMap = dabMap.get(new Pair<String, POS>("drink", POS.VERB));
		assertNotNull(wordMap);
		assertEquals(1, wordMap.size());
		assertEquals(1.0, wordMap.get("01170052v"), 0.001);

		wordMap = dabMap.get(new Pair<String, POS>("milk", POS.NOUN));
		assertNotNull(wordMap);
		assertEquals(1, wordMap.size());
		assertEquals(1.0, wordMap.get("07844042n"), 0.001);
	}

}
