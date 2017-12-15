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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.Lesk;

public class LeskTest
{
    List<String> s1 = Arrays.asList("foo bar bar".split(" "));
    List<String> s2 = Arrays.asList("foo baz bar quux quux fizz bar wozz bizz bar".split(" "));
    List<String> s3 = new ArrayList<String>();
    List<String> s4 = Arrays.asList(new String[] {"foo"});

    @Test
	public void magnitudeTest() {
    	assertEquals(2.236, Lesk.magnitude(s1), 0.001);
    	assertEquals(4.243, Lesk.magnitude(s2), 0.001);
    	assertEquals(0.000, Lesk.magnitude(s3), 0.001);
    	assertEquals(1.000, Lesk.magnitude(s4), 0.001);
    }

    @Test
	public void frequencyMatrixTest() {
    	Map<Object, Integer[]> m = Lesk.frequencyMatrix(new String[][] {(String[]) s1.toArray(), (String[]) s2.toArray()});
    	assertEquals(7, m.size());
    	assertNull(m.get("ferret"));
    	Integer[] i = m.get("bar");
    	assertNotNull(i);
    	assertEquals(2, i.length);
    	assertEquals(2, (int) i[0]);
    	assertEquals(3, (int) i[1]);
    }

}
