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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import liblinear.FeatureNode;
import liblinear.Problem;

import sg.edu.nus.comp.nlp.ims.feature.ABinaryFeature;
import sg.edu.nus.comp.nlp.ims.feature.ANumericFeature;
import sg.edu.nus.comp.nlp.ims.feature.IFeature;
import sg.edu.nus.comp.nlp.ims.instance.IInstance;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;

/**
 * convert instances in a lexelt to liblinear format
 * @author zhongzhi
 *
 */
public class CLibLinearLexeltWriter implements ILexeltWriter {

	// liblinear bias parameter
	protected double m_Bias = -1;
	// feature index maximum value
	protected int m_MaxIndex = 0;

	/**
	 * constructor
	 */
	public CLibLinearLexeltWriter() {

	}

	/**
	 * constructor
	 * @param bias bias
	 */
	public CLibLinearLexeltWriter(double bias) {
		this.m_Bias = bias;
	}

	/**
	 * set bias
	 * @param p_Bias bias
	 */
	public void setBias(double p_Bias) {
		this.m_Bias = p_Bias;
	}

	/**
	 * get bias
	 * @return bias
	 */
	public double getBias() {
		return this.m_Bias;
	}

	/**
	 * load the statistic of p_iLexelt
	 * for each feature type in statistic
	 * 	if feature is binary
	 * 		keep it
	 * 	else
	 * 		if feature is list and the number of values is less than 2
	 * 			one new feature
	 * 		else
	 * 			set each value as a new feature
	 * @param p_iLexelt lexelt
	 * @return indices
	 * @throws ClassNotFoundException
	 */
	protected int[][] loadStatistic(ILexelt p_iLexelt) throws ClassNotFoundException {
		int[][] retIndice = null;
		int accuIndex = 1;
		if (p_iLexelt != null) {
			IStatistic stat = p_iLexelt.getStatistic();
			int keySize = stat.getKeys().size();
			retIndice = new int[keySize][0];
			int keyIndex = 0;
			for (keyIndex = 0;keyIndex < keySize;keyIndex++) {
				Class <?> type = Class.forName(stat.getType(keyIndex));
				if (ANumericFeature.class.isAssignableFrom(type)
						|| ABinaryFeature.class.isAssignableFrom(type)) {
					retIndice[keyIndex] = new int[]{accuIndex++};
				} else {
					List <String> values = stat.getValue(keyIndex);
					retIndice[keyIndex] = new int[values.size()];
					for (int i = 0;i < values.size();i++) {
						retIndice[keyIndex][i] = accuIndex++;
					}
				}
			}
		}
		this.m_MaxIndex = accuIndex;
		if (this.m_Bias < 0) {
			this.m_MaxIndex--;
		}
		return retIndice;
	}

	/**
	 * get the vector of one instance
	 * @param p_Instance input instance
	 * @param p_Stat statistic
	 * @return feature vector
	 */
	protected FeatureNode[] getVector(IInstance p_Instance, IStatistic p_Stat, int[][] p_Indice){
		String value = null;
		int kIndex = 0;
		int featureSize = p_Instance.size();
		Hashtable <Integer, Double> exist = new Hashtable <Integer, Double>();
		for(int fIndex = 0;fIndex < featureSize;fIndex++) {
			IFeature feature = p_Instance.getFeature(fIndex);
			kIndex = p_Stat.getIndex(feature.getKey());
			if (kIndex < 0) {
				continue;
			}
			if (ANumericFeature.class.isInstance(feature)) {
				exist.put(p_Indice[kIndex][0], Double.parseDouble(feature.getValue()));
			} else if (ABinaryFeature.class.isInstance(feature)) {
				if (feature.getValue().equals("1")) {
					exist.put(p_Indice[kIndex][0], 1.0);
				}
			} else {
				List<String> values = p_Stat.getValue(kIndex);
				value = feature.getValue();
				if (value == null || !p_Stat.contains(kIndex, value)) {
					value = p_Stat.getDefaultValue();
				}
				for (int i = 0;i < values.size();i++) {
					if (values.get(i).equals(value)) {
						exist.put(p_Indice[kIndex][i], 1.0);
						break;
					}
				}
			}
		}
		ArrayList<Integer> indice = new ArrayList<Integer>(exist.keySet());
		Collections.sort(indice);
		FeatureNode[] retVal;
		if (this.m_Bias >= 0) {
			retVal = new FeatureNode[exist.size() + 1];
			retVal[retVal.length - 1] = new FeatureNode(this.m_MaxIndex, this.m_Bias);
		} else {
			retVal = new FeatureNode[exist.size()];
		}
		for (int i = 0;i < indice.size();i++) {
			retVal[i] = new FeatureNode(indice.get(i), exist.get(indice.get(i)));
		}
		return retVal;
	}

