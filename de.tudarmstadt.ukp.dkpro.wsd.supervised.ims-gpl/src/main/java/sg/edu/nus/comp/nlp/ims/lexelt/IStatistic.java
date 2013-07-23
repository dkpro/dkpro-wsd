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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import sg.edu.nus.comp.nlp.ims.instance.IInstance;

/**
 * statistic information interface. statistic of a lexelt.
 *
 * @author zhongzhi
 *
 */
public interface IStatistic extends Cloneable, Serializable {

	/**
	 * add one instance into the statistic.
	 *
	 * @param p_iInstance
	 *            instance
	 * @return add success or not
	 */
	public boolean addInstance(IInstance p_iInstance);

	/**
	 * remove one instance from the statistic
	 *
	 * @param p_iInstance
	 *            instance
	 * @return success or not
	 */
	public boolean removeInstance(IInstance p_iInstance);

	/**
	 * load the statistic information from file
	 *
	 * @param p_FileName
	 *            statistic file
	 * @return success or not
	 */
	public boolean loadFromFile(String p_FileName);

	/**
	 * write the statistic information into file
	 *
	 * @param p_FileName
	 *            file to be written
	 * @return success or not
	 */
	public boolean writeToFile(String p_FileName);

	/**
	 * set parameter of the statistic information
	 *
	 * @param p_Parameter
	 *            parameter name
	 * @param p_Value
	 *            parameter value
	 * @return success or not
	 */
	public boolean setParameter(String p_Parameter, int p_Value);

	/**
	 * get the value of p_Parameter
	 *
	 * @param p_Parameter
	 *            parameter name
	 * @return value
	 */
	public int getParameter(String p_Parameter);

	/**
	 * get count of tag
	 *
	 * @param p_Tag
	 *            tag name
	 * @return tag count
	 */
	public int getTagCount(String p_Tag);

	/**
	 * get the tag list
	 *
	 * @return tag list
	 */
	public Set<String> getTags();

	/**
	 * get the tags in order
	 *
	 * @return tag list
	 */
	public List<String> getTagsInOrder();

	/**
	 * get the number of instances
	 *
	 * @return size
	 */
	public int size();

	/**
	 * get the list of feature names in the statistic
	 *
	 * @return list of keys
	 */
	public List<String> getKeys();

	/**
	 * get the feature index of feature p_Key
	 *
	 * @param p_Key
	 *            feature name
	 * @return feature index
	 */
	public int getIndex(String p_Key);

	/**
	 * get the feature name of feature p_Index
	 *
	 * @param p_Index
	 *            feature index
	 * @return feature name
	 */
	public String getKey(int p_Index);

	/**
	 * get feature type of feature p_Key
	 *
	 * @param p_Key
	 *            feature name
	 * @return feature type
	 */
	public String getType(String p_Key);

	/**
	 * get the feature type of feature p_Index
	 *
	 * @param p_Index
	 *            feature index
	 * @return feature type
	 */
	public String getType(int p_Index);

	/**
	 * get the value list of feature p_key
	 *
	 * @param p_Key
	 *            feature name
	 * @return value list
	 */
	public List<String> getValue(String p_Key);

	/**
	 * get the value list of feature p_Index
	 *
	 * @param p_Index
	 *            feature index
	 * @return value list
	 */
	public List<String> getValue(int p_Index);

	/**
	 * get the default value of feature
	 *
	 * @return default value
	 */
	public String getDefaultValue();

	/**
	 * get the count of value p_Value of feature p_Key
	 *
	 * @param p_Key
	 *            feature name
	 * @param p_Value
	 *            feature value
	 * @return count
	 */
	public int getCount(String p_Key, String p_Value);

	/**
	 * get the count of value p_Value of feature p_Index
	 *
	 * @param p_Index
	 *            feature index
	 * @param p_Value
	 *            feature value
	 * @return count
	 */
	public int getCount(int p_Index, String p_Value);

	/**
	 * get the count of value p_Value of feature p_Key with p_Tag
	 *
	 * @param p_Key
	 *            feature name
	 * @param p_Value
	 *            feature value
	 * @param p_Tag
	 *            tag
	 * @return count
	 */
	public int getCount(String p_Key, String p_Value, String p_Tag);

	/**
	 * get the count of value p_Value of feature p_Key with p_Tag
	 *
	 * @param p_KeyIndex
	 *            feature index
	 * @param p_Value
	 *            feature value
	 * @param p_Tag
	 *            tag
	 * @return count
	 */
	public int getCount(int p_KeyIndex, String p_Value, String p_Tag);

	/**
	 * check whether feature p_Index contains value p_Value
	 *
	 * @param p_Index
	 *            feature index
	 * @param p_Value
	 *            feature value
	 * @return contains or not
	 */
	public boolean contains(int p_Index, String p_Value);

	/**
	 * select features
	 *
	 * @param p_Selector
	 *            feature selector
	 */
	public void select(IFeatureSelector p_Selector);

	/**
	 * check whether the statistic object has been processed like filtering and
	 * some others
	 *
	 * @return status
	 */
	public boolean isProcessed();

	/**
	 * clear the statistic
	 */
	public void clear();

}
