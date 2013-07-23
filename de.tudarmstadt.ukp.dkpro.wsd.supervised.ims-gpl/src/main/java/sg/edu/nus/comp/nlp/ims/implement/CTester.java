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

package sg.edu.nus.comp.nlp.ims.implement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.nlp.ims.classifiers.CLibLinearEvaluator;
import sg.edu.nus.comp.nlp.ims.classifiers.IEvaluator;
import sg.edu.nus.comp.nlp.ims.corpus.*;
import sg.edu.nus.comp.nlp.ims.feature.*;
import sg.edu.nus.comp.nlp.ims.instance.*;
import sg.edu.nus.comp.nlp.ims.io.*;
import sg.edu.nus.comp.nlp.ims.lexelt.*;
import sg.edu.nus.comp.nlp.ims.util.*;

/**
 * main interface of test.
 *
 * @author zhongzhi
 *
 */
public class CTester {
	// default instance extractor class name
	protected static final String INSTANCEEXTRACTOR = CInstanceExtractor.class
			.getName();
	// default feature extractor class name
	protected static final String FEATUREEXTRACTOR = CFeatureExtractorCombination.class
			.getName();
	// default corpus class name
	protected static final String CORPUS = CLexicalCorpus.class.getName();

	// evaluator
	protected IEvaluator m_Evaluator = new CLibLinearEvaluator();
	// writer
	protected IResultWriter m_Writer = new CResultWriter();
	// results
	protected ArrayList<Object> m_Results = new ArrayList<Object>();
	// instance extractor class name
	protected String m_InstanceExtractorName = INSTANCEEXTRACTOR;
	// feature extractor class name
	protected String m_FeatureExtractorName = FEATUREEXTRACTOR;
	// corpus class name
	protected String m_CorpusName = CORPUS;
	// delimiter
	protected String m_Delimiter = null;
	// sentence split
	protected boolean m_Split = false;
	// tokenized
	protected boolean m_Tokenized = false;
	// lemmatized
	protected boolean m_Lemmatized = false;
	// pos tagged
	protected boolean m_POSTagged = false;

	/**
	 * test xml file
	 *
	 * @param p_XmlFile
	 *            test file
	 * @throws Exception
	 *             test exception
	 */
	public void test(String p_XmlFile) throws Exception {
		Reader reader = new InputStreamReader(new FileInputStream(p_XmlFile));
		this.test(reader);
		reader.close();
	}

