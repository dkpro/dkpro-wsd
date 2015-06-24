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

import java.io.*; // import java.util.regex.Matcher;
// import java.util.regex.Pattern;

import opennlp.maxent.GISModel;
import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.ngram.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerME;

/**
 * opennlp POS tagger.
 *
 * @author zhongzhi
 *
 */
public final class COpenNLPPOSTagger extends APTBPOSTagger {

	private static MaxentModel MODEL = null;
	private static Dictionary DICTIONARY = null;
	private static POSDictionary POSDICT = null;

	/**
	 * set default pos tagger model
	 *
	 * @param p_Model
	 *            model
	 */
	public static void setDefaultModel(GISModel p_Model) {
		MODEL = p_Model;
	}

	/**
	 * set default POS tagger model
	 *
	 * @param p_File
	 *            model file
	 * @throws IOException
	 *             exception while reading model
	 */
	public static void setDefaultModel(File p_File) throws IOException {
		setDefaultModel(getGISModel(p_File));
	}

	/**
	 * set default pos tagger model
	 *
	 * @param p_FileName
	 *            model file name
	 * @throws IOException
	 *             exception while read model
	 */
	public static void setDefaultModel(String p_FileName) throws IOException {
		setDefaultModel(new File(p_FileName));
	}

	/**
	 * set default tag dictionary
	 *
	 * @param p_Dict
	 *            dictionary
	 */
	public static void setDefaultDictionary(Dictionary p_Dict) {
		DICTIONARY = p_Dict;
	}

	/**
	 * set default tag dictionary
	 *
	 * @param p_FileName
	 *            dictionary name
	 * @throws IOException
	 *             exception while reading dictionary
	 */
	public static void setDefaultDictionary(String p_FileName)
			throws IOException {
		setDefaultDictionary(new Dictionary(p_FileName));
	}

	/**
	 * set default tag dictionary
	 *
	 * @param p_POSDict
	 *            dictionary
	 */
	public static void setDefaultPOSDictionary(POSDictionary p_POSDict) {
		POSDICT = p_POSDict;
	}

	/**
	 * set default tag dictionary
	 *
	 * @param p_File
	 *            dictionary file
	 * @throws IOException
	 *             exception while reading dictionary
	 */
	public static void setDefaultPOSDictionary(String p_File)
			throws IOException {
		setDefaultPOSDictionary(p_File, true);
	}

