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

/**
 * a feature extractor combination for all-words tasks.
 *
 * @author zhongzhi
 *
 */
public class CAllWordsFeatureExtractorCombination extends CFeatureExtractorCombination {
	public CAllWordsFeatureExtractorCombination() {
		this.m_FeatureExtractors.clear();
		this.m_FeatureExtractors.add(new CPOSFeatureExtractor());
		this.m_FeatureExtractors.add(new CCollocationExtractor());
		this.m_FeatureExtractors.add(new CSurroundingWordExtractor(1, 1));
	}

	public CAllWordsFeatureExtractorCombination(
			ArrayList<IFeatureExtractor> p_FeatureExtractors) {
		super(p_FeatureExtractors);
	}
}
