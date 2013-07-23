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

import sg.edu.nus.comp.nlp.ims.corpus.ICorpus;

/**
 * feature extractor interface. extract features from corpus.
 * 
 * @author zhongzhi
 * 
 */
public interface IFeatureExtractor {
	/**
	 * set the index of instance which to be extracted from corpus
	 * 
	 * @param p_Index
	 *            instance index
	 * @return set success or not
	 */
	public boolean setCurrentInstance(int p_Index);

	/**
	 * get the ID of current instance to be extracted
	 * 
	 * @return instance id
	 */
	public String getCurrentInstanceID();

	/**
	 * set corpus to be extracted
	 * 
	 * @param p_Corpus
	 *            corpus to be extracted
	 * @return set success or not
	 */
	public boolean setCorpus(ICorpus p_Corpus);

	/**
	 * restart the iterator
	 * 
	 * @return success or not
	 */
	public boolean restart();

	/**
	 * whether has at least one more feature
	 * 
	 * @return has or not
	 */
	public boolean hasNext();

	/**
	 * get the next feature
	 * 
	 * @return feature
	 */
	public IFeature next();

}
