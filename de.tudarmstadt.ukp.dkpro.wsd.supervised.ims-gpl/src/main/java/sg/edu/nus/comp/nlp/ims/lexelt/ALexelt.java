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

import sg.edu.nus.comp.nlp.ims.instance.IInstance;

/**
 * abstract lexelt.
 *
 * @author zhongzhi
 *
 */
public abstract class ALexelt implements ILexelt {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;

	// statistic of instances in this lexelt
	protected IStatistic m_Statistic;

	// id of instances
	protected ArrayList<String> m_IDs;

	// instance list
	protected ArrayList<IInstance> m_Instances;

	// lexelt id
	protected String m_LexeltID;

	/**
	 * constructor with lexelt id
	 *
	 * @param p_LexeltID
	 *            lexelt id
	 */
	public ALexelt(String p_LexeltID) {
		this.m_Statistic = new CStatistic();
		this.m_IDs = new ArrayList<String>();
		this.m_Instances = new ArrayList<IInstance>();
		this.m_LexeltID = p_LexeltID;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getID()
	 */
	public String getID() {
		return this.m_LexeltID;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getInstanceIDs()
	 */
	public ArrayList<String> getInstanceIDs() {
		return this.m_IDs;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getStatistic()
	 */
	public IStatistic getStatistic() {
		return this.m_Statistic;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getInstanceID(int)
	 */
	public String getInstanceID(int p_Index) {
		return this.m_IDs.get(p_Index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getInstanceDocID(int)
	 */
	public String getInstanceDocID(int p_Index) {
		return this.m_Instances.get(p_Index).getDocID();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#size()
	 */
	public int size() {
		return this.m_Instances.size();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getTag(java.lang.String)
	 */
	public ArrayList<String> getTag(String p_InstanceID) {
		int index = this.m_IDs.indexOf(p_InstanceID);
		return this.getTag(index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getTag(int)
	 */
	public ArrayList<String> getTag(int p_Index) {
		return this.m_Instances.get(p_Index).getTag();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getValue(java.lang.String, java.lang.String)
	 */
	public String getValue(String p_InstanceID, String p_Key) {
		int index = this.m_IDs.indexOf(p_InstanceID);
		return this.getValue(index, p_Key);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getValue(int, java.lang.String)
	 */
	public String getValue(int p_Index, String p_Key) {
		return this.m_Instances.get(p_Index).getFeatureValue(p_Key);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getValue(int, int)
	 */
	public String getValue(int p_Index, int p_KeyIndex) {
		IInstance instance = this.m_Instances.get(p_Index);
		return instance.getFeatureValue(p_KeyIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#getInstance(int)
	 */
	public IInstance getInstance(int p_Index) {
		return this.m_Instances.get(p_Index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#addInstance(sg.edu.nus.comp.nlp.ims.instance.IInstance)
	 */
	public boolean addInstance(IInstance p_Instance) {
		return this.addInstance(p_Instance, false);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#setStatistic(sg.edu.nus.comp.nlp.ims.lexelt.IStatistic)
	 */
	public boolean setStatistic(IStatistic p_Statistic) {
		this.m_Statistic = p_Statistic;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#removeInstance(java.lang.String)
	 */
	public IInstance removeInstance(String p_InstanceID) {
		int index = this.m_IDs.indexOf(p_InstanceID);
		return this.removeInstance(index);
	}

}
