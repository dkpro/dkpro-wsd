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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import sg.edu.nus.comp.nlp.ims.lexelt.CResultInfo;
import sg.edu.nus.comp.nlp.ims.util.CArgumentManager;

/**
 * merge and save results to disk.
 *
 * @author zhongzhi
 *
 */
public class CAllWordsResultWriter implements IResultWriter {

	// save directory
	private String m_SaveDir;

	/**
	 * default constructor
	 */
	public CAllWordsResultWriter() {
		this(".");
	}

	/**
	 * constructor with initialed save directory
	 *
	 * @param p_SaveDir
	 *            save directory
	 */
	public CAllWordsResultWriter(String p_SaveDir) {
		this.m_SaveDir = p_SaveDir;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.IResultWriter#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] p_Options) {
		CArgumentManager argmgr = new CArgumentManager(p_Options);
		if (argmgr.has("s")) {
			this.m_SaveDir = argmgr.get("s");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.IResultWriter#write(java.lang.Object)
	 */
	@Override
	public void write(Object p_Result) throws IOException {
		if (List.class.isInstance(p_Result)) {
			File saveFile = new File(this.m_SaveDir);
			if (saveFile.exists() && saveFile.isDirectory()) {
				saveFile = new File(this.m_SaveDir + ".result");
			}
			System.err.println(saveFile.getAbsolutePath());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(saveFile)));
			writer.write(this.toString(p_Result));
			writer.flush();
			writer.close();
		} else {
			CResultInfo info = (CResultInfo) p_Result;
			System.err.println(this.m_SaveDir + "/" + info.getID()
					+ ".result");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(this.m_SaveDir + "/" + info.getID()
							+ ".result")));
			writer.write(this.toString(p_Result));
			writer.flush();
			writer.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.IResultWriter#toString(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String toString(Object p_Result) {
		if (List.class.isInstance(p_Result)) {
			Hashtable<String, Integer> indices = new Hashtable<String, Integer>();
			ArrayList<String> docIDs = new ArrayList<String>();
			ArrayList<String> classes = new ArrayList<String>();
			ArrayList<Double> probabilities = new ArrayList<Double>();
			for (CResultInfo info : (List<CResultInfo>) p_Result) {
				for (int instIdx = 0; instIdx < info.size(); instIdx++) {
					String docID = info.getDocID(instIdx);
					String id = info.getID(instIdx);
					if (docID == null) {
						docID = "";
					}
					int ansIdx = info.getAnswer(instIdx);
					if (indices.containsKey(id)) {
						int index = indices.get(id);
						if (info.probabilities[instIdx][ansIdx] > probabilities
								.get(index)
								&& info.probabilities[instIdx][ansIdx] != 1) {
							docIDs.set(index, docID);
							classes.set(index, info.classes[ansIdx]);
							probabilities.set(index,
									info.probabilities[instIdx][ansIdx]);
						}
					} else {
						indices.put(id, indices.size());
						docIDs.add(docID);
						classes.add(info.classes[ansIdx]);
						probabilities.add(info.probabilities[instIdx][ansIdx]);
					}
				}
			}
			StringBuilder builder = new StringBuilder();
			for (String id : indices.keySet()) {
				int index = indices.get(id);
				builder.append(docIDs.get(index));
				builder.append(" ");
				builder.append(id);
				builder.append(" ");
				builder.append(classes.get(index));
				builder.append("\n");
			}
			return builder.toString();
		}
		CResultInfo info = (CResultInfo) p_Result;
		StringBuilder builder = new StringBuilder();
		for (int instIdx = 0; instIdx < info.size(); instIdx++) {
			String docID = info.getDocID(instIdx);
			String id = info.getID(instIdx);
			if (docID == null) {
				docID = "";
			}
			builder.append(docID);
			builder.append(" ");
			builder.append(id);
			builder.append(" ");
			builder.append(info.classes[info.getAnswer(instIdx)]);
			builder.append("\n");
		}
		return builder.toString();
	}

}
