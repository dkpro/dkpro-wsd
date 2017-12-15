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

package de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.overlap;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.DotProduct;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.OverlapStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.PairedOverlap;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.SetOverlap;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class OverlapTests
{
    List<String> s1 = Arrays.asList("foo bar bar".split(" "));
    List<String> s2 = Arrays.asList("foo baz bar quux quux fizz bar wozz bizz".split(" "));
    List<String> s3 = new ArrayList<String>();
    final static double DELTA = 0.001;

    OverlapStrategy o;

    @Test
	public void pairedOverlapTest() {
    	o = new PairedOverlap();
    	assertEquals(3, o.overlap(s1, s2), DELTA);
    	assertEquals(0, o.overlap(s1, s3), DELTA);
    	assertEquals(0, o.overlap(s3, s1), DELTA);
    }

    @Test
	public void setOverlapTest() {
    	o = new SetOverlap();
    	assertEquals(2, o.overlap(s1, s2), DELTA);
    	assertEquals(0, o.overlap(s1, s3), DELTA);
    	assertEquals(0, o.overlap(s3, s1), DELTA);
    }

    @Test
	public void dotProductTest() {
    	o = new DotProduct();
    	assertEquals(5, o.overlap(s1, s2), DELTA);
    	assertEquals(0, o.overlap(s1, s3), DELTA);
    	assertEquals(0, o.overlap(s3, s1), DELTA);
    }
}
