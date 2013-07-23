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

import liblinear.Linear;
import liblinear.Model;
import liblinear.Parameter;
import liblinear.Problem;
import liblinear.SolverType;
import sg.edu.nus.comp.nlp.ims.io.CLibLinearLexeltWriter;
import sg.edu.nus.comp.nlp.ims.io.ILexeltWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.CModelInfo;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;

/**
 * call liblinear classifier to train model for instances of a lexelt.
 * @author zhongzhi
 *
 */
public class CLibLinearTrainer implements IModelTrainer {

	// bias
	protected double m_Bias = -1;
	Parameter m_Param = new Parameter(SolverType.L2R_L2LOSS_SVC_DUAL, 1, Double.POSITIVE_INFINITY);

	/**
	 * set parameters
	 * @param p_Param paramters
	 */
	public void setParam(Parameter p_Param) {
		this.m_Param = p_Param;
	}

	/**
	 * get parameters
	 * @return parameters
	 */
	public Parameter getParam() {
		return this.m_Param;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.IModelTrainer#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) {

	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.IModelTrainer#train(java.lang.Object)
	 */
	@Override
	public Object train(Object p_Lexelt) throws Exception {
		ILexelt lexelt = (ILexelt) p_Lexelt;
		CModelInfo retVal = new CModelInfo();
		retVal.lexelt = lexelt.getID();
		retVal.statistic = lexelt.getStatistic();
		if (((IStatistic) retVal.statistic).getTagsInOrder().size() <= 1) {
			retVal.model = null;
		} else {
			if (this.m_Param.getEps() == Double.POSITIVE_INFINITY) {
				if (this.m_Param.getSolverType() == SolverType.L2R_LR || this.m_Param.getSolverType() == SolverType.L2R_L2LOSS_SVC) {
					this.m_Param.setEps(0.01);
				} else if (this.m_Param.getSolverType() == SolverType.L2R_L2LOSS_SVC_DUAL || this.m_Param.getSolverType() == SolverType.L2R_L1LOSS_SVC_DUAL
						|| this.m_Param.getSolverType() == SolverType.MCSVM_CS) {
					this.m_Param.setEps(0.1);
				}
			}

			ILexeltWriter lexeltWriter = new CLibLinearLexeltWriter(this.m_Bias);
			Problem prob = (Problem) lexeltWriter.getInstances(lexelt);
			Model model = Linear.train(prob, this.m_Param);
			retVal.model = model;
		}
		return retVal;
	}

}
