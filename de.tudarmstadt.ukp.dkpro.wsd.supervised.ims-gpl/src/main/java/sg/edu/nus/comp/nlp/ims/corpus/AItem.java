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

/**
 * abstract item.
 *
 * @author zhongzhi
 *
 */
public abstract class AItem implements IItem {

	/**
	 * feature types in item
	 * @author zhongzhi
	 *
	 */
	public enum Features {
		TOKEN, LEMMA, POS
	}

	// values of the item
	protected ArrayList<String> m_Values = new ArrayList<String>();

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.IItem#size()
	 */
	public int size() {
		return this.m_Values.size();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.IItem#set(int, java.lang.String)
	 */
	public boolean set(int p_Index, String p_Value) {
		if (p_Index >= 0 && p_Index < this.m_Values.size()) {
			if (p_Value != null) {
				p_Value = p_Value.trim();
			}
			this.m_Values.set(p_Index, p_Value);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.IItem#get(int)
	 */
	public String get(int p_Index) {
		if (p_Index >= 0 && p_Index < this.m_Values.size()) {
			return this.m_Values.get(p_Index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < this.m_Values.size(); i++) {
			if (i != 0) {
				builder.append("/");
			}
			builder.append(this.m_Values.get(i));
		}
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract IItem clone();
}
