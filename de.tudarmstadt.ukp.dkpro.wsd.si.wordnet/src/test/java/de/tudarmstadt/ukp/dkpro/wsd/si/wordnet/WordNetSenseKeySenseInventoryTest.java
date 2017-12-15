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
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.List;

import net.sf.extjwnl.JWNLException;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;

public class WordNetSenseKeySenseInventoryTest
{
    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void deserializeTest() throws Exception {
        WordNetSenseKeySenseInventory si = new WordNetSenseKeySenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));
        FileInputStream fileIn = new FileInputStream("/home/miller/share/WordNet/WordNet-3.0/DKProWSD_SK_graph.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        UndirectedGraph<String, UnorderedPair<String>> g;
        g = (UndirectedGraph<String, UnorderedPair<String>>) in.readObject();
        in.close();
        fileIn.close();
        assertEquals(586348, g.getEdgeCount());
        assertEquals(206949, g.getVertexCount());
        UndirectedGraph<String, UnorderedPair<String>> h = si.getUndirectedGraph();
        assertEquals(586348, h.getEdgeCount());
        assertEquals(206949, h.getVertexCount());
    }

    @Ignore
    @Test
    public void wordNetSKSenseInventoryTest()
        throws Exception
    {

        WordNetSenseKeySenseInventory si = new WordNetSenseKeySenseInventory(
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
        assertEquals("mine%1:06:01::", mfsMineNoun);

        String mfsMineVerb = si.getMostFrequentSense("mine", POS.VERB);
        assertEquals("mine%2:34:00::", mfsMineVerb);
    }

    @Ignore
    @Test
    public void multipleWordNetSKSenseInventoryTest()
        throws Exception
    {
        @SuppressWarnings("unused")
        WordNetSenseKeySenseInventory si30 = new WordNetSenseKeySenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));

        @SuppressWarnings("unused")
        WordNetSenseKeySenseInventory si21 = new WordNetSenseKeySenseInventory(
                new URL(
                        "file:///home/miller/share/WordNet/WordNet-2.1/extjwnl_properties.xml"));

    }

    @Ignore
    @Test(expected = UnsupportedOperationException.class)
    public void testGraphModification()
        throws FileNotFoundException, JWNLException, IOException,
        SenseInventoryException
    {
        WordNetSenseKeySenseInventory si = new WordNetSenseKeySenseInventory(
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
