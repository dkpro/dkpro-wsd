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

package sg.edu.nus.comp.nlp.ims.feature;

import java.util.ArrayList;
import java.util.HashSet;

import sg.edu.nus.comp.nlp.ims.corpus.AItem;
import sg.edu.nus.comp.nlp.ims.corpus.ICorpus;
import sg.edu.nus.comp.nlp.ims.corpus.ISentence;
import sg.edu.nus.comp.nlp.ims.util.CSurroundingWordFilter;

/**
 * surrounding word extractor.
 *
 * @author zhongzhi
 *
 */
public class CSurroundingWordExtractor implements IFeatureExtractor {
	// corpus to be extracted
	protected ICorpus m_Corpus = null;

	// index of current instance
	protected int m_Index = -1;

	// current sentence to process
	protected ISentence m_Sentence = null;

	// item index in current sentence
	protected int m_IndexInSentence;

	// item length
	protected int m_InstanceLength;

	// index of surrounding word feature
	protected int m_SurroundingWordIndex = -1;

	// surrounding words of current instance
	protected HashSet<String> m_SurroundingWordSet = new HashSet<String>();

	// surrounding words of current instance
	protected ArrayList<String> m_SurroundingWords = new ArrayList<String>();

	// sentence before current sentence
	protected int m_Left;

	// sentence after current sentence
	protected int m_Right;

	// stop words filter
	protected CSurroundingWordFilter m_Filter = CSurroundingWordFilter.getInstance();

	// current feature
	protected IFeature m_CurrentFeature = null;

	// lemma index
	protected static int g_LIDX = AItem.Features.LEMMA.ordinal();

	// token index
	protected static int g_TIDX = AItem.Features.TOKEN.ordinal();

	/**
	 * constructor
	 */
	public CSurroundingWordExtractor() {
		this.m_Left = Integer.MAX_VALUE;
		this.m_Right = Integer.MAX_VALUE;
	}

	/**
	 * constructor
	 * @param p_Left
	 * 	number of sentences left to current sentence that will be used to extract surrounding words
	 * @param p_Right
	 * 	number of sentences right to current sentence that will be used to extract surrounding words
	 */
	public CSurroundingWordExtractor(int p_Left, int p_Right) {
		if (p_Left < 0 || p_Right < 0) {
			throw new IllegalArgumentException(
					"p_Before and p_After should be >= 0");
		}
		this.m_Left = p_Left;
		this.m_Right = p_Right;
	}

	/**
	 * constructor
	 * @param p_StopWords
	 *            stop word list
	 */
	public CSurroundingWordExtractor(HashSet<String> p_StopWords) {
		if (p_StopWords == null) {
			throw new IllegalArgumentException(
					"stop words list should not be null.");
		}
		this.m_Filter = new CSurroundingWordFilter(p_StopWords);
		this.m_Left = Integer.MAX_VALUE;
		this.m_Right = Integer.MAX_VALUE;
	}

