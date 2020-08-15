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

package de.tudarmstadt.ukp.dkpro.wsd.si;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class SenseInventoryBase
    implements SenseInventory
{
    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        List<String> senses = new ArrayList<String>();
        for (POS pos : POS.values()) {
            senses.addAll(getSenses(sod, pos));
        }
        return senses;
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        int maxUseCount = Integer.MIN_VALUE;
        String mostFrequentSense = null;
        for (String sense : getSenses(sod)) {
            int useCount = getUseCount(sense);
            if (maxUseCount < useCount) {
                maxUseCount = useCount;
                mostFrequentSense = sense;
            }
        }
        return mostFrequentSense;
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        int maxUseCount = Integer.MIN_VALUE;
        String mostFrequentSense = null;
        for (String sense : getSenses(sod, pos)) {
            int useCount = getUseCount(sense);
            if (maxUseCount < useCount) {
                maxUseCount = useCount;
                mostFrequentSense = sense;
            }
        }
        return mostFrequentSense;
    }
    
    @Override
    public String getSenseDescription(String senseId,boolean seclang)
        throws SenseInventoryException
    {
    	return  getSenseDescription(senseId);
    }
    
}
