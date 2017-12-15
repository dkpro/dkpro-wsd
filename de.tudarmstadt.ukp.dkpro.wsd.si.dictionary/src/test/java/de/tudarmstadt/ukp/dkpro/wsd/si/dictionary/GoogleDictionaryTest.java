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

package de.tudarmstadt.ukp.dkpro.wsd.si.dictionary;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.util.GoogleDictionary;


public class GoogleDictionaryTest {

	private static String path = "src/test/resources/dictionary/SpitkovskyChang/dict_google.txt.bz2";
	private static String output = "src/test/resources/dictionary/SpitkovskyChang/dict_google.ser";
	private static String needed_mentions = "src/test/resources/dictionary/SpitkovskyChang/needed_mentions.txt";

	@Test
	public void testGoogleDictionary() throws Exception {

		FileUtils.deleteQuietly(new File(output));

		GoogleDictionary dictionary = new GoogleDictionary(path, needed_mentions);
		Assert.assertNotNull(dictionary);

		ObjectOutputStream dictionaryWriter = new ObjectOutputStream (
				new BZip2CompressorOutputStream(
						new	FileOutputStream(output)));
		dictionaryWriter.writeObject (dictionary);
		dictionaryWriter.close();

		Assert.assertTrue(new File(output).exists());

		ObjectInputStream dictionaryReader =
			new ObjectInputStream(
					new BZip2CompressorInputStream(
							new FileInputStream(output)));

		dictionary = null;
		dictionary = (GoogleDictionary) dictionaryReader.readObject();

		Assert.assertNotNull(dictionary);
		System.out.println(dictionary.getNumberOfMentionEntityPairs());
		System.out.println(dictionary.getTargetSize());
		Assert.assertEquals(3, dictionary.getTargetValuePairs("claude_monet").size());

		dictionaryReader.close();

	}

}
