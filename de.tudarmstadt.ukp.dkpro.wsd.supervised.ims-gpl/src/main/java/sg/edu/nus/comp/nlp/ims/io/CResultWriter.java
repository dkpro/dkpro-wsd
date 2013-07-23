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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import sg.edu.nus.comp.nlp.ims.lexelt.CResultInfo;
import sg.edu.nus.comp.nlp.ims.util.CArgumentManager;

/**
 * save result to hard disk.
 *
 * @author zhongzhi
 *
 */
public class CResultWriter implements IResultWriter {

	// save directory
	protected String m_SaveDir;

	/**
	 * default constructor
	 */
	public CResultWriter() {
		this(".");
	}

	/**
	 * constructor with save directory
	 *
	 * @param p_SaveDir
	 *            save directory
	 */
	public CResultWriter(String p_SaveDir) {
		this.m_SaveDir = p_SaveDir;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.IResultWriter#write(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void write(Object p_Result) throws IOException {
		if (List.class.isInstance(p_Result)) {
			for (Object obj : (List<Object>) p_Result) {
				this.write(obj);
			}
			return;
		}
		CResultInfo info = (CResultInfo) p_Result;
		String savePath = this.getFile(info.getID());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(savePath)));
		for (int instIdx = 0; instIdx < info.size(); instIdx++) {
			String docID = info.getDocID(instIdx);
			String id = info.getID(instIdx);
			if (docID == null) {
				docID = "";
			}
			writer.write(docID);
			writer.write(" ");
			writer.write(id);
			writer.write(" ");
			writer.write(info.classes[info.getAnswer(instIdx)]);
			writer.newLine();
		}
		writer.flush();
		writer.close();
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
	 * @see sg.edu.nus.comp.nlp.ims.io.IResultWriter#toString(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String toString(Object p_Result) {
		if (List.class.isInstance(p_Result)) {
			StringBuilder builder = new StringBuilder();
			for (Object obj : (List<Object>) p_Result) {
				builder.append(this.toString(obj));
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

	/**
	 * get the path to save lexelt
	 *
	 * @param p_LexeltID
	 *            lexelt id
	 * @return path
	 */
	protected String getFile(String p_LexeltID) {
		return this.m_SaveDir + "/" + p_LexeltID + ".result";
	}
}
