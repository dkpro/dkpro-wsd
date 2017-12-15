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

package de.tudarmstadt.ukp.dkpro.wsd.alignment;

import static de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils.resolveLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;

import org.junit.Test;

public class WordNetVersionAlignmentTest
{
	int source = 3537241, target = 3498113, nonexistent = 1;
	String testFile = "classpath:/alignment_test.txt";

	@Test(expected = NoSuchElementException.class)
	public void failGetBestWeightedAlignment()
		throws IOException
	{
		WordNetVersionAlignment a = new WordNetVersionAlignment(
				resolveLocation(testFile, new WordNetVersionAlignmentTest(),
						null));
		a.getBestWeightedAlignment(nonexistent);
	}

	@Test(expected = NoSuchElementException.class)
	public void failGetBestAlignment()
		throws IOException
	{
		WordNetVersionAlignment a = new WordNetVersionAlignment(
				resolveLocation(testFile, new WordNetVersionAlignmentTest(),
						null));
		a.getBestAlignment(nonexistent);
	}

	@Test
	public void testWordNetVersionAlignment()
		throws IOException
	{
	    URL alignmentURL = resolveLocation(testFile, new WordNetVersionAlignmentTest(),
                null);
		WordNetVersionAlignment a = new WordNetVersionAlignment(alignmentURL);
		assertEquals(3, a.getWeightedAlignments(source).size());
		assertEquals(3, a.getAlignments(source).size());
		assertTrue(a.getAlignments(nonexistent).isEmpty());
		assertTrue(a.getWeightedAlignments(nonexistent).isEmpty());
		assertEquals(target, (int) a.getBestWeightedAlignment(source)
				.getFirst());
		assertEquals(0.808, a.getBestWeightedAlignment(source).getSecond(),
				0.001);
		assertEquals(0.808, a.getWeight(source, target), 0.001);
		assertTrue(a.hasAlignment(source));
		assertFalse(a.hasAlignment(nonexistent));
		assertEquals(target, a.getBestAlignment(source));
	}

}
