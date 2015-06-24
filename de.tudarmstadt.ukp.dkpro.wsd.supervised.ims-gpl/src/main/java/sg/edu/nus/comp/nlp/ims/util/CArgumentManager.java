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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * argument manager.
 *
 * @author zhongzhi
 *
 */
public class CArgumentManager {
	// key pattern
	protected static Pattern KEYPATTERN = Pattern.compile("^-(.*)$");

	// key -> index of key in m_Arguments
	protected Hashtable<String, String> m_Keys = new Hashtable<String, String>();

	// argument list
	protected ArrayList<String> m_Arguments = new ArrayList<String>();

	/**
	 * constructor
	 * @param p_Args arguments
	 */
	public CArgumentManager(String[] p_Args) {
		if (p_Args != null) {
			Matcher matcher = null;
			for (int i = 0; i < p_Args.length; i++) {
				matcher = KEYPATTERN.matcher(p_Args[i]);
				if (matcher.find()) {
					String key = matcher.group(1);
					if (++i >= p_Args.length) {
						throw new IllegalArgumentException(
								"argument format is wrong.");
					}
					this.m_Keys.put(key, p_Args[i]);
				} else {
					this.m_Arguments.add(p_Args[i]);
				}
			}
		}
	}

	/**
	 * whether has key p_Key
	 * @param p_Key key
	 * @return true or false
	 */
	public boolean has(String p_Key) {
		if (this.m_Keys.containsKey(p_Key)) {
			return true;
		}
		return false;
	}

	/**
	 * if p_Key is in the arguments, return the true value else return null
	 * @param p_Key key
	 * @return value
	 */
	public String get(String p_Key) {
		if (this.has(p_Key)) {
			return this.m_Keys.get(p_Key);
		}
		return null;
	}

	/**
	 * the size of arguments
	 * @return size
	 */
	public int size() {
		return this.m_Arguments.size();
	}

	/**
	 * get the argument
	 * @param p_Index index
	 * @return value
	 */
	public String get(int p_Index) {
		if (p_Index >= 0 && p_Index < this.m_Arguments.size()) {
			return this.m_Arguments.get(p_Index);
		}
		return null;
	}
}
