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

/**
 * abstract list feature selector
 * @author zhongzhi
 *
 */
public abstract class AListFeatureSelector implements IFeatureSelector {

	// feature cut off
	protected int m_M2 = 0;
	// status
	protected boolean m_Status = false;
	// feature filter information
	protected ArrayList<Type> m_FeatureFilterInfo = new ArrayList<Type>();
	// filter information of each feature value
	protected ArrayList<Hashtable<String, Boolean>> m_FeatureValueFilterInfo = new ArrayList<Hashtable<String, Boolean>>();
	// feature name
	protected String m_FeatureName = "";

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector#filter(sg.edu.nus.comp.nlp.ims.lexelt.IStatistic)
	 */
	public void filter(IStatistic p_Stat) {
		int keySize = p_Stat.getKeys().size();
		for (int iKey = 0; iKey < keySize; iKey++) {
			boolean sign = false;
			if (p_Stat.getType(iKey).equals(m_FeatureName)) {
				Hashtable<String, Boolean> valueSet = new Hashtable<String, Boolean>();
				String def = p_Stat.getDefaultValue();
				for (String value : p_Stat.getValue(iKey)) {
					if (p_Stat.getCount(iKey, value) >= this.m_M2
							|| def.equals(value)) {
						valueSet.put(value, false);
					} else {
						valueSet.put(value, true);
						sign = true;
					}
				}
				this.m_FeatureValueFilterInfo.add(valueSet);
				if (sign) {
					this.m_FeatureFilterInfo.add(Type.PART);
				} else {
					this.m_FeatureFilterInfo.add(Type.ACCEPT);
				}
			} else {
				this.m_FeatureFilterInfo.add(Type.ACCEPT);
				this.m_FeatureValueFilterInfo.add(null);
			}
		}
		this.m_Status = true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector#isFiltered(int)
	 */
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