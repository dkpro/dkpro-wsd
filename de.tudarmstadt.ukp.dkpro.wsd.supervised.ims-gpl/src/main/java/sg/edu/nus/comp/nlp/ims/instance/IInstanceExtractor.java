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
 * instance extractor interface. cooperate with a feature extractor to extract
 * instances from corpus
 * 
 * @author zhongzhi
 * 
 */
public interface IInstanceExtractor {

	/**
	 * check whether has more instance remain
	 * 
	 * @return has or not
	 */
	public boolean hasNext();

	/**
	 * get the next instance
	 * 
	 * @return instance
	 */
	public IInstance next();

	/**
	 * set corpus to be extracted
	 * 
	 * @param p_Corpus
	 *            corpus to be extracted
	 * @return set success or not
	 */
	public boolean setCorpus(ICorpus p_Corpus);

	/**
	 * set featureExtractor to the InstanceExtractor
	 * 
	 * @param p_FeatureExtactor
	 *            feature extractor
	 * @return set success or not
	 */
	public boolean setFeatureExtractor(IFeatureExtractor p_FeatureExtactor);
}
