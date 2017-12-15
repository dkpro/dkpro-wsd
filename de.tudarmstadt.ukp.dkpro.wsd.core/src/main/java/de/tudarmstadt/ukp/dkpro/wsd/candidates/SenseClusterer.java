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
package de.tudarmstadt.ukp.dkpro.wsd.candidates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;

/**
 * This class takes a pairwise (or n-wise, for n &lt;= 2) mapping of senses and
 * follows the transitive relations to produce n-way mappings (clusters) of
 * senses
 *
 * @author <a href="mailto:kim@ukp.informatik.tu-darmstadt.de">Jungi Kim</a>
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class SenseClusterer
{
    List<Set<String>> symmetricClosures = new ArrayList<Set<String>>();
    Map<String, Set<String>> membershipMap = new HashMap<String, Set<String>>();
    protected final Map<String, Set<String>> clusterMap = new HashMap<String, Set<String>>();
    private final Logger logger = Logger.getLogger(getClass());
    protected Transformer<String, String> senseTransformer = null;

    @Override
    public String toString()
    {
        return clusterMap.toString();
    }

    public SenseClusterer(String clusterUrl, String delimiterRegex)
        throws IOException
    {
        loadClusters(clusterUrl, delimiterRegex);
    }

    public SenseClusterer(String clusterUrl, String delimiterRegex,
            Transformer<String, String> senseTransformer)
        throws IOException
    {
        this.senseTransformer = senseTransformer;
        loadClusters(clusterUrl, delimiterRegex);
    }

    /**
     * Loads a sense cluster file, where each line is a cluster comprised of a
     * delimited list of sense IDs.
     *
     * @throws IOException
     */
    public void loadClusters(String clusterUrl, String delimiterRegex)
        throws IOException
    {
        logger.info("Loading sense clusters...");
        int numClusters = 0;
        InputStream is = ResourceUtils.resolveLocation(clusterUrl, this, null)
                .openStream();
        String content = IOUtils.toString(is, "UTF-8");
        BufferedReader br = new BufferedReader(new StringReader(content));
        String line;

        while ((line = br.readLine()) != null) {
            String[] lineParts = line.toLowerCase().split(delimiterRegex);
            if (lineParts.length < 2) {
                continue;
            }
            numClusters++;
            insertASet(createCluster(lineParts));
        }

        int numSenses = 0;
        for (Set<String> cluster : symmetricClosures) {
            for (String sense : cluster) {
                numSenses++;
                clusterMap.put(sense, cluster);
            }
        }
        logger.info("Read " + numClusters + " clusters; symmetric closure has "
                + symmetricClosures.size() + " clusters and " + numSenses
                + " senses");
    }

    /**
     * Given an array of sense IDs, returns a {@link Set} of sense IDs. If a
     * sense mapping exists, then apply it before returning the result.
     *
     * @param lineParts
     *            an array containing sense IDs
     * @return a set of sense IDs
     */
    protected Set<String> createCluster(String[] lineParts)
    {
        if (senseTransformer == null) {
            return new TreeSet<String>(Arrays.asList(lineParts));
        }

        Set<String> cluster = new TreeSet<String>();
        for (String sense : lineParts) {
            String synset = senseTransformer.transform(sense);
            if (synset == null) {
                logger.error("No mapping for sense ID " + sense);
                throw new IllegalArgumentException();
            }
            cluster.add(synset);
        }
        return cluster;
    }

    private Set<Set<String>> getAnExistingSetsWithCommonElement(Set<String> aSet)
    {
        Set<Set<String>> existingSetsWithCommonElement = new HashSet<Set<String>>();
        for (String element : aSet) {
            if (membershipMap.containsKey(element)) {
                existingSetsWithCommonElement.add(membershipMap.get(element));
            }
        }
        if (existingSetsWithCommonElement.size() == 0) {
            Set<String> commonSet = new TreeSet<String>();
            symmetricClosures.add(commonSet);
            existingSetsWithCommonElement.add(commonSet);
        }
        return existingSetsWithCommonElement;
    }

    private Set<String> mergeSets(Set<Set<String>> sets)
    {
        Iterator<Set<String>> i = sets.iterator();
        Set<String> mergedSet = i.next();
        if (mergedSet == null) {
            return null;
        }
        while (i.hasNext()) {
            Set<String> anotherSet = i.next();
            for (String element : anotherSet) {
                mergedSet.add(element);
                membershipMap.put(element, mergedSet);
            }
            symmetricClosures.remove(anotherSet);
        }
        return mergedSet;
    }

    private void insertASet(Set<String> aSet)
    {
        Set<Set<String>> commonSets = getAnExistingSetsWithCommonElement(aSet);
        Set<String> mergedSet = mergeSets(commonSets);

        for (String element : aSet) {
            mergedSet.add(element);
            membershipMap.put(element, mergedSet);
        }
    }

    /**
     * Given a sense ID, return its cluster. If the sense ID isn't in a cluster,
     * return a new cluster containing only the sense ID.
     *
     * @param sense
     * @return The cluster containing the given sense
     */
    public Set<String> getCluster(String sense)
    {
        Set<String> cluster = clusterMap.get(sense);
        if (cluster == null) {
            cluster = new TreeSet<String>();
            cluster.add(sense);
            clusterMap.put(sense, cluster);
        }
        return cluster;
    }

    /**
     * Given two sense IDs, determine whether they are in the same cluster.
     *
     * @param sense1
     *            The first sense ID
     * @param sense2
     *            The second sense ID
     * @return true if sense1 and sense2 are in the same cluster.
     */
    public boolean inSameCluster(String sense1, String sense2)
    {
        if (sense1.equals(sense2)) {
            return true;
        }
        Set<String> cluster1 = clusterMap.get(sense1);
        Set<String> cluster2 = clusterMap.get(sense2);
        if (cluster1 == null || cluster2 == null) {
            return false;
        }
        return cluster1.equals(cluster2);
    }

    /**
     * Given a set of sense IDs, determine whether they are all in the same
     * cluster.
     *
     * @return true if all sense IDs are in the same cluster
     */
    public boolean inSameCluster(Set<String> senses)
    {
        if (senses.size() <= 1) {
            return true;
        }
        Set<String> cluster = null;
        for (String sense : senses) {
            if (cluster == null) {
                cluster = clusterMap.get(sense);
                continue;
            }
            if (!cluster.equals(clusterMap.get(sense))) {
                return false;
            }
        }
        return true;
    }

    public void setSenseMap(Transformer<String, String> senseTransformer)
    {
        this.senseTransformer = senseTransformer;
    }

}
