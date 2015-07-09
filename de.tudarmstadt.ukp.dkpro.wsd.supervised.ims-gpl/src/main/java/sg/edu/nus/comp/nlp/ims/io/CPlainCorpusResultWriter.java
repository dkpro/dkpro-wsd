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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import sg.edu.nus.comp.nlp.ims.corpus.AItem;
import sg.edu.nus.comp.nlp.ims.corpus.ICorpus;
import sg.edu.nus.comp.nlp.ims.corpus.ISentence;
import sg.edu.nus.comp.nlp.ims.lexelt.CResultInfo;
import sg.edu.nus.comp.nlp.ims.util.CArgumentManager;

/**
 * result writer for plain corpus.
 * @author zhongzhi
 * The disambiguated word will be surrounded with tag &lt;x\\&gt;, with senses and probabilities as attribution.
 */
public class CPlainCorpusResultWriter implements IResultWriter {
	// save directory
	protected String m_SavePath;
	// corpus
	protected ICorpus m_Corpus;

	/**
	 * default constructor
	 */
	public CPlainCorpusResultWriter() {
		this(new Date().getTime() + "result.txt");
	}

	/**
	 * constructor
	 * @param p_SavePath savepath
	 */
	public CPlainCorpusResultWriter(String p_SavePath) {
		this(null, p_SavePath);
	}

	/**
	 * constructor
	 * @param p_Corpus corpus
	 */
	public CPlainCorpusResultWriter(ICorpus p_Corpus) {
		this.m_Corpus = p_Corpus;
	}

	/**
	 * constructor with initialed save directory
	 *
	 * @param p_SavePath
	 *            save directory
	 */
	public CPlainCorpusResultWriter(ICorpus p_Corpus, String p_SavePath) {
		this.m_Corpus = p_Corpus;
		this.m_SavePath = p_SavePath;
	}

	/**
	 * set corpus
	 * @param p_Corpus corpus
	 */
	public void setCorpus(ICorpus p_Corpus) {
		this.m_Corpus = p_Corpus;
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.IResultWriter#setOptions(java.lang.String[])
	 */
	@Override
	public void setOptions(String[] p_Options) {
		CArgumentManager argmgr = new CArgumentManager(p_Options);
		if (argmgr.has("s")) {
			this.m_SavePath = argmgr.get("s");
		}
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.IResultWriter#toString(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String toString(Object result) {
		StringBuilder builder = new StringBuilder();
		Hashtable <String, Integer> id2Index = new Hashtable <String, Integer>();
		for (int i = 0; i < this.m_Corpus.size(); i++) {
			id2Index.put(this.m_Corpus.getValue(i, "id"), i);
		}
		CResultInfo[] results = new CResultInfo[id2Index.size()];
		int[] indice = new int[id2Index.size()];
		if (List.class.isInstance(result)) {
			for (CResultInfo lexelt:(List<CResultInfo>)result) {
				for (int i = 0; i < lexelt.size(); i++) {
					String id = lexelt.getID(i);
					int index = id2Index.get(id);
					results[index] = lexelt;
					indice[index] = i;
				}
			}
		} else {
			CResultInfo lexelt = (CResultInfo) result;
			for (int i = 0; i < lexelt.size(); i++) {
				String id = lexelt.getID(i);
				int index = id2Index.get(id);
				results[index] = lexelt;
				indice[index] = i;
			}
		}
		int tIndex = AItem.Features.TOKEN.ordinal();
		int curr = 0;
		int sent = this.m_Corpus.getSentenceID(curr);
		int index = this.m_Corpus.getIndexInSentence(curr);
		int length = this.m_Corpus.getLength(curr);
		for (int s = 0; s < this.m_Corpus.numOfSentences(); s++) {
			ISentence sentence = this.m_Corpus.getSentence(s);
			for (int i = 0; i < sentence.size(); i++) {
				if (i != 0) {
					builder.append(" ");
				}
				String token = sentence.getItem(i).get(tIndex);
				if (s == sent && index == i) {
					if (results[curr].numClasses() == 1 && results[curr].classes[0] == null) {
						throw new IllegalStateException(results[curr].lexelt + ":" + sent + ":" + index + ":" + token);
					} else {
						boolean hasLegalSense = false;
						ArrayList<ArrayList<String>> senses = new ArrayList<ArrayList<String>>();
						ArrayList<ArrayList<Double>> probs = new ArrayList<ArrayList<Double>>();
						ArrayList<Double> totals = new ArrayList<Double>();
						do {
							if (results[curr].numClasses() != 1 || !results[curr].classes[0].equals("U")) {
								hasLegalSense = true;
								while (totals.size() < length) {
									senses.add(new ArrayList<String>());
									probs.add(new ArrayList<Double>());
									totals.add(0.0);
								}
								for (int c = 0; c < results[curr].numClasses(); c++) {
									if (results[curr].classes[c] == null) {
										throw new IllegalStateException(sent + ":" + index + ":" + token);
									}
									senses.get(length - 1).add(results[curr].classes[c]);
									double prob = results[curr].probabilities[indice[curr]][c];
									probs.get(length - 1).add(prob);
									totals.set(length - 1, totals.get(length - 1) + prob);
								}
							}
							curr++;
							if (curr < this.m_Corpus.size()) {
								sent = this.m_Corpus.getSentenceID(curr);
								index = this.m_Corpus.getIndexInSentence(curr);
								length = this.m_Corpus.getLength(curr);
							} else {
								sent = -1;
								index = -1;
								length = 0;
							}
						} while (s == sent && index == i);
						if (hasLegalSense) {
							builder.append("<x");
							for (int l = 0; l < totals.size(); l++) {
								if (senses.get(l).size() > 0) {
									builder.append(" length=\"" + (l + 1));
									for (int c = 0; c < senses.get(l).size(); c++) {
										builder.append(" ");
										builder.append(senses.get(l).get(c));
										builder.append("|");
										builder.append(probs.get(l).get(c) / totals.get(l));
									}
									builder.append("\"");
								}
							}
							builder.append(">");
						}
						builder.append(token);
						if (hasLegalSense) {
							builder.append("</x>");
						}
					}
				} else {
					builder.append(token);
				}
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.io.IResultWriter#write(java.lang.Object)
	 */
	@Override
	public void write(Object result) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(this.m_SavePath));
		writer.write(this.toString(result));
		writer.flush();
		writer.close();
	}

}
