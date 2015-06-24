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

package sg.edu.nus.comp.nlp.ims.corpus;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

import sg.edu.nus.comp.nlp.ims.util.*;

/**
 * interface for a plain text. extract all the content words according to the
 * POS tagging result.
 *
 * @author zhongzhi
 *
 */
public class CAllWordsPlainCorpus extends ACorpus {

	/**
	 * default constructor
	 */
	public CAllWordsPlainCorpus() {
		super();
	}

	/**
	 * constructor with some components
	 *
	 * @param p_POSTagger
	 *            POS tagger
	 * @param p_Splitter
	 *            Sentence splitter
	 * @param p_Tokenizer
	 *            tokenzier
	 * @param p_Lemmatizer
	 *            lemmatizer
	 */
	public CAllWordsPlainCorpus(IPOSTagger p_POSTagger,
			ISentenceSplitter p_Splitter, ITokenizer p_Tokenizer,
			ILemmatizer p_Lemmatizer) {
		super(p_POSTagger, p_Splitter, p_Tokenizer, p_Lemmatizer);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#load(java.io.BufferedReader)
	 */
	@Override
	public boolean load(Reader p_Reader) throws Exception {
		BufferedReader reader = new BufferedReader(p_Reader);
		System.err.println("spliting...");
		ArrayList<ArrayList<String>> paragraphs = new ArrayList<ArrayList<String>>();
		String line = null;
		ArrayList<String> paragraph = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty()) {
				paragraph.add(line);
			} else {
				paragraphs.add(paragraph);
				paragraph = new ArrayList<String>();
			}
		}
		if (paragraph.size() != 0) {
			paragraphs.add(paragraph);
		}
		paragraphs = this.split(paragraphs);
		System.err.println("tokenizing...");
		this.tokenize(paragraphs);
		System.err.println("tagging...");
		this.posTag();
		System.err.println("lemma...");
		this.lemmatize();
		this.genInfo();
		this.m_Ready = true;
		return true;
	}

	/**
	 * split paragraph into sentences
	 * @param p_Texts paragraph
	 * @return sentences
	 */
	protected ArrayList<ArrayList<String>> split(ArrayList<ArrayList<String>> p_Texts) {
		if (this.m_Split) {
			return p_Texts;
		}
		ArrayList<ArrayList<String>> retVal = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> text : p_Texts) {
			for (String line : text) {
				retVal.add(new ArrayList<String>(Arrays.asList(this.m_SentenceSplitter.split(line))));
			}
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ACorpus#tokenizeSentence(java.lang.String)
	 */
	protected void tokenizeSentence(String p_Sentence) {
		ISentence sentence = new CSentence();
		String[] tokens = null;
		if (this.m_Tokenized) {
			tokens = p_Sentence.trim().split("[ \t\n\r\f]+");
		} else {
			tokens = this.m_Tokenizer.tokenize(p_Sentence.trim());
		}
		IItem item = null;
		String token = null;
		int i = 0;
		while (i < tokens.length) {
			token = tokens[i++];
			item = new CItem();
			item.set(g_TIDX, token);
			sentence.appendItem(item);
		}
		this.m_Sentences.add(sentence);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ACorpus#genInfo()
	 */
	protected void genInfo() {
		String id;
		int paragraphID = 0;
		for (int sentIdx = 0; sentIdx < this.m_Sentences.size(); sentIdx++) {
			while (sentIdx >= this.m_Boundaries.get(paragraphID + 1)) {
				paragraphID++;
			}
			ISentence iSentence = this.m_Sentences.get(sentIdx);
			for (int i = 0; i < iSentence.size(); i++) {
				IItem item = iSentence.getItem(i);
				String pos = item.get(g_PIDX);
				String token = item.get(g_TIDX);
				if (token.equalsIgnoreCase("'s")) {
					token = "is";
				} else if (token.equalsIgnoreCase("'ve")) {
					token = "have";
				} else if (token.equalsIgnoreCase("'ll")) {
					token = "will";
				} else if (token.equalsIgnoreCase("'d")) {
					token = "had";
				} else if (token.equalsIgnoreCase("'re")) {
					token = "are";
				} else if (token.equalsIgnoreCase("'m")) {
					token = "am";
				}

				String lemma = item.get(g_LIDX);
				String lexelt = this.m_Lemmatizer.getLexelt(new String[]{token, pos, "force"});
				if (lexelt != null) {
					id = "d" + Integer.toString(paragraphID)
							+ ".s"
							+ Integer.toString(sentIdx - this.m_Boundaries.get(paragraphID))
							+ ".t" + Integer.toString(i);
					this.m_DocIDs.add(Integer.toString(paragraphID));
					this.m_IDs.add(id);
					this.m_InstancePOSs.add(pos);
					this.m_InstanceTokens.add(token);
					this.m_InstanceLemmas.add(lemma);
					this.m_LexeltIDs.add(lexelt);
					this.m_Indice.add(i);
					this.m_Lengths.add(1);
					this.m_SentenceIDs.add(sentIdx);
				}
			}
		}
	}
}
