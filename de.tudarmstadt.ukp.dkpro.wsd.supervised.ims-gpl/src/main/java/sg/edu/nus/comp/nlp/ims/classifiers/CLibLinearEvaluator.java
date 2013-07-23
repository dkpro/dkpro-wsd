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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import liblinear.FeatureNode;
import liblinear.Linear;
import liblinear.Model;
import liblinear.Problem;

import sg.edu.nus.comp.nlp.ims.io.CLibLinearLexeltWriter;
import sg.edu.nus.comp.nlp.ims.io.ILexeltWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.CStatistic;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;
import sg.edu.nus.comp.nlp.ims.lexelt.CResultInfo;
import sg.edu.nus.comp.nlp.ims.util.CArgumentManager;

/**
 * call liblinear to evaluate instances of a lexelt.
 * @author zhongzhi
 *
 */
public class CLibLinearEvaluator extends APreloadEvaluator {
	// directory stores statistic files
	protected String m_StatDir = null;
	// directory stores model files
	protected String m_ModelDir = null;
	// class index
	protected int m_ClassIndex = -1;
	protected static Pattern LEXELTPATTERN = Pattern.compile("^(.*\\.[nvar])\\-.*$");

	/**
	 * default constructor
	 */
	public CLibLinearEvaluator() {
		this(".", ".");
	}

	/**
	 * constructor with some parameters
	 * @param p_StatDir statistic directory
	 * @param p_ModelDir model directory
	 */
	public CLibLinearEvaluator(String p_StatDir, String p_ModelDir) {
		this(p_StatDir, p_ModelDir, "U");
	}

	/**
	 * constructor with some parameters
	 * @param p_StatDir statistic directory
	 * @param p_ModelDir model directory
	 * @param p_UnknownSense unknown sense mark
	 */
	public CLibLinearEvaluator(String p_StatDir, String p_ModelDir, String p_UnknownSense) {
		this(p_StatDir, p_ModelDir, p_UnknownSense, null);
	}

	/**
	 * constructor with some parameters
	 * @param p_StatDir statistic directory
	 * @param p_ModelDir model directory
	 * @param p_UnknownSense unknown sense mark
	 * @param p_LexeltList lexelts which have models
	 */
	public CLibLinearEvaluator(String p_StatDir, String p_ModelDir, String p_UnknownSense, ArrayList<String> p_LexeltList) {
		this(p_StatDir, p_ModelDir, p_UnknownSense, p_LexeltList, 100);
	}

	/**
	 * constructor with some parameters
	 * @param p_StatDir statistic directory
	 * @param p_ModelDir model directory
	 * @param p_UnknownSense unknown sense mark
	 * @param p_LexeltList lexelts which have models
	 * @param p_Capacity number kept in memory
	 */
	public CLibLinearEvaluator(String p_StatDir, String p_ModelDir, String p_UnknownSense, ArrayList<String> p_LexeltList, int p_Capacity) {
		this(p_StatDir, p_ModelDir, p_UnknownSense, p_LexeltList, p_Capacity, null);
	}

	/**
	 * constructor with some parameters
	 * @param p_StatDir statistic directory
	 * @param p_ModelDir model directory
	 * @param p_UnknownSense unknown sense mark
	 * @param p_LexeltList lexelts which have models
	 * @param p_Capacity number kept in memory
	 * @param p_StaticOnes lexelts which always kept in memory
	 */
	public CLibLinearEvaluator(String p_StatDir, String p_ModelDir, String p_UnknownSense, ArrayList<String> p_LexeltList, int p_Capacity, ArrayList<String> p_StaticOnes) {
		super(p_LexeltList, p_Capacity, p_StaticOnes);
		this.m_StatDir = p_StatDir;
		this.m_ModelDir = p_ModelDir;
		this.m_UnknownSense = p_UnknownSense;
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
			Model model = (Model) this.getModel(lexeltID);
			ILexeltWriter lexeltWriter = new CLibLinearLexeltWriter(model.getBias());
			Problem instances = (Problem) lexeltWriter.getInstances(lexelt);
			retVal.lexelt = lexelt.getID();
			retVal.docs = new String[lexelt.size()];
			retVal.ids = new String[lexelt.size()];
			retVal.probabilities = new double[instances.l][];
			retVal.classes = new String[stat.getTagsInOrder().size()];
			stat.getTagsInOrder().toArray(retVal.classes);
			for (int i = 0; i < instances.l; i++) {
				FeatureNode[] instance = instances.x[i];
				double[] probs = new double[model.getNrClass()];
				retVal.docs[i] = lexelt.getInstanceDocID(i);
				retVal.ids[i] = lexelt.getInstanceID(i);
				retVal.probabilities[i] = new double[retVal.classes.length];
				this.Predict(model, instance, probs);
				for (int c = 0; c < model.getNrClass(); c++) {
					if (model.getLabels()[c] > 0) {
						retVal.probabilities[i][model.getLabels()[c] - 1] = probs[c];
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * call liblinear.Linear and calculate the probabilities
	 * @param model model
	 * @param x input instance
	 * @param prob_estimates output probabilities
	 * @return answer
	 */
	protected int Predict(Model model, FeatureNode[] x, double[] prob_estimates) {
		int nr_class = model.getNrClass();
		int nr_w;
		if (nr_class == 2) {
			nr_w = 1;
		} else {
			nr_w = nr_class;
		}
		int label = Linear.predictValues(model, x, prob_estimates);
		for (int i = 0; i < nr_w; i++) {
			prob_estimates[i] = 1 / (1 + Math.exp(-prob_estimates[i]));
		}
		if (nr_class == 2) {
			prob_estimates[1] = 1 - prob_estimates[0];
		} else {
			double sum = 0;
			for (int i = 0; i < nr_class; i++) {
				sum += prob_estimates[i];
			}
			for (int i = 0; i < nr_class; i++) {
				prob_estimates[i] = prob_estimates[i] / sum;
			}
		}
		return label;
	}

	/*
	 * (non-Javadoc)
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
			ObjectInputStream ois = new ObjectInputStream(is);
			model = ois.readObject();
			ois.close();
		}
		return model;
	}

	/*
	 * (non-Javadoc)
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

	/*
	 * (non-Javadoc)
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
