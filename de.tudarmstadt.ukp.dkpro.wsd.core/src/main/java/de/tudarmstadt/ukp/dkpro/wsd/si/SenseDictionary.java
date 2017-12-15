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

/**
 * An interface for sense inventories which provide dictionary-style information
 * about senses, such as definitions, word forms, and example sentences
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public interface SenseDictionary
    extends SenseInventory
{
    /**
     * Get a set of example sentences for the given sense. If no examples exist,
     * an empty collection is returned.
     *
     * @param senseId
     *            The ID of the sense to examine.
     * @return A set of example sentences for the given sense.
     * @throws SenseInventoryException
     */
    Set<String> getSenseExamples(String senseId)
        throws SenseInventoryException;

    /**
     * Get a set of lemmas for the given sense. If no lemmas exist, an empty
     * collection is returned.
     *
     * @param senseId
     *            The ID of the sense to examine.
     * @return The set of lemmas for the given sense
     * @throws SenseInventoryException
     */
    Set<String> getSenseWords(String senseId)
        throws SenseInventoryException;

    /**
     * Get the definition of the given sense. If no definition exists, null is
     * returned.
     *
     * @param senseId
     *            The ID of the sense to examine.
     * @return The definition of the given sense.
     * @throws SenseInventoryException
     */
    String getSenseDefinition(String senseId)
        throws SenseInventoryException;

    /**
     * For sense inventories implementing SenseDictionary it is often expensive
     * to compute the sense's definition, synonyms, and example sentences. This
     * interface is intended to help such methods cache their sense details so
     * that they need be computed only once.
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    interface CachedDictionarySense
    {
        Set<String> getSynonyms() throws SenseInventoryException;

        Set<String> getExamples() throws SenseInventoryException;

        String getDefinition() throws SenseInventoryException;
    }
}
