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

package sg.edu.nus.comp.nlp.ims.io;

import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;

/**
 * lexelt writer interface. convert the instances in a lexelt to a special
 * format.
 *
 * @author zhongzhi
 *
 */
public interface ILexeltWriter {
	/**
	 * write information of p_iLexelt to p_FileName in some format
	 *
	 * @param p_Filename
	 *            file name
	 * @param p_Lexelt
	 *            lexelt
	 * @throws Exception exception while converting p_Lexelt
	 */
	public void write(String p_Filename, ILexelt p_Lexelt) throws Exception;

	/**
	 * write lexelt to a string
	 *
	 * @param p_Lexelt
	 *            lexelt
	 * @return feature vector string
	 * @throws Exception exception while converting p_Lexelt
	 */
	public String toString(ILexelt p_Lexelt) throws Exception;

	/**
	 * extract instances from p_iLexelt
	 *
	 * @param p_Lexelt
	 *            lexelt
	 * @return instances
	 * @throws Exception exception while converting p_Lexelt
	 */
	public Object getInstances(ILexelt p_Lexelt) throws Exception;
}
