/*******************************************************************************
 * IMS (It Makes Sense) -- NUS WSD System
 * Copyright (c) 2013 National University of Singapore.
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
 *
 ******************************************************************************/

package sg.edu.nus.comp.nlp.ims.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.didion.jwnl.JWNLException;

/**
 * WordNet lemmatizer with Penn Treebank POS tag set.
 *
 * @author zhongzhi
 *
 */
public class CPTBWNLemmatizer implements ILemmatizer {

	// wordnet lemmatizer
	protected CWordNetLemmatizer m_Lemmatizer = new CWordNetLemmatizer();

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ILemmatizer#lemmatize(java.lang.String[])
	 */
	@Override
	public String lemmatize(String[] input) {
		String lemma = input[0].toLowerCase();
		String pos = APTBPOSTagger.getShortForm(input[1]);
		if (pos != null) {
			return this.m_Lemmatizer.lemmatize(new String[]{lemma, pos});
		}
		return lemma;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ILemmatizer#guessLexelt(java.lang.String[])
	 */
	@Override
	public String guessLexelt(String[] input) {
		String lemma = input[0].toLowerCase();
		String pos = APTBPOSTagger.getShortForm(input[1]);
		String def = null;
		if (input.length > 2) {
			def = input[2];
		}
		return this.m_Lemmatizer.guessLexelt(new String[]{lemma, pos, def});
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ILemmatizer#getLexelt(java.lang.String[])
	 */
	public String getLexelt(String[] input) {
		String lemma = input[0].toLowerCase();
		String pos = APTBPOSTagger.getShortForm(input[1]);
		String def = null;
		if (input.length > 2) {
			def = input[2];
		}
		return this.m_Lemmatizer.getLexelt(new String[]{lemma, pos, def});
	}

	/**
	 * lemmatize a given document
	 * @param args prop.xml inputfile
	 * @throws JWNLException exception when loading wordnet
	 * @throws IOException exception when reading inputfile
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws JWNLException, IOException, InterruptedException {
		CJWNL.initial(new FileInputStream(args[0]));
		CPTBWNLemmatizer lemmatizer = new CPTBWNLemmatizer();
		if (true) {
			//System.out.println(lemmatizer.lemmatize(new String[]{"mainstreaming", "VB"}));
			//System.exit(0);
		}
		Pattern SPLITER = Pattern.compile("^(.*)/([^/]*)$");
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
		while ((line = reader.readLine()) != null) {
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreTokens()) {
				Matcher matcher = SPLITER.matcher(tokenizer.nextToken());
				if (matcher.matches()) {
					System.out.print(lemmatizer.lemmatize(new String[]{matcher.group(1), matcher.group(2)}));
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}
}
