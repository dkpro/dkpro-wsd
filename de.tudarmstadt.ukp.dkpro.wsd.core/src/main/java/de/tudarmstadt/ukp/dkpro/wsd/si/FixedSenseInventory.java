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
import java.util.Map;

/**
 * A simple sense inventory that always returns the same sense ID and
 * description, no matter what word is queried. Useful for testing purposes.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class FixedSenseInventory
    extends SenseInventoryBase
    implements SenseInventory
{

    String fixedSenseId;
    String fixedSenseDescription;
    List<String> senses;

    public FixedSenseInventory(String fixedSenseId, String fixedSenseDescription)
    {
        this.fixedSenseDescription = fixedSenseDescription;
        this.fixedSenseId = fixedSenseId;
        senses = new ArrayList<String>(1);
        senses.add(this.fixedSenseId);
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        return senses;
    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException
    {
        return senses;
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return fixedSenseId;
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return fixedSenseId;
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        if (!fixedSenseId.equals(senseId)) {
            throw new SenseInventoryException(new IllegalArgumentException());
        }
        return fixedSenseDescription;
    }

    @Override
    public String getSenseInventoryName()
    {
        return this.getClass().getCanonicalName();
    }

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public POS getPos(String senseId)
        throws SenseInventoryException
    {
        return POS.NOUN;
    }

    @Override
    public int getUseCount(String senseId)
        throws SenseInventoryException
    {
        return 1;
    }

}
