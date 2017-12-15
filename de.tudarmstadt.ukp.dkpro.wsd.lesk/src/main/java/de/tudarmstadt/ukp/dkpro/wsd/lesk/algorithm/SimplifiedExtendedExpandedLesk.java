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

package de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NormalizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.OverlapStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.AbstractLexicalExpander;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;

/**
 * A version of {@link SimplifiedExtendedLesk} whose tokenizer performs lexical
 * expansions
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class SimplifiedExtendedExpandedLesk
    extends SimplifiedExpandedLesk
{

    protected SenseTaxonomy inventory;

    public SimplifiedExtendedExpandedLesk(SenseTaxonomy inventory,
            OverlapStrategy overlapStrategy,
            NormalizationStrategy normalizationStrategy,
            AbstractLexicalExpander senseTokenizationStrategy,
            AbstractLexicalExpander contextTokenizationStrategy)
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

        for (String neighbour : inventory.getSenseNeighbours(sense)) {
            extendedSenseDescription.append('\n');
            extendedSenseDescription.append(inventory
                    .getSenseDescription(neighbour));
        }
        return extendedSenseDescription.toString();
    }

}
