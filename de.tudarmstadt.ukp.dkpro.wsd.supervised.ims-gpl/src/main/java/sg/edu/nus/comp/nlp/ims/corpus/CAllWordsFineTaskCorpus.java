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
import java.util.Arrays;
//import java.util.List;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sg.edu.nus.comp.nlp.ims.util.ILemmatizer;
import sg.edu.nus.comp.nlp.ims.util.IPOSTagger;
import sg.edu.nus.comp.nlp.ims.util.ISentenceSplitter;
import sg.edu.nus.comp.nlp.ims.util.ITokenizer;

/**
 * SensEval-2/3 and SemEval 2007 fine-grained all-words task test corpus.
 *
 * @author zhongzhi
 *
 */
public final class CAllWordsFineTaskCorpus extends CLexicalCorpus {

	/**
	 * default constructor
	 */
	public CAllWordsFineTaskCorpus() {
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
	public CAllWordsFineTaskCorpus(IPOSTagger p_POSTagger,
			ISentenceSplitter p_Splitter, ITokenizer p_Tokenizer,
			ILemmatizer p_Lemmatizer) {
		super(p_POSTagger, p_Splitter, p_Tokenizer, p_Lemmatizer);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see supreclass.CCorpus#load(java.io.InputStream)
	 */
	@Override
	public boolean load(Reader p_Reader) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(p_Reader);
		Element root = doc.getRootElement();
		ArrayList<String> texts = new ArrayList<String>();
		for (Object text : root.getChildren("text")) {
			texts.add(this.loadText((Element) text));
		}
		ArrayList<ArrayList<String>> paragraphs = this.split(texts);
		this.tokenize(paragraphs);
		this.posTag();
		this.lemmatize();
		this.genInfo();
		this.m_Ready = true;
		return true;
	}

	/**
	 * sentence split paragraphs
	 * @param p_Texts input
	 * @return sentences
	 */
	protected ArrayList<ArrayList<String>> split(ArrayList<String> p_Texts) {
		ArrayList<ArrayList<String>> retVal = new ArrayList<ArrayList<String>>();
		for (String text : p_Texts) {
			retVal.add(new ArrayList<String>(Arrays.asList(this.m_SentenceSplitter.split(text))));
		}
		return retVal;
	}

	// clean the dummy tokens in test file
	protected Pattern m_CleanPattern = Pattern
			.compile("(^(0|\\*[^ ]*)| (0|\\*[^ ]*) ?)");

	/**
	 * load texts
	 * @param p_Text text element
	 * @return plain text
	 * @throws Exception exception while loading text element
	 */
	protected String loadText(Element p_Text) throws Exception {
		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < p_Text.getContentSize(); i++) {
			Content cont = p_Text.getContent(i);
			if (org.jdom.Text.class.isInstance(cont)) {
				String value = cont.getValue().replaceAll("[\r\n]", " ").trim();
				value = this.m_CleanPattern.matcher(value).replaceAll(" ");
				builder.append(value + " ");
			} else {
				Element element = (Element) cont;
				String name = element.getName();
				if (name.equals("head")) {
					Attribute id = element.getAttribute("id");
					Attribute sats = element.getAttribute("sats");
					builder.append(HEADSTART + element.getValue().trim() + HEADEND
							+ " ");
					String sid = id.getValue();
					this.m_IDs.add(sid);
					if (sid.indexOf('.') >= 0) {
						this.m_DocIDs.add(sid.substring(0, sid.indexOf('.')));
					} else {
						this.m_DocIDs.add(sid);
					}
					if (sats != null) {
						String[] satIDs = sats.getValue().split("\\s");
						this.m_SatIDs.add(satIDs);
					} else {
						this.m_SatIDs.add(new String[0]);
					}
				} else if (name.equals("sat")) {
					Attribute id = element.getAttribute("id");
					int satIdx = this.m_SatID2Index.size();
					this.m_SatID2Index.put(id.getValue(), satIdx);
					builder.append(SATSTART + element.getValue() + SATEND + " ");
				} else {
					throw new IOException("Error:Invalid element[" + name
							+ "]\n");
				}
			}
		}
		return builder.toString().trim();
	}

	private final Pattern m_BePattern = Pattern.compile("'([sm]|re)");

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus#genInfo()
	 */
	@Override
    protected void genInfo() {
		super.genInfo();
		for (int i = 0; i < this.m_IDs.size(); i++) {
			IItem item = this.m_Sentences.get(this.m_SentenceIDs.get(i)).getItem(this.m_Indice.get(i));
			String pos = item.get(g_PIDX);
			if (this.m_BePattern.matcher(this.m_InstanceTokens.get(i)).matches()) {
				this.m_InstanceTokens.set(i, "be");
			}
			if (this.m_InstanceTokens.get(i).equals("%")) {
				this.m_InstanceTokens.set(i, "percent");
			}
			String lexelt = this.m_Lemmatizer.guessLexelt(new String[]{this.m_InstanceTokens.get(i), pos});
			if (lexelt == null) {
				// cannot find a lexelt which can perfectly matched
				lexelt = this.m_Lemmatizer.guessLexelt(new String[]{this.m_InstanceTokens.get(i), pos, "force"});
				if (lexelt == null) {
					lexelt = "U";
				}
			}
			this.m_LexeltIDs.add(lexelt);
		}
	}
}
