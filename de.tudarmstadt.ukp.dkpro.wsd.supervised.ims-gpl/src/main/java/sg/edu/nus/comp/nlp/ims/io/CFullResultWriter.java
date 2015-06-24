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

/**
 * write the result with probabilities to disk.
 *
 * @author zhongzhi
 *
 */
public class CFullResultWriter extends CResultWriter {

	/**
	 * default constructor
	 */
	public CFullResultWriter() {
		this(".");
	}

	/**
	 * constructor with save directory
	 *
	 * @param p_SaveDir
	 *            save directory
	 */
	public CFullResultWriter(String p_SaveDir) {
		super(p_SaveDir);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.CResultWriter#write(java.lang.Object)
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
			for (int i = 0; i < info.probabilities[instIdx].length; i++) {
				writer.write(" ");
				writer.write(info.classes[i]);
				writer.write(" ");
				writer.write(Double.toString(info.probabilities[instIdx][i]));
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.CResultWriter#toString(java.lang.Object)
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
			for (int i = 0; i < info.probabilities[instIdx].length; i++) {
				builder.append(" ");
				builder.append(info.classes[i]);
				builder.append(" ");
				builder.append(Double.toString(info.probabilities[instIdx][i]));
			}
			builder.append("\n");
		}
		return builder.toString();
	}

}
