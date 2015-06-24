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

package sg.edu.nus.comp.nlp.ims.lexelt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sg.edu.nus.comp.nlp.ims.feature.ABinaryFeature;
import sg.edu.nus.comp.nlp.ims.feature.AListFeature;
import sg.edu.nus.comp.nlp.ims.feature.ANumericFeature;
import sg.edu.nus.comp.nlp.ims.feature.IFeature;
import sg.edu.nus.comp.nlp.ims.instance.IInstance;
import sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector.Type;

/**
 * statistic of all training instances of a lexelt.
 *
 * @author zhongzhi
 *
 */
public class CStatistic implements IStatistic {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;
	// default value
	protected final String m_Default = "!DEF!";
	// status
	protected boolean m_Status = false;
	// size
	protected int m_Size = 0;
	// feature keys
	protected ArrayList<String> m_Keys;
	// key to feature index
	protected Hashtable<String, Integer> m_KeyMap;
	// feature types
	protected ArrayList<Class<? extends IFeature>> m_TypeEnum;
	protected ArrayList<Integer> m_Types;
	// feature values
	protected ArrayList<Hashtable<String, Integer>> m_Values;
	// feature value counts
	protected ArrayList<ArrayList<Integer>> m_ValueCount;
	// feature value count per tag
	protected ArrayList<ArrayList<ArrayList<Integer>>> m_ValueTagCount;
	// tag map
	protected Hashtable<String, Integer> m_TagMap;
	// tags
	protected ArrayList<String> m_Tags;
	// tag counts
	protected ArrayList<Integer> m_TagCount;
	// filter threshold
	protected int m_M2;
	// default separator
	protected static final String SEPARATOR = "\t";
	// pattern to split line
	protected Pattern m_SplitPattern = Pattern.compile(SEPARATOR + "+");
	// default encoding
	protected String m_Encoding = "ISO8859-1";

	/**
	 * default constructor
	 */
	public CStatistic() {
		this.m_ValueCount = new ArrayList<ArrayList<Integer>>();
		this.m_Keys = new ArrayList<String>();
		this.m_TypeEnum = new ArrayList<Class<? extends IFeature>>();
		this.m_Types = new ArrayList<Integer>();
		this.m_Values = new ArrayList<Hashtable<String, Integer>>();
		this.m_ValueTagCount = new ArrayList<ArrayList<ArrayList<Integer>>>();
		this.m_KeyMap = new Hashtable<String, Integer>();
		this.m_TagCount = new ArrayList<Integer>();
		this.m_TagMap = new Hashtable<String, Integer>();
		this.m_Tags = new ArrayList<String>();
		this.m_M2 = 0;
	}

