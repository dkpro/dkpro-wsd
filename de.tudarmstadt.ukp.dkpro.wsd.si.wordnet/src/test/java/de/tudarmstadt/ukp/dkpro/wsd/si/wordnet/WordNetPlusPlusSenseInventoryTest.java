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

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.List;

import net.sf.extjwnl.JWNLException;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WordNetPlusPlusSenseInventoryTest
{
    @Ignore
    @Test
	public void wordNetSenseInventoryTest()
		throws Exception
	{

		WordNetPlusPlusSenseInventory si = new WordNetPlusPlusSenseInventory(
				new URL(
						"file:///home/miller/share/WordNet/WordNet-2.1/wordnet_properties.xml"),
				new URL(
						"file:///home/miller/share/WordNet++/WordNet++-UKP/WordNet++-UKP_2.1_synsets.tsv"), 0.6);

		List<String> senses = si.getSenses("car");

        assertEquals(5, senses.size());

        for (String sense : senses) {
            System.out.println("ID: " + sense);
            System.out.println("Description: " + si.getSenseDescription(sense));
            System.out.println("Definition: " + si.getSenseDefinition(sense));
            System.out.println("Examples: " + si.getSenseExamples(sense));
            System.out.println("Words: " + si.getSenseWords(sense));
            System.out.println("Neighbours: " + si.getSenseNeighbours(sense));
            System.out.println();
        }

		String mfsMineNoun = si.getMostFrequentSense("mine", POS.NOUN);
        assertEquals("03768346n", mfsMineNoun);

		String mfsMineVerb = si.getMostFrequentSense("mine", POS.VERB);
        assertEquals("01163620v", mfsMineVerb);

		// When instantiating again the WordNet resource shouldn't be reread
		si = new WordNetPlusPlusSenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-2.1/wordnet_properties.xml"),
                new URL(
                        "file:///home/miller/share/WordNet++/WordNet++-UKP/WordNet++-UKP_2.1_synsets.tsv"), 0.6);
	}

    @Ignore
    @Test(expected = JWNLException.class)
    public void multipleWordNetSenseInventoryTest()
        throws Exception
    {
        @SuppressWarnings("unused")
        WordNetPlusPlusSenseInventory si = new WordNetPlusPlusSenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-2.1/wordnet_properties.xml"),
                new URL(
                        "file:///home/miller/share/WordNet++/WordNet++-UKP/WordNet++-UKP_2.1_synsets.tsv"), 0.6);

        // Trying to instantiate multiple times with different versions should
        // fail
        si = new WordNetPlusPlusSenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-3.0/wordnet_properties.xml"),
                new URL(
                        "file:///home/miller/share/WordNet++/WordNet++-UKP/WordNet++-UKP_2.1_synsets.tsv"), 0.6);
    }

    @Ignore
    @Test(expected = UnsupportedOperationException.class)
    public void testGraphModification()
        throws Exception
    {
        WordNetPlusPlusSenseInventory si = new WordNetPlusPlusSenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-2.1/wordnet_properties.xml"),
                new URL(
                        "file:///home/miller/share/WordNet++/WordNet++-UKP/WordNet++-UKP_2.1_synsets.tsv"), 0.6);

        Graph<String, UnorderedPair<String>> g = si.getUndirectedGraph();

        // This should just reuse the existing graph instead of building a new
        // one
        Graph<String, UnorderedPair<String>> h = si.getUndirectedGraph();

        assertEquals(g, h);

        g.addVertex("Foo");
    }

}
