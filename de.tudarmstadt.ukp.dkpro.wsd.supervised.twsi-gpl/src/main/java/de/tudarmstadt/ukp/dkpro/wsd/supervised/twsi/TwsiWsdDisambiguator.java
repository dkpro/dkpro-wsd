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
package de.tudarmstadt.ukp.dkpro.wsd.supervised.twsi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.langtech.substituter.MLSenseSubstituter;
import de.tudarmstadt.langtech.substituter.SenseSubstituter;
import de.tudarmstadt.langtech.substituter.Substitution;
import de.tudarmstadt.langtech.substituter.settings.Configuration;
import de.tudarmstadt.langtech.substituter.util.Util;
import de.tudarmstadt.langtech.viterbitagger.Viterbitagger;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmDocumentTextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.twsi.TwsiSenseInventory;


/**
 * A disambiguator for the TWSI dataset
 *
 * @author nico.erbs@gmail.com
 *
 */
public class TwsiWsdDisambiguator
	implements WSDAlgorithmDocumentTextBasic
{
	TwsiSenseInventory inventory;
	SenseSubstituter sensub;


	public TwsiWsdDisambiguator(TwsiSenseInventory inventory)
	{
		setSenseInventory(inventory);
	}

	@Override
	public String getDisambiguation(String documentText)
		throws SenseInventoryException
	{
		Viterbitagger tagger = new Viterbitagger(Configuration.taggerModelPrefix);

		String sentence = Util.tokenize(documentText);
		String taggedSentence = "%^%/BOS" + tagger.addPOStags(sentence) + " %$%/EOS";
		sentence = "%^% " + sentence + " %$%";

		String[] words = sentence.replace("  ", " ").toLowerCase().split(" ");
		String[] wordTag = taggedSentence.replace("  ", " ").split(" ");
		String[] postags = new String[words.length];

		for (int i = 0; i < wordTag.length; i++)
		{
			String parts[] = wordTag[i].split("/");
			postags[i] = parts[1];
		}

		List<String> wordList = Arrays.asList(words);
		List<String> resultList = new ArrayList<String>();

		for (int i = 0; i < wordList.size(); i++)
		{
			Substitution subst = sensub.getSubstitution(i, words, postags);

			if (subst != null)
			{
				resultList.add(subst.getSense().replaceAll("@@", ""));
			}
			else
			{
				resultList.add(wordList.get(i));
			}
		}

		// Remove BOF + EOF words
		resultList.remove(0);
		resultList.remove(resultList.size() - 1);

		return StringUtils.join(resultList, " ");
	}

	@Override
	public String getDisambiguationMethod()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public SenseInventory getSenseInventory()
	{
		return inventory;
	}

	@Override
	public void setSenseInventory(SenseInventory senseInventory)
	{
		this.inventory = (TwsiSenseInventory)senseInventory;
		this.sensub = new MLSenseSubstituter(inventory.getConfigFile().getAbsolutePath());
	}
}
