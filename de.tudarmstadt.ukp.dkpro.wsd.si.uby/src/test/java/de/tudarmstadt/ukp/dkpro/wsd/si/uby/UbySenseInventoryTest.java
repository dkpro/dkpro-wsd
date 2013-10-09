/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.dkpro.wsd.si.uby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
@Ignore
public class UbySenseInventoryTest
{
    private static UbySenseInventory si;

    @Ignore
    @BeforeClass
    public static void setUpBeforeClass()
        throws Exception
    {
        si = new UbySenseInventory(
                "localhost/uby_release_1_0",
                "com.mysql.jdbc.Driver", "mysql", "username", "password",
                false);
    }

    @Ignore
    @Test
    public void ubySenseInventoryTest()
        throws SenseInventoryException
    {

        System.out.println(si.getSenseInventoryName());

        List<String> senses = si.getSenses("set", POS.NOUN);

        assertEquals(44, senses.size());

        for (String sense : senses) {
            printSenseInformation(sense);
        }
    }

    @Ignore
    @Test
    public void frequencyTest()
        throws SenseInventoryException
    {
        si.setLexicon(null);
        System.out.println(si.getSenseInventoryName());
        assertEquals("FN_Sense_3049", si.getMostFrequentSense("set"));

        si.setLexicon("WordNet");
        System.out.println(si.getSenseInventoryName());
        assertNull(si.getMostFrequentSense("set"));
    }

    @Ignore
    @Test
    public void alignmentTest()
        throws SenseInventoryException
    {
        final String id = "WN_Sense_40443";
        si.setLexicon("WordNet");

        Set<String> alignments = si.getSenseAlignments(id);
        assertEquals(1, alignments.size());
        System.out.println("Monolingual alignments of " + id + ": " + si.getSenseAlignments(id));
        for (String sense : alignments) {
            printSenseInformation(sense);
        }

        si.setAllowMultilingualAlignments(true);
        alignments = si.getSenseAlignments(id);
        assertEquals(12, alignments.size());
        System.out.println("All alignments of " + id + ": " + si.getSenseAlignments(id));
        for (String sense : alignments) {
            printSenseInformation(sense);
        }
    }

    private void printSenseInformation(String sense) throws SenseInventoryException {
        System.out.println("Uby sense ID: " + sense);
        System.out.println("Lexicon sense ID:" + si.getLexiconSenseId(sense));
        System.out.println("Lexicon synset ID:" + si.getLexiconSynsetId(sense));
        System.out.println("Description: " + si.getSenseDescription(sense));
        System.out.println("Definition: " + si.getSenseDefinition(sense));
        System.out.println("Examples: " + si.getSenseExamples(sense));
        System.out.println("Words: " + si.getSenseWords(sense));
        System.out.println("Neighbours: " + si.getSenseNeighbours(sense));
        System.out.println("Alignments: " + si.getSenseAlignments(sense));
        System.out.println();
    }
}