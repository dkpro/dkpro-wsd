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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.SortedSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import de.tudarmstadt.ukp.dkpro.wsd.Pair;

/**
 * An API for the <a href=
 * "http://www.talp.upc.edu/index.php/technology/resources/multilingual-lexicons-and-machine-translation-resources/multilingual-lexicons/98-wordnet-mappings"
 * >UPC WordNet Mappings</a>
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WordNetVersionAlignment
{
	private final SortedSetMultimap<Integer, Pair<Integer, Double>> map;

	private static class CompareSecond
		implements Comparator<Pair<Integer, Double>>
	{
		@Override
		public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2)
		{
			return o1.getSecond() < o2.getSecond() ? -1
					: (o1.getSecond() == o2.getSecond() ? 0 : 1);
		}
	}

	/**
	 * Read in a UPC WordNet mapping
	 *
	 * @param url
	 *            the location of the UPC WordNet mapping
	 * @throws IOException
	 */
	public WordNetVersionAlignment(URL url)
		throws IOException
	{
	    InputStream is = url.openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		map = TreeMultimap.create(Ordering.natural(), new CompareSecond());
		String line;

		while ((line = br.readLine()) != null) {
			Scanner s = new Scanner(line);
			s.useLocale(new Locale("en", "us"));
			Integer source = s.nextInt();
			while (s.hasNextInt()) {
				Integer target = s.nextInt();
				Double weight = s.nextDouble();
				map.put(source, new Pair<Integer, Double>(target, weight));
			}
			s.close();
		}

		br.close();
		is.close();
	}

	/**
	 * Returns the weight associated with the mapping between synset1 and
	 * synset2, or null if no such mapping exists.
	 *
	 * @param synset1
	 * @param synset2
	 * @return the weight associated with the mapping between synset1 and
	 *         synset2
	 */
	public Double getWeight(int synset1, int synset2)
	{
		for (Pair<Integer, Double> alignment : map.get(synset1)) {
			if (alignment.getFirst() == synset2) {
				return alignment.getSecond();
			}
		}
		return null;
	}

	/**
	 * Returns a collection view of all weighted alignments associated with the
	 * given synset. If no alignments exist, an empty collection is returned.
	 *
	 * @param synset
	 * @return the collection of weighted alignments associated with the given
	 *         synset
	 */
	public SortedSet<Pair<Integer, Double>> getWeightedAlignments(int synset)
	{
		return map.get(synset);
	}

	/**
	 * Returns a collection view of all alignment targets associated with the
	 * given synset. If no alignments exist, an empty collection is returned.
	 *
	 * @param synset
	 * @return the collection of alignment targets associated with the given
	 *         synset
	 */
	public Collection<Integer> getAlignments(int synset)
	{
		List<Integer> alignments = new ArrayList<Integer>();
		for (Pair<Integer, Double> alignment : map.get(synset)) {
			alignments.add(alignment.getFirst());
		}
		return alignments;
	}

	/**
	 * Returns true if an alignment exists for the given synset
	 *
	 * @param synset
	 *            the synset to search for
	 */
	public boolean hasAlignment(int synset)
	{
		return map.containsKey(synset);
	}

	/**
	 * Returns the highest-weighted alignment to the given synset. Throws an
	 * exception if the synset has no alignment.
	 *
	 * @param synset
	 * @return the highest-weighted alignment to the given synset
	 */
	public Pair<Integer, Double> getBestWeightedAlignment(int synset)
		throws NoSuchElementException
	{
		return map.get(synset).last();
	}

	/**
	 * Returns the highest-weighted alignment target of the given synset. Throws
	 * an exception if the synset has no alignment.
	 *
	 * @param synset
	 * @return the highest-weighted alignment target of the given synset
	 */
	public int getBestAlignment(int synset)
		throws NoSuchElementException
	{
		return map.get(synset).last().getFirst();
	}

	/**
	 * Takes a 1:1 mapping of synsets to objects, and returns a new mapping with
	 * each synset replaced with its best alignment. Throws an exception if a
	 * two synsets are aligned to the same target, unless skipDuplicateKeys is
	 * true. Throws an exception if a synset is not in the alignment, unless
	 * skipUnknownKeys is true.
	 *
	 * @param map
	 * @return a new mapping with each synset replaced with its best alignment
	 * @throws Exception
	 */
	public <V> Map<Integer, V> convertMapKeys(Map<Integer, V> map,
			boolean skipDuplicateKeys, boolean skipUnknownKeys)
		throws Exception
	{
		Map<Integer, V> newMap = new HashMap<Integer, V>();
		for (Integer key : map.keySet()) {
			if (skipUnknownKeys && !hasAlignment(key)) {
				continue;
			}
			Integer newKey = getBestAlignment(key);
			if (skipDuplicateKeys == false && newMap.containsKey(newKey)) {
				throw new Exception("Duplicate key");
			}
			newMap.put(newKey, map.get(key));
		}
		return newMap;
	}

	/**
	 * Takes a 1:1 mapping of synsets to objects, and returns a new multimapping
	 * with each synset replaced with its alignment. If a synset has multiple
	 * alignment targets, then behaviour is determined by the addAllAlignments
	 * parameter: if true, an entry for each alignment is entered; otherwise, an
	 * entry for only the best alignment is entered. Throws an exception if a
	 * synset is not in the alignment, unless skipUnknownKeys is true.
	 *
	 * @param map
	 * @return a new multimapping with each synset replaced with its alignment
	 * @throws Exception
	 */
	public <V> Multimap<Integer, V> convertMultimapKeys(Map<Integer, V> map,
			boolean addAllAlignments, boolean skipUnknownKeys)
		throws Exception
	{
		Multimap<Integer, V> newMap = HashMultimap.create();
		for (Integer key : map.keySet()) {
			if (skipUnknownKeys == false && !hasAlignment(key)) {
				throw new Exception("Missing alignment");
			}
			if (addAllAlignments == false) {
				Integer newKey = getBestAlignment(key);
				newMap.put(newKey, map.get(key));
			}
			else {
				for (Integer newKey : getAlignments(key)) {
					newMap.put(newKey, map.get(key));
				}
			}
		}
		return newMap;
	}
}
