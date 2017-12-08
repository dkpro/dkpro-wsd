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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * wordnet sense index.
 *
 * @author zhongzhi
 *
 */
public class CWordNetSenseIndex implements ISenseIndex {
	protected static Pattern LEMMAPATTERN = Pattern.compile("^([^\\s]+)%");
	protected static Pattern POSPATTERN = Pattern.compile("\\%(\\d)");

	/**
	 * first sense of each lexelt lexelt -&gt; first sense
	 */
	protected Hashtable<String, String> m_FirstSenses = new Hashtable<String, String>();
	// offsets of lexelts
	protected Hashtable<String, List<String>> m_Offsets = new Hashtable<String, List<String>>();
	// senses of lexelts
	protected Hashtable<String, List<String>> m_Senses = new Hashtable<String, List<String>>();
	// sense index
	protected Hashtable<String, Integer> m_Indice = new Hashtable<String, Integer>();
	// sense offset
	protected ArrayList<String> m_SenseOffset = new ArrayList<String>();
	// sense number
	protected ArrayList<Integer> m_SenseNo = new ArrayList<Integer>();
	// sense glosses
	protected Hashtable<String, String> m_Glosses = new Hashtable<String, String>();

	/**
	 * sense type
	 *
	 * @author zhongzhi
	 *
	 */
	public enum SenseType {
		Original, // original wordnet sense: lemma%pos00xxx
		Number, // sense number
		Offset
		// offset-pos
	}

	/**
	 * constructor
	 * @param p_SenseIndexFile sense index file path
	 * @throws IOException exception while loading sense index
	 */
	public CWordNetSenseIndex(String p_SenseIndexFile) throws IOException {
		this(p_SenseIndexFile, SenseType.Original);
	}

	/**
	 * constructor
	 * @param p_SenseIndexFile sense index file path
	 * @param p_SenseType sense type
	 * @throws IOException exception while loading sense index
	 */
	public CWordNetSenseIndex(String p_SenseIndexFile, SenseType p_SenseType)
			throws IOException {
		Hashtable<String, Integer> inverseIndice = new Hashtable<String, Integer>();
		ArrayList<ArrayList<Integer>> ordersSet = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<String>> offsetsSet = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> sensesSet = new ArrayList<ArrayList<String>>();
		BufferedReader reader = new BufferedReader(new FileReader(
				p_SenseIndexFile));
		String line = null;
		StringTokenizer tokenizer = null;
		while ((line = reader.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() >= 3) {
				String sense = tokenizer.nextToken(); // sense
				String offset = tokenizer.nextToken(); // offset
				int no = Integer.parseInt(tokenizer.nextToken()); // number
				String lemma = getLemma(sense);
				String pos = getPOS(sense);
				lemma = lemma.replace('-', '_');
				String lexelt = lemma + "." + pos;
				if (!inverseIndice.containsKey(lexelt)) {
					inverseIndice.put(lexelt, inverseIndice.size());
					ordersSet.add(new ArrayList<Integer>());
					offsetsSet.add(new ArrayList<String>());
					sensesSet.add(new ArrayList<String>());
				}
				int index = inverseIndice.get(lexelt);
				ordersSet.get(index).add(no);
				offsetsSet.get(index).add(offset + "." + pos);
				sensesSet.get(index).add(sense);
				sense = this.getSense(sense, offset, pos, no, p_SenseType);
				this.m_Indice.put(sense, this.m_Indice.size());
				this.m_SenseNo.add(no);
				this.m_SenseOffset.add(offset + "." + pos);
				if (no == 1) {
					this.m_FirstSenses.put(lemma + "." + pos, sense);
				}
			} else {
			    reader.close();
				throw new IOException("Error format of sense index file! ["
						+ line + "]");
			}
		}
		reader.close();
		for (String lexelt : inverseIndice.keySet()) {
			int index = inverseIndice.get(lexelt);
			ArrayList<Integer> orders = ordersSet.get(index);
			String[] offsets = new String[orders.size()];
			String[] senses = new String[orders.size()];
			for (int i = 0; i < orders.size(); i++) {
				offsets[orders.get(i) - 1] = offsetsSet.get(index).get(i);
				senses[orders.get(i) - 1] = sensesSet.get(index).get(i);
			}
			int nullCnt = 0;
			for (int i = orders.size() - 1; i >= 0; i--) {
				if (offsets[i] == null) {
					nullCnt++;
				}
			}
			if (nullCnt > 0) {
				String[] newOffsets = new String[orders.size() - nullCnt];
				String[] newSenses = new String[orders.size() - nullCnt];
				System.arraycopy(offsets, 0, newOffsets, 0, newOffsets.length);
				System.arraycopy(senses, 0, newSenses, 0, newSenses.length);
				offsets = newOffsets;
				senses = newSenses;
			}
			this.m_Offsets.put(lexelt, Arrays.asList(offsets));
			this.m_Senses.put(lexelt, Arrays.asList(senses));
		}
	}

