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

package de.tudarmstadt.ukp.dkpro.wsd.si.lsr;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.si.POS;

public class LsrSenseInventoryTest
{

    @Ignore
    @Test
    public void lsrSenseInventoryTest() throws Exception {

        LsrSenseInventory lsrSI = new LsrSenseInventory("wordnet", "en");

        List<String> senses = lsrSI.getSenses("car");

        assertEquals(5, senses.size());

        for (String sense : senses) {
            System.out.println("ID: " + sense);
            System.out.println("Description: " + lsrSI.getSenseDescription(sense));
            System.out.println("Neighbours: " + lsrSI.getSenseNeighbours(sense));
            System.out.println();
        }

        String mfsMine = lsrSI.getMostFrequentSense("mine");
        assertEquals("mine#3768346|---n", mfsMine);

        String mfsMineNoun = lsrSI.getMostFrequentSense("mine", POS.NOUN);
        assertEquals("mine#3768346|---n", mfsMineNoun);

        String mfsMineVerb = lsrSI.getMostFrequentSense("mine", POS.VERB);
        assertEquals("mine#1163620|---v", mfsMineVerb);
    }
}
