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

package sg.edu.nus.comp.nlp.ims.lexelt;

import sg.edu.nus.comp.nlp.ims.instance.IInstance;

/**
 * a common lexelt.
 *
 * @author zhongzhi
 *
 */
public class CLexelt extends ALexelt {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor with lexelt id
	 *
	 * @param p_LexeltID
	 *            lexelt id
	 */
	public CLexelt(String p_LexeltID) {
		super(p_LexeltID);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.ILexelt#addInstance(sg.edu.nus.comp.nlp.ims.instance.IInstance, boolean)
	 */
	public boolean addInstance(IInstance p_iInstance, boolean p_AddToStat) {
		synchronized (this) {
			if (p_iInstance != null) {
				String id = p_iInstance.getID();
				if (id != null && !id.isEmpty()) {
					this.m_IDs.add(id);
					this.m_Instances.add(p_iInstance);
					if (p_AddToStat) {
						this.m_Statistic.addInstance(p_iInstance);
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * remove instance from lexelt no action will be done to the statistic of
	 * this lexelt
	 *
	 * @param p_Index
	 *            index
	 * @return removed instance
	 */
	@Override
	public IInstance removeInstance(int p_Index) {
		synchronized (this) {
			if (p_Index < this.m_Instances.size()) {
				this.m_IDs.remove(p_Index);
				return this.m_Instances.remove(p_Index);
			} else {
				throw new IndexOutOfBoundsException("index " + p_Index
						+ " is out of boundary(0 to " + this.m_Instances.size()
						+ ").");
			}
		}
	}

}
