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

import libsvm.svm_model;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import sg.edu.nus.comp.nlp.ims.io.CLibSVMLexeltWriter;
import sg.edu.nus.comp.nlp.ims.io.ILexeltWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.CModelInfo;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;

/**
 * call libsvm classifier to train model for instances of a lexelt.
 * @author zhongzhi
 *
 */
public class CLibSVMTrainer implements IModelTrainer {
	protected svm_parameter m_Param = new svm_parameter();

	/**
	 * constructor
	 */
	public CLibSVMTrainer() {
		// default values
		this.m_Param.svm_type = svm_parameter.C_SVC;
		this.m_Param.kernel_type = svm_parameter.RBF;
		this.m_Param.degree = 3;
		this.m_Param.gamma = 0;	// 1/k
		this.m_Param.coef0 = 0;
		this.m_Param.nu = 0.5;
		this.m_Param.cache_size = 100;
		this.m_Param.C = 1;
		this.m_Param.eps = 1e-3;
		this.m_Param.p = 0.1;
		this.m_Param.shrinking = 1;
		this.m_Param.probability = 0;
		this.m_Param.nr_weight = 0;
		this.m_Param.weight_label = new int[0];
		this.m_Param.weight = new double[0];
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.IModelTrainer#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) {
		/*
		CArgumentManager argmgr = new CArgumentManager(options);
		if (argmgr.has("s")) {
			this.m_Param.svm_type = Integer.parseInt(argmgr.get("s"));
		}
		if (argmgr.has("h")) {
			this.m_Param.probability = Integer.parseInt(argmgr.get("h"));
		}
		if (argmgr.has("p")) {
			this.m_Param.p = Double.parseDouble(argmgr.get("p"));
		}
		if (argmgr.has("e")) {
			this.m_Param.eps = Double.parseDouble(argmgr.get("e"));
		}
		if (argmgr.has("c")) {
			this.m_Param.C = Double.parseDouble(argmgr.get("c"));
		}
		if (argmgr.has("m")) {
			this.m_Param.cache_size = Double.parseDouble(argmgr.get("m"));
		}
		if (argmgr.has("n")) {
			this.m_Param.nu = Double.parseDouble(argmgr.get("n"));
		}
		if (argmgr.has("r")) {
			this.m_Param.coef0 = Double.parseDouble(argmgr.get("r"));
		}
		if (argmgr.has("g")) {
			this.m_Param.gamma = Double.parseDouble(argmgr.get("g"));
		}
		if (argmgr.has("d")) {
			this.m_Param.degree = Integer.parseInt(argmgr.get("d"));
		}
		if (argmgr.has("t")) {
			this.m_Param.kernel_type = Integer.parseInt(argmgr.get("s"));
		}*/
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
			ILexeltWriter lexeltWriter = new CLibSVMLexeltWriter();
			svm_problem prob = (svm_problem) lexeltWriter.getInstances(lexelt);
			svm_model model = libsvm.svm.svm_train(prob, this.m_Param);
			retVal.model = model;
		}
		return retVal;
	}

	/**
	 * set parameters
	 * @param p_Param parameters
	 */
	public void setParam(svm_parameter p_Param) {
		this.m_Param = p_Param;
	}

	/**
	 * get parameters
	 * @return parameters
	 */
	public svm_parameter getParam() {
		return this.m_Param;
	}
}
