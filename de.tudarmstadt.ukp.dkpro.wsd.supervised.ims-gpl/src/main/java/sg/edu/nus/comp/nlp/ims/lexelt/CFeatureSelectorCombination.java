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
import java.util.List;

/**
 * feature selector combination.
 * @author zhongzhi
 *
 */
public class CFeatureSelectorCombination implements IFeatureSelector {

	// final feature size
	protected int m_Size = 0;
	// feature values
	protected ArrayList<List<String>> m_Values = new ArrayList<List<String>>();
	// feature selectors
	protected ArrayList<IFeatureSelector> m_Selectors = null;
	// status
	protected boolean m_Status = false;

	/**
	 * constructor
	 *
	 * @param p_Selectors
	 *            list of feature selectors
	 */
	public CFeatureSelectorCombination(ArrayList<IFeatureSelector> p_Selectors) {
		this.m_Selectors = p_Selectors;
	}

	/**
	 * clear
	 */
	protected void clear() {
		this.m_Size = 0;
		this.m_Values.clear();
		this.m_Status = false;
	}

	/**
	 * check whether feature selectors have been set
	 * @return true or false
	 */
	protected boolean checkSelectors() {
		return this.m_Selectors != null && this.m_Selectors.size() != 0;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector#filter(sg.edu.nus.comp.nlp.ims.lexelt.IStatistic)
	 */
	@Override
	public void filter(IStatistic p_Stat) {
		this.clear();
		this.m_Size = p_Stat.getKeys().size();
		for (int i = 0; i < this.m_Size; i++) {
			this.m_Values.add(p_Stat.getValue(i));
		}
		if (this.checkSelectors()) {
			for (IFeatureSelector selector : this.m_Selectors) {
				selector.filter(p_Stat);
			}
		}
		this.m_Status = true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector#isFiltered(int)
	 */
	@Override
	public Type isFiltered(int featureIndex) {
		Type type = Type.ACCEPT;
		if (this.checkSelectors()) {
			for (IFeatureSelector selector : this.m_Selectors) {
				Type type2 = selector.isFiltered(featureIndex);
				if (type2.equals(Type.FILTER)) {
					type = type2;
					break;
				}
				if (type.equals(Type.ACCEPT)) {
					type = type2;
				}
			}
		}
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector#isFiltered(int, java.lang.String)
	 */
	@Override
	public Type isFiltered(int featureIndex, String value) {
		Type type = Type.ACCEPT;
		if (this.checkSelectors()) {
			for (IFeatureSelector selector : this.m_Selectors) {
				Type type2 = selector.isFiltered(featureIndex, value);
				if (type2.equals(Type.FILTER)) {
					type = type2;
					break;
				}
				if (type.equals(Type.ACCEPT)) {
					type = type2;
				}
			}
		}
		return type;
	}

}
