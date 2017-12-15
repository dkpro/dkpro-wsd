/**
 * Copyright 2017
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import net.didion.jwnl.JWNLException;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.ResourceLoaderException;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmDocumentTextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.LsrSenseInventory;

public class ImsWsdDisambiguatorTest {

    /**
     * Example how to use IMS WSD
     * To run this example you need to install WordNet and install libraries from IMS.
     * You'll find allr requires libraries at http://www.comp.nus.edu.sg/~nlp/sw/lib.tar.gz.
     * Extract contents to the folder src/main/resources/ims/lib/ 
     * @throws SenseInventoryException
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws IOException 
     * @throws JWNLException 
     */
	@Test
	@Ignore
	public void imsWsdDisambiguatorTest() throws SenseInventoryException, JWNLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        SenseInventory inventory;
        try {
            inventory = new LsrSenseInventory("wordnet", "en");
        } catch (ResourceLoaderException e) {
            throw new SenseInventoryException(e);
        }
        
        WSDAlgorithmDocumentTextBasic wsdAlgo = new ImsWsdDisambiguator(inventory);

        String input = "I got money in the bank. Lucie sits on the bank in the park.";
        String output = "I 2x40x00xx 1x21x00xx in the 1x14x00xx .\nLucie 2x35x00xx on the 1x14x00xx in the 1x15x00xx .\n";

        assertEquals(output,wsdAlgo.getDisambiguation(input));
	}
}