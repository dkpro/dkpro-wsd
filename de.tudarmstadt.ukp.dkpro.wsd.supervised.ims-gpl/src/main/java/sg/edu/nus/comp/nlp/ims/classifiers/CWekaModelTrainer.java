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

import sg.edu.nus.comp.nlp.ims.io.*;
import sg.edu.nus.comp.nlp.ims.lexelt.CModelInfo;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * call weka-3.2.3 classifier to train model for instances of a lexelt.
 *
 * @author zhongzhi
 *
 */
public class CWekaModelTrainer implements IModelTrainer {

	protected String m_ClassifierName = CMultiClassesSVM.class.getName();

	protected String[] m_Argvs = new String[] { "-I", "0" };

	protected int m_ClassIndex = -1;

	/**
	 * default constructor
	 */
	public CWekaModelTrainer() {
	}

	/**
	 * constructor with specified classifier and its parameters
	 *
	 * @param p_ClassifierName
	 *            classifier name
	 * @param p_Argvs
	 *            arguments
	 * @param p_ClassIndex
	 *            class index
	 */
	public CWekaModelTrainer(String p_ClassifierName, String[] p_Argvs,
			int p_ClassIndex) {
		this.m_ClassifierName = p_ClassifierName;
		this.m_Argvs = p_Argvs;
		this.m_ClassIndex = p_ClassIndex;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifier.IModelTrainer#train(java.lang.Object)
	 */
	@Override
	public Object train(Object p_Lexelt) throws Exception {
		ILexelt lexelt = (ILexelt) p_Lexelt;
		CModelInfo retVal = new CModelInfo();
		retVal.lexelt = lexelt.getID();
		retVal.statistic = lexelt.getStatistic();
		if (((IStatistic) retVal.statistic).getTags().size() <= 1) {
			retVal.model = null;
		} else {
			String classifierName = this.m_ClassifierName;
			String[] args = this.m_Argvs.clone();
			ILexeltWriter lexeltWriter = new CWekaSparseLexeltWriter();
			Instances instances = (Instances) lexeltWriter.getInstances(lexelt);
			Classifier model = null;
			int classIdx = this.m_ClassIndex;
			if (classIdx < 0) {
				classIdx = instances.numAttributes() - 1;
			}
			instances.setClassIndex(classIdx);
			model = Classifier.forName(classifierName, args);
			model.buildClassifier(instances);
			retVal.model = model;
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifier.IModelTrainer#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] p_Options) {
		if (p_Options.length < 2) {
			throw new IllegalArgumentException(
					"modelDir statisticDir classIndex unknown");
		}
		this.m_ClassIndex = Integer.parseInt(p_Options[0]);
		this.m_ClassifierName = p_Options[1];
		this.m_Argvs = new String[p_Options.length - 2];
		System.arraycopy(p_Options, 2, this.m_Argvs, 0, p_Options.length - 2);
	}

}
