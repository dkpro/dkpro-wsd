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

import sg.edu.nus.comp.nlp.ims.corpus.ICorpus;

/**
 * a combination of feature extractors.
 *
 * @author zhongzhi
 *
 */
public class CFeatureExtractorCombination implements IFeatureExtractor {
	// list of feature extractor
	protected ArrayList<IFeatureExtractor> m_FeatureExtractors = new ArrayList<IFeatureExtractor>();

	// feature extractor index
	protected int m_Index = 0;

	// instance index
	protected int m_InstanceIndex = 0;

	// corpus
	protected ICorpus m_Corpus = null;

	// current feature
	protected IFeature m_CurrentFeature = null;

	/**
	 * constructor
	 */
	public CFeatureExtractorCombination() {
		this.m_FeatureExtractors.add(new CPOSFeatureExtractor());
		this.m_FeatureExtractors.add(new CCollocationExtractor());
		this.m_FeatureExtractors.add(new CSurroundingWordExtractor());
	}

	/**
	 * constructor
	 *
	 * @param p_FeatureExtractors
	 *            feature extractor list
	 */
	public CFeatureExtractorCombination(
			ArrayList<IFeatureExtractor> p_FeatureExtractors) {
		if (p_FeatureExtractors == null) {
			throw new IllegalArgumentException("argument cannot be null.");
		}
		this.m_FeatureExtractors.addAll(p_FeatureExtractors);
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
		if (this.validIndex(this.m_InstanceIndex)) {
			return this.m_Corpus.getValue(this.m_InstanceIndex, "id");
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
		if (this.validIndex(this.m_InstanceIndex)) {
			while (this.m_Index < this.m_FeatureExtractors.size()) {
				if (this.m_FeatureExtractors.get(this.m_Index).hasNext()) {
					this.m_CurrentFeature = this.m_FeatureExtractors.get(
							this.m_Index).next();
				}
				if (this.m_CurrentFeature != null) {
					return true;
				}
				this.m_Index++;
			}
		}
		return false;
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
		this.m_Index = 0;
		for (IFeatureExtractor fe : this.m_FeatureExtractors) {
			if (!fe.restart()) {
				return false;
			}
		}
		return true;
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
		this.m_InstanceIndex = 0;
		for (IFeatureExtractor fe : this.m_FeatureExtractors) {
			if (!fe.setCorpus(p_Corpus)) {
				return false;
			}
		}
		this.restart();
		this.m_InstanceIndex = -1;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#setCurrentInstance(int)
	 */
	@Override
	public boolean setCurrentInstance(int p_InstanceIndex) {
		for (IFeatureExtractor fe : this.m_FeatureExtractors) {
			if (!fe.setCurrentInstance(p_InstanceIndex)) {
				return false;
			}
		}
		this.m_InstanceIndex = p_InstanceIndex;
		return true;
	}

}