	/**
	 * set default tag dictionary
	 *
	 * @param p_FileName
	 *            dictionary file name
	 * @param caseSensitive
	 *            case sensitive
	 * @throws IOException
	 *             exception while read dictionary
	 */
	public static void setDefaultPOSDictionary(String p_FileName,
			boolean caseSensitive) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(p_FileName)));
		setDefaultPOSDictionary(reader, caseSensitive);
		reader.close();
	}

	/**
	 * set default tag dictionary
	 *
	 * @param p_Reader
	 *            dictionary reader
	 * @throws IOException
	 *             exception while reading dictionary
	 */
	public static void setDefaultPOSDictionary(BufferedReader p_Reader)
			throws IOException {
		POSDictionary dict = new POSDictionary(p_Reader, true);
		setDefaultPOSDictionary(dict);
	}

	/**
	 * set default tag dictionary
	 *
	 * @param p_Reader
	 *            dictionary reader
	 * @param caseSensitive
	 *            case sensitive
	 * @throws IOException
	 *             exception while read dictionary
	 */
	public static void setDefaultPOSDictionary(BufferedReader p_Reader,
			boolean caseSensitive) throws IOException {
		POSDictionary dict = new POSDictionary(p_Reader, caseSensitive);
		setDefaultPOSDictionary(dict);
	}

	/**
	 * load GISModel from file
	 *
	 * @param p_File
	 *            model file
	 * @return GISModel
	 * @throws IOException
	 *             io exception while loading model
	 */
	private static GISModel getGISModel(File p_File) throws IOException {
		return new SuffixSensitiveGISModelReader(p_File).getModel();
	}

	private POSTaggerME m_Tagger = null;

	/**
	 * constructor
	 */
	public COpenNLPPOSTagger() {
		this(MODEL, DICTIONARY, POSDICT);
	}

	/**
	 * constructor
	 * @param p_Model opennlp pos tagger model
	 */
	public COpenNLPPOSTagger(MaxentModel p_Model) {
		this(p_Model, DICTIONARY);
	}

	/**
	 * constructor
	 * @param p_ModelFile model file
	 * @throws IOException exception while reading model
	 */
	public COpenNLPPOSTagger(File p_ModelFile) throws IOException {
		this(getGISModel(p_ModelFile));
	}

	/**
	 * constructor
	 * @param p_ModelFile model file
	 * @throws IOException exception while reading model
	 */
	public COpenNLPPOSTagger(String p_ModelFile) throws IOException {
		this(new File(p_ModelFile));
	}

	/**
	 * constructor
	 * @param p_Dict dictionary
	 */
	public COpenNLPPOSTagger(Dictionary p_Dict) {
		this(MODEL, p_Dict);
	}

	/**
	 * constructor
	 * @param p_POSDict pos dictionary
	 */
	public COpenNLPPOSTagger(POSDictionary p_POSDict) {
		this(MODEL, DICTIONARY, p_POSDict);
	}

	/**
	 * constructor
	 * @param p_Model pos tagger model
	 * @param p_POSDict pos dictionary
	 */
	public COpenNLPPOSTagger(MaxentModel p_Model, POSDictionary p_POSDict) {
		this(p_Model, DICTIONARY, p_POSDict);
	}

	/**
	 * constructor
	 * @param p_Model pos tagger model
	 * @param p_Dictionary dictionary
	 */
	public COpenNLPPOSTagger(MaxentModel p_Model, Dictionary p_Dictionary) {
		this(p_Model, p_Dictionary, POSDICT);
	}

	/**
	 * constructor
	 * @param p_Dictionary dictionary
	 * @param p_POSDict pos dictionary
	 */
	public COpenNLPPOSTagger(Dictionary p_Dictionary, POSDictionary p_POSDict) {
		this(MODEL, p_Dictionary, p_POSDict);
	}

	/**
	 * constructor
	 * @param p_Model pos tagger model
	 * @param p_Dictionary dictionary
	 * @param p_POSDict pos dictionary
	 */
	public COpenNLPPOSTagger(MaxentModel p_Model, Dictionary p_Dictionary,
			POSDictionary p_POSDict) {
		if (p_Model != null) {
			if (p_POSDict != null) {
				this.m_Tagger = new POSTaggerME(p_Model, new DefaultPOSContextGenerator(p_Dictionary), p_POSDict);
			} else {
				this.m_Tagger = new POSTaggerME(p_Model, new DefaultPOSContextGenerator(p_Dictionary));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.APTBPOSTagger#tag(java.lang.String)
	 */
	@Override
	public String tag(String p_Sentence) {
		if (this.m_Tagger == null) {
			throw new IllegalStateException("no pos tagger model is specified.");
		}
		if (p_Sentence == null || p_Sentence.isEmpty()) {
			return "";
		}
		return this.m_Tagger.tag(p_Sentence);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.APTBPOSTagger#getTag(java.lang.String)
	 */
	@Override
	public String getTag(String input) {
		String retVal = null;
		if (input != null) {
			int index = input.lastIndexOf('/');
			if (index >= 0 && index < input.length()) {
				retVal = input.substring(index + 1);
			}
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.APTBPOSTagger#getToken(java.lang.String)
	 */
	@Override
	public String getToken(String input) {
		String retVal = null;
		if (input != null) {
			int index = input.lastIndexOf('/');
			if (index >= 0 && index < input.length()) {
				retVal = input.substring(0, index);
			}
		}
		return retVal;
	}

	/**
	 * pos tag a given file, which was supposed to be tokenized
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		CArgumentManager argmgr = new CArgumentManager(args);
		COpenNLPPOSTagger.setDefaultModel(argmgr.get("ptm"));
		if (argmgr.has("dict")) {
			COpenNLPPOSTagger.setDefaultDictionary(argmgr.get("dict"));
		}
		if (argmgr.has("tagdict")) {
			COpenNLPPOSTagger.setDefaultPOSDictionary(argmgr.get("tagdict"));
		}
		COpenNLPPOSTagger tagger = new COpenNLPPOSTagger();
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(argmgr.get(0))));
		while ((line = reader.readLine()) != null) {
			System.out.println(tagger.tag(line));
		}
		reader.close();
	}
}
