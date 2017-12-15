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
package de.tudarmstadt.ukp.dkpro.wsd.si;

import java.util.Set;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * An interface for sense inventories which encode relationships between senses
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public interface SenseTaxonomy
    extends SenseInventory
{
    /**
     *
     * @return An undirected graph representing the sense inventory
     */
    UndirectedGraph<String, UnorderedPair<String>> getUndirectedGraph()
        throws SenseInventoryException, UnsupportedOperationException;

    /**
    * Sets an undirected graph representing the sense inventory. This method
    * is useful for loading serialized graphs which are expensive to construct
    * at run-time.
    *
    * @param graph  The graph to set.
    *
    */
   void setUndirectedGraph(UndirectedGraph<String, UnorderedPair<String>> graph)
       throws SenseInventoryException, UnsupportedOperationException;

    /**
     *
     * @param senseId
     * @return A set of sense IDs of senses related to the given sense, or an
     *         empty collection of there are no related senses.
     * @throws SenseInventoryException
     * @throws UnsupportedOperationException
     */
    Set<String> getSenseNeighbours(String senseId)
        throws SenseInventoryException, UnsupportedOperationException;

    /**
     * For sense inventories implementing SenseTaxonomy it is often expensive to
     * compute the sense's neighbours. This interface is intended to help such
     * methods cache their sense details so that they need be computed only
     * once.
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    interface CachedTaxonomySense
    {
        Set<String> getNeighbours() throws SenseInventoryException;
    }

}
