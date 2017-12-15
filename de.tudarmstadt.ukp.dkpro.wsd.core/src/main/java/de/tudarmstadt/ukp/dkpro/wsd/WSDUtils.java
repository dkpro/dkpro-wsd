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

package de.tudarmstadt.ukp.dkpro.wsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * I/O utilities for WSD
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class WSDUtils
{
	/**
	 * Read key-value pairs of the specified types from the specified columns of
	 * a whitespace-delimited text file into a Map.
	 *
	 * @param url
	 *            Location of the file to read
	 * @param keyColumn
	 *            The index of the column giving the keys
	 * @param keyClass
	 *            The class of the key
	 * @param valueColumn
	 *            The index of the column giving the values
	 * @param valueClass
	 *            The class of the value
	 * @return A map of keys to values
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static <K, V> Map<K, V> readMap(final URL url, final int keyColumn,
			final Class<K> keyClass, final int valueColumn,
			final Class<V> valueClass)
		throws IOException, IllegalArgumentException
	{
		return readMap(url, keyColumn, keyClass, valueColumn, valueClass,
				"[ \\t]+");
	}

	/**
	 * Read key-value pairs of the specified types from the specified columns of
	 * a delimited text file into a Map.
	 *
	 * @param url
	 *            Location of the file to read
	 * @param keyColumn
	 *            The index of the column giving the keys
	 * @param keyClass
	 *            The class of the key
	 * @param valueColumn
	 *            The index of the column giving the values
	 * @param valueClass
	 *            The class of the value
	 * @param delimiterRegex
	 *            A regular expression for the field delimiter
	 * @return A map of keys to values
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static <K, V> Map<K, V> readMap(final URL url, final int keyColumn,
			final Class<K> keyClass, final int valueColumn,
			final Class<V> valueClass, final String delimiterRegex)
		throws IOException, IllegalArgumentException
	{
		Map<K, V> map = new HashMap<K, V>();
		InputStream is = url.openStream();
		String content = IOUtils.toString(is, "UTF-8");
		BufferedReader br = new BufferedReader(new StringReader(content));

		Constructor<K> keyConstructor;
		Constructor<V> valueConstructor;
		try {
			keyConstructor = keyClass.getConstructor(String.class);
			valueConstructor = valueClass.getConstructor(String.class);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		String line;
		String[] lineParts;

		while ((line = br.readLine()) != null) {
			lineParts = line.split(delimiterRegex);
			K key;
			V value;
			try {
				key = keyConstructor.newInstance(lineParts[keyColumn - 1]);
				value = valueConstructor
						.newInstance(lineParts[valueColumn - 1]);
			}
			catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Read key-value pairs of the specified types from the specified columns of
	 * a whitespace-delimited text file into a Multimap.
	 *
	 * @param url
	 *            Location of the file to read
	 * @param keyColumn
	 *            The index of the column giving the keys
	 * @param keyClass
	 *            The class of the key
	 * @param valueColumn
	 *            The index of the column giving the values
	 * @param valueClass
	 *            The class of the value
	 * @return A map of keys to values
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static <K, V> Multimap<K, V> readMultimap(final URL url,
			final int keyColumn, final Class<K> keyClass,
			final int valueColumn, final Class<V> valueClass)
		throws IOException, IllegalArgumentException
	{
		return readMultimap(url, keyColumn, keyClass, valueColumn, valueClass,
				"[ \\t]+");
	}

	/**
	 * Read key-value pairs of the specified types from the specified columns of
	 * a delimited text file into a Multimap.
	 *
	 * @param url
	 *            Location of the file to read
	 * @param keyColumn
	 *            The index of the column giving the keys
	 * @param keyClass
	 *            The class of the key
	 * @param valueColumn
	 *            The index of the column giving the values
	 * @param valueClass
	 *            The class of the value
	 * @param delimiterRegex
	 *            A regular expression for the field delimiter
	 * @return A map of keys to values
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static <K, V> Multimap<K, V> readMultimap(final URL url,
			final int keyColumn, final Class<K> keyClass,
			final int valueColumn, final Class<V> valueClass,
			final String delimiterRegex)
		throws IOException, IllegalArgumentException
	{
		Multimap<K, V> map = HashMultimap.create();
		InputStream is = url.openStream();
		String content = IOUtils.toString(is, "UTF-8");
		BufferedReader br = new BufferedReader(new StringReader(content));

		Constructor<K> keyConstructor;
		Constructor<V> valueConstructor;
		try {
			keyConstructor = keyClass.getConstructor(String.class);
			valueConstructor = valueClass.getConstructor(String.class);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		String line;
		String[] lineParts;

		while ((line = br.readLine()) != null) {
			lineParts = line.split(delimiterRegex);
			K key;
			V value;
			try {
				key = keyConstructor.newInstance(lineParts[keyColumn - 1]);
				value = valueConstructor
						.newInstance(lineParts[valueColumn - 1]);
			}
			catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			map.put(key, value);
		}
		return map;
	}

    /**
     * Truncate a string to a given length
     *
     * @param string
     * @param length
     * @return The string truncated to the given length
     */
    public static String truncate(String string, int length)
    {
        if (string == null) {
            return null;
        }

        if (string.length() <= length) {
            return new String(string);
        }

        return string.substring(0, length) + "…";
    }

}
