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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sg.edu.nus.comp.nlp.ims.util.*;

/**
 * SemEval 2007 coarse-grained all-words task test corpus.
 *
 * @author zhongzhi
 *
 */
public class CAllWordsCoarseTaskCorpus extends CLexicalCorpus {

	/**
	 * default constructor
	 */
	public CAllWordsCoarseTaskCorpus() {
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
	public CAllWordsCoarseTaskCorpus(IPOSTagger p_POSTagger,
			ISentenceSplitter p_Splitter, ITokenizer p_Tokenizer,
			ILemmatizer p_Lemmatizer) {
		super(p_POSTagger, p_Splitter, p_Tokenizer, p_Lemmatizer);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus#load(java.io.BufferedReader)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean load(Reader p_Reader) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(p_Reader);
		Element root = doc.getRootElement();
		ArrayList<ArrayList<String>> texts = new ArrayList<ArrayList<String>>();
		for (Object text : root.getChildren("text")) {
			texts.add(this.loadText((Element) text));
		}
		this.tokenize(texts);
		this.posTag();
		this.lemmatize();
		this.genInfo();
		this.m_Ready = true;
		return true;
	}

	/**
	 * load text
	 * @param p_Text text element
	 * @return paragraphs
	 * @throws Exception exception when loading text element
	 */
	@SuppressWarnings("unchecked")
	protected ArrayList<String> loadText(Element p_Text) throws Exception {
		ArrayList<String> text = new ArrayList<String>();
		List sentences = p_Text.getChildren();
		for (int i = 0; i < sentences.size(); i++) {
			Element sentence = (Element) sentences.get(i);
			text.add(this.loadSentence(sentence));
		}
		return text;
	}

	/**
	 * load sentence
	 * @param p_Sentence sentence elment
	 * @return sentence
	 * @throws Exception exception when loading sentence element
	 */
	protected String loadSentence(Element p_Sentence) throws Exception {
		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < p_Sentence.getContentSize(); i++) {
			Content cont = p_Sentence.getContent(i);
			if (org.jdom.Text.class.isInstance(cont)) {
				String value = cont.getValue().replaceAll("[\r\n]", " ").trim();
				builder.append(value + " ");
			} else {
				Element element = (Element) cont;
				String name = element.getName();
				if (name.equals("instance")) {
					Attribute id = element.getAttribute("id");
					Attribute lemma = element.getAttribute("lemma");
					Attribute pos = element.getAttribute("pos");
					builder.append(HEADSTART + element.getValue().trim().replace(' ', '_') + HEADEND
							+ " ");
					String sid = id.getValue();
					this.m_IDs.add(sid);
					if (sid.indexOf('.') >= 0) {
						this.m_DocIDs.add(sid.substring(0, sid.indexOf('.')));
					} else {
						this.m_DocIDs.add(sid);
					}
					this.m_InstanceTokens.add(element.getValue().toLowerCase().replaceAll("[\\s-]", "_"));
					this.m_InstanceLemmas.add(lemma.getValue().toLowerCase().replaceAll("[\\s-]", "_"));
					this.m_InstancePOSs.add(pos.getValue());
					this.m_LexeltIDs.add(lemma.getValue().toLowerCase()
							.replaceAll("[\\s-]", "_")
							+ "." + pos.getValue());
				} else {
					throw new IOException("Error:Invalid element[" + name
							+ "]\n");
				}
			}
		}
		return builder.toString().trim();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus#genInfo()
	 */
	protected void genInfo() {

	}

}
