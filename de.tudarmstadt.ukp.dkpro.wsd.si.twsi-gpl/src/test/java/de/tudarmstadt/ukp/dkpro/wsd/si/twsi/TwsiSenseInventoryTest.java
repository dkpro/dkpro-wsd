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
package de.tudarmstadt.ukp.dkpro.wsd.si.twsi;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class TwsiSenseInventoryTest
{
    @Ignore
    @Test
    public void run()
        throws Exception
    {
        TwsiSenseInventoryBase si = new TwsiSenseInventoryBase(
                "/home/miller/src/TWSISubstituter/conf/TWSI2_config.conf");

        // Checking sense descriptions
        assertEquals(si.getSenseDescription("present@@3"),
                "demonstrate, introduce, display, show");
        assertEquals(
                si.getSenseDescription("prime@@5"),
                "peak, peak audience, peak viewing, elite, important, main, most important, primary, prime time television");

        // Checking most frequent sense
        assertEquals(si.getMostFrequentSense("present"), "present@@1");
        assertEquals(si.getMostFrequentSense("prime"), "prime@@3");

        // Checking all senses
        List<String> senses = new ArrayList<String>();
        senses.add("present@@1");
        senses.add("present@@2");
        senses.add("present@@3");
        senses.add("present@@4");
        assertEquals(si.getSenses("present"), senses);

        senses = new ArrayList<String>();
        senses.add("prime@@1");
        senses.add("prime@@2");
        senses.add("prime@@3");
        senses.add("prime@@5");
        assertEquals(si.getSenses("prime"), senses);
    }
}
