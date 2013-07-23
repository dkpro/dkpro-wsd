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
 * sentence interface. a sentence consists of several items.
 * 
 * @author zhongzhi
 * 
 */
public interface ISentence extends Cloneable {
	/**
	 * append Item to the end
	 * 
	 * @param p_Item
	 *            item to append
	 * @return success or not
	 */
	public boolean appendItem(IItem p_Item);

	/**
	 * get Item of p_Index if p_Index is out of range, return null
	 * 
	 * @param p_Index
	 *            item index
	 * @return item
	 */
	public IItem getItem(int p_Index);

	/**
	 * get length of this sentence
	 * 
	 * @return size
	 */
	public int size();

	/**
	 * set parameter
	 * 
	 * @param p_Key
	 *            parameter key
	 * @param p_Value
	 *            parameter value
	 * @return success or not
	 */
	public boolean set(String p_Key, String p_Value);

	/**
	 * get parameter
	 * 
	 * @param p_Key
	 *            parameter key
	 * @return parameter value
	 */
	public String get(String p_Key);

	/**
	 * clear the sentence
	 */
	public void clear();
}
