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

package de.tudarmstadt.ukp.dkpro.wsd.si;

import java.util.List;
import java.util.Map;

/**
 * A basic interface for sense inventories which map subjects of disambiguation
 * (usually lemmas) to sense IDs, and return various types of information on
 * senses
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public interface SenseInventory
{

    /**
     * Attention: For most sense inventories this operation will be rather slow.
     *
     * @return A map of all (subject of disambiguation / senseId) pairs in the
     *         sense inventory.
     * @throws SenseInventoryException
     */
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException;

    /**
     * @param sod
     *            The subject of disambiguation (i.e. the surface string).
     * @return The list of senses associated with this SoD in the sense
     *         inventory, or an empty collection if their are no senses for this
     *         SoD.
     * @throws SenseInventoryException
     */
    public List<String> getSenses(String sod)
        throws SenseInventoryException;

    /**
     * @param sod
     *            The subject of disambiguation (i.e. the surface string).
     * @param pos
     *            The part of speech of the sod.
     * @return The list of senses associated with this SoD in the sense
     *         inventory, or an empty collection if their are no senses for this
     *         SoD.
     * @throws SenseInventoryException
     */
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException;

    /**
     * @param sod
     *            The subject of disambiguation (i.e. the surface string).
     * @return The most frequent sense for the SoD, if known. In the event of a
     *         tie in sense frequencies, only one is returned. Null is returned
     *         if the SoD exists in the sense inventory but no frequency
     *         information is available for it. Null is returned in the SoD does
     *         not exist in the sense inventory. Throws
     *         {@link UnsupportedOperationException} if the sense inventory does
     *         not provide most frequent sense information at all.
     * @throws SenseInventoryException
     * @throws UnsupportedOperationException
     */
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException;

    /**
     * @param sod
     *            The subject of disambiguation (i.e. the surface string).
     * @param pos
     *            The part of speech of the sod.
     * @return The most frequent sense for the SoD, or null if there is no sense
     *         for this SoD. Throws {@link UnsupportedOperationException} if the
     *         sense inventory has no way to determine the most frequent sense
     *         of a SoD.
     * @throws SenseInventoryException
     * @throws UnsupportedOperationException
     */
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException;

    /**
     * @param senseId
     * @return A human readable description of the current sense represented by
     *         its senseId or an empty string if no description is available. A
     *         {@link SenseInventoryException} is thrown if the senseId is
     *         invalid.
     * @throws SenseInventoryException
     */
    public String getSenseDescription(String senseId)
        throws SenseInventoryException;

    /**
     * @param senseId
     * @return The part of speech of the sense represented by the given senseId.
     *         A {@link SenseInventoryException} is thrown if the senseId is
     *         invalid.
     * @throws SenseInventoryException
     */
    public POS getPos(String senseId)
        throws SenseInventoryException;

    /**
     * @return The name of the sense inventory.
     */
    public String getSenseInventoryName();
}
