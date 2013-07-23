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

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk;

import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.normalization.NormalizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.overlap.OverlapStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.tokenization.TokenizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseAlignment;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;

/**
 * Disambiguates words using the simplified aligned Lesk algorithm -- i.e., the
 * definitions of the subject of disambiguation, augmented with those of aligned
 * senses, are compared against its context
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 */
public class SimplifiedAlignedLesk
    extends SimplifiedExtendedLesk
{

    /**
     * @param overlapStrategy
     * @param normalizationStrategy
     * @param tokenizationStrategy
     */
    public SimplifiedAlignedLesk(SenseTaxonomy inventory,
            OverlapStrategy overlapStrategy,
            NormalizationStrategy normalizationStrategy,
            TokenizationStrategy senseTokenizationStrategy,
            TokenizationStrategy contextTokenizationStrategy)
    {
        super(inventory, overlapStrategy, normalizationStrategy,
                senseTokenizationStrategy, contextTokenizationStrategy);
        this.inventory = inventory;
    }

    /**
     * Returns the extended sense description for the given sense.
     *
     * @param sense
     *            The sense ID
     * @return The concatenation of the sense description with all those
     *         descriptions of other senses in a semantic relation
     * @throws SenseInventoryException
     */
    @Override
    protected String getSenseDescription(String sense)
        throws SenseInventoryException
    {
        StringBuffer extendedSenseDescription = new StringBuffer(
                inventory.getSenseDescription(sense));

        for (String alignment : ((SenseAlignment) inventory)
                .getSenseAlignments(sense)) {
            extendedSenseDescription.append('\n');
            extendedSenseDescription.append(inventory
                    .getSenseDescription(alignment));
        }
        return extendedSenseDescription.toString();
    }

}
