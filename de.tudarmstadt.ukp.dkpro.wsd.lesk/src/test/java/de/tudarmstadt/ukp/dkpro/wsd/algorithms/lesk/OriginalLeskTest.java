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

//package de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//
//import java.util.Arrays;
//import java.util.Map;
//
//import org.junit.Test;
//
//import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.normalization.NoNormalization;
//import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.overlap.PairedOverlap;
//import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.overlap.SetOverlap;
//import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.tokenization.StringSplit;
//import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
//import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
//import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
//import de.tudarmstadt.ukp.dkpro.wsd.si.TestSenseInventory;
//
//
//public class OriginalLeskTest
//{
//    SenseInventory inventory = new TestSenseInventory();
//    String[] contextWords1 = {"bat", "bank", "test"};
//    POS[] contextPos1 = {POS.NOUN, POS.NOUN, POS.NOUN};
//    String[] sodSenses2 = {"foo foo foo", "bar", "baz"};
//    String[][] contextSenses2 = {{"foo foo foo"}, {"bar"}, {"bar baz"}};
//
//	@Test
//    public void leskTest() throws SenseInventoryException {
//        OriginalLesk l1 = new OriginalLesk(inventory,
//                new SetOverlapa(),
//                new NoNormalization(),
//                new StringSplit()
//        );
//
//        OriginalLesk l2 = new OriginalLesk(inventory,
//                new PairedOverlap(),
//                new NoNormalization(),
//                new StringSplit()
//        );
//
//        l1.setSenseInventory(inventory);
//        l2.setSenseInventory(inventory);
//        Map<String, Double> senseMap;
//        assertEquals(1, l1.lesk(sodSenses2, contextSenses2));
//        assertEquals(0, l2.lesk(sodSenses2, contextSenses2));
//
//        // Find the sense of "bank" which best matches the senses of the word "bat"
//        senseMap = l2.getDisambiguation("bank", POS.NOUN, new String[] {"bat"}, new POS[] {POS.NOUN});
//        assertEquals(1, senseMap.size());
//        assertEquals(1.0, senseMap.get("bank2"), 0.001);
//
//        // Find the sense of "bank" which best matches the senses of the word "test"
//        senseMap = l2.getDisambiguation("bank", POS.NOUN, new String[] {"test"}, new POS[] {POS.NOUN});
//        assertEquals(1, senseMap.size());
//        assertEquals(1.0, senseMap.get("bank1"), 0.001);
//
//        // Find the sense of "bank" which best matches the senses of the word "bat"
//        senseMap = l2.getDisambiguation("bank", new String[] {"bat"});
//        assertEquals(1, senseMap.size());
//        assertEquals(1.0, senseMap.get("bank2"), 0.001);
//
//        // Find the sense of "bank" which best matches the senses of the word "test"
//        senseMap = l2.getDisambiguation("bank", new String[] {"test"});
//        assertEquals(1, senseMap.size());
//        assertEquals(1.0, senseMap.get("bank1"), 0.001);
//}
//
//	@Test
//    public void sodNotFoundTest() throws SenseInventoryException {
//        OriginalLesk l = new OriginalLesk(inventory,
//                new PairedOverlap(),
//                new NoNormalization(),
//                new StringSplit()
//        );
//        l.setSenseInventory(inventory);
//    	assertNull(l.getDisambiguation("this term isn't in the inventory", POS.NOUN, contextWords1, contextPos1));
//    }
//
//	@Test(expected=IllegalArgumentException.class)
//    public void contextMismatchTest() throws SenseInventoryException {
//        OriginalLesk l = new OriginalLesk(inventory,
//                new PairedOverlap(),
//                new NoNormalization(),
//                new StringSplit()
//        );
//    	l.getDisambiguation(null, null, Arrays.copyOfRange(contextWords1, 0, 1), contextPos1);
//    }
//
//}
