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
 * sense index interface. refer to the index.sense file in wordnet.
 * 
 * @author zhongzhi
 * 
 */
public interface ISenseIndex {
	/**
	 * get the first sense of p_Lexelt
	 * 
	 * @param p_Lexelt
	 *            lexelt id
	 * @return first sense
	 */
	public String getFirstSense(String p_Lexelt);

	/**
	 * get sense number of p_Sense
	 * 
	 * @param p_Sense
	 *            sense
	 * @return sense number
	 */
	public int getSenseNo(String p_Sense);

}
