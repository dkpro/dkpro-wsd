/*******************************************************************************
 * Copyright 2013
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.util.UkbDictionary;

/**
 * An implementation of a dictionary for UKB format
 *
 * @author nico.erbs@gmail.com
 *
 */
public class UkbDictionaryInventory
	implements IUkbDictionary
{

	private UkbDictionary dictionary;

	public UkbDictionaryInventory(String inputPath, String serializiblePath, String neededMentionsPath) throws FileNotFoundException, IOException
	{
		//Open the serialized version or create it from scratch
		try{
//			System.out.println("Trying to load dictionary from serializable.");
			ObjectInputStream dictionaryReader =
				new ObjectInputStream(
						new BZip2CompressorInputStream(
								new FileInputStream(serializiblePath)));
				dictionary = (UkbDictionary) dictionaryReader.readObject();
			dictionaryReader.close();
//			System.out.println("Loaded dictionary from serializable.");
		}
		catch(Exception e){
//			System.out.println("Trying to load dictionary from input.");
			dictionary = new UkbDictionary(inputPath, neededMentionsPath);
			System.out.println("Loaded dictionary from input.");

			ObjectOutputStream dictionaryWriter =
				new ObjectOutputStream(
						new BZip2CompressorOutputStream(
								new FileOutputStream(serializiblePath)));
				dictionaryWriter.writeObject(dictionary);
			dictionaryWriter.close();
//			System.out.println("Stored dictionary in serializable.");
		}

	}

	@Override
	public Map<String, Double> getWeightedSenses(String sod)
			throws SenseInventoryException {

		return dictionary.getWeightedSenses(sod);
	}

	@Override
	public Map<String, Double> getPopularityWeightedSenses(String sod)
			throws SenseInventoryException {

		return dictionary.getPopularityWeightedSenses(sod);
	}

	public Map<String, Double> getRandomnylWeightedSenses(String sod) {
		return dictionary.getRandomnylWeightedSenses(sod);
	}

	@Override
	public Map<String, Double> getRandomlyWeightedSenses(String sod)
			throws SenseInventoryException {
		return dictionary.getPopularityWeightedSenses(sod);
	}

	@Override
	public String getMostFrequentSense(String sod)
			throws SenseInventoryException, UnsupportedOperationException {
		return dictionary.getMostFrequentTarget(sod);
	}

	@Override
	public String getMostFrequentSense(String sod, POS pos)
			throws SenseInventoryException, UnsupportedOperationException {
		return dictionary.getMostFrequentTarget(sod);
	}

	@Override
	public String getSenseDescription(String senseId)
			throws SenseInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<String>> getSenseInventory()
			throws SenseInventoryException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSenseInventoryName() {
		return "UkbDict";
	}

	@Override
	public List<String> getSenses(String sod) throws SenseInventoryException {
		return dictionary.getTargets(sod);
	}

	@Override
	public List<String> getSenses(String sod, POS pos)
			throws SenseInventoryException, UnsupportedOperationException {
		return dictionary.getTargets(sod);
	}

}
