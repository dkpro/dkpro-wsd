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

import java.util.ArrayList;
import java.util.Hashtable;

import sg.edu.nus.comp.nlp.ims.feature.IFeature;

/**
 * a common instance.
 *
 * @author zhongzhi
 *
 */
public class CInstance implements IInstance {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;
	// feature keys
	protected ArrayList<String> m_Keys;
	// key to index
	protected Hashtable<String, Integer> m_KeyMap;
	// features
	protected ArrayList<IFeature> m_Features;
	// instance id
	protected String m_ID;
	// document id
	protected String m_DocID;
	// lexelt id
	protected String m_LexeltID;
	// instance tags
	protected ArrayList<String> m_Tags;

	/**
	 * initial with instance id and lexelt id
	 *
	 * @param p_InstanceID
	 *            instance id
	 * @param p_LexeltID
	 *            instance lexelt id
	 */
	public CInstance(String p_InstanceID, String p_LexeltID) {
		this(p_InstanceID, p_LexeltID, null);
	}

	/**
	 * initial with instance id, lexelt id and document id
	 *
	 * @param p_InstanceID
	 *            instance id
	 * @param p_LexeltID
	 *            instance lexelt id
	 * @param p_DocumentID
	 *            instance document id
	 */
	public CInstance(String p_InstanceID, String p_LexeltID, String p_DocumentID) {
		this.m_ID = p_InstanceID;
		this.m_LexeltID = p_LexeltID;
		this.m_DocID = p_DocumentID;
		this.m_Keys = new ArrayList<String>();
		this.m_KeyMap = new Hashtable<String, Integer>();
		this.m_Features = new ArrayList<IFeature>();
		this.m_Tags = new ArrayList<String>();
	}

	/**
	 * initial with instance id
	 *
	 * @param p_InstanceID
	 *            instance id
	 */
	public CInstance(String p_InstanceID) {
		this.m_ID = p_InstanceID;
		this.m_LexeltID = null;
		this.m_Keys = new ArrayList<String>();
		this.m_KeyMap = new Hashtable<String, Integer>();
		this.m_Features = new ArrayList<IFeature>();
		this.m_Tags = new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#addFeature(sg.edu.nus.comp.nlp.ims.feature.IFeature)
	 */
	public boolean addFeature(IFeature p_Feature) {
		String key = null;
		if (p_Feature != null) {
			key = p_Feature.getKey();
			if (key != null && !key.isEmpty()) {
				if (this.m_KeyMap.containsKey(key)) {
					int index = this.m_KeyMap.get(key);
					this.m_Features.set(index, p_Feature);
				} else {
					this.m_KeyMap.put(key, this.m_Keys.size());
					this.m_Keys.add(key);
					this.m_Features.add(p_Feature);
				}
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getTag()
	 */
	public ArrayList<String> getTag() {
		return this.m_Tags;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getID()
	 */
	public String getID() {
		return this.m_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getDocID()
	 */
	public String getDocID() {
		return this.m_DocID;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getLexeltID()
	 */
	public String getLexeltID() {
		return this.m_LexeltID;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#setDocID(java.lang.String)
	 */
	public boolean setDocID(String p_DocID) {
		this.m_DocID = p_DocID;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#setLexeltID(java.lang.String)
	 */
	public boolean setLexeltID(String p_LexeltID) {
		p_LexeltID = p_LexeltID.trim();
		this.m_LexeltID = p_LexeltID;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#setTag(java.lang.String)
	 */
	public boolean setTag(String p_Tag) {
		return this.setTag(p_Tag, true);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#setTag(java.lang.String, boolean)
	 */
	public boolean setTag(String p_Tag, boolean p_Add) {
		if (p_Tag != null) {
			p_Tag = p_Tag.trim();
			if (!p_Tag.isEmpty()) {
				if (!p_Add) {
					this.m_Tags.clear();
				}
				this.m_Tags.add(p_Tag);
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getFeature(java.lang.String)
	 */
	public IFeature getFeature(String p_Key) {
		if (this.m_KeyMap.containsKey(p_Key)) {
			return this.m_Features.get(this.m_KeyMap.get(p_Key));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getFeature(int)
	 */
	public IFeature getFeature(int p_Index) {
		if (p_Index >= 0 && p_Index < this.m_Features.size()) {
			return this.m_Features.get(p_Index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getFeatureValue(java.lang.String)
	 */
	public String getFeatureValue(String p_Key) {
		return this.getFeatureValue(this.m_KeyMap.get(p_Key));
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getFeatureValue(int)
	 */
	public String getFeatureValue(int p_KeyIndex) {
		return this.m_Features.get(p_KeyIndex).getValue();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#getFeatureName(int)
	 */
	public String getFeatureName(int p_Index) {
		if (p_Index >= 0 && p_Index < this.m_Features.size()) {
			return this.m_Keys.get(p_Index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.instance.IInstance#size()
	 */
	public int size() {
		return this.m_Keys.size();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		CInstance copy = new CInstance(this.m_ID);
		copy.m_LexeltID = this.m_LexeltID;
		copy.m_KeyMap = (Hashtable<String, Integer>) this.m_KeyMap.clone();
		copy.m_Features = (ArrayList<IFeature>) this.m_Features.clone();
		copy.m_Keys = (ArrayList<String>) this.m_Keys.clone();
		copy.m_Tags = (ArrayList<String>) this.m_Tags.clone();
		return copy;
	}

}
