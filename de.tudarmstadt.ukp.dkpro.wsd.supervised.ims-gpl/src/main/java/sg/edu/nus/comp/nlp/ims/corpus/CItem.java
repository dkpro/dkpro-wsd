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
 * common item in a sentence.
 *
 * @author zhongzhi
 *
 */
public class CItem extends AItem {

	/**
	 * default constructor
	 */
	public CItem() {
		for (int i = Features.values().length - 1; i >= 0; i--) {
			this.m_Values.add(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.corpus.AItem#clone()
	 */
	public IItem clone() {
		CItem clone = new CItem();
		for (String value : this.m_Values) {
			clone.m_Values.add(value);
		}
		return clone;
	}

}
