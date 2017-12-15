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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.MostFrequentSenseBaseline;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmIndividualBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.TestSenseInventory;

public class MostFrequentSenseBaselineTest {

    @Test
    public void mfsBaselineTest() throws SenseInventoryException {
        SenseInventory inventory = new TestSenseInventory();

        WSDAlgorithmIndividualBasic wsdAlgo = new MostFrequentSenseBaseline(inventory);

        assertEquals(1, wsdAlgo.getDisambiguation("bank").size());
        assertEquals(1, wsdAlgo.getDisambiguation("bat").size());
        assertEquals(1, wsdAlgo.getDisambiguation("test").size());
        assertEquals(null, wsdAlgo.getDisambiguation("humpelgrpf"));

        assertEquals(1.0, wsdAlgo.getDisambiguation("bank").get("bank1"), 0.000001);
        assertEquals(1.0, wsdAlgo.getDisambiguation("bat").get("bat1"), 0.000001);
        assertEquals(1.0, wsdAlgo.getDisambiguation("test").get("test1"), 0.000001);
    }
}