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
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import sg.edu.nus.comp.nlp.ims.util.CArgumentManager;
import sg.edu.nus.comp.nlp.ims.util.ISenseIndex;

/**
 * Evaluator which preload some models into the memory for speed consideration.
 * @author zhongzhi
 *
 */
public abstract class APreloadEvaluator implements IEvaluator {

	// unknown sense
	protected String m_UnknownSense = "U";
	// path separator
	protected String m_FileSeparator = System.getProperty("file.separator");
	// sense index
	protected ISenseIndex m_SenseIndex = null;
	// default capacity
	protected int m_Capacity = 100;
	// classifier models
	protected Object[] m_Classifiers = null;
	// classifier statistics
	protected Object[] m_Statistics = null;
	// models permanently in memory
	protected HashSet <Integer> m_PermanentlyInMemory = new HashSet<Integer>();
	// lexelt index
	protected Hashtable <String, Integer> m_ReverseIndice = null;
	// most recently used lexelt
	protected ArrayList <String> m_MostRecentlyInMemory = new ArrayList<String>();

	/**
	 * constructor
	 */
	protected APreloadEvaluator() {
		this(null);
	}

	/**
	 * constructor given a list of lexelts which have training model
	 * @param p_LexeltList lexelt list
	 */
	protected APreloadEvaluator(ArrayList <String> p_LexeltList) {
		this(p_LexeltList, 100);
	}

	/**
	 * constructor given a list of lexelts which have training model and the capacity of keeping models in memory
	 * @param p_LexeltList lexelt list
	 * @param p_Capacity number of models kept in memory
	 */
	protected APreloadEvaluator(ArrayList <String> p_LexeltList, int p_Capacity) {
		this(p_LexeltList, p_Capacity, null);
	}

	/**
	 * constructor given a list of lexelts which have training model and the capacity of keeping models in memory
	 * @param p_LexeltList lexelt list
	 * @param p_Capacity number of models kept in memory
	 * @param p_StaticOnes lexelts which will always be kept in memory
	 */
	protected APreloadEvaluator(ArrayList <String> p_LexeltList, int p_Capacity, ArrayList<String> p_StaticOnes) {
		this.initial(p_LexeltList, p_Capacity, p_StaticOnes);
	}

	/**
	 * get statistic from disk
	 * @param p_LexeltID lexelt id
	 * @return statistic
	 */
	protected abstract Object loadStatisticFromDisk(String p_LexeltID) throws Exception;

	/**
	 * get model from disk
	 * @param p_LexeltID lexelt id
	 * @return model
	 */
	protected abstract Object loadModelFromDisk(String p_LexeltID) throws Exception;

	/**
	 * get model for p_LexeltID
	 * @param p_LexeltID lexelt id
	 * @return model
	 * @throws Exception exception
	 */
	protected Object getModel(String p_LexeltID) throws Exception {
		Object model = null;
		if (this.m_ReverseIndice == null) {
			return this.loadModelFromDisk(p_LexeltID);
		} else if (this.m_ReverseIndice.containsKey(p_LexeltID)) {
			int index = this.m_ReverseIndice.get(p_LexeltID);
			if (!this.m_PermanentlyInMemory.contains(index)) {
				if (!this.m_MostRecentlyInMemory.contains(index)) {
					Object stat = this.loadStatisticFromDisk(p_LexeltID);
					model = this.loadModelFromDisk(p_LexeltID);
					synchronized (this) {
						if (this.m_MostRecentlyInMemory.contains(p_LexeltID)) {
							this.m_MostRecentlyInMemory.remove(this.m_MostRecentlyInMemory.indexOf(p_LexeltID));
						} else {
							if (this.m_MostRecentlyInMemory.size() > this.m_Capacity) {
								this.m_MostRecentlyInMemory.remove(0);
							}
						}
						this.m_Classifiers[index] = model;
						this.m_Statistics[index] = stat;
						this.m_MostRecentlyInMemory.add(p_LexeltID);
					}
				}
			}
			model = this.m_Classifiers[index];
		}
		return model;
	}

	/**
	 * get statistic for p_LexeltID
	 * @param p_LexeltID lexelt id
	 * @return statistic
	 * @throws Exception exception
	 */
	protected Object getStatistic(String p_LexeltID) throws Exception {
		Object stat = null;
		if (this.m_ReverseIndice == null) {
			return this.loadStatisticFromDisk(p_LexeltID);
		} else if (this.m_ReverseIndice.containsKey(p_LexeltID)) {
			int index = this.m_ReverseIndice.get(p_LexeltID);
			if (!this.m_PermanentlyInMemory.contains(index)) {
				if (!this.m_MostRecentlyInMemory.contains(index)) {
					Object model = this.loadModelFromDisk(p_LexeltID);
					stat = this.loadStatisticFromDisk(p_LexeltID);
					synchronized (this) {
						if (this.m_MostRecentlyInMemory.contains(p_LexeltID)) {
							this.m_MostRecentlyInMemory.remove(this.m_MostRecentlyInMemory.indexOf(p_LexeltID));
						} else {
							if (this.m_MostRecentlyInMemory.size() > this.m_Capacity) {
								this.m_MostRecentlyInMemory.remove(0);
							}
						}
						this.m_Classifiers[index] = model;
						this.m_Statistics[index] = stat;
						this.m_MostRecentlyInMemory.add(p_LexeltID);
					}
				}
			}
			stat = this.m_Statistics[index];
		}
		return stat;
	}

	/**
	 * initial preload evaluator
	 * @param p_LexeltList lexelt list
	 * @param p_Capacity capacity
	 * @param p_StaticOnes always kept in memory list
	 */
	protected void initial(ArrayList <String> p_LexeltList, int p_Capacity, ArrayList<String> p_StaticOnes) {
		try {
			if (p_LexeltList != null && p_LexeltList.size() > 0) {
				this.m_ReverseIndice = new Hashtable<String, Integer>();
				for (int i = 0; i < p_LexeltList.size(); i++) {
					this.m_ReverseIndice.put(p_LexeltList.get(i), i);
				}
				this.m_Capacity = p_Capacity;
				this.m_Classifiers = new Object[this.m_ReverseIndice.size()];
				this.m_Statistics = new Object[this.m_ReverseIndice.size()];
				for (String lexelt : p_StaticOnes) {
					if (this.m_ReverseIndice.containsKey(lexelt)) {
						int index = this.m_ReverseIndice.get(lexelt);
						this.m_Classifiers[index] = this.loadModelFromDisk(lexelt);
						this.m_Statistics[index] = this.loadStatisticFromDisk(lexelt);
						this.m_PermanentlyInMemory.add(index);
						System.err.print(".");
					}
				}
				System.err.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.IEvaluator#setSenseIndex(sg.edu.nus.comp.nlp.ims.util.ISenseIndex)
	 */
	@Override
	public void setSenseIndex(ISenseIndex p_SenseIndex) {
		this.m_SenseIndex = p_SenseIndex;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.classifiers.IEvaluator#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] p_Options) {
		CArgumentManager argmgr = new CArgumentManager(p_Options);
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
