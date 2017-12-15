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
package de.tudarmstadt.ukp.dkpro.wsd.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.extjwnl.JWNLException;
import de.tudarmstadt.ukp.dkpro.wsd.Pair;
import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.algorithm.DegreeCentralityWSD;
import de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.algorithm.JungGraphVisualizer;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetSenseKeySenseInventory;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * This class displays a simple, interactive visualization of the degree
 * centrality WSD algorithm described in the paper
 * "An Experimental Study of Graph Connectivity for Unsupervised Word Sense Disambiguation"
 * by R. Navigli and M. Lapata
 * (<em>IEEE Transactions on Pattern Analysis and Machine Intelligence</em>
 * 32(4):678–692, 2010). When the class is run, a window will appear showing a
 * sense graph. Clicking anywhere on the graph will step through the algorithm.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class GraphVisualizationExample
{
    public static void main(String[] args)
        throws MalformedURLException, JWNLException, IOException,
        SenseInventoryException, InterruptedException,
        UnsupportedOperationException, ClassNotFoundException
    {

        // Create an instance of the WordNet sense inventory. You need to
        // create an extJWNL properties file and change the value of the
        // PARAM_WORDNET_PROPERTIES_URL to point to its location on your file
        // system.
        WordNetSenseKeySenseInventory inventory = new WordNetSenseKeySenseInventory(
                new FileInputStream(
                        "/home/miller/share/WordNet/WordNet-3.0/extjwnl_properties.xml"));

        // If you happen to have a serialized WordNet graph, you can specify
        // its location here and it will be read in. (If no serialized graph is
        // available, this demo will still work, but it can take several minutes
        // to compute the graph.)
        inventory
                .setUndirectedGraph(deserializeGraph("/home/miller/share/WordNet/WordNet-3.0/DKProWSD_SK_graph.ser"));

        // Instantiate a class for performing the degree centrality algorithm
        DegreeCentralityWSD wsdAlgorithm = new DegreeCentralityWSD(inventory);

        // Set up a graph visualizer
        JungGraphVisualizer g = new JungGraphVisualizer();
        inventory.setSenseDescriptionFormat("<html><b>%w</b><br />%d</html>");
        g.setAnimationDimensions(1000, 700); // Window dimensions in pixels
        g.setAnimationDelay(0); // Delay of 0 ms at each step
        g.setInteractive(true); // Pause for mouse click at each step

        // Bind the visualizer to the algorithm
        wsdAlgorithm.setGraphVisualizer(g);

        // Create a simple sentence whose words will be disambiguated
        List<Pair<String, POS>> sentence = new ArrayList<Pair<String, POS>>();
        sentence.add(new Pair<String, POS>("drink", POS.VERB));
        sentence.add(new Pair<String, POS>("milk", POS.NOUN));
        // You can add more words if desired...
        // sentence.add(new Pair<String, POS>("straw", POS.NOUN));

        // Run the algorithm on the sentence
        wsdAlgorithm.setSearchDepth(4);
        @SuppressWarnings("unused")
        Map<Pair<String, POS>, Map<String, Double>> dabMap = wsdAlgorithm
                .getDisambiguation(sentence);
    }

    /**
     * Reads in a serialized WordNet graph.
     *
     * @return the WordNet graph
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static UndirectedGraph<String, UnorderedPair<String>> deserializeGraph(
            String serializedGraphFilename)
        throws IOException, ClassNotFoundException
    {
        System.out.println("Reading graph...");
        File graphfile = new File(serializedGraphFilename);
        if (graphfile.exists() == false) {
            return null;
        }
        FileInputStream fileIn = new FileInputStream(graphfile);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        UndirectedGraph<String, UnorderedPair<String>> g;
        g = (UndirectedGraph<String, UnorderedPair<String>>) in.readObject();
        in.close();
        fileIn.close();
        System.out.println("Read a graph with " + g.getEdgeCount()
                + " edges and " + g.getVertexCount() + " vertices");
        return g;
    }

}
