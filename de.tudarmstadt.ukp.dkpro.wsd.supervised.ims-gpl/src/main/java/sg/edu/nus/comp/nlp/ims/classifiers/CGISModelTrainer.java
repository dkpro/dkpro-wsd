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

import java.io.Reader;
import java.io.StringReader;

import opennlp.maxent.*;
import sg.edu.nus.comp.nlp.ims.io.CGISLexeltWriter;
import sg.edu.nus.comp.nlp.ims.io.ILexeltWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.CModelInfo;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;
import sg.edu.nus.comp.nlp.ims.util.CArgumentManager;

/**
 * call maxent with GIS kernel to train model for instances of a lexelt.
 * @author zhongzhi
 *
 */
public class CGISModelTrainer implements IModelTrainer {

	protected int m_Iteration = 300;
	protected int m_Cutoff = 0;
	protected double m_SmoothingObservation = 0.1;
	protected boolean m_PrintMessages = false;
	protected boolean m_Smoothing = false;

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.IModelTrainer#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] options) {
		CArgumentManager argmgr = new CArgumentManager(options);
		if (argmgr.has("i")) {
			this.m_Iteration = Integer.parseInt(argmgr.get("i"));
		}
		if (argmgr.has("c")) {
			this.m_Cutoff = Integer.parseInt(argmgr.get("c"));
		}
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
			// convert the data into maxent format
			ILexeltWriter lexeltWriter = new CGISLexeltWriter();
			Reader reader = new StringReader((String) lexeltWriter.getInstances((ILexelt) p_Lexelt));
			EventStream es = new BasicEventStream(new PlainTextByLineDataStream(reader));
			GIS.SMOOTHING_OBSERVATION = this.m_SmoothingObservation;
			retVal.model = GIS.trainModel(es, m_Iteration, m_Cutoff, this.m_Smoothing, this.m_PrintMessages);
			reader.close();
		}
		return retVal;
	}

}
