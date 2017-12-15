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

package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.sf.extjwnl.JWNLException;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import edu.uci.ics.jung.graph.Graph;

public class WordNetSynsetSenseInventoryTest
{
    @Ignore
    @Test
    public void wordNetSenseInventoryTest()
        throws Exception
    {

        WordNetSynsetSenseInventory si = new WordNetSynsetSenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));

        final String lemma = "car";
        List<String> senses = si.getSenses(lemma);

        assertEquals(5, senses.size());

        for (String sense : senses) {
            System.out.println("ID: " + sense);
            System.out.println("Sense key: " + si.getWordNetSenseKey(sense, lemma));
            System.out.println("Description: " + si.getSenseDescription(sense));
            System.out.println("Definition: " + si.getSenseDefinition(sense));
            System.out.println("Examples: " + si.getSenseExamples(sense));
            System.out.println("Words: " + si.getSenseWords(sense));
            System.out.println("Neighbours: " + si.getSenseNeighbours(sense));
            System.out.println("Use count: " + si.getUseCount(sense));
            System.out.println();
        }

        String mfsMineNoun = si.getMostFrequentSense("mine", POS.NOUN);
        assertEquals("03768346n", mfsMineNoun);

        String mfsMineVerb = si.getMostFrequentSense("mine", POS.VERB);
        assertEquals("01163620v", mfsMineVerb);

        // Trying to instantiate multiple times with the same version shouldn't
        // result in an error
        si = new WordNetSynsetSenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));

        // Trying to instantiate multiple times with a stream rather than an
        // URL should print a warning
        si = new WordNetSynsetSenseInventory(
                new FileInputStream(
                        "/home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));
    }

    @Ignore
    @Test
    public void multipleWordNetSenseInventoryTest()
        throws Exception
    {
        @SuppressWarnings("unused")
        WordNetSynsetSenseInventory si30 = new WordNetSynsetSenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));

        @SuppressWarnings("unused")
        WordNetSynsetSenseInventory si21 = new WordNetSynsetSenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-2.1/extjwnl_properties.xml"));

    }

    @Ignore
    @Test(expected = UnsupportedOperationException.class)
    public void testGraphModification()
        throws FileNotFoundException, JWNLException, IOException,
        SenseInventoryException
    {
        WordNetSynsetSenseInventory si = new WordNetSynsetSenseInventory(
                new FileInputStream(
                        "/home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));

        Graph<String, UnorderedPair<String>> g = si.getUndirectedGraph();

        // This should just reuse the existing graph instead of building a new
        // one
        Graph<String, UnorderedPair<String>> h = si.getUndirectedGraph();

        assertEquals(g, h);

        g.addVertex("Foo");
    }

}
