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

package de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.normalization;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.FirstObjects;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.FirstUniqueObjects;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.MostObjects;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.MostUniqueObjects;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NoNormalization;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NormalizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.ProductMagnitude;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.TotalObjects;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.TotalUniqueObjects;


/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class NormalizationTests
{
    List<String> s1 = Arrays.asList("foo bar bar".split(" "));
    List<String> s2 = Arrays.asList("foo bar baz quux quux fizz wozz bizz".split(" "));
    List<String> s3 = new ArrayList<String>();

    NormalizationStrategy n;

    @Test
	public void firstObjectsTest() {
		n = new FirstObjects();
		assertEquals(3.0, n.normalizer(s1, s2), 0.001);
		assertEquals(0.0, n.normalizer(s3, s3), 0.001);
	}

	@Test
	public void firstUniqueObjectsTest() {
		n = new FirstUniqueObjects();
		assertEquals(2.0, n.normalizer(s1, s2), 0.001);
		assertEquals(0.0, n.normalizer(s3, s3), 0.001);
	}

	@Test
	public void mostObjectsTest() {
		n = new MostObjects();
		assertEquals(8.0, n.normalizer(s1, s2), 0.001);
		assertEquals(0.0, n.normalizer(s3, s3), 0.001);
	}

	@Test
	public void mostUniqueObjectsTest() {
		n = new MostUniqueObjects();
		assertEquals(7.0, n.normalizer(s1, s2), 0.001);
		assertEquals(0.0, n.normalizer(s3, s3), 0.001);
	}

	@Test
	public void totalObjectsTest() {
		n = new TotalObjects();
		assertEquals(11.0, n.normalizer(s1, s2), 0.001);
		assertEquals(0.0, n.normalizer(s3, s3), 0.001);
	}

	@Test
	public void totalUniqueObjectsTest() {
		n = new TotalUniqueObjects();
		assertEquals(7.0, n.normalizer(s1, s2), 0.001);
		assertEquals(0.0, n.normalizer(s3, s3), 0.001);
	}

	@Test
	public void noNormalizationTest() {
		n = new NoNormalization();
		assertEquals(1.0, n.normalizer(s1, s2), 0.001);
		assertEquals(1.0, n.normalizer(s3, s3), 0.001);
		assertEquals(1.0, n.normalizer(null, null), 0.001);
	}

	@Test
	public void productMagnitudeTest() {
		n = new ProductMagnitude();
		assertEquals(7.071, n.normalizer(s1, s2), 0.001);
		assertEquals(0.0, n.normalizer(s3, s3), 0.001);
	}
}
