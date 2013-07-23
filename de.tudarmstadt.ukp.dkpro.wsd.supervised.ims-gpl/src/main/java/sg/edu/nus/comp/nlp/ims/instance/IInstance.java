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

import java.io.Serializable;
import java.util.ArrayList;

import sg.edu.nus.comp.nlp.ims.feature.IFeature;

/**
 * instance interface.
 *
 * @author zhongzhi
 *
 */
public interface IInstance extends Cloneable, Serializable {
	/**
	 * add one feature to the instance. if the feature(identified by its key)
	 * has already been set, the old one will be replaced.
	 *
	 * @param p_Feature
	 *            feature
	 * @return add success or not
	 */
	public boolean addFeature(IFeature p_Feature);

	/**
	 * the the number of features in this instance
	 *
	 * @return feature size
	 */
	public int size();

	/**
	 * get the feature of this instance with p_Name
	 *
	 * @param p_Name
	 *            feature name
	 * @return feature
	 */
	public IFeature getFeature(String p_Name);

	/**
	 * get the feature of index p_Index
	 *
	 * @param p_Index
	 *            feature index
	 * @return feature
	 */
	public IFeature getFeature(int p_Index);

	/**
	 * get the feature value of feature of p_Index
	 *
	 * @param p_Index
	 *            feature index
	 * @return feature value
	 */
	public String getFeatureValue(int p_Index);

	/**
	 * get the feature value of feature of p_Name
	 *
	 * @param p_Name
	 *            feature name
	 * @return feature value
	 */
	public String getFeatureValue(String p_Name);

	/**
	 * get the name of feature of p_Index
	 *
	 * @param p_Index
	 *            feature index
	 * @return feature name
	 */
	public String getFeatureName(int p_Index);

	/**
	 * get the ID of this instance
	 *
	 * @return instance ID
	 */
	public String getID();

	/**
	 * get the document ID of this instance
	 *
	 * @return document id
	 */
	public String getDocID();

	/**
	 * get the lexelt ID of this instance
	 *
	 * @return lexelt ID
	 */
	public String getLexeltID();

	/**
	 * get tag(s) of this instance
	 *
	 * @return tags
	 */
	public ArrayList<String> getTag();

	/**
	 * add p_Tag to tag list
	 *
	 * @param p_Tag
	 *            tag
	 * @return success or not
	 */
	public boolean setTag(String p_Tag);

	/**
	 * add p_Tag to tag list if p_Add == true else remove all tags and set tag
	 * as p_Tag
	 *
	 * @param p_Tag
	 *            tag
	 * @param p_Add
	 *            append or replace
	 * @return success or not
	 */
	public boolean setTag(String p_Tag, boolean p_Add);

	/**
	 * set lexelt ID
	 *
	 * @param p_LexeltID
	 *            lexelt ID
	 * @return set success or not
	 */
	public boolean setLexeltID(String p_LexeltID);

	/**
	 * set document ID
	 *
	 * @param p_DocID
	 *            document ID
	 * @return success or not
	 */
	public boolean setDocID(String p_DocID);

	/**
	 * clone instance
	 *
	 * @return clone instance
	 */
	public Object clone();
}
