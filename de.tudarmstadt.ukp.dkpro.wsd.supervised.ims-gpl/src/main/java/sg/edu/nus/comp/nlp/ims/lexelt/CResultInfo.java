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

/**
 * result information
 * @author zhongzhi
 *
 */
public class CResultInfo {
	// probabilities
	public double[][] probabilities;
	// classes
	public String[] classes;
	// lexelt
	public String lexelt;
	public String[] ids;
	public String[] docs;

	public CResultInfo() {

	}

	/**
	 * get number of instances
	 *
	 * @return number of instances
	 */
	public int size() {
		return this.ids.length;
//		return this.lexelt.size();
	}

	/**
	 * get number of classes
	 *
	 * @return number of classes
	 */
	public int numClasses() {
		return this.classes.length;
	}

	/**
	 * get the answer index for instance
	 *
	 * @param p_Instance
	 *            instance index
	 * @return answer index
	 */
	public int getAnswer(int p_Instance) {
		int max = -1;
		double maxValue = -1;
		for (int i = 0; i < probabilities[p_Instance].length; i++) {
			if (maxValue < probabilities[p_Instance][i]) {
				maxValue = probabilities[p_Instance][i];
				max = i;
			}
		}
		return max;
	}

	/**
	 * get instance id
	 *
	 * @param p_Instance
	 *            instance index
	 * @return instance id
	 */
	public String getID(int p_Instance) {
		return this.ids[p_Instance];
//		return this.lexelt.getInstanceID(p_Instance);
	}

	/**
	 * get lexelt id
	 * @return lexelt id
	 */
	public String getID() {
		return this.lexelt;
	}

	/**
	 * get instance document id
	 * @param p_Instance instance index
	 * @return document id
	 */
	public String getDocID(int p_Instance) {
		return this.docs[p_Instance];
	}
}