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

package sg.edu.nus.comp.nlp.ims.corpus;

/**
 * item interface. item is the basic unit in a sentence. For English, it is
 * always a word.
 *
 * @author zhongzhi
 *
 */
public interface IItem extends Cloneable {

	/**
	 * get the number of values in this item
	 * @return number of values
	 */
	public int size();

	/**
	 * get the value of p_Index
	 *
	 * @param p_Index
	 *            value index
	 * @return value
	 */
	public String get(int p_Index);

	/**
	 * set the value in p_Index
	 *
	 * @param p_Index
	 *            value index
	 * @param p_Value
	 *            value to set
	 * @return success or not
	 */
	public boolean set(int p_Index, String p_Value);

	/**
	 * clone
	 * @return clone
	 */
	public IItem clone();

}
