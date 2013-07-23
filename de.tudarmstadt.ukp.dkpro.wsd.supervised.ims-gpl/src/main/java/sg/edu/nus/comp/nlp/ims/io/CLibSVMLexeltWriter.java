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

import sg.edu.nus.comp.nlp.ims.feature.ABinaryFeature;
import sg.edu.nus.comp.nlp.ims.feature.ANumericFeature;
import sg.edu.nus.comp.nlp.ims.feature.IFeature;
import sg.edu.nus.comp.nlp.ims.instance.IInstance;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.IStatistic;

import libsvm.svm_node;
import libsvm.svm_problem;

/**
 * convert instances in a lexelt to libsvm format.
 * @author zhongzhi
 *
 */
public class CLibSVMLexeltWriter implements ILexeltWriter {
	/**
	 * default constructor
	 */
	public CLibSVMLexeltWriter() {

	}

	/* (non-Javadoc)
	 * @see lexelt.ILexeltWriter#write(java.lang.String, lexelt.ILexelt)
	 */
	public void write(String p_Filename, ILexelt p_iLexelt) throws IOException, ClassNotFoundException {
		int[][] indice = this.loadStatistic(p_iLexelt);
		if (indice == null) {
			throw new IllegalArgumentException("the input lexelt should not be null.");
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(p_Filename));
		IStatistic stat = p_iLexelt.getStatistic();
		int size = p_iLexelt.size(); // instance count
		for (int i = 0;i < size;i++) {
			IInstance instance = p_iLexelt.getInstance(i);
			writer.write(this.toString(instance, stat, indice));
		}
		writer.flush();
		writer.close();
	}

	/* (non-Javadoc)
	 * @see lexelt.ILexeltWriter#getString(lexelt.ILexelt)
	 */
	public String toString(ILexelt p_iLexelt) throws ClassNotFoundException {
		int[][] indice = this.loadStatistic(p_iLexelt);
		if (indice == null) {
			throw new IllegalArgumentException("the input lexelt should not be null.");
		}
		StringBuilder builder = new StringBuilder();
		IStatistic stat = p_iLexelt.getStatistic();
		int size = p_iLexelt.size(); // instance count
		for (int i = 0;i < size;i++) {
			IInstance instance = p_iLexelt.getInstance(i);
			builder.append(this.toString(instance, stat, indice));
		}
		return builder.toString();
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
	 * @throws ClassNotFoundException cannot find the defined type
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
		return retIndice;
	}

	/**
	 *
	 * @param p_Instance
	 * @param p_Stat
	 * @return
	 */
	private String toString(IInstance p_Instance, IStatistic p_Stat, int[][] p_Indice){
		StringBuilder featureBuilder = new StringBuilder();
		svm_node[] features = this.getVector(p_Instance, p_Stat, p_Indice);
		for (int i = 0; i < features.length; i++) {
			featureBuilder.append(" ");
			featureBuilder.append(features[i].index);
			featureBuilder.append(":");
			featureBuilder.append(features[i].value);
		}
		String featureOnly = featureBuilder.toString();
		StringBuilder builder = new StringBuilder();
		for (Integer tag : this.processTags(p_Stat, p_Instance.getTag())) {
			builder.append(tag.toString());
			builder.append(featureOnly);
			builder.append("\n");
		}
		return builder.toString();
	}

	/**
	 * get the vector of one instance
	 * @param p_Instance
	 * @param p_Stat
	 * @return
	 */
	private svm_node[] getVector(IInstance p_Instance, IStatistic p_Stat, int[][] p_Indice){
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
		svm_node[] retVal = new svm_node[exist.size()];
		for (int i = 0;i < indice.size();i++) {
			retVal[i] = new svm_node();
			retVal[i].index = indice.get(i);
			retVal[i].value = exist.get(indice.get(i));
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see lexelt.ILexeltWriter#getInstances(lexelt.ILexelt)
	 */
	public Object getInstances(ILexelt p_Lexelt) throws ClassNotFoundException {
		svm_problem retVal = new svm_problem();
		ArrayList<svm_node[]> featureVectors = new ArrayList<svm_node[]>();
		ArrayList<Double> classes = new ArrayList<Double>();
		int[][] indice = this.loadStatistic(p_Lexelt);
		if (indice == null) {
			throw new IllegalArgumentException("the input lexelt should not be null.");
		}
		IStatistic stat = p_Lexelt.getStatistic();
		int size = p_Lexelt.size(); // instance count
		for (int i = 0;i < size;i++) {
			IInstance instance = p_Lexelt.getInstance(i);
			svm_node[] featureVector = this.getVector(instance, stat, indice);
			ArrayList <String> tags = instance.getTag();
			if (tags.size() > 0) {
				for (String tag:tags) {
					double c = Double.parseDouble(tag);
					featureVectors.add(Arrays.copyOf(featureVector, featureVector.length));
					classes.add(c);
				}
			} else {
				featureVectors.add(featureVector);
				classes.add(new Double(0));
			}
		}
		retVal.l = featureVectors.size();
		retVal.x = new svm_node[retVal.l][];
		retVal.y = new double[retVal.l];
		for (int i = 0; i < featureVectors.size(); i++) {
			retVal.x[i] = featureVectors.get(i);
			retVal.y[i] = classes.get(i);
		}
		return retVal;
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

}
