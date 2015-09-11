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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import sg.edu.nus.comp.nlp.ims.util.ILemmatizer;
import sg.edu.nus.comp.nlp.ims.util.IPOSTagger;
import sg.edu.nus.comp.nlp.ims.util.ISentenceSplitter;
import sg.edu.nus.comp.nlp.ims.util.ITokenizer;

/**
 * SensEval-2 lexical sample task test corpus. (Also used for training.)
 *
 * @author zhongzhi
 *
 */
public class CLexicalCorpus extends ACorpus {
	protected static String HEADSTART = "_HEAD_START_";
	protected static String HEADEND = "_HEAD_END_";

	protected static String SATSTART = "_SAT_START_";
	protected static String SATEND = "_SAT_END_";

	protected static Pattern HEADPATTERN = Pattern.compile(HEADSTART + "(.*)"
			+ HEADEND);
	protected static Pattern HEADSTARTPATTERN = Pattern.compile(HEADSTART + "(.*)");
	protected static Pattern HEADENDPATTERN = Pattern.compile("(.*)" + HEADEND);

	protected static Pattern SATPATTERN = Pattern.compile(SATSTART + "(.*)"
			+ SATEND);
	protected static Pattern SATSTARTPATTERN = Pattern.compile(SATSTART + "(.*)");
	protected static Pattern SATENDPATTERN = Pattern.compile("(.*)" + SATEND);

	protected static final String LEXELTMARK = "_LEXELT_MARK_";
	protected static Pattern LEXELTPATTERN = Pattern.compile("(" + LEXELTMARK
			+ ")");

	/**
	 * default constructor
	 */
	public CLexicalCorpus() {
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
	public CLexicalCorpus(IPOSTagger p_POSTagger, ISentenceSplitter p_Splitter,
			ITokenizer p_Tokenizer, ILemmatizer p_Lemmatizer) {
		super(p_POSTagger, p_Splitter, p_Tokenizer, p_Lemmatizer);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#load(java.io.BufferedReader)
	 */
	@Override
    public boolean load(Reader p_Reader) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(p_Reader);
		Element root = doc.getRootElement(); // corpus
		ArrayList<ArrayList<String>> texts = new ArrayList<ArrayList<String>>();
		for (Object text : root.getChildren()) { // instance
			texts.add(this.loadLexelt((Element) text));
		}
		texts = this.split(texts);
		this.tokenize(texts);
		this.posTag();
		this.lemmatize();
		this.genInfo();
		this.m_Ready = true;
		return true;
	}

	private ArrayList<String> loadLexelt(Element p_Lexelt) throws Exception {
		ArrayList<String> retVal = new ArrayList<String>();
		String id = p_Lexelt.getAttributeValue("item");
		for (Object element : p_Lexelt.getChildren()) {
			retVal.add(this.loadInstance((Element) element, id));
		}
		return retVal;
	}

	private String loadInstance(Element p_Instance, String p_LexeltID)
			throws IOException {
		boolean status = false; // head found or not
		String id = p_Instance.getAttributeValue("id");
		String docID = p_Instance.getAttributeValue("docsrc");
		String[] tags = new String[0];

		Element context = p_Instance.getChild("context");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < context.getContentSize(); i++) {
			Content content = context.getContent(i);
			if (org.jdom2.Text.class.isInstance(content)) {
				builder.append(" " + content.getValue());
			} else {
				Element element = (Element) content;
				String name = element.getName();
				if (name.equals("head")) {
					builder.append(" " + HEADSTART
							+ element.getValue().trim().replace(' ', '_') + HEADEND);
					if (status) {
						throw new IOException(
								"Error: Multiple target word in one instance:"
										+ id + "\n");
					} else {
						status = true;
					}
					Attribute sats = element.getAttribute("sats");
					this.m_IDs.add(id);
					this.m_DocIDs.add(docID);
					this.m_Tags.add(tags);
					if (sats != null) {
						this.m_SatIDs.add(sats.getValue().split("\\s"));
						String tail = "";
						if (p_LexeltID.endsWith("\\.[nvar]")) {
							tail = p_LexeltID.substring(
									p_LexeltID.length() - 2, p_LexeltID
											.length());
						}
						this.m_LexeltIDs.add(p_LexeltID + LEXELTMARK + tail);
					} else {
						this.m_SatIDs.add(new String[0]);
						this.m_LexeltIDs.add(p_LexeltID);
					}
				} else if (name.equals("sat")) {
					builder.append(" " + SATSTART + element.getValue()
									+ SATEND);
					Attribute satID = element.getAttribute("id");
					this.m_SatID2Index.put(satID.getValue(), this.m_SatID2Index
							.size());
				}
			}
		}
		if (!status) { // no head word
			throw new IOException("Error:No head word in instance:" + id + "\n");
		}
		return builder.toString().trim();
	}

