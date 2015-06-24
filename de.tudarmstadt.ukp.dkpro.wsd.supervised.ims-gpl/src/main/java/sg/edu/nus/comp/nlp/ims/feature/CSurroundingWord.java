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

package sg.edu.nus.comp.nlp.ims.feature;

/**
 * surrounding word feature.
 *
 * @author zhongzhi
 *
 */
public class CSurroundingWord extends ABinaryFeature {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 */
	public CSurroundingWord() {
		this.m_Key = null;
		this.m_Value = true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.ABinaryFeature#clone()
	 */
	public Object clone() {
		CSurroundingWord clone = new CSurroundingWord();
		clone.m_Key = this.m_Key;
		return clone;
	}
}