	/**
	 * generate feature vector for one instance
	 * @param p_Instance input instance
	 * @param p_Stat statisitc of training data set
	 * @param p_Indice feature indice
	 * @return feature vector
	 */
	protected String toString(IInstance p_Instance, IStatistic p_Stat, int[][] p_Indice){
		StringBuilder featureBuilder = new StringBuilder();
		FeatureNode[] features = this.getVector(p_Instance, p_Stat, p_Indice);
		for (int i = 0; i < features.length; i++) {
			featureBuilder.append(" ");
			featureBuilder.append(features[i].index);
			featureBuilder.append(":");
			featureBuilder.append(features[i].value);
		}
		String featureOnly = featureBuilder.toString();
		StringBuilder featureVector = new StringBuilder();
		for (Integer tag : this.processTags(p_Stat, p_Instance.getTag())) {
			featureVector.append(tag.toString());
			featureVector.append(featureOnly);
			featureVector.append("\n");
		}
		return featureVector.toString();
	}

	/**
	 * change tags to integer (start from 1)
	 * @param p_Stat statistic
	 * @param p_Tags real tags
	 * @return new tags
	 */
	protected HashSet<Integer> processTags(IStatistic p_Stat, ArrayList<String> p_Tags) {
		HashSet<Integer> retVal = new HashSet<Integer>();
		if (p_Tags == null || p_Tags.size() == 0) {
			retVal.add(0);
		} else {
			for (String tag : p_Tags) {
				Integer iTag = 0;
				if (!tag.equals("'?'") && !tag.equals("?")) {
					iTag = p_Stat.getTagsInOrder().indexOf(tag);
					if (iTag < 0) {
						iTag = -1;
					}
					iTag++;
	            }
				retVal.add(iTag);
			}
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.ILexeltWriter#getInstances(sg.edu.nus.comp.nlp.ims.lexelt.ILexelt)
	 */
	@Override
	public Object getInstances(ILexelt p_Lexelt) throws ClassNotFoundException {
		Problem retVal = new Problem();
		ArrayList<FeatureNode[]> featureVectors = new ArrayList<FeatureNode[]>();
		ArrayList<Integer> classes = new ArrayList<Integer>();
		int[][] indice = this.loadStatistic(p_Lexelt);
		if (indice == null) {
			throw new IllegalArgumentException("the input lexelt should not be null.");
		}
		IStatistic stat = p_Lexelt.getStatistic();
		int size = p_Lexelt.size(); // instance count
		for (int i = 0;i < size;i++) {
			IInstance instance = p_Lexelt.getInstance(i);
			FeatureNode[] featureVector = this.getVector(instance, stat, indice);
			for (Integer tag:this.processTags(stat, instance.getTag())) {
				featureVectors.add(Arrays.copyOf(featureVector, featureVector.length));
				classes.add(tag);
			}
		}
		retVal.l = featureVectors.size();
		retVal.n = this.m_MaxIndex;
		retVal.bias = this.m_Bias;
		retVal.x = new FeatureNode[retVal.l][];
		retVal.y = new int[retVal.l];
		for (int i = 0; i < retVal.l; i++) {
			retVal.x[i] = featureVectors.get(i);
			retVal.y[i] = classes.get(i);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.ILexeltWriter#toString(sg.edu.nus.comp.nlp.ims.lexelt.ILexelt)
	 */
	@Override
	public String toString(ILexelt p_Lexelt) throws ClassNotFoundException {
		int[][] indice = this.loadStatistic(p_Lexelt);
		if (indice == null) {
			throw new IllegalArgumentException("the input lexelt should not be null.");
		}
		StringBuilder builder = new StringBuilder();
		IStatistic stat = p_Lexelt.getStatistic();
		int size = p_Lexelt.size(); // instance count
		for (int i = 0;i < size;i++) {
			IInstance instance = p_Lexelt.getInstance(i);
			builder.append(this.toString(instance, stat, indice));
		}
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.ILexeltWriter#write(java.lang.String, sg.edu.nus.comp.nlp.ims.lexelt.ILexelt)
	 */
	@Override
	public void write(String p_Filename, ILexelt p_Lexelt) throws IOException, ClassNotFoundException {
		int[][] indice = this.loadStatistic(p_Lexelt);
		if (indice == null) {
			throw new IllegalArgumentException("the input lexelt should not be null.");
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(p_Filename));
		IStatistic stat = p_Lexelt.getStatistic();
		int size = p_Lexelt.size(); // instance count
		for (int i = 0;i < size;i++) {
			IInstance instance = p_Lexelt.getInstance(i);
			writer.write(this.toString(instance, stat, indice));
		}
		writer.flush();
		writer.close();
	}

}
