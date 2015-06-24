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

package sg.edu.nus.comp.nlp.ims.classifiers;

import sg.edu.nus.comp.nlp.ims.util.ISenseIndex;

/**
 * evaluator interface.
 * 
 * @author zhongzhi
 * 
 */
public interface IEvaluator {
	/**
	 * evaluate p_Lexelt
	 * 
	 * @param p_Lexelt
	 *            lexelt
	 * @return evaluation result
	 * @throws Exception
	 *             evaluation exception
	 */
	public Object evaluate(Object p_Lexelt) throws Exception;

	/**
	 * set sense index
	 * 
	 * @param p_SenseIndex
	 *            sense index
	 */
	public void setSenseIndex(ISenseIndex p_SenseIndex);

	/**
	 * set options
	 * 
	 * @param p_Options
	 *            options
	 */
	public void setOptions(String[] p_Options);
}