	/**
	 * constructor with file encoding
	 * @param p_Encoding file encoding
	 */
	public CStatistic(String p_Encoding) {
		this();
		this.m_Encoding = p_Encoding;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#addInstance(sg.edu.nus.comp.nlp.ims.instance.IInstance)
	 */
	public boolean addInstance(IInstance p_Instance) {
		if (p_Instance != null) {
			ArrayList<Integer> tagIndice = new ArrayList<Integer>();
			int size = p_Instance.size();
			for (String tag : p_Instance.getTag()) {
				if (!this.m_TagMap.containsKey(tag)) {
					this.m_TagMap.put(tag, this.m_TagMap.size());
					this.m_Tags.add(tag);
					this.m_TagCount.add(0);
				}
				int index = this.m_TagMap.get(tag);
				tagIndice.add(index);
				this.m_TagCount.set(index, this.m_TagCount.get(index) + 1);
				this.m_Size++;
			}
			for (int i = 0; i < size; i++) {
				String key = (String) p_Instance.getFeatureName(i);
				IFeature feature = p_Instance.getFeature(i);
				String value = feature.getValue();
				if (!this.m_KeyMap.containsKey(key)) {
					this.m_KeyMap.put(key, this.m_Keys.size());
					this.m_Keys.add(key);
					Class<? extends IFeature> type = feature.getClass();
					if (this.m_TypeEnum.indexOf(type) < 0) {
						this.m_TypeEnum.add(type);
					}
					int tIdx = this.m_TypeEnum.indexOf(type);
					this.m_Types.add(tIdx);
					Hashtable<String, Integer> values = new Hashtable<String, Integer>();
					ArrayList<Integer> valueCount = new ArrayList<Integer>();
					ArrayList<ArrayList<Integer>> valueTagCount = new ArrayList<ArrayList<Integer>>();

					this.m_Values.add(values);
					this.m_ValueCount.add(valueCount);
					this.m_ValueTagCount.add(valueTagCount);
					if (ANumericFeature.class.isInstance(feature)) {
						continue;
					}
					if (ABinaryFeature.class.isInstance(feature)) {
						values.put("0", 0);
						values.put("1", 1);
						valueCount.add(0);
						valueCount.add(0);
						valueTagCount.add(new ArrayList<Integer>());
						valueTagCount.add(new ArrayList<Integer>());
					}
					if (AListFeature.class.isInstance(feature)) {
						values.put(this.m_Default, 0);
						valueCount.add(0);
						valueTagCount.add(new ArrayList<Integer>());
					}
				}

				int keyIdx = this.m_KeyMap.get(key);
				if (value == null || value.isEmpty()) {
					value = this.m_Default;
				}
				Hashtable<String, Integer> values = this.m_Values.get(keyIdx);
				ArrayList<Integer> valueCount = this.m_ValueCount.get(keyIdx);
				ArrayList<ArrayList<Integer>> valueTagCount = this.m_ValueTagCount.get(keyIdx);
				if (!values.containsKey(value)) {
					values.put(value, values.size());
					valueCount.add(0);
					valueTagCount.add(new ArrayList<Integer>());
				}
				int valueIdx = values.get(value);
				valueCount.set(valueIdx, valueCount.get(valueIdx) + 1);
				ArrayList<Integer> counts = valueTagCount.get(valueIdx);
				while (counts.size() < this.m_TagMap.size()) {
					counts.add(0);
				}
				for (int tagIdx : tagIndice) {
					counts.set(tagIdx, counts.get(tagIdx) + 1);
				}
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#removeInstance(sg.edu.nus.comp.nlp.ims.instance.IInstance)
	 */
	public boolean removeInstance(IInstance p_iInstance) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getKeys()
	 */
	public List<String> getKeys() {
		return this.m_Keys;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getParameter(java.lang.String)
	 */
	public int getParameter(String p_Parameter) {
		return this.m_M2;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#loadFromFile(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean loadFromFile(String p_FileName) {
		this.clear();
		try {
			String line;
			String[] tokens;
			BufferedReader reader;
			if (p_FileName.endsWith(".gz")) {
				reader = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(new FileInputStream(p_FileName)), this.m_Encoding));
			} else {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(p_FileName), this.m_Encoding));
			}
			line = reader.readLine();
			tokens = this.m_SplitPattern.split(line);
			for (int i = 0; i < tokens.length; i++) {
				this.m_TagMap.put(tokens[i], i);
				this.m_Tags.add(tokens[i]);
			}
			line = reader.readLine();
			tokens = this.m_SplitPattern.split(line);
			for (int i = 0; i < tokens.length; i++) {
				this.m_TypeEnum.add((Class<? extends IFeature>) Class
						.forName(tokens[i]));
			}
			while ((line = reader.readLine()) != null) {
				Hashtable<String, Integer> valueMap = new Hashtable<String, Integer>();
				tokens = this.m_SplitPattern.split(line);
				String key = tokens[0];
				this.m_KeyMap.put(key, this.m_Keys.size());
				this.m_Keys.add(key);
				int type = Integer.parseInt(tokens[1]);
				this.m_Types.add(type);
				int iValue = 0;
				for (int i = 2; i < tokens.length; i++, iValue++) {
					valueMap.put(tokens[i], iValue);
				}
				this.m_Values.add(valueMap);
			}
			reader.close();
			this.m_Status = true;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#setParameter(java.lang.String, int)
	 */
	public boolean setParameter(String p_Parameter, int p_Value) {
		if (p_Value < 0) {
			p_Value = 0;
		}
		this.m_M2 = p_Value;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#writeToFile(java.lang.String)
	 */
	public boolean writeToFile(String p_FileName) {
		try {
			BufferedWriter writer;
			if (p_FileName.endsWith(".gz")) {
				writer = new BufferedWriter(new OutputStreamWriter(
						new GZIPOutputStream(new FileOutputStream(p_FileName)), this.m_Encoding));
			} else {
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(p_FileName), this.m_Encoding));
			}
			for (int i = 0; i < this.m_Tags.size(); i++) {
				writer.write(this.m_Tags.get(i));
				writer.write(SEPARATOR);
			}
			writer.write("\n");
			for (int i = 0; i < this.m_TypeEnum.size(); i++) {
				writer.write(this.m_TypeEnum.get(i).getName());
				writer.write(SEPARATOR);
			}
			writer.write("\n");
			for (int keyIndex = 0; keyIndex < this.m_Keys.size(); keyIndex++) {
				String key = this.m_Keys.get(keyIndex);
				writer.write(key);
				writer.write(SEPARATOR);
				int type = this.m_Types.get(keyIndex);
				writer.write(Integer.toString(type));
				Hashtable<String, Integer> valueMap = this.m_Values
						.get(keyIndex);
				String[] values = new String[valueMap.size()];
				for (String value : valueMap.keySet()) {
					values[valueMap.get(value)] = value;
				}
				for (int iValue = 0; iValue < values.length; iValue++) {
					writer.write(SEPARATOR);
					writer.write(values[iValue]);
				}
				writer.write("\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		CStatistic clone = new CStatistic();
		clone.m_Keys = (ArrayList<String>) this.m_Keys.clone();
		clone.m_KeyMap = (Hashtable<String, Integer>) this.m_KeyMap.clone();
		clone.m_TypeEnum = (ArrayList<Class<? extends IFeature>>) this.m_TypeEnum
				.clone();
		clone.m_Types = (ArrayList<Integer>) this.m_Types.clone();
		clone.m_M2 = this.m_M2;
		clone.m_TagCount = (ArrayList<Integer>) this.m_TagCount.clone();
		clone.m_TagMap = (Hashtable<String, Integer>) this.m_TagMap.clone();
		clone.m_Tags = (ArrayList<String>) this.m_Tags.clone();
		clone.m_Values = new ArrayList<Hashtable<String, Integer>>();
		Iterator it = this.m_Values.iterator();
		while (it.hasNext()) {
			Hashtable<String, Integer> value = (Hashtable<String, Integer>) it
					.next();
			clone.m_Values.add((Hashtable<String, Integer>) value.clone());
		}
		clone.m_ValueCount = new ArrayList<ArrayList<Integer>>();
		it = this.m_ValueCount.iterator();
		while (it.hasNext()) {
			ArrayList<Integer> uia = (ArrayList<Integer>) it.next();
			clone.m_ValueCount.add((ArrayList<Integer>) uia.clone());
		}
		clone.m_ValueTagCount = new ArrayList<ArrayList<ArrayList<Integer>>>();
		it = this.m_ValueTagCount.iterator();
		while (it.hasNext()) {
			ArrayList<ArrayList<Integer>> counts = (ArrayList<ArrayList<Integer>>) it
					.next();
			ArrayList<ArrayList<Integer>> copy = new ArrayList<ArrayList<Integer>>();
			for (int i = 0; i < counts.size(); i++) {
				copy.add((ArrayList<Integer>) counts.get(i).clone());
			}
			clone.m_ValueTagCount.add(copy);
		}
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#contains(int, java.lang.String)
	 */
	public boolean contains(int p_KeyIndex, String p_Value) {
		if (p_KeyIndex >= 0 && p_KeyIndex < this.m_Keys.size()) {
			return this.m_Values.get(p_KeyIndex).containsKey(p_Value);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getCount(java.lang.String, java.lang.String)
	 */
	public int getCount(String p_Key, String p_Value) {
		int keyIndex = this.m_KeyMap.get(p_Key);
		return this.getCount(keyIndex, p_Value);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getCount(int, java.lang.String)
	 */
	public int getCount(int p_Index, String p_Value) {
		if (p_Index >= 0 && p_Index < this.m_Keys.size()) {
			ArrayList<Integer> counts = this.m_ValueCount.get(p_Index);
			Hashtable<String, Integer> values = this.m_Values.get(p_Index);
			if (this.m_TypeEnum.get(this.m_Types.get(p_Index)).getSuperclass()
					.equals(ANumericFeature.class)) {
				return 0;
			}
			if (this.m_TypeEnum.get(this.m_Types.get(p_Index)).getSuperclass()
					.equals(ABinaryFeature.class)
					&& p_Value.equals("0")) {
				p_Value = "1";
				if (values.containsKey(p_Value)) {
					return this.m_Size - counts.get(values.get(p_Value));
				}
			}
			if (values.containsKey(p_Value)) {
				return counts.get(values.get(p_Value));
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getIndex(java.lang.String)
	 */
	public int getIndex(String p_Key) {
		if (this.m_KeyMap.containsKey(p_Key)) {
			return this.m_KeyMap.get(p_Key);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getKey(int)
	 */
	public String getKey(int p_Index) {
		if (p_Index >= 0 && p_Index < this.m_Keys.size()) {
			return this.m_Keys.get(p_Index);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#size()
	 */
	public int size() {
		return this.m_Size;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getType(int)
	 */
	public String getType(int p_Index) {
		if (p_Index >= 0 && p_Index < this.m_Keys.size()) {
			// return this.m_Types.get(p_Index).name();
			return this.m_TypeEnum.get(this.m_Types.get(p_Index)).getName();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getType(java.lang.String)
	 */
	public String getType(String p_Key) {
		int index = this.m_KeyMap.get(p_Key);
		return this.getType(index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getValue(int)
	 */
	public List<String> getValue(int p_Index) {
		if (p_Index >= 0 && p_Index < this.m_Keys.size()) {
			String[] values = new String[this.m_Values.get(p_Index).size()];
			for (Map.Entry<String, Integer> i : this.m_Values.get(p_Index)
					.entrySet()) {
				values[i.getValue()] = i.getKey();
			}
			return Arrays.asList(values);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getValue(java.lang.String)
	 */
	public List<String> getValue(String p_Key) {
		int index = this.m_KeyMap.get(p_Key);
		return this.getValue(index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#clear()
	 */
	public void clear() {
		this.m_KeyMap.clear();
		this.m_Keys.clear();
		this.m_Types.clear();
		this.m_TypeEnum.clear();
		this.m_TagMap.clear();
		this.m_Tags.clear();
		this.m_TagCount.clear();
		this.m_Values.clear();
		this.m_ValueCount.clear();
		this.m_ValueTagCount.clear();
		this.m_Status = false;
		this.m_M2 = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getTags()
	 */
	public Set<String> getTags() {
		return this.m_TagMap.keySet();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getTagsInOrder()
	 */
	public List<String> getTagsInOrder() {
		return this.m_Tags;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getTagCount(java.lang.String)
	 */
	public int getTagCount(String p_Tag) {
		int i = this.m_TagMap.get(p_Tag);
		if (i >= 0) {
			return this.m_TagCount.get(i);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getDefaultValue()
	 */
	public String getDefaultValue() {
		return this.m_Default;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getCount(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int getCount(String p_Key, String p_Value, String p_Tag) {
		return this.getCount(this.m_KeyMap.get(p_Key), p_Value, p_Tag);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#getCount(int, java.lang.String, java.lang.String)
	 */
	@Override
	public int getCount(int p_KeyIndex, String p_Value, String p_Tag) {
		if (this.m_TypeEnum.get(this.m_Types.get(p_KeyIndex)).getSuperclass()
				.equals(ANumericFeature.class)) {
			return 0;
		}
		if (this.m_TypeEnum.get(this.m_Types.get(p_KeyIndex)).getSuperclass()
				.equals(ABinaryFeature.class)) {
			if (p_Value.equals("0")) {
				return this.m_TagCount.get(this.m_TagMap.get(p_Tag))
						- this.getCount(p_KeyIndex, "1", p_Tag);
			}
		}
		ArrayList<Integer> valueTagCount = this.m_ValueTagCount.get(p_KeyIndex)
				.get(this.m_Values.get(p_KeyIndex).get(p_Value));
		int index = this.m_TagMap.get(p_Tag);
		if (index >= valueTagCount.size()) {
			return 0;
		}
		return valueTagCount.get(index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#isProcessed()
	 */
	@Override
	public boolean isProcessed() {
		return this.m_Status;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.lexelt.IStatistic#select(sg.edu.nus.comp.nlp.ims.lexelt.IFeatureSelector)
	 */
	@Override
	public void select(IFeatureSelector p_Selector) {
		this.m_Status = true;
		p_Selector.filter(this);

		Hashtable<String, Integer> keyMap = new Hashtable<String, Integer>();
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Hashtable<String, Integer>> values = new ArrayList<Hashtable<String,Integer>>();
		ArrayList<ArrayList<Integer>> valueCount = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<ArrayList<Integer>>> valueTagCount = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (int i = 0; i < this.m_Keys.size(); i++) {
			Type type = p_Selector.isFiltered(i);
			if (type.equals(Type.PART)) {
				List<String> vs = this.getValue(i);
				for (String value : vs) {
					Type vType = p_Selector.isFiltered(i, value);
					if (vType.equals(Type.FILTER)) {
						this.remove(i, value);
					}
				}
				if (this.m_Values.get(i).size() != 0) {
					keys.add(this.m_Keys.get(i));
					keyMap.put(this.m_Keys.get(i), keyMap.size());
					values.add(this.m_Values.get(i));
					valueCount.add(this.m_ValueCount.get(i));
					valueTagCount.add(this.m_ValueTagCount.get(i));
				}
			} else if (type.equals(Type.ACCEPT)) {
				keys.add(this.m_Keys.get(i));
				keyMap.put(this.m_Keys.get(i), keyMap.size());
				values.add(this.m_Values.get(i));
				valueCount.add(this.m_ValueCount.get(i));
				valueTagCount.add(this.m_ValueTagCount.get(i));
			}
		}
		this.m_KeyMap = keyMap;
		this.m_Keys = keys;
		this.m_Values = values;
		this.m_ValueCount = valueCount;
		this.m_ValueTagCount = valueTagCount;

/*		ArrayList<Integer> removeFeatures = new ArrayList<Integer>();
		for (int iKey = this.m_Keys.size() - 1; iKey >= 0; iKey--) {
			Type type = p_Selector.isFiltered(iKey);
			if (type.equals(Type.PART)) {
				List<String> values = this.getValue(iKey);
				for (String value : values) {
					Type vType = p_Selector.isFiltered(iKey, value);
					if (vType.equals(Type.FILTER)) {
						this.remove(iKey, value);
					}
				}
				if (this.m_Values.get(iKey).size() == 0) {
					this.remove(iKey);
				}
			} else if (type.equals(Type.FILTER)) {
				removeFeatures.add(iKey);
				this.remove(iKey);
			}
		}
*/	}

	/**
	 * check the status
	 */
	protected void check() {
		if (this.m_Status) {
			throw new IllegalStateException("The counts are not record.");
		}
	}

	/**
	 * remove a feature
	 *
	 * @param p_FeatureIndex
	 *            feature index
	 */
	protected void remove(int p_FeatureIndex) {
		String removeKey = this.m_Keys.remove(p_FeatureIndex);
		this.m_KeyMap.remove(removeKey);
		Set<String> keys = this.m_KeyMap.keySet();
		for (String key : keys) {
			int index = this.m_KeyMap.get(key);
			if (index > p_FeatureIndex) {
				this.m_KeyMap.put(key, --index);
			}
		}
		this.m_Values.remove(p_FeatureIndex);
		this.m_ValueTagCount.remove(p_FeatureIndex);
	}

	/**
	 * remove a value of a feature
	 *
	 * @param p_FeatureIndex
	 *            feature index
	 * @param p_Value
	 *            feature value
	 */
	protected void remove(int p_FeatureIndex, String p_Value) {
		Hashtable<String, Integer> valueMap = this.m_Values.get(p_FeatureIndex);
		int iValue = valueMap.remove(p_Value);
		Set<String> values = valueMap.keySet();
		for (String value : values) {
			int index = valueMap.get(value);
			if (index > iValue) {
				valueMap.put(value, --index);
			}
		}
		this.m_ValueCount.get(p_FeatureIndex).remove(iValue);
		this.m_ValueTagCount.get(p_FeatureIndex).remove(iValue);
	}

}
