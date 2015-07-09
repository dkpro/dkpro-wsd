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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.MorphologicalProcessor;

/**
 * an interface to call jwnl.
 *
 * @author zhongzhi
 *
 */
public final class CJWNL {
	private static Dictionary dictionary;
	private static MorphologicalProcessor processor;
	private static Hashtable<String, String> cacheNOUN;
	private static Hashtable<String, String> cacheVERB;
	private static Hashtable<String, String> cacheADJ;
	private static Hashtable<String, String> cacheADV;
	private static Pattern LEMMAPATTERN = Pattern.compile("^([^\\s]+)%");
	private static Pattern POSPATTERN = Pattern.compile("\\%(\\d)");
	private static boolean STATE = false;
	public final static ArrayList<String> SHORTs = new ArrayList<String>();
	static {
		SHORTs.add("n");
		SHORTs.add("v");
		SHORTs.add("a");
		SHORTs.add("r");
	}

	/**
	 * initial jwnl
	 *
	 * @param p_Prop
	 *            prop input stream
	 * @throws JWNLException
	 *             jwnl exception
	 */
	public static void initial(InputStream p_Prop) throws JWNLException {
		STATE = false;
		String os = System.getProperty("os.name");
		System.setProperty("os.name", "unix");
		try {
			JWNL.initialize(p_Prop);
		} catch (JWNLException e) {
			System.setProperty("os.name", os);
			throw e;
		}
		STATE = true;
		dictionary = net.didion.jwnl.dictionary.Dictionary.getInstance();
		processor = dictionary.getMorphologicalProcessor();
		cacheADV = new Hashtable<String, String>();
		cacheADJ = new Hashtable<String, String>();
		cacheVERB = new Hashtable<String, String>();
		cacheNOUN = new Hashtable<String, String>();
	}

	/**
	 * check whether JWNL has been initialed
	 * @return status
	 */
	public static boolean isInitialed() {
		return STATE;
	}

	/**
	 * check status
	 */
	public static void checkStatus() {
		if (!isInitialed()) {
			throw new IllegalStateException("JWNL has not been initialed.");
		}
	}

	/**
	 * check whether there is sense definition for token p_Token of pos p_POS
	 * @param p_Token word
	 * @param p_POS part-of-speech
	 * @return has or not
	 */
	public static boolean hasSense(String p_Token, String p_POS) {
		checkStatus();
		POS pos = null;
		if (p_POS.equals("n")) {
			pos = POS.NOUN;
		} else if (p_POS.equals("a")) {
			pos = POS.ADJECTIVE;
		} else if (p_POS.equals("v")) {
			pos = POS.VERB;
		} else if (p_POS.equals("r")) {
			pos = POS.ADVERB;
		}
		return hasSense(p_Token, pos);
	}

	/**
	 * check whether there is sense definition for token p_Token of pos p_POS
	 * @param p_Token word
	 * @param p_POS part-of-speech
	 * @return has or not
	 */
	public static boolean hasSense(String p_Token, POS p_POS) {
		checkStatus();
		try {
			if (p_POS != null) {
				IndexWord indexWord = dictionary.lookupIndexWord(p_POS, p_Token);
				if (indexWord != null && indexWord.getLemma().replace(' ', '_').equals(p_Token)) {
					return true;
				}
			}
		} catch (JWNLException e) {
		}
		return false;
	}

	/**
	 * given the sense key (word%[1-5]:00...), return the sense number of it
	 *
	 * @param p_SenseKey
	 * @return sense number
	 */
	/*
	 * public static int getSenseNumber(String p_SenseKey) { return
	 * senseIndex.getSenseNo(p_SenseKey); }
	 */
	/**
	 * get the first sense
	 *
	 * @param p_LexeltID
	 * @return first sense
	 */
	/*
	 * public static String getFirstSense(String p_LexeltID) { if (true) {
	 * return "FIRST"; } return senseIndex.getFirstSense(p_LexeltID); }
	 */
	/**
	 * get POS given WordNet sense
	 *
	 * @param p_SenseKey
	 *            sense
	 * @return POS
	 */
	public static String getPOS(String p_SenseKey) {
		Matcher matcher = POSPATTERN.matcher(p_SenseKey);
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

	/**
	 * get the lemma given the sense key
	 *
	 * @param p_SenseKey
	 *            sense
	 * @return lemma
	 */
	public static String getLemma(String p_SenseKey) {
		Matcher matcher = LEMMAPATTERN.matcher(p_SenseKey);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * get the root form of p_Token with p_POS
	 *
	 * @param p_Token
	 *            word
	 * @param p_POS
	 *            part-of-speech
	 * @return root form
	 */
	public static String getRootForm(String p_Token, String p_POS) {
		checkStatus();
		if (p_Token == null) {
			return null;
		}
		p_Token = p_Token.trim().toLowerCase();
		String rootForm = p_Token;
		try {
			if (!rootForm.isEmpty()) {
				if (p_POS.equals("n")) {
					if (cacheNOUN.containsKey(p_Token)) {
						rootForm = cacheNOUN.get(p_Token);
					} else {
						rootForm = getRootForm(POS.NOUN, p_Token);
						cacheNOUN.put(p_Token, rootForm);
					}
				} else if (p_POS.equals("v")) {
					if (cacheVERB.containsKey(p_Token)) {
						rootForm = cacheVERB.get(p_Token);
					} else {
						rootForm = getRootForm(POS.VERB, p_Token);
						cacheVERB.put(p_Token, rootForm);
					}
				} else if (p_POS.equals("a")) {
					if (cacheADJ.containsKey(p_Token)) {
						rootForm = cacheADJ.get(p_Token);
					} else {
						rootForm = getRootForm(POS.ADJECTIVE, p_Token);
						cacheADJ.put(p_Token, rootForm);
					}
				} else if (p_POS.equals("r")) {
					if (cacheADV.containsKey(p_Token)) {
						rootForm = cacheADV.get(p_Token);
					} else {
						rootForm = getRootForm(POS.ADVERB, p_Token);
						cacheADV.put(p_Token, rootForm);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootForm;
	}

	// legal pattern
	private static Pattern LEGALPATTERN = Pattern.compile("^[a-z\\-_ \\.\\/']*$");

	// delimiter pattern
	@SuppressWarnings("unused")
    private static Pattern DELIMITERPATTERN = Pattern.compile("[\\-_ \\/]");

	@SuppressWarnings("unchecked")
	private static String getRootForm(POS p_POS, String p_Token) throws JWNLException {
		checkStatus();
		if (LEGALPATTERN.matcher(p_Token).matches()) {
//			if (DELIMITERPATTERN.matcher(p_Token).find()) {
				List<String> indexWords = processor.lookupAllBaseForms(p_POS, p_Token);
				if (indexWords.size() > 0) {
					for (String lemma:indexWords) {
						if (lemma.equals(p_Token)) {
							return lemma;
						}
					}
					p_Token = indexWords.get(0);
				}
/*			} else {
				IndexWord indexWord = processor.lookupBaseForm(p_POS, p_Token);
				if (indexWord != null) {
					p_Token = indexWord.getLemma();
				}
			}
*/		}
		return p_Token;
	}
}
