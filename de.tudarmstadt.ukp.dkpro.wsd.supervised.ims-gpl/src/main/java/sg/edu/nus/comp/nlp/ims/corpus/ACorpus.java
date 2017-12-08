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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import sg.edu.nus.comp.nlp.ims.util.*;

/**
 * abstract corpus.
 *
 * @author zhongzhi
 *
 */
public abstract class ACorpus implements ICorpus {

	// token index
	protected static final int g_TIDX = AItem.Features.TOKEN.ordinal();
	// lemma index
	protected static final int g_LIDX = AItem.Features.LEMMA.ordinal();
	// pos index
	protected static final int g_PIDX = AItem.Features.POS.ordinal();

	// is the corpus ready
	protected boolean m_Ready;

	// all sentences in the corpus
	protected ArrayList<ISentence> m_Sentences;

	// instances id list
	protected ArrayList<String> m_IDs;

	// instance document id list
	protected ArrayList<String> m_DocIDs;

	// instance token list
	protected ArrayList<String> m_InstanceTokens;

	// instance lemma list
	protected ArrayList<String> m_InstanceLemmas;

	// instance pos list
	protected ArrayList<String> m_InstancePOSs;

	// instance lexelt list
	protected ArrayList<String> m_LexeltIDs;

	// instance sentence No. list
	protected ArrayList<Integer> m_SentenceIDs;

	// instance index No. list
	protected ArrayList<Integer> m_Indice;

	// instance length
	protected ArrayList<Integer> m_Lengths;

	// instance tag
	protected ArrayList<String[]> m_Tags;

	// satellite id list
	protected ArrayList<String[]> m_SatIDs;

	// mapping from satellite id to instance index
	protected Hashtable<String, Integer> m_SatID2Index;

	// sentence No. of satellite
	protected ArrayList<Integer> m_SatSentenceIDs;

	// word indice of satellites
	protected ArrayList<Integer> m_SatIndice;

	// the indice of sentences which are boundaries of paragraphs
	protected ArrayList<Integer> m_Boundaries;

	// default delimiter
	protected String m_DefaultDelimiter = "/";

	// delimiter
	protected String m_Delimiter = this.m_DefaultDelimiter;

	// postagger
	protected IPOSTagger m_POSTagger = null;
	/*
	 * whether the input contains pos info
	 * 	if true
	 * 		m_POSTagger will be called.
	 */
	protected boolean m_POSTagged = false;

	// sentence splitter
	protected ISentenceSplitter m_SentenceSplitter;
	/*
	 * whether the input is already split
	 * 	if true
	 * 		m_SentenceSplitter will not be called.
	 */
	protected boolean m_Split = false;

	// tokenizer
	protected ITokenizer m_Tokenizer = null;
	/*
	 * whether the input is already tokenized
	 * 	if true
	 * 		m_Tokenizer will not be called.
	 */
	protected boolean m_Tokenized = false;

	// lemmatizer
	protected ILemmatizer m_Lemmatizer;
	/*
	 * whether the input contains lemma info
	 * 	if true
	 *   	m_Lemmatizer will not be called.
	 */
	protected boolean m_Lemmatized = false;

	/**
	 * whether the input is already split
	 * @param split whether split
	 */
	public void setSplit(boolean split) {
		this.m_Split = split;
	}

	/**
	 * whether sentences are already tokenized
	 * @param tokenized whether tokenized
	 */
	public void setTokenized(boolean tokenized) {
		this.m_Tokenized = tokenized;
	}

	/**
	 * whether the lemma info is provided
	 * @param p_Lemmatized whether lemmatized
	 */
	public void setLemmatized(boolean p_Lemmatized) {
		this.m_Lemmatized = p_Lemmatized;
	}

	/**
	 * whether the pos info is provided
	 * @param p_POSTagged whether pos tagged
	 */
	public void setPOSTagged(boolean p_POSTagged) {
		this.m_POSTagged = p_POSTagged;
	}

