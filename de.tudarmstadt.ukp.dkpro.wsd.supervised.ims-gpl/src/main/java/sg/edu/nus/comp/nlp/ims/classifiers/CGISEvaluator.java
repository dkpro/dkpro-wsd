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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import opennlp.maxent.BasicContextGenerator;
import opennlp.maxent.ContextGenerator;
import opennlp.maxent.GISModel;
import opennlp.maxent.io.ObjectGISModelReader;
import sg.edu.nus.comp.nlp.ims.io.CGISLexeltWriter;
import sg.edu.nus.comp.nlp.ims.io.ILexeltWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.CResultInfo;
import sg.edu.nus.comp.nlp.ims.lexelt.CStatistic;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;
import sg.edu.nus.comp.nlp.ims.util.CArgumentManager;

/**
 * call maxent with GIS kernel to evaluate instances of a lexelt.
 * @author zhongzhi
 *
 */
public class CGISEvaluator extends APreloadEvaluator {
	// directory stores statistic files
	protected String m_StatDir = null;
	// directory stores model files
	protected String m_ModelDir = null;
	// class index
	protected int m_ClassIndex = -1;
	protected static Pattern LEXELTPATTERN = Pattern.compile("^(.*\\.[nvar])\\-.*$");

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.APreloadEvaluator#loadModelFromDisk(java.lang.String)
	 */
	@Override
	protected Object loadModelFromDisk(String p_LexeltID) throws Exception {
		Object model = null;
		File modelFile = new File(this.m_ModelDir + this.m_FileSeparator + p_LexeltID + ".model.gz");
		if (!modelFile.exists()) {
			modelFile = new File(this.m_ModelDir + this.m_FileSeparator + p_LexeltID + ".model");
		}
		if (!modelFile.exists()) {
			Matcher matcher = LEXELTPATTERN.matcher(p_LexeltID);
			if (matcher.matches()) {
				model = this.getModel(matcher.group(1));
			}
		} else {
			InputStream is = new FileInputStream(modelFile);
			if (modelFile.getName().endsWith(".gz")) {
				is = new GZIPInputStream(is);
			}
			ObjectGISModelReader reader = new ObjectGISModelReader(new ObjectInputStream(is));
			model = reader.getModel();
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.APreloadEvaluator#loadStatisticFromDisk(java.lang.String)
	 */
	@Override
	protected Object loadStatisticFromDisk(String p_LexeltID) throws Exception {
		Object stat = null;
		File statFile = new File(this.m_StatDir + this.m_FileSeparator + p_LexeltID + ".stat.gz");
		if (!statFile.exists()) {
			statFile = new File(this.m_StatDir + this.m_FileSeparator + p_LexeltID + ".stat");
		}
		if (!statFile.exists()) {
			Matcher matcher = LEXELTPATTERN.matcher(p_LexeltID);
			if (matcher.matches()) {
				stat = this.getStatistic(matcher.group(1));
			}
		} else {
			CStatistic tmp = new CStatistic();
			if (!tmp.loadFromFile(statFile.getAbsolutePath())) {
				tmp = null;
			}
			stat = tmp;
		}
		return stat;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.IEvaluator#evaluate(java.lang.Object)
	 */
	@Override
	public Object evaluate(Object p_Lexelt) throws Exception {
		ILexelt lexelt = (ILexelt) p_Lexelt;
		String lexeltID = lexelt.getID();
		IStatistic stat = (IStatistic) this.getStatistic(lexeltID);
		int type = 2;
		String firstSense = this.m_UnknownSense;
		if (stat == null) {
			type = 1;
			if (this.m_SenseIndex != null) {
				String first = this.m_SenseIndex.getFirstSense(lexeltID);
				if (first != null) {
					firstSense = first;
				}
			}
		} else {
			if (stat.getTagsInOrder().size() == 1) {
				type = 1;
				firstSense = stat.getTagsInOrder().get(0);
			} else {
				type = stat.getTagsInOrder().size();
			}
		}
		CResultInfo retVal = new CResultInfo();
		switch (type) {
		case 0:
			throw new Exception("no tag for lexelt " + lexeltID + ".");
		case 1:
			retVal.lexelt = lexelt.getID();
			retVal.docs = new String[lexelt.size()];
			retVal.ids = new String[lexelt.size()];
			retVal.classes = new String[] { firstSense };
			retVal.probabilities = new double[lexelt.size()][1];
			for (int i = 0; i < retVal.probabilities.length; i++) {
				retVal.probabilities[i][0] = 1;
				retVal.docs[i] = lexelt.getInstanceDocID(i);
				retVal.ids[i] = lexelt.getInstanceID(i);
			}
			break;
		default:
			lexelt.setStatistic(stat);
			GISModel model = (GISModel) this.getModel(lexeltID);
			ILexeltWriter lexeltWriter = new CGISLexeltWriter();
			retVal.lexelt = lexelt.getID();
			retVal.docs = new String[lexelt.size()];
			retVal.ids = new String[lexelt.size()];
			retVal.probabilities = new double[lexelt.size()][];
			retVal.classes = new String[stat.getTagsInOrder().size()];
			stat.getTagsInOrder().toArray(retVal.classes);
			BufferedReader instances = new BufferedReader(new StringReader(lexeltWriter.toString(lexelt)));
			for (int i = 0; i < lexelt.size(); i++) {
				String instance = instances.readLine();
				retVal.docs[i] = lexelt.getInstanceDocID(i);
				retVal.ids[i] = lexelt.getInstanceID(i);
				retVal.probabilities[i] = new double[retVal.classes.length];
				this.predict(model, instance, retVal.probabilities[i]);
			}
		}
		return retVal;
	}

	/**
	 * predict one instance
	 * @param model GISModel
	 * @param instance test instance
	 * @param distribution return distribution
	 * @return predicted sense
	 */
	protected int predict(GISModel model, String instance, double[] distribution) {
        // convert the instance into maxent format
        ContextGenerator cg = new BasicContextGenerator();
        // predict the distribution
        double [] ocs = model.eval(cg.getContext(instance));
        for (int i = 0; i < model.getNumOutcomes(); i++) {
        	distribution[i] = ocs[model.getIndex(Integer.toString(i + 1))];
        }
        return Integer.parseInt(model.getBestOutcome(ocs)) - 1;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.IEvaluator#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] p_Options) {
		CArgumentManager argmgr = new CArgumentManager(p_Options);
		if (argmgr.has("m")) {
			this.m_ModelDir = argmgr.get("m");
			this.m_StatDir = this.m_ModelDir;
		}
		if (argmgr.has("s")) {
			this.m_StatDir = argmgr.get("s");
		}
		if (argmgr.has("u")) {
			this.m_UnknownSense = argmgr.get("u");
		}
		if (argmgr.has("cap")) {
			this.m_Capacity = Integer.parseInt(argmgr.get("cap"));
		}
		if (this.m_Capacity <= 0) {
			this.m_Capacity = 1;
		}
		try {
			ArrayList<String> lexelts = new ArrayList<String>();
			if (argmgr.has("l") && argmgr.get("l") != null) {
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(
						argmgr.get("l")));
				while ((line = reader.readLine()) != null) {
					lexelts.add(line);
				}
				reader.close();
			}
			ArrayList<String> always = new ArrayList<String>();
			if (argmgr.has("always") && argmgr.get("always") != null) {
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(argmgr.get("always")));
				while ((line = reader.readLine()) != null) {
					always.add(line);
				}
				reader.close();
			}
			this.initial(lexelts, this.m_Capacity, always);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

}
