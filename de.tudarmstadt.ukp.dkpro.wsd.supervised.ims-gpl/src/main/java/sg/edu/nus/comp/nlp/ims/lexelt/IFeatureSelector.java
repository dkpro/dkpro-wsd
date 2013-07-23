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

/**
 * feature selector interface.
 *
 * @author zhongzhi
 *
 */
public interface IFeatureSelector {

	/**
	 * select type
	 * @author zhongzhi
	 *
	 */
	public enum Type {
		FILTER, PART, ACCEPT
	}

	/**
	 * filter statistic
	 *
	 * @param p_Stat
	 *            input statistic
	 */
	public void filter(IStatistic p_Stat);

	/**
	 * check whether p_FeatureIndex is filtered
	 *
	 * @param p_FeatureIndex
	 *            feature index
	 * @return filter type
	 */
	public Type isFiltered(int p_FeatureIndex);

	/**
	 * check whether p_FeatureIndex's p_Value is filtered
	 *
	 * @param p_FeatureIndex
	 *            feature index
	 * @param p_Value
	 *            feature value
	 * @return filter type
	 */
	public Type isFiltered(int p_FeatureIndex, String p_Value);
}
