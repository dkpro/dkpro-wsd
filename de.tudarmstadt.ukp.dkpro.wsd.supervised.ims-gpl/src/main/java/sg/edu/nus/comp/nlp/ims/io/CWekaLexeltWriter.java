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

package sg.edu.nus.comp.nlp.ims.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import sg.edu.nus.comp.nlp.ims.feature.ABinaryFeature;
import sg.edu.nus.comp.nlp.ims.feature.ANumericFeature;
import sg.edu.nus.comp.nlp.ims.feature.IFeature;
import sg.edu.nus.comp.nlp.ims.instance.IInstance;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * convert instances in a lexelt to weka sparse instances format.
 *
 * @author zhongzhi
 *
 */
public class CWekaLexeltWriter implements ILexeltWriter {
	// quotation pattern
	protected static Pattern QuotPattern = Pattern.compile("([\'\"%\\\\])");
	// comma pattern
	protected static Pattern CommaPattern = Pattern.compile("[, \\{\\}%\\?\'\"]");

	/**
	 * default constructor
	 */
	public CWekaLexeltWriter() {

	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.ILexeltWriter#write(java.lang.String, sg.edu.nus.comp.nlp.ims.lexelt.ILexelt)
	 */
	@Override
    public void write(String p_FileName, ILexelt p_Lexelt) throws IOException, ClassNotFoundException {
		if (p_Lexelt != null) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					p_FileName));
			writer.write(this.getHeader(p_Lexelt));
			writer.write("@data\n");
			IStatistic stat = p_Lexelt.getStatistic();
			int size = p_Lexelt.size(); // instance count
			for (int i = 0; i < size; i++) {
				IInstance instance = p_Lexelt.getInstance(i);
				writer.write(this.getFeatureVector(instance, stat));
			}
			writer.flush();
			writer.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.ILexeltWriter#toString(sg.edu.nus.comp.nlp.ims.lexelt.ILexelt)
	 */
	@Override
    public String toString(ILexelt p_Lexelt) throws ClassNotFoundException {
		StringBuilder builder = new StringBuilder();
		if (p_Lexelt != null) {
			IStatistic stat = p_Lexelt.getStatistic();
			builder.append(this.getHeader(p_Lexelt));
			builder.append("@data\n");
			int size = p_Lexelt.size(); // instance count
			for (int i = 0; i < size; i++) {
				builder.append(this.getFeatureVector(p_Lexelt.getInstance(i), stat));
			}
			return builder.toString();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.ILexeltWriter#getInstances(sg.edu.nus.comp.nlp.ims.lexelt.ILexelt)
	 */
	@Override
    public Object getInstances(ILexelt p_Lexelt) throws ClassNotFoundException {
		String relation = p_Lexelt.getID();
		FastVector attributes = new FastVector();
		int capacity = p_Lexelt.size();

		IStatistic stat = p_Lexelt.getStatistic();
		Attribute ids = new Attribute("#ID");
		attributes.addElement(ids);
		int keySize = stat.getKeys().size();
		for (int keyIdx = 0; keyIdx < keySize; keyIdx++) {
			String key = stat.getKey(keyIdx);
			String type = stat.getType(keyIdx);
			if (ANumericFeature.class.isAssignableFrom(Class.forName(type))) {
				attributes.addElement(new Attribute(key));
			} else {
				FastVector attributeValues = new FastVector();
				List<String> values = stat.getValue(keyIdx);
				for (String value : values) {
					attributeValues.addElement(value);
				}
				if (attributeValues.size() == 0) {
					throw new IllegalStateException("No attribute specified.");
				}
				attributes.addElement(new Attribute(key, attributeValues));
			}
		}
		FastVector attributeValues = new FastVector();
		for (String tag : stat.getTags()) {
			attributeValues.addElement(tag);
		}
		attributes.addElement(new Attribute("#TAG",	attributeValues));

		Instances instances = new Instances(relation, attributes, capacity);
		for (int instIdx = 0; instIdx < p_Lexelt.size(); instIdx++) {
			IInstance instance = p_Lexelt.getInstance(instIdx);
			int keyIdx = 0;
			double value;
			IFeature feature;

			int featureSize = instance.size();
			Hashtable<Integer, Double> features = new Hashtable<Integer, Double>();
			ArrayList<Integer> exist = new ArrayList<Integer>();
			for (int featIdx = 0; featIdx < featureSize; featIdx++) {
				feature = instance.getFeature(featIdx);
				keyIdx = stat.getIndex(feature.getKey());
				if (keyIdx < 0) {
					continue;
				}
				if (ANumericFeature.class.isInstance(feature)) {
					value = Double.parseDouble(feature.getValue());
				} else if (ABinaryFeature.class.isInstance(feature)) {
					value = instances.attribute(keyIdx + 1).indexOfValue(feature.getValue());
				} else {
					String fv = feature.getValue();
					if (fv == null || !stat.contains(keyIdx, fv)) {
						fv = stat.getDefaultValue();
					}
					value = instances.attribute(keyIdx + 1).indexOfValue(fv);
				}
				features.put(keyIdx + 1, value);
				exist.add(keyIdx + 1);
			}
			exist.add(keySize + 1);
			Collections.sort(exist);

			double[] attValues = new double[keySize + 2];
			ids.addStringValue(instance.getID());
			attValues[0] = ids.indexOfValue(instance.getID());
			int begin, end = -1;
			for (int valueIdx = 0; valueIdx < exist.size(); valueIdx++) {
				begin = end + 1;
				end = exist.get(valueIdx);
				for (int i = begin; i < end; i++) {
					if (instances.attribute(i).isNumeric()) {
						attValues[i] = 0;
					} else {
						attValues[i] = instances.attribute(i).indexOfValue("0");
					}
				}
				if (end <= keySize) {
					attValues[end] = features.get(end);
				}
			}

			for (String tag : instance.getTag()) {
				if (tag.equals("'?'") || tag.equals("?")) {
					attValues[keySize + 1] = Instance.missingValue();
				} else {
					attValues[keySize + 1] = instances.attribute(keySize + 1)
							.indexOfValue(tag);
				}
				Instance ins = new Instance(1, attValues);
				instances.add(ins);
			}
			if (instance.getTag().size() == 0) {
				attValues[keySize + 1] = Instance.missingValue();
				Instance ins = new Instance(1, attValues);
				instances.add(ins);
			}
		}
		return instances;
	}

	/**
	 * generate the head part of arff file
	 * @param p_Lexelt lexelt
	 * @return arff head
	 * @throws ClassNotFoundException
	 */
	protected String getHeader(ILexelt p_Lexelt) throws ClassNotFoundException {
		StringBuilder builder = new StringBuilder();
		if (p_Lexelt != null) {
			builder.append("@relation " + p_Lexelt.getID()
					+ "\n\n% instance id\n@attribute id string\n\n");
			IStatistic stat = p_Lexelt.getStatistic();
			int keySize = stat.getKeys().size();
			int attrName = 2;
			for (int keyIdx = 0; keyIdx < keySize; keyIdx++) {
				String key = stat.getKey(keyIdx);
				String type = stat.getType(keyIdx);
				builder.append("% <" + key + ">\n");
				if (ANumericFeature.class.isAssignableFrom(Class.forName(type))) {
					builder.append("@attribute " + attrName + " NUMERIC\n\n");
				} else {
					List<String> values = stat.getValue(keyIdx);
					String value = null;
					builder.append("@attribute " + attrName + " {");
					if (values.size() > 0) {
						value = values.get(0);
						value = this.amendValue(value);
						builder.append(value);
						for (int i = 1; i < values.size(); i++) {
							value = values.get(i);
							value = this.amendValue(value);
							builder.append(", " + value);
						}
					}
					builder.append("}\n\n");
				}
				attrName++;
			}
			builder.append("% tags\n@attribute " + attrName + " {");
			ArrayList<String> tags = new ArrayList<String>();
			tags.addAll(stat.getTags());
			Collections.sort(tags);

			Iterator<String> it = tags.iterator();
			if (it.hasNext()) {
				String tag = it.next();
				tag = this.amendValue(tag);
				builder.append(tag);
				while (it.hasNext()) {
					tag = it.next();
					tag = this.amendValue(tag);
					builder.append(", " + tag);
				}
			}
			builder.append("}\n\n");
			return builder.toString();
		}
		return null;
	}

	/**
	 * generate the feature vector for one instance
	 * @param p_Instance instance
	 * @param p_Stat statistic of training instances
	 * @return feature vector
	 */
	protected String getFeatureVector(IInstance p_Instance, IStatistic p_Stat) {
		String value = null;
		String key = null;
		int keySize = p_Stat.getKeys().size();
		int keyIdx = 0;
		int featureSize = p_Instance.size();
		Hashtable<Integer, String> features = new Hashtable<Integer, String>();
		ArrayList<Integer> exist = new ArrayList<Integer>();
		for (int featIdx = 0; featIdx < featureSize; featIdx++) {
			IFeature feature = p_Instance.getFeature(featIdx);
			key = feature.getKey();
			keyIdx = p_Stat.getIndex(key);
			if (keyIdx < 0) {
				continue;
			}
			if (ANumericFeature.class.isInstance(feature)) {
				value = feature.getValue();
			} else if (ABinaryFeature.class.isInstance(feature)) {
				value = feature.getValue();
			} else {
				value = feature.getValue();
				if (value != null && p_Stat.contains(keyIdx, value)) {
					value = this.amendValue(value);
				} else {
					value = p_Stat.getDefaultValue();
				}
			}
			value += ", ";
			features.put(keyIdx, value);
			exist.add(keyIdx);
		}
		exist.add(keySize);
		Collections.sort(exist);
		StringBuilder featBuilder = new StringBuilder(p_Instance.getID() + ", ");
		int begin, end = -1;
		for (int valueIdx = 0; valueIdx < exist.size(); valueIdx++) {
			begin = end + 1;
			end = exist.get(valueIdx);
			for (int i = begin; i < end; i++) {
				featBuilder.append("0, ");
			}
			if (end < keySize) {
				featBuilder.append(features.get(end));
			}
		}

		String featureOnly = featBuilder.toString();
		StringBuilder featureVector = new StringBuilder("");
		for (String tag : p_Instance.getTag()) {
			tag = this.amendValue(tag);
			if (tag.equals("'?'")) {
				tag = "?";
			}
			featureVector.append(featureOnly + tag + "\n");
		}
		if (p_Instance.getTag().size() == 0) {
			featureVector.append(featureOnly + "?}\n");
		}
		return featureVector.toString();
	}

	/**
	 * amend feature value
	 * @param p_Value feature value
	 * @return new value
	 */
	protected String amendValue(String p_Value) {
		if (p_Value != null) {
			p_Value = QuotPattern.matcher(p_Value).replaceAll("\\\\$1");
			if (CommaPattern.matcher(p_Value).find()) {
				p_Value = "\'" + p_Value + "\'";
			}
		}
		return p_Value;
	}

}
