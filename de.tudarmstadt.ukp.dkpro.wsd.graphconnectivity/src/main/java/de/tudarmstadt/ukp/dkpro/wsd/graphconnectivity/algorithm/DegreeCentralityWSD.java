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

package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.Pair;
import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import edu.uci.ics.jung.graph.Graph;

/**
 * Disambiguate words by constructing a disambiguation graph as per Navigli &amp;
 * Lapata (2010) and then finding the senses with the highest degree
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class DegreeCentralityWSD
    extends GraphConnectivityWSD
{

    protected int minDegree = 1;

    private final static Logger logger = Logger
            .getLogger(DegreeCentralityWSD.class.getName());

    public DegreeCentralityWSD(SenseTaxonomy inventory)
    {
        super(inventory);
    }

    /**
     * Sets the degree below which the algorithm will not attempt a sense
     * assignment
     *
     * @param minDegree
     */
    public void setMinDegree(int minDegree)
    {
        this.minDegree = minDegree;
    }

    /**
     * Gets the degree below which the algorithm will not attempt a sense
     * assignment
     *
     */
    public int getMinDegree()
    {
        return minDegree;
    }

    @Override
    protected Map<Pair<String, POS>, Map<String, Double>> getDisambiguation(
            Collection<Pair<String, POS>> sods,
            Graph<String, UnorderedPair<String>> dGraph)
        throws SenseInventoryException
    {
        final Map<Pair<String, POS>, Map<String, Double>> solutions = new HashMap<Pair<String, POS>, Map<String, Double>>();
        int disambiguatedCount = 0;

        for (Pair<String, POS> wsdItem : sods) {
            List<String> senses = inventory.getSenses(wsdItem.getFirst(),
                    wsdItem.getSecond());
            String highestDegree = null;

            for (String sense : senses) {
                if (dGraph.degree(sense) < minDegree) {
                    continue;
                }
                if (highestDegree == null
                        || dGraph.degree(sense) > dGraph.degree(highestDegree)) {
                    highestDegree = sense;
                }
            }
            if (highestDegree == null) {
                logger.debug("Failed to disambiguate " + wsdItem);
                continue;
            }

            // Note that instead of returning a single mapping to the sense
            // with the highest score, we could instead return a map of all
            // senses weighted by their degree (normalized to the maximum
            // degree). In this case falling back to the most frequent sense
            // would not be necessary.
            disambiguatedCount++;
            Map<String, Double> senseScores = new HashMap<String, Double>();
            senseScores.put(highestDegree, 1.0);
            solutions.put(wsdItem, senseScores);

            logger.debug("\"" + wsdItem.getFirst() + "\" = " + highestDegree
                    + ": " + inventory.getSenseDescription(highestDegree));
            if (graphVisualizer != null) {
                graphVisualizer.highlight(highestDegree);
            }
        }

        logger.debug("Disambiguated " + (disambiguatedCount) + " of "
                + sods.size() + " items");
        return solutions;
    }

}
