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
 * feature with a double figure as value.
 * @author zhongzhi
 *
 */
public class CDoubleFeature extends ANumericFeature {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected Double m_Value = Double.MAX_VALUE;

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.ANumeralFeature#clone()
	 */
	@Override
	public Object clone() {
		CDoubleFeature clone = new CDoubleFeature();
		clone.m_Key = this.m_Key;
		clone.m_Value = this.m_Value;
		return null;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeature#getValue()
	 */
	@Override
	public String getValue() {
		return this.m_Value.toString();
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeature#setValue(java.lang.String)
	 */
	@Override
	public boolean setValue(String value) {
		try {
			this.m_Value = Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
		return true;
	}

}
