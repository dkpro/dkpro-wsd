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

package sg.edu.nus.comp.nlp.ims.instance;

import sg.edu.nus.comp.nlp.ims.corpus.ICorpus;
import sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor;

/**
 * a common instance extractor.
 *
 * @author zhongzhi
 *
 */
public class CInstanceExtractor implements IInstanceExtractor {
	// extract corpus
	protected ICorpus m_Corpus;
	// feature extractor
	protected IFeatureExtractor m_FeatureExtractor;
	// ready to extract instances
	protected boolean m_Ready;
	// instance count
	protected int m_InstanceSize;
	// instance iterator
	protected int m_Iterator;

	/**
	 * default constructor
	 */
	public CInstanceExtractor() {
		this.m_Corpus = null;
		this.m_FeatureExtractor = null;
		this.m_InstanceSize = 0;
		this.m_Iterator = 0;
		this.m_Ready = false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstanceExtractor#next()
	 */
	public IInstance next() {
		if (this.hasNext()) {
			IInstance instance = new CInstance(this.m_Corpus.getValue(
					this.m_Iterator, "id"));
			instance.setDocID(this.m_Corpus.getValue(this.m_Iterator, "docid"));
			this.m_FeatureExtractor.setCurrentInstance(this.m_Iterator);

			String lexeltID = this.m_Corpus.getValue(this.m_Iterator,
					"lexeltID");

			instance.setLexeltID(lexeltID);
			for (String tag : this.m_Corpus.getTag(this.m_Iterator)) {
				instance.setTag(tag);
			}
			this.m_FeatureExtractor.restart();
			while (this.m_FeatureExtractor.hasNext()) {
				instance.addFeature(this.m_FeatureExtractor.next());
			}
			this.m_Iterator++;
			return instance;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstanceExtractor#hasNext()
	 */
	public boolean hasNext() {
		if (!this.m_Ready) {
			if (this.m_Corpus == null || this.m_FeatureExtractor == null) {
				return false;
			}
			this.m_FeatureExtractor.setCorpus(this.m_Corpus);
			this.m_Ready = true;
			this.m_Iterator = 0;
		}
		return this.m_Iterator < this.m_InstanceSize;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstanceExtractor#setCorpus(sg.edu.nus.comp.nlp.ims.corpus.ICorpus)
	 */
	public boolean setCorpus(ICorpus p_Corpus) {
		if (p_Corpus != null) {
			this.m_Corpus = p_Corpus;
			this.m_Ready = false;
			this.m_InstanceSize = this.m_Corpus.size();
			this.m_Iterator = 0;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstanceExtractor#setFeatureExtractor(sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor)
	 */
	public boolean setFeatureExtractor(IFeatureExtractor p_FeatureExtactor) {
		if (p_FeatureExtactor != null) {
			this.m_FeatureExtractor = p_FeatureExtactor;
			this.m_Ready = false;
			return true;
		}
		return false;
	}

}