	/**
	 * generate sense of the given sense type
	 *
	 * @param p_Sense
	 *            sense
	 * @param p_Offset
	 *            offset
	 * @param p_POS
	 *            pos
	 * @param p_No
	 *            sense number
	 * @param p_SenseType
	 *            sense type
	 * @return sense
	 */
	protected String getSense(String p_Sense, String p_Offset, String p_POS,
			Integer p_No, SenseType p_SenseType) {
		if (p_SenseType.equals(SenseType.Number)) {
			return p_No.toString();
		}
		if (p_SenseType.equals(SenseType.Offset)) {
			return p_Offset + "-" + p_POS;
		}
		return p_Sense;
	}

	/**
	 * get the lemma of given sense
	 *
	 * @param p_Sense
	 *            sense
	 * @return lemma
	 */
	public static String getLemma(String p_Sense) {
		Matcher matcher = LEMMAPATTERN.matcher(p_Sense);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * get the POS of given sense
	 *
	 * @param p_Sense
	 *            sense
	 * @return POS (n, v, a, r)
	 */
	public static String getPOS(String p_Sense) {
		Matcher matcher = POSPATTERN.matcher(p_Sense);
		if (matcher.find()) {
			String stype = matcher.group(1);
			if (stype.equals("1")) {
				return "n";
			} else if (stype.equals("2")) {
				return "v";
			} else if (stype.equals("3") || stype.equals("5")) {
				return "a";
			} else if (stype.equals("4")) {
				return "r";
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ISenseIndex#getFirstSense(java.lang.String)
	 */
	@Override
	public String getFirstSense(String p_Lexelt) {
		if (this.m_FirstSenses.containsKey(p_Lexelt)) {
			return this.m_FirstSenses.get(p_Lexelt);
		}
		p_Lexelt = p_Lexelt.replaceAll("-", "_");
		if (this.m_FirstSenses.containsKey(p_Lexelt)) {
			return this.m_FirstSenses.get(p_Lexelt);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ISenseIndex#getSenseNo(java.lang.String)
	 */
	@Override
	public int getSenseNo(String sense) {
		if (this.m_Indice.containsKey(sense)) {
			return this.m_SenseNo.get(this.m_Indice.get(sense));
		}
		return 0;
	}

	/**
	 * get the sense gloss
	 *
	 * @param p_Sense
	 *            sense id
	 * @return sense gloss
	 */
	public String getSenseGloss(String p_Sense) {
		if (this.m_Indice.containsKey(p_Sense)) {
			return this.m_Glosses.get(this.m_SenseOffset.get(this.m_Indice
					.get(p_Sense)));
		}
		return null;
	}

	/**
	 * get the ordered offset list of p_Lexelt
	 *
	 * @param p_Lexelt
	 *            lexelt
	 * @return offset list
	 */
	public List<String> getOffsets(String p_Lexelt) {
		p_Lexelt = p_Lexelt.replace('-', '_');
		if (this.m_Offsets.containsKey(p_Lexelt)) {
			return this.m_Offsets.get(p_Lexelt);
		}
		return null;
	}

	/**
	 * get the ordered sense list of p_Lexelt
	 *
	 * @param p_Lexelt
	 *            lexelt
	 * @return sense list
	 */
	public List<String> getSenses(String p_Lexelt) {
		p_Lexelt = p_Lexelt.replace('-', '_');
		if (this.m_Senses.containsKey(p_Lexelt)) {
			return this.m_Senses.get(p_Lexelt);
		}
		return null;
	}

	/**
	 * get lexelts
	 * @return lexelts
	 */
	public Set<String> getLexelts() {
		return this.m_Offsets.keySet();
	}

	/**
	 * load WordNet data files
	 *
	 * @param p_Dir
	 *            WordNet dict directory
	 * @throws IOException
	 *             exception during reading data.pos
	 */
	public void loadDataFiles(String p_Dir) throws IOException {
		String line = null;
		String[] files = new String[] { "data.noun", "data.verb", "data.adj",
				"data.adv" };
		for (String file : files) {
			file = p_Dir + "/" + file;
			System.err.println(file);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith(" ")) {
					String[] parts = line.split("\\|");
					StringTokenizer tokenizer = new StringTokenizer(parts[0]);
					String offset = tokenizer.nextToken();
					tokenizer.nextToken();
					String pos = tokenizer.nextToken();
					if (pos.equals("s")) {
						pos = "a";
					}
					offset += "." + pos;
					this.m_Glosses.put(offset, parts[1].replace("'", "\""));
				}
			}
			reader.close();
		}
	}

}
