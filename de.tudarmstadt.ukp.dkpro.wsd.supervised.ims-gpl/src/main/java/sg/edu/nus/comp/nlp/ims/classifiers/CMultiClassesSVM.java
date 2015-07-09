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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SparseInstance;
import weka.core.Utils;

/**
 * a multi-classes svm classifier for WEKA.
 *
 * @author zhongzhi
 *
 */
public class CMultiClassesSVM extends Classifier implements
		OptionHandler {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;

	// random classifier
	protected ZeroR m_ZeroR = null;

	// a binary SMO classifier
	protected Classifier m_Classifier = new SMO();

	// classifiers for each class
	protected Classifier[] m_Classifiers = null;

	// class names
	protected Attribute m_ClassAttribute = null;

	// output format
	protected Instances m_OutputFormat = null;

	// id index
	protected int m_IndexOfID = -1;

	// class index
	protected int m_ClassIndex = -1;

	/*
	 * (non-Javadoc)
	 * @see weka.classifiers.DistributionClassifier#distributionForInstance(weka.core.Instance)
	 */
	@Override
	public double[] distributionForInstance(Instance p_Instance)
			throws Exception {
		double[] probs = new double[p_Instance.numClasses()];
		Instance newInst = this.filterInstance(p_Instance);
		newInst.setDataset(this.m_OutputFormat);
		newInst.setMissing(newInst.classAttribute());
		if (this.m_Classifiers == null) {
			return new double[] { 1 };
		}
		if (this.m_Classifiers.length == 1) {
		    return this.m_Classifiers[0].distributionForInstance(newInst);
		}
		for (int i = 0; i < this.m_Classifiers.length; i++) {
			if (this.m_Classifiers[i] != null) {
			    double[] current = this.m_Classifiers[i].distributionForInstance(newInst);
				for (int j = 0; j < this.m_ClassAttribute.numValues(); j++) {
					if (j == i) {
						probs[j] += current[1];
					} else {
						probs[j] += current[0];
					}
				}
			}
		}
		if (Utils.gr(Utils.sum(probs), 0)) {
			Utils.normalize(probs);
			return probs;
		} else {
			return m_ZeroR.distributionForInstance(newInst);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see weka.classifiers.Classifier#buildClassifier(weka.core.Instances)
	 */
	@Override
	public void buildClassifier(Instances p_Instances) throws Exception {
		Instances newInsts = null;
		if (this.m_Classifier == null) {
			throw new IllegalStateException("No base classifier has been set!");
		}

		this.m_ZeroR = new ZeroR();
		this.m_ZeroR.buildClassifier(p_Instances);

		this.m_ClassAttribute = p_Instances.classAttribute();
		this.getOutputFormat(p_Instances);
		int numClassifiers = p_Instances.numClasses();
		switch (numClassifiers) {
		case 1:
			this.m_Classifiers = null;
			break;
		case 2:
			this.m_Classifiers = Classifier.makeCopies(this.m_Classifier, 1);
			newInsts = new Instances(this.m_OutputFormat, 0);
			for (int i = 0; i < p_Instances.numInstances(); i++) {
				Instance inst = this.filterInstance(p_Instances.instance(i));
				inst.setDataset(newInsts);
				newInsts.add(inst);
			}
			this.m_Classifiers[0].buildClassifier(newInsts);
			break;
		default:
			this.m_Classifiers = Classifier.makeCopies(this.m_Classifier,
					numClassifiers);
			Hashtable<String, ArrayList<Double>> id2Classes = null;
			if (this.m_IndexOfID >= 0) {
				id2Classes = new Hashtable<String, ArrayList<Double>>();
				for (int i = 0; i < p_Instances.numInstances(); i++) {
					Instance inst = p_Instances.instance(i);
					String id = inst.stringValue(this.m_IndexOfID);
					if (!id2Classes.containsKey(id)) {
						id2Classes.put(id, new ArrayList<Double>());
					}
					id2Classes.get(id).add(inst.classValue());
				}
			}
			for (int classIdx = 0; classIdx < this.m_Classifiers.length; classIdx++) {
				newInsts = this.genInstances(p_Instances, classIdx, id2Classes);
				this.m_Classifiers[classIdx].buildClassifier(newInsts);
			}
		}
	}

	/**
	 * get output format
	 *
	 * @param p_Instances
	 *            input format
	 */
	protected void getOutputFormat(Instances p_Instances) {
		FastVector newAtts, newVals;
		// Compute new attributes
		newAtts = new FastVector(p_Instances.numAttributes());
		for (int j = 0; j < p_Instances.numAttributes(); j++) {
			Attribute att = p_Instances.attribute(j);
			if (j != p_Instances.classIndex()) {
				newAtts.addElement(att.copy());
			} else {
				if (p_Instances.classAttribute().isNumeric()) {
					newAtts.addElement(new Attribute(att.name()));
				} else {
					newVals = new FastVector(2);
					newVals.addElement("negative");
					newVals.addElement("positive");
					newAtts.addElement(new Attribute(att.name(), newVals));
				}
			}
		}

		// Construct new header
		this.m_OutputFormat = new Instances(p_Instances.relationName(), newAtts, 0);
		this.m_OutputFormat.setClassIndex(p_Instances.classIndex());
		if (this.m_IndexOfID >= 0) {
			this.m_OutputFormat.deleteAttributeAt(this.m_IndexOfID);
		}
	}

	/**
	 * generate instances for classifier classIdx
	 *
	 * @param p_Instances
	 *            input instances
	 * @param p_ClassIndex
	 *            class index
	 * @param p_ID2Classes
	 *            instance ids
	 * @return new instances
	 */
	protected Instances genInstances(Instances p_Instances, double p_ClassIndex,
			Hashtable<String, ArrayList<Double>> p_ID2Classes) {
		Instances newInsts = new Instances(this.m_OutputFormat, 0);
		for (int i = 0; i < p_Instances.numInstances(); i++) {
			Instance inst = p_Instances.instance(i);
			Instance newInst = null;
			if (SparseInstance.class.isInstance(inst)) {
				newInst = new SparseInstance(inst);
			} else {
				newInst = new Instance(inst);
			}
			if (newInst.value(p_Instances.classIndex()) == p_ClassIndex) {
				newInst.setValue(inst.classIndex(), 1);
			} else {
				if (p_ID2Classes == null
						|| !p_ID2Classes.get(inst.stringValue(this.m_IndexOfID))
								.contains(new Double(p_ClassIndex))) {
					newInst.setValue(inst.classIndex(), 0);
				} else {
					continue;
				}
			}
			newInst.deleteAttributeAt(this.m_IndexOfID);
			newInst.setDataset(newInsts);
			newInsts.add(newInst);
		}
		return newInsts;
	}

	/**
	 * filter instance
	 *
	 * @param p_Instance
	 *            input instance
	 * @return filtered instance
	 */
	protected Instance filterInstance(Instance p_Instance) {
		Instance retVal = null;
		if (SparseInstance.class.isInstance(p_Instance)) {
			retVal = new SparseInstance(p_Instance);
		} else {
			retVal = new Instance(p_Instance);
		}
		if (this.m_IndexOfID >= 0) {
			retVal.deleteAttributeAt(this.m_IndexOfID);
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * @see weka.core.OptionHandler#getOptions()
	 */
	@Override
	public String[] getOptions() {
		String[] classifierOptions = new String[0];
		if ((m_Classifier != null) && (m_Classifier instanceof OptionHandler)) {
			classifierOptions = ((OptionHandler) m_Classifier).getOptions();
		}
		String[] options = new String[classifierOptions.length + 3];
		int current = 0;

		options[current++] = "-I";
		options[current++] = "" + this.m_IndexOfID;

		options[current++] = "--";

		System.arraycopy(classifierOptions, 0, options, current,
				classifierOptions.length);
		current += classifierOptions.length;
		while (current < options.length) {
			options[current++] = "";
		}
		return options;
	}

	/*
	 * (non-Javadoc)
	 * @see weka.core.OptionHandler#listOptions()
	 */
	@SuppressWarnings("rawtypes")
    @Override
	public Enumeration listOptions() {
		Vector<Object> vec = new Vector<Object>(1);
		vec.addElement(new Option("\tSets the index of instance id.", "I", 1,
				"-I <index of instance id>"));
		if (m_Classifier != null) {
			try {
				vec
						.addElement(new Option("", "", 0,
								"\nOptions specific to classifier "
										+ this.m_Classifier.getClass()
												.getName() + ":"));
				Enumeration enume = ((OptionHandler) this.m_Classifier)
						.listOptions();
				while (enume.hasMoreElements()) {
					vec.addElement(enume.nextElement());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return vec.elements();
	}

	/*
	 * (non-Javadoc)
	 * @see weka.core.OptionHandler#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] p_Options) throws Exception {
		String iid = Utils.getOption('I', p_Options);
		if (iid != null) {
			this.m_IndexOfID = Integer.parseInt(iid);
		}
		((OptionHandler) this.m_Classifier).setOptions(Utils
				.partitionOptions(p_Options));
	}

}
