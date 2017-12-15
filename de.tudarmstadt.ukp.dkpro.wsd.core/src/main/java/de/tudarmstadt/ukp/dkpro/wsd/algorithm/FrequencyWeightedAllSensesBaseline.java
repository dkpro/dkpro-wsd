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

package de.tudarmstadt.ukp.dkpro.wsd.algorithm;

import java.util.Map;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseWeightedInventory;

/**
 * A word sense disambiguation algorithm which, given a subject of
 * disambiguation, looks up all the candidate senses in the sense inventory and
 * chooses all of them, weighting them according to their score. This approach is
 * more useful than the all senses baseline as its results are weighted.
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 *
 */
public class FrequencyWeightedAllSensesBaseline
    extends AbstractWSDAlgorithm
    implements WSDAlgorithmIndividualBasic
{

    public FrequencyWeightedAllSensesBaseline(SenseInventory inventory)
    {
        super(inventory);
    }

    @Override
    public Map<String, Double> getDisambiguation(String sod)
        throws SenseInventoryException
    {
//        System.out.println(sod + "\t" + ((SenseWeightedInventory)inventory).getWeightedSenses(sod));
        return ((SenseWeightedInventory)inventory).getWeightedSenses(sod);
    }
}