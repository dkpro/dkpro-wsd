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
package de.tudarmstadt.ukp.dkpro.wsd.wsi.si;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.tudarmstadt.ukp.dkpro.wsd.WSDException;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

public class InducedSenseInventory
    implements SenseInventory
{
    /**
     * Associates the list of sense-ids to every lexical item in the inventory
     * 
     */
    private final Map<String, List<String>> inventory = new TreeMap<String, List<String>>();

    /**
     * List of related terms per sense-id
     * 
     */
    private final Map<String, List<String>> clusters = new TreeMap<String, List<String>>();;

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        return inventory;
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        sod = sod.toLowerCase().replace('_', ' ');

        if (!inventory.containsKey(sod))
            // throw new SenseInventoryException("the item " + sod +
            // " is not part of this inventory");
            return new LinkedList<String>();
        else

            return inventory.get(sod);

    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return getSenses(sod);
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        List<String> senses = getSenses(sod);
        int max = 0;
        String result = null;
        for (String sense : senses) {
            Collection<String> cluster = clusters.get(sense);
            if (max < cluster.size()) {
                max = cluster.size();
                result = sense;
            }
        }
        return result;

    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException();

    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        final StringBuilder resBuilder = new StringBuilder();
        if (clusters.containsKey(senseId)) {
            for (final String s : clusters.get(senseId)) {
                resBuilder.append(s + " ");
            }
        }
        else {
            return null;
        }
        return resBuilder.toString().trim();
    }

    @Override
    public String getSenseInventoryName()
    {
        return "WSI";
    }

    /**
     * Adds a new sense to the inventory
     * 
     * @param sod
     * @param senseId
     * @param cluster
     */
    public void addSense(String sod, String senseId, Collection<String> cluster)
    {
        if (!inventory.containsKey(sod))
            inventory.put(sod, new LinkedList<String>());
        inventory.get(sod).add(senseId);
        if (!clusters.containsKey(senseId))
            clusters.put(senseId, new LinkedList<String>());
        clusters.get(senseId).addAll(cluster);

    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Induced Inventory, ");
        sb.append(inventory.keySet().size());
        sb.append(" terms,");
        sb.append(clusters.keySet().size());
        sb.append(" sensekeys");
        return sb.toString();
    }

    public void writeInventory(String file)
    {
        // TODO Auto-generated method stub

    }

    /**
     * Loads a serialized sense inventory from a json file
     * 
     * @param fileName
     * @throws WSDException
     */
    public void loadInventory(String fileName)
        throws WSDException
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
            JsonNode root = mapper.readTree(fileReader);
            Iterator<JsonNode> it = root.elements();
            while (it.hasNext()) {
                JsonNode termNode = it.next();
                if (termNode.has("term")) {
                    String term = termNode.get("term").textValue();
                    Iterator<JsonNode> it2 = termNode.get("clusters").elements();
                    int senseIndex = 0;
                    while (it2.hasNext()) {
                        String senseId = term + "_" + senseIndex++;
                        JsonNode clusterNode = it2.next();
                        Iterator<JsonNode> it3 = clusterNode.elements();
                        Collection<String> cluster = new LinkedList<String>();
                        while (it3.hasNext()) {

                            Iterator<JsonNode> it4 = it3.next().elements();
                            while (it4.hasNext()) {
                                JsonNode wordNode = it4.next();
                                cluster.add(wordNode.textValue());
                            }

                        }
                        addSense(term, senseId, cluster);

                    }

                }
            }
            System.out.println("loaded " + inventory.size() + " terms and " + clusters.size()
                    + " sense clusters");
        }
        catch (Exception e) {
            throw new WSDException(e);
        }

    }
}
