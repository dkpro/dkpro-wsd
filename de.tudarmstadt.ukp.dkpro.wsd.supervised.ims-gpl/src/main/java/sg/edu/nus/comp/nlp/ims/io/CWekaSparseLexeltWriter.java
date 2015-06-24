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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

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
import weka.core.SparseInstance;

/**
 * convert instances in a lexelt to weka sparse instances format.
 *
 * @author zhongzhi
 *
 */
public class CWekaSparseLexeltWriter extends CWekaLexeltWriter {
	/**
	 * default constructor
	 */
	public CWekaSparseLexeltWriter() {

	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.CWekaLexeltWriter#getInstances(sg.edu.nus.comp.nlp.ims.lexelt.ILexelt)
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
		attributes.addElement(new Attribute("#TAG", attributeValues));

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
			Collections.sort(exist);

			double[] attrValues = new double[exist.size() + 2];
			int[] indices = new int[exist.size() + 2];
			ids.addStringValue(instance.getID());
			attrValues[0] = ids.indexOfValue(instance.getID());
			indices[0] = 0;
			for (int valueIdx = 0; valueIdx < exist.size(); valueIdx++) {
				indices[valueIdx + 1] = exist.get(valueIdx);
				attrValues[valueIdx + 1] = features.get(indices[valueIdx + 1]);
			}
			Attribute tags = instances.attribute(keySize + 1);
			indices[exist.size() + 1] = keySize + 1;
			for (String tag : instance.getTag()) {
				if (tag.equals("'?'") || tag.equals("?")) {
					attrValues[exist.size() + 1] = Instance.missingValue();
				} else {
					attrValues[exist.size() + 1] = tags.indexOfValue(tag);
				}
				SparseInstance ins = new SparseInstance(1, attrValues, indices,
						keySize + 2);
				instances.add(ins);
			}
			if (instance.getTag().size() == 0) {
				attrValues[exist.size() + 1] = Instance.missingValue();
				SparseInstance ins = new SparseInstance(1, attrValues, indices,
						keySize + 2);
				instances.add(ins);
			}
		}
		return instances;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.CWekaLexeltWriter#getFeatureVector(sg.edu.nus.comp.nlp.ims.instance.IInstance, sg.edu.nus.comp.nlp.ims.lexelt.IStatistic)
	 */
	@Override
	protected String getFeatureVector(IInstance p_Instance,	IStatistic p_Stat) {
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
			keyIdx += 1;
			features.put(keyIdx, value);
			exist.add(keyIdx);
		}
		Collections.sort(exist);
		StringBuffer featureBuffer = new StringBuffer("{ 0 "
				+ p_Instance.getID() + ", ");
		for (int valueIdx = 0; valueIdx < exist.size(); valueIdx++) {
			featureBuffer.append(exist.get(valueIdx));
			featureBuffer.append(" ");
			featureBuffer.append(features.get(exist.get(valueIdx)));
		}
		featureBuffer.append(keySize + 2 + " ");
		String featureOnly = featureBuffer.toString();
		StringBuffer featureVector = new StringBuffer("");
		for (String tag : p_Instance.getTag()) {
			tag = this.amendValue(tag);
			if (tag.equals("'?'")) {
				tag = "?";
			}
			featureVector.append(featureOnly + tag + "}\n");
		}
		if (p_Instance.getTag().size() == 0) {
			featureVector.append(featureOnly + "?}\n");
		}
		return featureVector.toString();
	}

}
