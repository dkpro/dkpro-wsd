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

package sg.edu.nus.comp.nlp.ims.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * a scorer for senseval format tasks
 * @author zhongzhi
 *
 */
public class CScorer {
	// decimal format
	protected NumberFormat m_Formatter3 = new DecimalFormat("#0.000");
	// decimal format
	protected NumberFormat m_Formatter2 = new DecimalFormat("#0.00");

	// list of scored intances
	protected HashSet <String> m_Scored = new HashSet<String>();
	// keys
	protected Hashtable <String, Instance> m_Keys = new Hashtable <String, Instance>();
	// number of instances attempted
	protected double m_Attempted = 0;
	// number of instances correct
	protected double m_Correct = 0;
	// total number of instances
	protected double m_Total = 0;

	/**
	 * one instance
	 * @author zhongzhi
	 *
	 */
	public class Instance {
		ArrayList<String> senses = new ArrayList<String>();
//		ArrayList<Double> weights = null;
	}

	/**
	 * clear keys and answers
	 */
	public void clear() {
		this.m_Keys.clear();
		this.m_Scored.clear();
		this.m_Attempted = 0;
		this.m_Correct = 0;
		this.m_Total = 0;
	}

	/**
	 * set one instance key
	 * @param id instance id
	 * @param key answer key
	 */
	public void setKey(String id, Instance key) {
		if (!this.m_Keys.containsKey(id)) {
			this.m_Keys.put(id, key);
			this.m_Total++;
		} else {
			System.err.println("Instance [" + id + "] Key is duplicated.");
		}
	}

	/**
	 * set keys
	 * @param keys answer keys
	 */
	public void setKey(Hashtable <String, Instance> keys) {
		for (String id:keys.keySet()) {
			this.setKey(id, keys.get(id));
		}
	}

	/**
	 * score an instance
	 * @param id instance id
	 * @param answer instance answer
	 */
	public void score(String id, Instance answer) {
		if (!this.m_Scored.contains(id)) {
			this.m_Scored.add(id);
			if (this.m_Keys.containsKey(id)) {
				this.m_Attempted++;
				double correct = 0;
				Instance key = this.m_Keys.get(id);
				for (String sense : answer.senses) {
					if (key.senses.contains(sense)) {
						correct++;
					}
				}
				if (answer.senses.size() != 0) {
					correct /= answer.senses.size();
				}
				this.m_Correct += correct;
			} else {
				System.err.println("Instance [" + id + "] Key does not exsist.");
			}
		} else {
			System.err.println("Instance [" + id + "] is already scored.");
		}
	}

	/**
	 * score some instances
	 * @param answers answers of instances
	 */
	public void score(Hashtable <String, Instance> answers) {
		for (String id:answers.keySet()) {
			this.score(id, answers.get(id));
		}
	}

	/**
	 * get precision
	 * @return precision
	 */
	public double getPrecision() {
		if (this.m_Attempted != 0) {
			return this.m_Correct / this.m_Attempted;
		}
		return Double.NaN;
	}

	/**
	 * get recall
	 * @return recall
	 */
	public double getRecall() {
		if (this.m_Total != 0) {
			return this.m_Correct / this.m_Total;
		}
		return Double.NaN;
	}

	/**
	 * get attempted
	 * @return attempted
	 */
	public double getAttempted() {
		if (this.m_Total != 0) {
			return this.m_Attempted / this.m_Total;
		}
		return Double.NaN;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String retVal = "precision: " + this.m_Formatter3.format(this.getPrecision());
		retVal += " (" + this.m_Formatter2.format(this.m_Correct) + " correct of " + this.m_Formatter2.format(this.m_Attempted) + " attempted)\n";
		retVal += "recall: " + this.m_Formatter3.format(this.getRecall());
		retVal += " (" + this.m_Formatter2.format(this.m_Correct) + " correct of " + this.m_Formatter2.format(this.m_Total) + " in total)\n";
		retVal += "attempted: " + this.m_Formatter3.format(this.getAttempted());
		retVal += " (" + this.m_Formatter2.format(this.m_Attempted) + " attempted of " + this.m_Formatter2.format(this.m_Total) + " in total)\n";
		return retVal;
	}

	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String line;
		BufferedReader reader = null;
		CArgumentManager argmgr = new CArgumentManager(args);
		if (argmgr.size() < 2) {
			System.err.println("answer_file key_file");// -sensemap sense_map_file");
			System.exit(1);
		}
		CScorer scorer = new CScorer();
//		Hashtable <String, String> coarse = new Hashtable <String, String>();
//		// load sense map
//		if (argmgr.has("sensemap")) {
//			reader = new BufferedReader(new InputStreamReader(new FileInputStream(argmgr.get("sensemap"))));
//			while ((line = reader.readLine()) != null) {
//				StringTokenizer tokenizer = new StringTokenizer(line);
//				tokenizer.countTokens();
//			}
//		}
		// load key file
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(argmgr.get(1))));
		while ((line = reader.readLine()) != null) {
			StringTokenizer tokenizer = new StringTokenizer(line);
			String id = tokenizer.nextToken();
			id += "_" + tokenizer.nextToken();
			Instance ins = scorer.new Instance();
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token.equals("!!")) {
					break;
				}
				ins.senses.add(token);
			}
            scorer.setKey(id, ins);
		}
		reader.close();

		// load answer file
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(argmgr.get(0))));
		while ((line = reader.readLine()) != null) {
			StringTokenizer tokenizer = new StringTokenizer(line);
			String id = tokenizer.nextToken();
			id += "_" + tokenizer.nextToken();
			Instance ins = scorer.new Instance();
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token.equals("!!")) {
					break;
				}
				ins.senses.add(token);
			}
			scorer.score(id, ins);
		}
		System.out.println("score for [" + argmgr.get(0) + "] using key [" + argmgr.get(1) + "]:");
		System.out.println(scorer.toString());
	}

}
