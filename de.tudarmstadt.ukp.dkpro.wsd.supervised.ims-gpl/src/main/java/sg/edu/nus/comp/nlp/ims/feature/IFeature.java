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

import java.io.Serializable;

/**
 * feature interface.
 *
 * @author zhongzhi
 *
 */
public interface IFeature extends Cloneable, Serializable {

	/**
	 * set feature value
	 *
	 * @param p_Value
	 *            feature value
	 * @return success or not
	 */
	public boolean setValue(String p_Value);

	/**
	 * get feature value
	 *
	 * @return value
	 */
	public String getValue();

	/**
	 * set feature key
	 *
	 * @param p_Key
	 *            feature key
	 * @return success or not
	 */
	public boolean setKey(String p_Key);

	/**
	 * get feature key
	 *
	 * @return feature key
	 */
	public String getKey();

	/**
	 * clone this feature
	 *
	 * @return clone feature
	 */
	public Object clone();
}
