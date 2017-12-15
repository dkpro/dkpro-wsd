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

package de.tudarmstadt.ukp.dkpro.wsd.evaluation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.Pair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * A class which computes statistics on a WSD corpus
 *
 */
public class CorpusStatistics
    extends JCasAnnotator_ImplBase
{
    public final static String SENSE_INVENTORY_RESOURCE = "SenseInventory";
    @ExternalResource(key = SENSE_INVENTORY_RESOURCE)
    protected SenseInventory inventory;

    public static final String PARAM_SHOW_LEGEND = "showLegend";
    @ConfigurationParameter(name = PARAM_SHOW_LEGEND, mandatory = false, description = "Whether to show the legend in the output", defaultValue = "true")
    protected Boolean showLegend;

    protected Map<POS, Integer> polysemousInstanceCount;
    protected Map<POS, Integer> uniquePolysemousInstanceCount;
    protected Map<POS, Integer> monosemousInstanceCount;
    protected Map<POS, Integer> uniqueMonosemousInstanceCount;
    protected Map<POS, Integer> unknownInstanceCount;
    protected Map<POS, Integer> uniqueUnknownInstanceCount;
    protected Map<POS, Integer> polysemousSenseCount;
    protected Map<POS, Integer> uniquePolysemousSenseCount;
    protected Set<Pair<String, POS>> uniqueInstances;

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        Collection<WSDItem> wsdItems = JCasUtil.select(aJCas, WSDItem.class);

        for (WSDItem wsdItem : wsdItems) {
            POS pos = POS.valueOf(wsdItem.getPos());
            String sod = wsdItem.getSubjectOfDisambiguation();
            int senseCount;
            try {
                senseCount = inventory.getSenses(sod, pos).size();
            }
            catch (Exception e) {
                throw new AnalysisEngineProcessException(e);
            }

            if (senseCount == 0) {
                increment(unknownInstanceCount, pos);
            }
            else if (senseCount == 1) {
                increment(monosemousInstanceCount, pos);
            }
            else {
                increment(polysemousInstanceCount, pos);
                increment(polysemousSenseCount, pos, senseCount);
            }

            if (uniqueInstances.add(new Pair<String, POS>(sod, pos))) {
                if (senseCount == 0) {
                    increment(uniqueUnknownInstanceCount, pos);
                }
                else if (senseCount == 1) {
                    increment(uniqueMonosemousInstanceCount, pos);
                }
                else {
                    increment(uniquePolysemousInstanceCount, pos);
                    increment(uniquePolysemousSenseCount, pos, senseCount);
                }
            }
        }

    }

    protected Integer increment(Map<POS, Integer> map, POS pos)
    {
        return map.put(pos, Integer.valueOf(map.get(pos) + 1));
    }

    protected Integer increment(Map<POS, Integer> map, POS pos, int value)
    {
        return map.put(pos, Integer.valueOf(map.get(pos) + value));
    }

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        super.collectionProcessComplete();

        int totalUnknownInstanceCount = 0;
        int totalMonosemousInstanceCount = 0;
        int totalPolysemousInstanceCount = 0;
        int totalPolysemousSenseCount = 0;

        System.out.println("Instances\n=========\n");
        System.out.format("%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\n",
                "POS", "total", "known", "unknown", "monosem", "polysem",
                "senses", "AP", "APP");
        System.out.format("%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\n",
                "---", "-----", "-----", "-------", "-------", "-------",
                "------", "--", "---");
        for (POS pos : POS.values()) {
            totalUnknownInstanceCount += unknownInstanceCount.get(pos);
            totalMonosemousInstanceCount += monosemousInstanceCount.get(pos);
            totalPolysemousInstanceCount += polysemousInstanceCount.get(pos);
            totalPolysemousSenseCount += polysemousSenseCount.get(pos);
            putCorpusStats(pos.toString(), unknownInstanceCount.get(pos),
                    monosemousInstanceCount.get(pos),
                    polysemousInstanceCount.get(pos),
                    polysemousSenseCount.get(pos));
        }
        putCorpusStats("all", totalUnknownInstanceCount,
                totalMonosemousInstanceCount, totalPolysemousInstanceCount,
                totalPolysemousSenseCount);

        totalUnknownInstanceCount = 0;
        totalMonosemousInstanceCount = 0;
        totalPolysemousInstanceCount = 0;
        totalPolysemousSenseCount = 0;

        System.out.println("\nUnique instances\n================\n");
        System.out.format("%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\n",
                "POS", "total", "known", "unknown", "monosem", "polysem",
                "senses", "AP", "APP");
        System.out.format("%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\n",
                "---", "-----", "-----", "-------", "-------", "-------",
                "------", "--", "---");
        for (POS pos : POS.values()) {
            totalUnknownInstanceCount += uniqueUnknownInstanceCount.get(pos);
            totalMonosemousInstanceCount += uniqueMonosemousInstanceCount
                    .get(pos);
            totalPolysemousInstanceCount += uniquePolysemousInstanceCount
                    .get(pos);
            totalPolysemousSenseCount += uniquePolysemousSenseCount.get(pos);
            putCorpusStats(pos.toString(), uniqueUnknownInstanceCount.get(pos),
                    uniqueMonosemousInstanceCount.get(pos),
                    uniquePolysemousInstanceCount.get(pos),
                    uniquePolysemousSenseCount.get(pos));
        }
        putCorpusStats("all", totalUnknownInstanceCount,
                totalMonosemousInstanceCount, totalPolysemousInstanceCount,
                totalPolysemousSenseCount);

        if (showLegend) {
            System.out.println("\nLegend\n======\n");
            System.out.println("total  : total instances");
            System.out
                    .println("known  : instances of terms found in the sense inventory");
            System.out
                    .println("unknown: instances of terms not found in the sense inventory");
            System.out.println("monosem: instances of monosemous terms");
            System.out.println("polysem: instances of polysemous terms");
            System.out
                    .println("senses : number of polysemous term/sense pairings");
            System.out
                    .println("AP     : average polysemy excluding unknown terms");
            System.out
                    .println("APP    : average polysemy excluding unknown and monosemous terms");
        }
    }

    private void putCorpusStats(String pos, int unknownInstanceCount,
            int monosemousInstanceCount, int polysemousInstanceCount,
            int polysemousSenseCount)
    {
        System.out.format("%7s\t", pos);
        System.out.format("%7s\t", unknownInstanceCount
                + monosemousInstanceCount + polysemousInstanceCount);
        System.out.format("%7s\t", monosemousInstanceCount
                + polysemousInstanceCount);
        System.out.format("%7d\t", unknownInstanceCount);
        System.out.format("%7d\t", monosemousInstanceCount);
        System.out.format("%7d\t", polysemousInstanceCount);
        System.out.format("%7d\t", polysemousSenseCount);
        System.out.format("%7.4f\t",
                (double) (polysemousSenseCount + monosemousInstanceCount)
                        / (polysemousInstanceCount + monosemousInstanceCount));
        System.out.format("%7.4f\n", (double) polysemousSenseCount
                / polysemousInstanceCount);
    }

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        uniqueInstances = new HashSet<Pair<String, POS>>();
        polysemousInstanceCount = new HashMap<POS, Integer>();
        uniquePolysemousInstanceCount = new HashMap<POS, Integer>();
        monosemousInstanceCount = new HashMap<POS, Integer>();
        uniqueMonosemousInstanceCount = new HashMap<POS, Integer>();
        unknownInstanceCount = new HashMap<POS, Integer>();
        uniqueUnknownInstanceCount = new HashMap<POS, Integer>();
        polysemousSenseCount = new HashMap<POS, Integer>();
        uniquePolysemousSenseCount = new HashMap<POS, Integer>();
        for (POS pos : POS.values()) {
            polysemousInstanceCount.put(pos, Integer.valueOf(0));
            uniquePolysemousInstanceCount.put(pos, Integer.valueOf(0));
            monosemousInstanceCount.put(pos, Integer.valueOf(0));
            uniqueMonosemousInstanceCount.put(pos, Integer.valueOf(0));
            unknownInstanceCount.put(pos, Integer.valueOf(0));
            uniqueUnknownInstanceCount.put(pos, Integer.valueOf(0));
            polysemousSenseCount.put(pos, Integer.valueOf(0));
            uniquePolysemousSenseCount.put(pos, Integer.valueOf(0));
        }
    }

}
