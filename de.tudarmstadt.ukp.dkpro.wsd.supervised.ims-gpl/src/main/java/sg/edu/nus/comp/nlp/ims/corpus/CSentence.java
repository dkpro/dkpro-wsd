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
 * common sentence conducted with CItems.
 *
 * @author zhongzhi
 *
 */
public class CSentence extends ASentence {

	/**
	 * default constructor
	 */
	public CSentence() {
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.ISentence#appendItem(sg.edu.nus.comp.nlp.ims.corpus.IItem)
	 */
	public boolean appendItem(IItem p_Item) {
		if (p_Item != null) {
			return this.m_Items.add(p_Item);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		CSentence clone = new CSentence();
		for (IItem item : this.m_Items) {
			clone.appendItem((IItem) item.clone());
		}
		return clone;
	}

}
