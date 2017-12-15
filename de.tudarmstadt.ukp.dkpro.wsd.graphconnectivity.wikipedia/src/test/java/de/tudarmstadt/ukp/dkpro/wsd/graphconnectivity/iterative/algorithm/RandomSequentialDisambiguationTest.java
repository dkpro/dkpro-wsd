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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.algorithm.RandomSequentialDisambiguation;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseWeightedInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.GoogleDictionaryInventory;

/**
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 *
 */
public class RandomSequentialDisambiguationTest {

	@Test
	public void testSequentialDisambiguation() throws SenseInventoryException, FileNotFoundException, IOException
	{
	    SenseWeightedInventory inventory = new GoogleDictionaryInventory(null, "src/test/resources/dictionary/SpitkovskyChang/dict_google.ser", null);
		RandomSequentialDisambiguation wsdAlgorithm = new RandomSequentialDisambiguation(inventory);

		assertNotNull(inventory);
		assertNotNull(wsdAlgorithm);

//		{claude_monet=[Claude_Monet], 10_things_i_hate_about_you=[10_Things_I_Hate_About_You, 10_Things_I_Hate_About_You]}

		Collection<Collection<String>> sods = new ArrayList<Collection<String>>();

		Collection<String> sequence0 = new ArrayList<String>();
		sequence0.add("Claude Monet");
		sequence0.add("Claude Monet");
		sequence0.add("Something");
		sods.add(sequence0);

		Collection<String> sequence1 = new ArrayList<String>();
		sequence1.add("10 Things I hate about you");
		sequence1.add("Anything");
		sods.add(sequence1);

//		System.out.println(wsdAlgorithm.getDisambiguation(sods));

		List<Map<String, Map<String, Double>>> disambiguation = wsdAlgorithm.getDisambiguation(sods);

		assertNotNull(disambiguation);
		System.out.println(disambiguation);

		assertEquals(2, disambiguation.size());

		assertEquals(1, disambiguation.get(0).size());

		assertTrue(disambiguation.get(0).get("Claude Monet").containsKey("Claude_Monet"));

		assertTrue(disambiguation.get(0).get("Claude Monet").get("Claude_Monet") > .5d);

	}
}