	/**
	 * set delimiter
	 * @param p_Delimiter delimiter
	 */
	public void setDelimiter(String p_Delimiter) {
		if (p_Delimiter == null) {
			p_Delimiter = this.m_DefaultDelimiter;
		}
		this.m_Delimiter = p_Delimiter;
	}

	/**
	 * default constructor
	 */
	public ACorpus() {
		this(new COpenNLPPOSTagger(), new COpenNLPSentenceSplitter(),
				new CPennTreeBankTokenizer(), new CPTBWNLemmatizer());
	}

	/**
	 * constructor with some components
	 *
	 * @param p_POSTagger
	 *            pos tagger
	 * @param p_Splitter
	 *            sentence splitter
	 * @param p_Tokenizer
	 *            tokenizer
	 * @param p_Lemmatizer
	 *            lemmatizer
	 */
	public ACorpus(IPOSTagger p_POSTagger, ISentenceSplitter p_Splitter,
			ITokenizer p_Tokenizer, ILemmatizer p_Lemmatizer) {
		this.m_Ready = false;
		this.m_Tags = new ArrayList<String[]>();
		this.m_LexeltIDs = new ArrayList<String>();
		this.m_Indice = new ArrayList<Integer>();
		this.m_Lengths = new ArrayList<Integer>();
		this.m_IDs = new ArrayList<String>();
		this.m_DocIDs = new ArrayList<String>();
		this.m_InstanceTokens = new ArrayList<String>();
		this.m_InstanceLemmas = new ArrayList<String>();
		this.m_InstancePOSs = new ArrayList<String>();
		this.m_SentenceIDs = new ArrayList<Integer>();
		this.m_Sentences = new ArrayList<ISentence>();
		this.m_SatID2Index = new Hashtable<String, Integer>();
		this.m_SatIDs = new ArrayList<String[]>();
		this.m_SatIndice = new ArrayList<Integer>();
		this.m_SatSentenceIDs = new ArrayList<Integer>();
		this.m_Boundaries = new ArrayList<Integer>();
		this.m_POSTagger = p_POSTagger;
		this.m_SentenceSplitter = p_Splitter;
		this.m_Tokenizer = p_Tokenizer;
		this.m_Lemmatizer = p_Lemmatizer;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#load(java.io.Reader)
	 */
	@Override
    public abstract boolean load(Reader p_Reader) throws Exception;

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#getSentence(int)
	 */
	@Override
    public ISentence getSentence(int p_SentenceID) {
		if (this.isValidSentence(p_SentenceID)) {
			return this.m_Sentences.get(p_SentenceID);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#getIndexInSentence(int)
	 */
	@Override
    public int getIndexInSentence(int p_Index) {
		if (this.isValidInstance(p_Index)) {
			return this.m_Indice.get(p_Index);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#getLength(int)
	 */
	@Override
    public int getLength(int p_Index) {
		if (this.isValidInstance(p_Index)) {
			return this.m_Lengths.get(p_Index);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#size()
	 */
	@Override
    public int size() {
		return this.m_IDs.size();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#numOfSentences()
	 */
	@Override
    public int numOfSentences() {
		return this.m_Sentences.size();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#getSentenceID(int)
	 */
	@Override
    public int getSentenceID(int p_Index) {
		if (this.isValidInstance(p_Index)) {
			return this.m_SentenceIDs.get(p_Index);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#getTag(int)
	 */
	@Override
    public String[] getTag(int p_Index) {
		if (this.isValidInstance(p_Index)) {
			if (this.m_Tags.size() == 0) {
				return new String[] { "?" };
			}
			return this.m_Tags.get(p_Index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#clear()
	 */
	@Override
    public void clear() {
		this.m_Ready = false;
		this.m_Indice.clear();
		this.m_Lengths.clear();
		this.m_IDs.clear();
		this.m_DocIDs.clear();
		this.m_InstanceTokens.clear();
		this.m_InstanceLemmas.clear();
		this.m_LexeltIDs.clear();
		this.m_InstancePOSs.clear();
		this.m_SentenceIDs.clear();
		this.m_Sentences.clear();
		this.m_Tags.clear();
		this.m_Boundaries.clear();
		this.m_SatID2Index.clear();
		this.m_SatIDs.clear();
		this.m_SatIndice.clear();
		this.m_SatSentenceIDs.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#getLowerBoundary(int)
	 */
	@Override
    public int getLowerBoundary(int p_Sentence) {
		if (this.isValidSentence(p_Sentence)) {
			int upper = this.m_Boundaries.size();
			int lower = 0;
			int mid = 0;
			int sentenceid = 0;
			if (upper <= 0) {
				return 0;
			}
			while (upper > lower + 1) {
				mid = (lower + upper) / 2;
				sentenceid = this.m_Boundaries.get(mid);
				if (sentenceid == p_Sentence) {
					lower = mid;
					break;
				} else if (sentenceid < p_Sentence) {
					lower = mid;
				} else {
					upper = mid;
				}
			}
			return this.m_Boundaries.get(lower);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#getUpperBoundary(int)
	 */
	@Override
    public int getUpperBoundary(int p_Sentence) {
		if (this.isValidSentence(p_Sentence)) {
			int upper = this.m_Boundaries.size();
			int lower = 0;
			int mid = 0;
			int sentenceid = 0;
			if (upper <= 0) {
				return this.m_Sentences.size();
			}
			while (upper > lower + 1) {
				mid = (lower + upper) / 2;
				sentenceid = this.m_Boundaries.get(mid);
				if (sentenceid == p_Sentence) {
					upper = mid + 1;
					break;
				} else if (sentenceid < p_Sentence) {
					lower = mid;
				} else {
					upper = mid;
				}
			}
			return this.m_Boundaries.get(upper);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ICorpus#getValue(int, java.lang.String)
	 */
	@Override
    public String getValue(int p_Index, String p_Key) {
		if (p_Key == null || !this.isValidInstance(p_Index)) {
			return null;
		}
		p_Key = p_Key.toLowerCase().trim();
		if (p_Key.equals("pos")) {
			return this.m_InstancePOSs.get(p_Index);
		}
		if (p_Key.equals("id")) {
			return this.m_IDs.get(p_Index);
		}
		if (p_Key.equals("docid")) {
			return this.m_DocIDs.get(p_Index);
		}
		if (p_Key.equals("token")) {
			return this.m_InstanceTokens.get(p_Index);
		}
		if (p_Key.equals("lemma")) {
			return this.m_InstanceLemmas.get(p_Index);
		}
		if (p_Key.equals("lexeltid") && p_Index < this.m_LexeltIDs.size()) {
			return this.m_LexeltIDs.get(p_Index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
		StringBuilder builder = new StringBuilder();
		int nextBoundary = 1;
		for (int sent = 0; sent < this.m_Sentences.size(); sent++) {
			if (sent != 0) {
				if (nextBoundary < this.m_Boundaries.size() && this.m_Boundaries.get(nextBoundary) == sent) {
					builder.append("\n");
					nextBoundary++;
				}
			}
			ISentence sentence = this.m_Sentences.get(sent);
			for (int i = 0; i < sentence.size(); i++) {
				if (i != 0) {
					builder.append(" ");
				}
				IItem item = sentence.getItem(i);
				for (int v = 0; v < item.size(); v++) {
					if (v != 0) {
						builder.append(this.m_Delimiter);
					}
					builder.append(item.get(v));
				}
			}
			builder.append("\n");
		}
		return builder.toString();
	}

//	// alphabet pattern
//	protected Pattern m_AlphabeticPattern = Pattern.compile("^[a-zA-Z]+$");
//
//	/**
//	 * check whether the input consists of alphabets
//	 *
//	 * @param p_Word
//	 *            word
//	 * @return true or not
//	 */
//	protected boolean alphabetic(String p_Word) {
//		return this.m_AlphabeticPattern.matcher(p_Word).matches();
//	}

	/**
	 * tokenize the texts
	 * @param p_Texts texts
	 */
	protected void tokenize(ArrayList<ArrayList<String>> p_Texts) {
		for (ArrayList<String> text : p_Texts) {
			this.m_Boundaries.add(this.m_Sentences.size());
			for (String sentence : text) {
				if (sentence.trim().isEmpty()) {
					continue;
				}
				this.tokenizeSentence(sentence);
			}
		}
		this.m_Boundaries.add(this.m_Sentences.size());
	}

	/**
	 * tokenize a sentence
	 * @param p_Sentence input sentence
	 */
	protected abstract void tokenizeSentence(String p_Sentence);

	/**
	 * pos tagging
	 */
	protected void posTag() {
		String sentence = null;
		ISentence iSentence = null;
		for (int sentIdx = 0;sentIdx < this.m_Sentences.size();sentIdx++) {
			iSentence = this.m_Sentences.get(sentIdx);
			// if POS delimiter is provided, postagger will not be called
			if (this.m_POSTagged) {
				for (int i = 0; i < iSentence.size(); i++) {
					IItem item = iSentence.getItem(i);
					String token = item.get(g_TIDX);
					int index = token.lastIndexOf(this.m_Delimiter);
					String pos = null;
					if (index > 0 && index < token.length()) {
						pos = token.substring(index + this.m_Delimiter.length());
						token = token.substring(0, index);
						if (pos.isEmpty()) {
							index = token.lastIndexOf(this.m_Delimiter);
							if (index > 0 && index < token.length()) {
								pos = token.substring(index + this.m_Delimiter.length()) + this.m_Delimiter;
								token = token.substring(0, index);
							}
						}
					}
					item.set(g_TIDX, token);
					item.set(g_PIDX, pos);
				}
			} else {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < iSentence.size(); i++) {
					if (i != 0) {
						builder.append(" ");
					}
					String token = iSentence.getItem(i).get(g_TIDX);
					if (this.m_Lemmatized) {
						int index = token.lastIndexOf(this.m_Delimiter);
						if (index > 0 && index < token.length()) {
							String lemma = token.substring(index + this.m_Delimiter.length());
							token = token.substring(0, index);
							if (lemma.isEmpty()) {
								index = token.lastIndexOf(this.m_Delimiter);
								if (index > 0 && index < token.length()) {
									token = token.substring(0, index);
								}
							}
						}
					}
					builder.append(token);
				}
				sentence = this.m_POSTagger.tag(builder.toString());
				StringTokenizer tokenizer = new StringTokenizer(sentence);
				if (tokenizer.countTokens() != iSentence.size()) {
					throw new IllegalStateException("Error: the pos tagging result of sentence "
							+ sentIdx + ":[" + sentence + "] is wrong length.\n");
				}
				for (int i = 0; i < iSentence.size(); i++) {
					String tokenAndPOS = tokenizer.nextToken();
					IItem item = iSentence.getItem(i);
					String pos = this.m_POSTagger.getTag(tokenAndPOS);
					String token = this.m_POSTagger.getToken(tokenAndPOS);
					if (pos == null || token == null) {
						throw new IllegalStateException("Error: tag result [" + tokenAndPOS
								+ "] wrong format\n");
					}
					item.set(g_TIDX, token);
					item.set(g_PIDX, pos);
				}
			}
		}
	}

	/**
	 * lemmatizing
	 */
	protected void lemmatize() {
		for (int sentIdx = 0; sentIdx < this.m_Sentences.size(); sentIdx++) {
			ISentence sentence = this.m_Sentences.get(sentIdx);
			for (int i = 0; i < sentence.size(); i++) {
				IItem item = sentence.getItem(i);
				String token = item.get(g_TIDX);
				String pos = item.get(g_PIDX);
				String lemma = token.toLowerCase();
				if (this.m_Lemmatized) {
					int index = token.lastIndexOf(this.m_Delimiter);
					if (index > 0 && index < token.length()) {
						lemma = token.substring(index + this.m_Delimiter.length());
						token = token.substring(0, index);
						if (lemma.isEmpty()) {
							index = token.lastIndexOf(this.m_Delimiter);
							if (index > 0 && index < token.length()) {
								lemma = token.substring(index + this.m_Delimiter.length()) + this.m_Delimiter;
								token = token.substring(0, index);
							}
						}
					}
					item.set(g_TIDX, token);
				} else {
//					if (this.alphabetic(lemma)) {
						lemma = this.m_Lemmatizer.lemmatize(new String[] { lemma, pos });
//					}
				}
				item.set(g_LIDX, lemma);
			}
		}
	}

	/**
	 * collection some information
	 */
	protected void genInfo() {
		for (int instIdx = 0; instIdx < this.m_IDs.size(); instIdx++) {
			int instanceSentence = this.m_SentenceIDs.get(instIdx);
			int instanceIndex = this.m_Indice.get(instIdx);
			String[] satIDs = this.m_SatIDs.get(instIdx);
			IItem item = this.m_Sentences.get(instanceSentence).getItem(instanceIndex);
			if (satIDs.length == 0) {
				this.m_InstanceTokens.add(item.get(g_TIDX));
				this.m_InstanceLemmas.add(item.get(g_LIDX));
			} else {
				int[] sentIDs = new int[satIDs.length + 1];
				int[] indexs = new int[satIDs.length + 1];
				int[] sort = new int[satIDs.length + 1];
				for (int i = 0; i < satIDs.length; i++) {
					String id = satIDs[i];
					if (!this.m_SatID2Index.containsKey(id)) {
						throw new IllegalStateException("Error:cannot find sat:" + id
								+ "\n");
					}
					int index = this.m_SatID2Index.get(id);
					sentIDs[i] = this.m_SatSentenceIDs.get(index);
					indexs[i] = this.m_SatIndice.get(index);
					sort[i] = i;
				}
				sentIDs[satIDs.length] = instanceSentence;
				indexs[satIDs.length] = instanceIndex;
				sort[satIDs.length] = satIDs.length;
				for (int i = satIDs.length; i > 0; i--) {
					int max = i;
					for (int k = 0; k < i; k++) {
						if (sentIDs[sort[k]] > sentIDs[sort[max]]
						    || sentIDs[sort[k]] == sentIDs[sort[max]] && indexs[sort[k]] > indexs[sort[max]]) {
							max = k;
						}
					}
					int tmp = sort[max];
					sort[max] = sort[i];
					sort[i] = tmp;
				}
				item = this.m_Sentences.get(sentIDs[sort[0]]).getItem(indexs[sort[0]]);
				StringBuilder tokenBuilder = new StringBuilder(item.get(g_TIDX));
				StringBuilder lemmaBuilder = new StringBuilder(item.get(g_LIDX));
				for (int i = 1; i <= satIDs.length; i++) {
					item = this.m_Sentences.get(sentIDs[sort[i]]).getItem(indexs[sort[i]]);
					tokenBuilder.append("_");
					tokenBuilder.append(item.get(g_TIDX));
					lemmaBuilder.append("_");
					lemmaBuilder.append(item.get(g_LIDX));
				}
				this.m_InstanceTokens.add(tokenBuilder.toString().toLowerCase());
				this.m_InstanceLemmas.add(lemmaBuilder.toString());
			}
		}
		for (int i = 0; i < this.m_IDs.size(); i++) {
			IItem item = this.m_Sentences.get(this.m_SentenceIDs.get(i)).getItem(this.m_Indice.get(i));
			this.m_InstancePOSs.add(item.get(g_PIDX));
		}
	}

	/**
	 * whether the corpus is ready
	 * @return ready or not
	 */
	protected boolean isReady() {
		return this.m_Ready;
	}

	/**
	 * check whether the instance is valid
	 * @param p_Index instance index
	 * @return valid or not
	 */
	protected boolean isValidInstance(int p_Index) {
		return this.isReady() && p_Index >= 0 && p_Index < this.m_IDs.size();
	}

	/**
	 * check whether the sentence index is valid
	 * @param p_Index sentence index
	 * @return valid or not
	 */
	protected boolean isValidSentence(int p_Index) {
		return this.isReady() && p_Index >= 0 && p_Index < this.m_Sentences.size();
	}
}
