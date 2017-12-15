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

package de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.SimplifiedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NoNormalization;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.SetOverlap;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.StringSplit;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.TestSenseInventory;


public class SimplifiedLeskTest
{
    SenseInventory inventory = new TestSenseInventory();
	SimplifiedLesk l = new SimplifiedLesk(inventory,
	        new SetOverlap(),
	        new NoNormalization(),
	        new StringSplit(),
	        new StringSplit()
	);
    String[] contextWords1 = {"bat", "bank", "test"};
    POS[] contextPos1 = {POS.NOUN, POS.NOUN, POS.NOUN};
    String[] sodSenses2 = {"foo foo foo", "bar", "baz"};
    String[][] contextSenses2 = {{"foo foo foo"}, {"bar"}, {"bar baz"}};

    @Test
    public void leskTest() throws SenseInventoryException {
        l.setSenseInventory(inventory);
        Map<String, Double> senseMap;

        // Find the sense of "bank" which best matches the context string "financial institution"
        senseMap = l.getDisambiguation("bank", POS.NOUN, "financial institution");
        assertEquals(1, senseMap.size());
        assertEquals(1.0, senseMap.get("bank1"), 0.001);

        // Find the sense of "bank" which best matches the context string "river otter"
        senseMap = l.getDisambiguation("bank", POS.NOUN, "river otter");
        assertEquals(1, senseMap.size());
        assertEquals(1.0, senseMap.get("bank2"), 0.001);

        // Find the sense of "bank" which best matches the context string "financial institution"
        senseMap = l.getDisambiguation("bank", "financial institution");
        assertEquals(1, senseMap.size());
        assertEquals(1.0, senseMap.get("bank1"), 0.001);

        // Find the sense of "bank" which best matches the context string "river otter"
        senseMap = l.getDisambiguation("bank", "river otter");
        assertEquals(1, senseMap.size());
        assertEquals(1.0, senseMap.get("bank2"), 0.001);
}

	@Test
    public void contextWordsNotFoundTest() throws SenseInventoryException {
        l.setSenseInventory(inventory);
        Map<String, Double> senseMap = l.getDisambiguation("bank", POS.NOUN, "none of these terms are in the sense inventory");
        assertEquals(0, senseMap.size());
    }
}
