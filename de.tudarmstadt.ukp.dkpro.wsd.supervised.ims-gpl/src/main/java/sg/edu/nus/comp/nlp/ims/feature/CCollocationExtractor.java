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

import sg.edu.nus.comp.nlp.ims.corpus.AItem;
import sg.edu.nus.comp.nlp.ims.corpus.ICorpus;
import sg.edu.nus.comp.nlp.ims.corpus.IItem;
import sg.edu.nus.comp.nlp.ims.corpus.ISentence;

/**
 * collocation extractor.
 *
 * @author zhongzhi
 *
 */
public class CCollocationExtractor implements IFeatureExtractor {
	// the beginning positions of collocations
	protected ArrayList<Integer> m_Begins = new ArrayList<Integer>();

	// the ending positions of collocations
	protected ArrayList<Integer> m_Ends = new ArrayList<Integer>();

	// source corpus
	protected ICorpus m_Corpus = null;

	// current feature
	protected IFeature m_CurrentFeature = null;

	// index of collocation feature
	protected int m_CollocationIndex = -1;

	// index of current instance
	protected int m_Index = -1;

	// current sentence to process
	protected ISentence m_Sentence = null;

	// item index in current sentence
	protected int m_IndexInSentence;

	// instance length
	protected int m_InstanceLength;

	// token index
	protected static int g_TIDX = AItem.Features.TOKEN.ordinal();

	/**
	 * default constructor
	 */
	public CCollocationExtractor() {
		this.m_Begins.add(-2);
		this.m_Ends.add(-2);
		this.m_Begins.add(-1);
		this.m_Ends.add(-1);
		this.m_Begins.add(1);
		this.m_Ends.add(1);
		this.m_Begins.add(2);
		this.m_Ends.add(2);
		this.m_Begins.add(-2);
		this.m_Ends.add(-1);
		this.m_Begins.add(-1);
		this.m_Ends.add(1);
		this.m_Begins.add(1);
		this.m_Ends.add(2);
		this.m_Begins.add(-3);
		this.m_Ends.add(-1);
		this.m_Begins.add(-2);
		this.m_Ends.add(1);
		this.m_Begins.add(-1);
		this.m_Ends.add(2);
		this.m_Begins.add(1);
		this.m_Ends.add(3);
	}

	/**
	 * constructor
	 *
	 * @param p_Begins
	 *            collocation begins
	 * @param p_Ends
	 *            collocation ends
	 */
	public CCollocationExtractor(ArrayList<Integer> p_Begins,
			ArrayList<Integer> p_Ends) {
		if (p_Begins == null || p_Ends == null
				|| p_Begins.size() != p_Ends.size()) {
			throw new IllegalArgumentException();
		}
		this.m_Begins.addAll(p_Begins);
		this.m_Ends.addAll(p_Ends);
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
	 * form collocation name
	 *
	 * @param p_Begin
	 *            collocation beginning index
	 * @param p_End
	 *            collocation ending index
	 * @return feature name
	 */
	protected String formCollocationName(int p_Begin, int p_End) {
		String collocation = "";
		if (p_Begin < 0) {
			collocation += "C_" + -p_Begin;
		} else {
			collocation += "C" + p_Begin;
		}
		if (p_End < 0) {
			collocation += "C_" + -p_End;
		} else {
			collocation += "C" + p_End;
		}
		return collocation;
	}

	/**
	 * get collocation
	 *
	 * @param p_Begin
	 *            collocation beginning index
	 * @param p_End
	 *            collocation ending index
	 * @return feature value
	 */
	protected String getCollocation(int p_Begin, int p_End) {
		String collocation = "";
		IItem item = null;
		if (p_Begin == p_End && p_Begin == 0) {
			return this.m_Sentence.getItem(this.m_IndexInSentence).get(g_TIDX).toLowerCase().trim();
		}
		p_Begin += this.m_IndexInSentence;
		p_End += this.m_IndexInSentence + this.m_InstanceLength;
		for (int i = p_Begin; i < p_End; i++) {
			if (i >= this.m_IndexInSentence && i < this.m_IndexInSentence + this.m_InstanceLength) {
				continue;
			}
			if (i >= 0 && i < this.m_Sentence.size()) {
				item = this.m_Sentence.getItem(i);
				collocation += item.get(g_TIDX).toLowerCase() + " ";
			} else {
				collocation += "^ ";
			}
		}
		return collocation.trim();
	}

	/**
	 * get the next feature of current instance
	 *
	 * @return feature
	 */
	protected IFeature getNext() {
		IFeature feature = new CCollocation();
		if (this.m_CollocationIndex >= 0
				&& this.m_CollocationIndex < this.m_Begins.size()) {
			feature.setKey(this.formCollocationName(this.m_Begins
					.get(this.m_CollocationIndex), this.m_Ends
					.get(this.m_CollocationIndex)));
			feature.setValue(this.getCollocation(this.m_Begins
					.get(this.m_CollocationIndex), this.m_Ends
					.get(this.m_CollocationIndex)));
			this.m_CollocationIndex++;
			return feature;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#next()
	 */
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
	public boolean restart() {
		this.m_CollocationIndex = 0;
		this.m_CurrentFeature = null;
		return this.validIndex(this.m_Index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#setCorpus(sg.edu.nus.comp.nlp.ims.corpus.ICorpus)
	 */
	public boolean setCorpus(ICorpus p_Corpus) {
		if (p_Corpus == null) {
			return false;
		}
		this.m_Corpus = p_Corpus;
		this.m_Index = 0;
		this.restart();
		this.m_Index = -1;
		this.m_IndexInSentence = -1;
		this.m_InstanceLength = -1;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#setCurrentInstance(int)
	 */
	public boolean setCurrentInstance(int p_Index) {
		if (this.validIndex(p_Index)) {
			this.m_Index = p_Index;
			this.m_IndexInSentence = this.m_Corpus.getIndexInSentence(p_Index);
			this.m_InstanceLength = this.m_Corpus.getLength(p_Index);
			this.m_Sentence = this.m_Corpus.getSentence(this.m_Corpus
					.getSentenceID(p_Index));

			this.restart();
			return true;
		}
		return false;
	}

}
