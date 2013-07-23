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
 * POS feature extractor.
 *
 * @author zhongzhi
 *
 */
public class CPOSFeatureExtractor implements IFeatureExtractor {
	// the positions of part-of-speeches
	protected ArrayList<Integer> m_POSs = new ArrayList<Integer>();

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

	// index of POS feature
	protected int m_POSIndex = -1;

	// current feature
	protected IFeature m_CurrentFeature = null;

	protected static int g_PIDX = AItem.Features.POS.ordinal();

	/**
	 * constructor
	 *
	 * @param p_Indice
	 *            pos tag interested
	 */
	public CPOSFeatureExtractor(ArrayList<Integer> p_Indice) {
		if (p_Indice == null) {
			throw new IllegalArgumentException();
		}
		this.m_POSs.addAll(p_Indice);
	}

	/**
	 * constructor
	 */
	public CPOSFeatureExtractor() {
		this.m_POSs.add(-3);
		this.m_POSs.add(-2);
		this.m_POSs.add(-1);
		this.m_POSs.add(0);
		this.m_POSs.add(1);
		this.m_POSs.add(2);
		this.m_POSs.add(3);
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
	 * get the next feature of current instance
	 *
	 * @return feature
	 */
	protected IFeature getNext() {
		IFeature feature = null;
		if (this.m_POSIndex >= 0 && this.m_POSIndex < this.m_POSs.size()) {
			feature = new CPOSFeature();
			feature.setKey(this.formPOSName(this.m_POSs.get(this.m_POSIndex)));
			feature.setValue(this.getPOS(this.m_POSs.get(this.m_POSIndex)));
			this.m_POSIndex++;
		}
		return feature;
	}

	/**
	 * get the part-of-speech of item p_Index + m_IndexInSentence
	 *
	 * @param p_Index
	 *            index
	 * @return feature value
	 */
	protected String getPOS(int p_Index) {
		if (p_Index > 0) {
			p_Index += this.m_InstanceLength - 1;
		}
		p_Index += this.m_IndexInSentence;
		if (p_Index >= 0 && p_Index < this.m_Sentence.size()) {
			IItem item = this.m_Sentence.getItem(p_Index);
			return item.get(g_PIDX);
		}
		return "NULL";
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
	 * form POS feature name
	 *
	 * @param p_Index
	 *            index
	 * @return feature name
	 */
	protected String formPOSName(int p_Index) {
		if (p_Index < 0) {
			return "POS_" + -p_Index;
		}
		return "POS" + p_Index;
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
		this.m_POSIndex = 0;
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
	@Override
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
