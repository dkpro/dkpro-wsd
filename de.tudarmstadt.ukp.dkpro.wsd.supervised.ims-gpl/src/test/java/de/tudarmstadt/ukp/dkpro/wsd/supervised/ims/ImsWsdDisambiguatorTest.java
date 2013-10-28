/**
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
 */
package de.tudarmstadt.ukp.dkpro.wsd.supervised.ims;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.ResourceLoaderException;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.WSDAlgorithmDocumentBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.LsrSenseInventory;

public class ImsWsdDisambiguatorTest {

    @Test
    @Ignore
    public void imsWsdDisambiguatorTest() throws SenseInventoryException {
        SenseInventory inventory;
        try {
            inventory = new LsrSenseInventory("wordnet", "en");
        } catch (ResourceLoaderException e) {
            throw new SenseInventoryException(e);
        }
        
        WSDAlgorithmDocumentBasic wsdAlgo = new ImsWsdDisambiguator(inventory);

        String input = "I got money in the bank.";
        String output = "{I 2x40x00xx 1x21x00xx in the 1x14x00xx .\n=1.0}";
        
        Assert.assertEquals(output, wsdAlgo.getDisambiguation(input).toString());
    }
}