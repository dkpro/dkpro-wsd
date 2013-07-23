/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package de.tudarmstadt.ukp.dkpro.wsd.supervised.ims;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.ResourceLoaderException;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.WSDAlgorithmDocumentBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.LsrSenseInventory;

public class ImsWsdDisambiguatorTest {

    @Test
    public void imsWsdDisambiguatorTest() throws SenseInventoryException {
        SenseInventory inventory;
        try {
            inventory = new LsrSenseInventory("wordnet", "en");
        } catch (ResourceLoaderException e) {
            throw new SenseInventoryException(e);
        }

        WSDAlgorithmDocumentBasic wsdAlgo = new ImsWsdDisambiguator(inventory);

        System.out.println(wsdAlgo.getDisambiguation("I got money in the bank."));
    }
}