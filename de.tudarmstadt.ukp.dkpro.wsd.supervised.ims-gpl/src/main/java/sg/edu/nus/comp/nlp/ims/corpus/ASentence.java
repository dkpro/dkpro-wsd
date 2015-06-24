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

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * superclass of sentence.
 *
 * @author zhongzhi
 *
 */
public abstract class ASentence implements ISentence {
	// items in sentence
	protected ArrayList<IItem> m_Items = null;
	// sentence values
	protected Hashtable<String, String> m_Values = new Hashtable<String, String>();

	/**
	 * default constructor
	 */
	public ASentence() {
		this.m_Items = new ArrayList<IItem>();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ISentence#clear()
	 */
	public void clear() {
		this.m_Items.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ISentence#getItem(int)
	 */
	public IItem getItem(int p_Index) {
		if (p_Index >= 0 && p_Index < this.m_Items.size()) {
			return this.m_Items.get(p_Index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ISentence#get(java.lang.String)
	 */
	public String get(String p_Key) {
		return this.m_Values.get(p_Key);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ISentence#set(java.lang.String, java.lang.String)
	 */
	public boolean set(String p_Key, String p_Value) {
		this.m_Values.put(p_Key, p_Value);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ISentence#size()
	 */
	public int size() {
		return this.m_Items.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retVal = null;
		if (this.m_Items != null && this.m_Items.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < this.m_Items.size(); i++) {
				if (i != 0) {
					builder.append(" ");
				}
				builder.append(this.m_Items.get(i).toString());
			}
			retVal = builder.toString();
		}
		return retVal;
	}
}
