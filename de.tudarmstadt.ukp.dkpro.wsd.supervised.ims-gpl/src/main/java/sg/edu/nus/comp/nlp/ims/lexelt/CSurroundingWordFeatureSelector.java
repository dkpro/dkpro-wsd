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

package sg.edu.nus.comp.nlp.ims.lexelt;

import java.util.ArrayList;
import java.util.Hashtable;

import sg.edu.nus.comp.nlp.ims.feature.CSurroundingWord;

/**
 * surrounding word feature selector.
 *
 * @author zhongzhi
 *
 */
public class CSurroundingWordFeatureSelector implements IFeatureSelector {
	// feature cut off
	protected int m_M2 = 0;
	// status
	protected boolean m_Status = false;
	// feature filter information
	protected ArrayList<Type> m_FeatureFilterInfo = new ArrayList<Type>();
	// filter information of feature value
	protected ArrayList<Hashtable<String, Boolean>> m_FeatureValueFilterInfo = new ArrayList<Hashtable<String, Boolean>>();

	/**
	 * constructor
	 *
	 * @param p_M2
	 *            m2 threshold
	 */
	public CSurroundingWordFeatureSelector(int p_M2) {
		this.m_M2 = p_M2;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector#filter(sg.edu.nus.comp.nlp.ims.lexelt.IStatistic)
	 */
	@SuppressWarnings("unchecked")
	public void filter(IStatistic p_Stat) {
		String surroundingWord = CSurroundingWord.class.getName();
		int keySize = p_Stat.getKeys().size();
		for (int iKey = 0; iKey < keySize; iKey++) {
			if (p_Stat.getType(iKey).equals(surroundingWord)) {
				if (p_Stat.getCount(iKey, "1") >= this.m_M2) {
					this.m_FeatureFilterInfo.add(Type.ACCEPT);
				} else {
					this.m_FeatureFilterInfo.add(Type.FILTER);
				}
			} else {
				this.m_FeatureFilterInfo.add(Type.ACCEPT);
			}
		}
		this.m_Status = true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector#isFiltered(int)
	 */
	@Override
	public Type isFiltered(int p_FeatureIndex) {
		return this.m_FeatureFilterInfo.get(p_FeatureIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector#isFiltered(int, java.lang.String)
	 */
	@Override
	public Type isFiltered(int p_FeatureIndex, String p_Value) {
		if (!this.m_FeatureFilterInfo.get(p_FeatureIndex).equals(Type.PART)) {
			return this.m_FeatureFilterInfo.get(p_FeatureIndex);
		}
		if (this.m_FeatureValueFilterInfo.get(p_FeatureIndex).get(p_Value)) {
			return Type.FILTER;
		} else {
			return Type.ACCEPT;
		}
	}
}
