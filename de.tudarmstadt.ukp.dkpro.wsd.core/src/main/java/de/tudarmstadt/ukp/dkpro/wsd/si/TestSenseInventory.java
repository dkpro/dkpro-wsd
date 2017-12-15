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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple sense inventory with hard-coded entries. Useful for testing
 * purposes.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class TestSenseInventory
    extends SenseInventoryBase
    implements SenseInventory
{

    @SuppressWarnings("serial")
    private static final Map<String, List<String>> senseInventoryMap = new HashMap<String, List<String>>()
    {
        {
            put("bat", Arrays.asList(new String[] { "bat1", "bat2" }));
            put("bank", Arrays.asList(new String[] { "bank1", "bank2" }));
            put("test", Arrays.asList(new String[] { "test1" }));
        }
    };

    @SuppressWarnings("serial")
    private static final Map<String, String> descriptionsmap = new HashMap<String, String>()
    {
        {
            put("bat1", "baseball bat");
            put("bat2", "animal");
            put("bank1", "financial institute");
            put("bank2", "river bank with animal");
            put("test1", "first and only sense of test sense financial");
        }
    };

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        return senseInventoryMap;
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        if (senseInventoryMap.containsKey(sod)) {
            return senseInventoryMap.get(sod);
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException
    {
        return getSenses(sod);
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        List<String> ids = getSenses(sod);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return getMostFrequentSense(sod);
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        if (descriptionsmap.containsKey(senseId)) {
            return descriptionsmap.get(senseId);
        }
        else {
            return "";
        }
    }

    @Override
    public String getSenseInventoryName()
    {
        return this.getClass().getCanonicalName();
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
        if (senseId.equals("bat1")) {
            return 5;
        }
        if (senseId.equals("bat2")) {
            return 4;
        }
        if (senseId.equals("bank1")) {
            return 3;
        }
        if (senseId.equals("bank2")) {
            return 2;
        }
        if (senseId.equals("test1")) {
            return 1;
        }
        throw new SenseInventoryException("Unknown sense ID: " + senseId);
    }

}
