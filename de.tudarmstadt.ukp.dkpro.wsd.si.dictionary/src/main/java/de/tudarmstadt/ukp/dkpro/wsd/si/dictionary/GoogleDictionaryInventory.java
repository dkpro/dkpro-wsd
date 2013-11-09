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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.util.GoogleDictionary;

/**
 * The implementation of a Google inventory
 *
 * @author nico.erbs@gmail.com
 *
 */
public class GoogleDictionaryInventory
    implements IGoogleDictionary
{

    private final static Logger logger = Logger
            .getLogger(GoogleDictionaryInventory.class.getName());

    private GoogleDictionary dictionary;

    public GoogleDictionaryInventory(String inputPath, String serializiblePath,
            String neededMentionsPath)
        throws FileNotFoundException, IOException
    {
        // Open the serialized version or create it from scratch
        try {
            logger.debug("Trying to load dictionary "
                    + this.getClass().getSimpleName() + " from serializable.");
            ObjectInputStream dictionaryReader = new ObjectInputStream(
                    new BZip2CompressorInputStream(new FileInputStream(
                            serializiblePath)));
            dictionary = (GoogleDictionary) dictionaryReader.readObject();
            dictionaryReader.close();
            logger.debug("Loaded dictionary " + this.getClass().getSimpleName()
                    + " from serializable.");
        }
        catch (Exception e) {
            logger.debug("Trying to load dictionary "
                    + this.getClass().getSimpleName() + " from input.");
            dictionary = new GoogleDictionary(inputPath, neededMentionsPath);
            System.out.println("Loaded dictionary "
                    + this.getClass().getSimpleName() + " from input.");

            ObjectOutputStream dictionaryWriter = new ObjectOutputStream(
                    new BZip2CompressorOutputStream(new FileOutputStream(
                            serializiblePath)));
            dictionaryWriter.writeObject(dictionary);
            dictionaryWriter.close();
            logger.debug("Stored dictionary " + this.getClass().getSimpleName()
                    + " in serializable.");

        }
    }

    @Override
    public Map<String, Double> getWeightedSenses(String sod)
        throws SenseInventoryException
    {

        return dictionary.getWeightedSenses(sod);
    }

    public Map<String, Double> getRandomnylWeightedSenses(String sod)
    {
        return dictionary.getRandomnylWeightedSenses(sod);
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return dictionary.getMostFrequentTarget(sod);
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return dictionary.getMostFrequentTarget(sod);
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        return "";
    }

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        Map<String, List<String>> senseInventory = new HashMap<String, List<String>>();

        Map<String, List<String[]>> weightedSenseInventory = dictionary
                .getSenseInventory();
        List<String> senses;
        for (String key : weightedSenseInventory.keySet()) {
            senses = new ArrayList<String>();
            for (String[] sense : weightedSenseInventory.get(key)) {
                senses.add(sense[0]);
            }
            senseInventory.put(key, senses);
        }

        return senseInventory;
    }

    @Override
    public String getSenseInventoryName()
    {
        return "GoogleDict";
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        return dictionary.getTargets(sod);
    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return dictionary.getTargets(sod);
    }

    @Override
    public Map<String, Double> getAlternativeWikipediaWeightedSenses(String sod)
        throws SenseInventoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Double> getEnglishlanguageWeightedSenses(String sod)
        throws SenseInventoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Double> getInterlanguageWeightedSenses(String sod)
        throws SenseInventoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Double> getWikipediaWeightedSenses(String sod)
        throws SenseInventoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
