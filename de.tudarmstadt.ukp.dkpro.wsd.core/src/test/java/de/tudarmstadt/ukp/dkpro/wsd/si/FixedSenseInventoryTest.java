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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class FixedSenseInventoryTest
{

    @Test
    public void testReturnValues()
        throws SenseInventoryException, UnsupportedOperationException
    {
        final String ID = "U";
        final String DESC = "unknown";
        List<String> senses;

        FixedSenseInventory inventory = new FixedSenseInventory(ID, DESC);

        assertEquals(ID, inventory.getMostFrequentSense("foo"));
        assertEquals(ID, inventory.getMostFrequentSense("bar"));
        assertEquals(ID, inventory.getMostFrequentSense("foo", POS.ADJ));
        assertEquals(ID, inventory.getMostFrequentSense("bar", POS.ADJ));

        senses = inventory.getSenses("foo");
        assertNotNull(senses);
        assertEquals(1, senses.size());
        assertEquals(ID, senses.get(0));

        senses = inventory.getSenses("foo", POS.ADJ);
        assertNotNull(senses);
        assertEquals(1, senses.size());
        assertEquals(ID, senses.get(0));

        assertEquals(DESC, inventory.getSenseDescription(ID));
    }

}