	/**
	 * constructor
	 * @param p_Left
	 * 	number of sentences left to current sentence that will be used to extract surrounding words
	 * @param p_Right
	 * 	number of sentences right to current sentence that will be used to extract surrounding words
	 * @param p_StopWords
	 * 	stop word list
	 */
	public CSurroundingWordExtractor(int p_Left, int p_Right,
			HashSet<String> p_StopWords) {
		if (p_Left < 0 || p_Right < 0) {
			throw new IllegalArgumentException(
					"p_Before and p_After should be >= 0");
		}
		if (p_StopWords == null) {
			throw new IllegalArgumentException(
					"stop words list should not be null.");
		}
		this.m_Left = p_Left;
		this.m_Right = p_Right;
		this.m_Filter = new CSurroundingWordFilter(p_StopWords);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#getCurrentInstanceID()
	 */
	@Override
	public String getCurrentInstanceID() {
		if (this.validIndex(this.m_Index)) {
			return this.m_Corpus.getValue(this.m_Index, "id");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (this.m_CurrentFeature != null) {
			return true;
		}
		if (this.validIndex(this.m_Index)) {
			this.m_CurrentFeature = this.getNext();
			if (this.m_CurrentFeature != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get next feature
	 *
	 * @return feature
	 */
	private IFeature getNext() {
		IFeature feature = null;
		if (this.m_SurroundingWords != null && this.m_SurroundingWordIndex >= 0
				&& this.m_SurroundingWordIndex < this.m_SurroundingWords.size()) {
			feature = new CSurroundingWord();
			feature.setKey(this.m_SurroundingWords
					.get(this.m_SurroundingWordIndex));
			this.m_SurroundingWordIndex++;
		}
		return feature;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#next()
	 */
	@Override
	public IFeature next() {
		IFeature feature = null;
		if (this.hasNext()) {
			feature = this.m_CurrentFeature;
			this.m_CurrentFeature = null;
		}
		return feature;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#restart()
	 */
	@Override
	public boolean restart() {
		this.m_SurroundingWordIndex = 0;
		this.m_CurrentFeature = null;
		return this.validIndex(this.m_Index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#setCorpus(sg.edu.nus.comp.nlp.ims.corpus.ICorpus)
	 */
	@Override
	public boolean setCorpus(ICorpus p_Corpus) {
		if (p_Corpus == null) {
			return false;
		}
		this.m_Corpus = p_Corpus;
		this.m_SurroundingWordSet.clear();
		this.m_SurroundingWords.clear();
		this.m_Index = 0;
		this.restart();
		this.m_Index = -1;
		this.m_IndexInSentence = -1;
		this.m_InstanceLength = -1;
		return true;
	}

	/**
	 * check the validity of index
	 *
	 * @param p_Index
	 *            index
	 * @return valid or not
	 */
	protected boolean validIndex(int p_Index) {
		if (this.m_Corpus != null && this.m_Corpus.size() > p_Index
				&& p_Index >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * check whether word is in stop word list or contains no alphabet
	 *
	 * @param p_Word
	 *            word
	 * @return true if it should be filtered, else false
	 */
	public boolean filter(String p_Word) {
		return this.m_Filter.filter(p_Word);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#setCurrentInstance(int)
	 */
	@Override
	public boolean setCurrentInstance(int p_Index) {
		if (this.validIndex(p_Index)) {
			this.m_Index = p_Index;
			this.m_IndexInSentence = this.m_Corpus.getIndexInSentence(p_Index);
			this.m_InstanceLength = this.m_Corpus.getLength(p_Index);
			int currentSent = this.m_Corpus.getSentenceID(p_Index);
			this.m_Sentence = this.m_Corpus.getSentence(currentSent);
			this.m_SurroundingWords.clear();
			this.m_SurroundingWordSet.clear();

			String keyWord = null;
			int lower = this.m_Corpus.getLowerBoundary(currentSent);
			int upper = this.m_Corpus.getUpperBoundary(currentSent);
			for (int sentIdx = lower; sentIdx < upper; sentIdx++) {
				if (currentSent - sentIdx > this.m_Left
						|| sentIdx - currentSent > this.m_Right) {
					continue;
				}
				ISentence sentence = this.m_Corpus.getSentence(sentIdx);
				if (sentence != null) {
					for (int i = 0; i < sentence.size(); i++) {
						keyWord = sentence.getItem(i).get(g_TIDX);
						if (this.filter(keyWord)) {
							continue;
						}
						keyWord = sentence.getItem(i).get(g_LIDX);
						if ((sentIdx != currentSent || i < this.m_IndexInSentence || i >= this.m_IndexInSentence + this.m_InstanceLength)
								&& !this.m_SurroundingWordSet.contains(keyWord)) {
							this.m_SurroundingWordSet.add(keyWord);
							this.m_SurroundingWords.add(keyWord);
						}
					}
				}
			}
			this.restart();
			return true;
		}
		return false;
	}

}