	private ArrayList<ArrayList<String>> split(ArrayList<ArrayList<String>> p_Lexelts) {
		ArrayList<ArrayList<String>> retVal = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> lexelt : p_Lexelts) {
			for (String context : lexelt) {
				if (this.m_Split) {
					retVal.add(new ArrayList<String>(Arrays.asList(context.split("\r\n|\r|\n"))));
				} else {
					retVal.add(new ArrayList<String>(Arrays.asList(this.m_SentenceSplitter.split(context))));
				}
			}
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ACorpus#tokenizeSentence(java.lang.String)
	 */
	@Override
    protected void tokenizeSentence(String p_Sentence) {
		ISentence sentence = new CSentence();
		String[] tokens = null;
		if (this.m_Tokenized) {
			tokens = p_Sentence.trim().split("[ \t\n\r\f]+");
		} else {
			tokens = this.m_Tokenizer.tokenize(p_Sentence.trim());
		}
		IItem item;
		String token;
		int tokenIndex = 0;
		int i = 0;
		while (i < tokens.length) {
			token = tokens[i++];
			item = new CItem();
			Matcher matcher = HEADPATTERN.matcher(token);
			if (matcher.find()) { // load Head
				this.m_SentenceIDs.add(this.m_Sentences.size());
				this.m_Indice.add(tokenIndex);
				this.m_Lengths.add(1);
				token = matcher.group(1);
			} else {
				matcher = HEADSTARTPATTERN.matcher(token);
				if (matcher.find()) {
					this.m_SentenceIDs.add(this.m_Sentences.size());
					this.m_Indice.add(tokenIndex);
					this.m_Lengths.add(1);
					String head = matcher.group(1);
					while (i < tokens.length) {
						token = tokens[i++];
						if (token.endsWith(HEADEND)) {
							matcher = HEADENDPATTERN.matcher(token);
							matcher.matches();
							head += matcher.group(1);
							break;
						}
						head += token;
					}
					token = head;
				} else {
					matcher = SATPATTERN.matcher(token);
					if (matcher.find()) {
						this.m_SatSentenceIDs.add(this.m_Sentences.size());
						this.m_SatIndice.add(tokenIndex);
						token = matcher.group(1);
					} else {
						matcher = SATSTARTPATTERN.matcher(token);
						if (matcher.find()) {
							this.m_SatSentenceIDs.add(this.m_Sentences.size());
							this.m_SatIndice.add(tokenIndex);
							String sat = matcher.group(1);
							while (i < tokens.length) {
								token = tokens[i++];
								if (token.equals(SATEND)) {
									matcher = SATENDPATTERN.matcher(token);
									matcher.matches();
									sat += matcher.group(1);
									break;
								}
								sat += token;
							}
							token = sat;
						}
					}
				}
			}
			item.set(g_TIDX, token);
			sentence.appendItem(item);
			tokenIndex++;
		}
		this.m_Sentences.add(sentence);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ACorpus#genInfo()
	 */
	@Override
    protected void genInfo() {
		super.genInfo();
		for (int i = 0; i < this.m_IDs.size(); i++) {
			if (this.m_LexeltIDs.size() > i) {
				Matcher matcher = LEXELTPATTERN.matcher(this.m_LexeltIDs
						.get(i));
				if (matcher.find()) {
					this.m_LexeltIDs.set(i, matcher.replaceAll("-" + this.m_InstanceLemmas.get(i)));
				}
			}
		}

	}
}
