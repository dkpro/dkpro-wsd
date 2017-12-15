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
package de.tudarmstadt.ukp.dkpro.wsd.supervised.twsi;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.si.twsi.TwsiSenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.twsi.TwsiSenseInventoryBase;

public class TwsiWsdDisambiguatorTest
{
    // TODO: It looks like the format of the return string of
    // getDisambiguation() has no relation to the values expected by the
    // asserts. Probably the API changed at some point but this test never got
    // updated.

    @Ignore
	@Test
	public void run()
		throws Exception
	{
		TwsiSenseInventory si = new TwsiSenseInventoryBase("/home/miller/src/TWSISubstituter/conf/TWSI2_config.conf");

		TwsiWsdDisambiguator disamb = new TwsiWsdDisambiguator(si);


		// Sentence 1
		Map<String, Double> expResult = new HashMap<String, Double>();
		expResult.put("capital@@3", 0.5);
		String result = disamb.getDisambiguation("The company manages over 10 million dollars in capital assets.");
		assertEquals(result, expResult);

		// Sentence 2
		expResult = new HashMap<String, Double>();
		expResult.put("capital@@5", 0.5);
		result = disamb.getDisambiguation("When imposing capital punishment, the state becomes a murder of sorts.");
		assertEquals(result, expResult);

		// Sentence 3
		expResult = new HashMap<String, Double>();
		expResult.put("capital@@1", 0.5);
		result = disamb.getDisambiguation("Capital cities are sometimes deliberately planned by government to house the seat of government of the nation or subdivision.");
		assertEquals(result, expResult);

		// Some output
		for (String sense : si.getSenses("capital"))
		{
			System.out.println(sense + ": " + si.getSenseDescription(sense));
		}
	}
}
