/**
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tudarmstadt.ukp.dkpro.wsd.si.germanet;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
@Ignore
public class GermaNetLexUnitSenseInventoryTest
{

    private static GermaNetLexUnitSenseInventory si;

    @Ignore
    @BeforeClass
    public static void setUpBeforeClass()
        throws Exception
    {
        si = new GermaNetLexUnitSenseInventory(
                "/home/miller/share/GermaNet/GermaNet_7.0/GN_V70_XML");
    }

    @Ignore
    @Test(expected = UnsupportedOperationException.class)
    public void mfsPOSTest()
        throws UnsupportedOperationException, SenseInventoryException
    {
        si.getMostFrequentSense("Frettchen", POS.NOUN);
    }

    @Ignore
    @Test(expected = UnsupportedOperationException.class)
    public void mfsTest()
        throws UnsupportedOperationException, SenseInventoryException
    {
        si.getMostFrequentSense("Frettchen");
    }

    @Ignore
    @Test(expected = SenseInventoryException.class)
    public void noSuchSense()
        throws SenseInventoryException
    {
        si.getSenseDescription("-500");
    }

    @Ignore
    @Test
    public void germaNetLexUnitSenseInventoryTest()
        throws Exception
    {

        final String lemma = "abbauen";
        List<String> senses = si.getSenses(lemma);

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
    }

    @Ignore
    @Test
    public void testGraph()
        throws SenseInventoryException, UnsupportedOperationException
    {
        Graph<String, UnorderedPair<String>> g = si.getUndirectedGraph();
        System.out.println("Graph has " + g.getVertexCount() + " vertices and "
                + g.getEdgeCount() + " edges");
    }

}
