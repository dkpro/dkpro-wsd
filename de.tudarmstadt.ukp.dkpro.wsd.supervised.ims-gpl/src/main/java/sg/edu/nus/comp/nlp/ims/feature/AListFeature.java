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
 * feature type with list value.
 * @author zhongzhi
 *
 */
@SuppressWarnings("serial")
public abstract class AListFeature implements IFeature {
	// feature key
	protected String m_Key;
	// feature value
	protected String m_Value;

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeature#getKey()
	 */
	@Override
    public String getKey() {
		return this.m_Key;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeature#getValue()
	 */
	@Override
    public String getValue() {
		return this.m_Value;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeature#setKey(java.lang.String)
	 */
	@Override
    public boolean setKey(String p_Key) {
		if (p_Key != null) {
			p_Key = p_Key.trim();
			if (!p_Key.isEmpty()) {
				this.m_Key = p_Key;
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeature#setValue(java.lang.String)
	 */
	@Override
    public boolean setValue(String p_Value) {
		if (p_Value != null) {
			p_Value = p_Value.trim();
			if (!p_Value.isEmpty()) {
				this.m_Value = p_Value;
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
    public abstract Object clone();
}