	/**
	 * test a xml file with given lexelt ids for each test instance
	 *
	 * @param p_XmlFile
	 *            test file
	 * @param p_LexeltFile
	 *            lexelt id of each instances
	 * @throws Exception
	 *             test exception
	 */
	public void test(String p_XmlFile, String p_LexeltFile) throws Exception {
		String line = null;
		StringTokenizer tokenizer = null;
		Hashtable<String, ArrayList<String>> instanceLexeltIDs = new Hashtable<String, ArrayList<String>>();
		BufferedReader lexeltReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(p_LexeltFile)));
		while ((line = lexeltReader.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() < 2) {
				lexeltReader.close();
			}
			String id = tokenizer.nextToken();
			ArrayList<String> lexeltIDs = new ArrayList<String>();
			while (tokenizer.hasMoreTokens()) {
				lexeltIDs.add(tokenizer.nextToken());
			}
			instanceLexeltIDs.put(id, lexeltIDs);
		}
		lexeltReader.close();
		Reader reader = new InputStreamReader(new FileInputStream(p_XmlFile));
		this.test(reader, instanceLexeltIDs);
		reader.close();
	}

	/**
	 * test
	 *
	 * @param p_XmlReader
	 *            test file reader
	 * @throws Exception
	 *             test exceptoin
	 */
	public void test(Reader p_XmlReader) throws Exception {
		this.test(p_XmlReader, null);
	}

	/**
	 * test
	 *
	 * @param p_XmlReader
	 *            test file reader
	 * @param p_InstanceLexeltIDs
	 *            instace lexelt ids
	 * @throws Exception
	 *             test exception
	 */
	public void test(Reader p_XmlReader, Hashtable<String, ArrayList<String>> p_InstanceLexeltIDs)
			throws Exception {
		IInstanceExtractor instExtractor = (IInstanceExtractor) Class.forName(
				this.m_InstanceExtractorName).newInstance();
		IFeatureExtractor featExtractor = (IFeatureExtractor) Class.forName(
				this.m_FeatureExtractorName).newInstance();
		ACorpus corpus = (ACorpus) Class.forName(this.m_CorpusName)
				.newInstance();
		if (this.m_Delimiter != null) {
			corpus.setDelimiter(this.m_Delimiter);
		}
		corpus.setSplit(this.m_Split);
		corpus.setTokenized(this.m_Tokenized);
		corpus.setPOSTagged(this.m_POSTagged);
		corpus.setLemmatized(this.m_Lemmatized);
		corpus.load(p_XmlReader);

		if (this.m_Writer != null && CPlainCorpusResultWriter.class.isInstance(this.m_Writer)) {
			((CPlainCorpusResultWriter)this.m_Writer).setCorpus(corpus);
		}
		instExtractor.setCorpus(corpus);
		instExtractor.setFeatureExtractor(featExtractor);

		Hashtable<String, ILexelt> lexelts = new Hashtable<String, ILexelt>();
		while (instExtractor.hasNext()) {
			IInstance instance = instExtractor.next();
			String lexeltID = instance.getLexeltID();
			if (p_InstanceLexeltIDs != null) {
				if (p_InstanceLexeltIDs.containsKey(instance.getID())) {
					ArrayList<String> ids = p_InstanceLexeltIDs.get(instance
							.getID());
					for (int i = 0; i < ids.size(); i++) {
						lexeltID = ids.get(i);
						if (!lexelts.containsKey(lexeltID)) {
							lexelts.put(lexeltID, new CLexelt(lexeltID));
						}
						lexelts.get(lexeltID).addInstance(instance);
					}
				} else {
					throw new Exception("instance \"" + instance.getID()
							+ "\" is not defined in lexelt file.");
				}
			} else {
				if (!lexelts.containsKey(lexeltID)) {
					lexelts.put(lexeltID, new CLexelt(lexeltID));
				}
				lexelts.get(lexeltID).addInstance(instance);
			}
		}
		ArrayList<String> lexeltIDs = new ArrayList<String>();
		lexeltIDs.addAll(lexelts.keySet());
		Collections.sort(lexeltIDs);
		for (String lexeltID : lexeltIDs) {
			System.err.println(lexeltID);
			Object lexelt = lexelts.remove(lexeltID);
			this.m_Results.add(this.m_Evaluator.evaluate(lexelt));
		}
	}

	/**
	 * get results
	 *
	 * @return results
	 */
	public ArrayList<Object> getResults() {
		return this.m_Results;
	}

	/**
	 * whether the input is already split
	 * @param p_Split whether split
	 */
	public void setSplit(boolean p_Split) {
		this.m_Split = p_Split;
	}

	/**
	 * whether sentences are already tokenized
	 * @param p_Tokenized whether tokenized
	 */
	public void setTokenized(boolean p_Tokenized) {
		this.m_Tokenized = p_Tokenized;
	}

	/**
	 * whether the pos info is provided
	 * @param p_POSTagged whether pos tagged
	 */
	public void setPOSTagged(boolean p_POSTagged) {
		this.m_POSTagged = p_POSTagged;
	}

	/**
	 * whether the lemma info is provided
	 * @param p_Lemmatized whether lemmatized
	 */
	public void setLemmatized(boolean p_Lemmatized) {
		this.m_Lemmatized = p_Lemmatized;
	}

	/**
	 * set the delimiter
	 * @param p_Delimiter delimiter
	 */
	public void setDelimiter(String p_Delimiter) {
		this.m_Delimiter = p_Delimiter;
	}

	/**
	 * set evaluator
	 *
	 * @param p_Evaluator
	 *            evaluator
	 */
	public void setEvaluator(IEvaluator p_Evaluator) {
		this.m_Evaluator = p_Evaluator;
	}

	/**
	 * set writer
	 *
	 * @param p_Writer
	 *            writer
	 */
	public void setWriter(IResultWriter p_Writer) {
		this.m_Writer = p_Writer;
	}

	/**
	 * set the corpus class name
	 *
	 * @param p_Name
	 *            corpus class name
	 */
	public void setCorpusClassName(String p_Name) {
		this.m_CorpusName = p_Name;
	}

	/**
	 * set the instance extractor name
	 *
	 * @param p_Name
	 *            instance extractor name
	 */
	public void setInstanceExtractorName(String p_Name) {
		this.m_InstanceExtractorName = p_Name;
	}

	/**
	 * set the feature extractor name
	 *
	 * @param p_Name
	 *            feature extractor name
	 */
	public void setFeatureExtractorName(String p_Name) {
		this.m_FeatureExtractorName = p_Name;
	}

	/**
	 * write result
	 *
	 * @throws IOException
	 *             exception while write
	 */
	public void write() throws IOException {
		this.m_Writer.write(this.m_Results);
	}

	/**
	 * clear results
	 */
	public void clear() {
		this.m_Results.clear();
	}

	/**
	 * @param p_Args
	 *            arguments
	 */
	public static void main(String[] p_Args) {
		try {
			String generalOptions = "Usage: testPath modelDir statisticDir saveDir\n"
					+ "\t-i class name of Instance Extractor(default sg.edu.nus.comp.nlp.ims.instance.CInstanceExtractor)\n"
					+ "\t-f class name of Feature Extractor(default sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination)\n"
					+ "\t-c class name of Corpus(default sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus)\n"
					+ "\t-e class name of Evaluator(default sg.edu.nus.comp.nlp.ims.classifiers.CLibLinearEvaluator)\n"
					+ "\t-r class name of Result Writer(default sg.edu.nus.comp.nlp.ims.io.CResultWriter)\n"
					+ "\t-lexelt path of lexelt file\n"
					+ "\t-is path of index.sense(option)\n"
					+ "\t-prop path of prop.xml for JWNL\n"
					+ "\t-split 1/0 whether the corpus is sentence splitted(default 0)\n"
					+ "\t-ssm path of sentence splitter model\n"
					+ "\t-token 1/0 whether the corpus is tokenized(default 0)\n"
					+ "\t-pos 1/0 whether the pos tag is provided in corpus(default 0)\n"
					+ "\t-ptm path POS tagger model\n"
					+ "\t-dict path of dictionary for opennlp POS tagger(option)\n"
					+ "\t-tagdict path of tagdict for POS tagger(option)\n"
					+ "\t-lemma 1/0 whether the lemma is provided in the corpus(default 0)\n"
					+ "\t-delimiter the delimiter to separate tokens, lemmas and POS tags (default \"/\")\n"
					+ "\t-type type of testPath\n"
					+ "\t\tdirectory: test all xml files under directory testPath\n"
					+ "\t\tlist: test all files listed in file testPath\n"
					+ "\t\tfile(default): test file testPath\n";

			CArgumentManager argmgr = new CArgumentManager(p_Args);
			if (argmgr.size() != 4) { // check arguments
				throw new IllegalArgumentException(generalOptions);
			}
			CTester tester = new CTester();
			String type = "file";
			File testPath = new File(argmgr.get(0));
			String modelDir = argmgr.get(1);
			String statDir = argmgr.get(2);
			String saveDir = argmgr.get(3);
			String evaluatorName = CLibLinearEvaluator.class.getName();
			String writerName = CResultWriter.class.getName();
			String lexeltFile = null;
			if (argmgr.has("lexelt")) {
				lexeltFile = argmgr.get("lexelt");
			}
			if (argmgr.has("type")) {
				type = argmgr.get("type");
			}

			// initial JWordNet
			if (!argmgr.has("prop")) {
				System.err.println("prop.xml file for JWNL has not been set.");
				throw new IllegalArgumentException(generalOptions);
			}
			CJWNL.initial(new FileInputStream(argmgr.get("prop")));

			// set sentence splitter
			if (argmgr.has("split") && Integer.parseInt(argmgr.get("split")) == 1) {
				tester.setSplit(true);
			}
			if (argmgr.has("ssm")) {
				COpenNLPSentenceSplitter.setDefaultModel(argmgr.get("ssm"));
			}

			if (argmgr.has("token") && Integer.parseInt(argmgr.get("token")) == 1) {
				tester.setTokenized(true);
			}

			// set pos tagger
			if (argmgr.has("pos") && Integer.parseInt(argmgr.get("pos")) == 1) {
				tester.setPOSTagged(true);
				tester.setTokenized(true);
			}
			if (argmgr.has("ptm")) {
				COpenNLPPOSTagger.setDefaultModel(argmgr.get("ptm"));
			}
			if (argmgr.has("dict")) {
				COpenNLPPOSTagger.setDefaultDictionary(argmgr.get("dict"));
			}
			if (argmgr.has("tagdict")) {
				COpenNLPPOSTagger.setDefaultPOSDictionary(argmgr.get("tagdict"));
			}

			if (argmgr.has("lemma") && Integer.parseInt(argmgr.get("lemma")) == 1) {
				tester.setLemmatized(true);
				tester.setTokenized(true);
			}

			if (argmgr.has("delimiter")) {
				tester.setDelimiter(argmgr.get("delimiter"));
			}

			// set evaluator
			if (argmgr.has("e")) {
				evaluatorName = argmgr.get("e");
			}
			IEvaluator evaluator = (IEvaluator) Class.forName(evaluatorName)
					.newInstance();
			if (argmgr.has("l") && argmgr.has("permanent")) {
				evaluator.setOptions(new String[]{"-m", modelDir, "-s", statDir, "-l", argmgr.get("l"), "-permanent", argmgr.get("permanent")});
			} else if (argmgr.has("l")) {
				evaluator.setOptions(new String[]{"-m", modelDir, "-s", statDir, "-l", argmgr.get("l")});
			} else {
				evaluator.setOptions(new String[]{"-m", modelDir, "-s", statDir});
			}

			evaluator.setOptions(new String[] { "-m", modelDir, "-s", statDir });
			if (argmgr.has("is")) {
				ISenseIndex senseIndex = new CWordNetSenseIndex(argmgr
						.get("is"));
				evaluator.setSenseIndex(senseIndex);
			}
			// set result writer
			if (argmgr.has("r")) {
				writerName = argmgr.get("r");
			}
			IResultWriter writer = (IResultWriter) Class.forName(writerName)
					.newInstance();
			writer.setOptions(new String[] { "-s", saveDir });

			tester.setEvaluator(evaluator);
			tester.setWriter(writer);
			if (argmgr.has("i")) {
				tester.setInstanceExtractorName(argmgr.get("i"));
			}
			if (argmgr.has("f")) {
				tester.setFeatureExtractorName(argmgr.get("f"));
			}
			if (argmgr.has("c")) {
				tester.setCorpusClassName(argmgr.get("c"));
			}

			Pattern xmlPattern = Pattern.compile("([^\\/]*)\\.xml$");
			Matcher matcher = null;
			ArrayList<File> testFiles = new ArrayList<File>();
			if (type.equals("list")) { // in file
				String line = null;
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(testPath)));
				while ((line = reader.readLine()) != null) {
					testFiles.add(new File(line));
				}
				reader.close();
			} else if (type.equals("directory")) {
				if (!testPath.exists() || !testPath.isDirectory()) {
					throw new Exception("Error: cannot not find test path "
							+ testPath.getName() + "!\n");
				}
				File[] files = testPath.listFiles();
				for (File file : files) {
					matcher = xmlPattern.matcher(file.getAbsolutePath());
					if (matcher.find()) {
						testFiles.add(file);
					}
				}
			} else {
				testFiles.add(testPath);
			}
			for (File testFile : testFiles) {
				System.err.println("testing " + testFile.getAbsolutePath());
				if (lexeltFile != null) {
					tester.test(testFile.getAbsolutePath(), lexeltFile);
				} else {
					tester.test(testFile.getAbsolutePath());
				}
				System.err.println("writing results");
				tester.write();
				tester.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
