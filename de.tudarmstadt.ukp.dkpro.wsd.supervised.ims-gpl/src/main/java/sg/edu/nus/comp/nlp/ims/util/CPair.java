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

package sg.edu.nus.comp.nlp.ims.util;

/**
 * pair template
 * @author zhongzhi
 *
 */
public class CPair <F, S> {
	// first value
	protected F m_First;
	// second value
	protected S m_Second;

	/**
	 * constructor
	 * @param p_First first value
	 * @param p_Second second value
	 */
	public CPair(F p_First, S p_Second) {
		m_First = p_First;
		m_Second = p_Second;
	}

	/**
	 * get first value
	 * @return first
	 */
	public F getFirst() {
		return m_First;
	}

	/**
	 * get second value
	 * @return second
	 */
	public S getSecond() {
		return m_Second;
	}

}
