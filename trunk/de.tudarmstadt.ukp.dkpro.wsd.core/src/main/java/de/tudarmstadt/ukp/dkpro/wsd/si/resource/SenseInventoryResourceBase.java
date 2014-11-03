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

package de.tudarmstadt.ukp.dkpro.wsd.si.resource;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseAlignment;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import edu.uci.ics.jung.graph.UndirectedGraph;

// TODO: Split the SenseTaxonomy and SenseAlignment-specific methods and fields
// into those concrete classes which actually use them

/**
 * An abstract resource wrapping sense inventories
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public abstract class SenseInventoryResourceBase
    extends Resource_ImplBase
    implements SenseInventory, SenseTaxonomy, SenseAlignment
{

    protected SenseInventory inventory;

    public final static String PARAM_SENSE_INVENTORY_NAME = "senseInventoryName";
    @ConfigurationParameter(name = PARAM_SENSE_INVENTORY_NAME, mandatory = false, description = "An optional name to override that provided by the sense inventory")
    protected String senseInventoryName;

    public final static String PARAM_GRAPH_URL = "graphUrl";
    @ConfigurationParameter(name = PARAM_GRAPH_URL, mandatory = false, description = "The URL of a serialized sense graph to read in")
    protected String graphUrl;

    @SuppressWarnings("unchecked")
    @Override
    public void afterResourcesInitialized() throws ResourceInitializationException
    {
        super.afterResourcesInitialized();

        if (!(inventory instanceof SenseTaxonomy)) {
            return;
        }

        if (graphUrl == null) {
            return;
        }

        try {
            URL url = ResourceUtils.resolveLocation(graphUrl, this, null);
            InputStream urlInputStream = url.openStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    urlInputStream);
            UndirectedGraph<String, UnorderedPair<String>> g;
            g = (UndirectedGraph<String, UnorderedPair<String>>) objectInputStream
                    .readObject();
            objectInputStream.close();
            urlInputStream.close();
            ((SenseTaxonomy) inventory).setUndirectedGraph(g);

        }
        catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public Set<String> getSenseNeighbours(String senseId)
        throws SenseInventoryException, UnsupportedOperationException
    {
        if (inventory instanceof SenseTaxonomy) {
            return ((SenseTaxonomy) inventory).getSenseNeighbours(senseId);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Set<String> getSenseAlignments(String senseId)
        throws SenseInventoryException, UnsupportedOperationException
    {
        if (inventory instanceof SenseAlignment) {
            return ((SenseAlignment) inventory).getSenseAlignments(senseId);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        return inventory.getSenseInventory();
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        return inventory.getSenses(sod);
    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException
    {
        return inventory.getSenses(sod, pos);
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws UnsupportedOperationException, SenseInventoryException
    {
        return inventory.getMostFrequentSense(sod);
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws UnsupportedOperationException, SenseInventoryException
    {
        return inventory.getMostFrequentSense(sod, pos);
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        return inventory.getSenseDescription(senseId);
    }

    @Override
    public POS getPos(String senseId)
        throws SenseInventoryException
    {
        return inventory.getPos(senseId);
    }

    @Override
    public int getUseCount(String senseId)
        throws SenseInventoryException
    {
        return inventory.getUseCount(senseId);
    }

    @Override
    public String getSenseInventoryName()
    {
        if (senseInventoryName == null) {
            return inventory.getSenseInventoryName();
        }
        else {
            return senseInventoryName;
        }
    }

    @Override
    public UndirectedGraph<String, UnorderedPair<String>> getUndirectedGraph()
        throws SenseInventoryException
    {
        if (inventory instanceof SenseTaxonomy) {
            return ((SenseTaxonomy) inventory).getUndirectedGraph();
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setUndirectedGraph(
            UndirectedGraph<String, UnorderedPair<String>> graph)
        throws SenseInventoryException, UnsupportedOperationException
    {
        if (inventory instanceof SenseTaxonomy) {
            ((SenseTaxonomy) inventory).setUndirectedGraph(graph);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

}
