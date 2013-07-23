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

import sg.edu.nus.comp.nlp.ims.classifiers.CLibLinearEvaluator;
import sg.edu.nus.comp.nlp.ims.classifiers.IEvaluator;
import sg.edu.nus.comp.nlp.ims.corpus.ACorpus;
import sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus;
import sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination;
import sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor;
import sg.edu.nus.comp.nlp.ims.instance.CInstanceExtractor;
import sg.edu.nus.comp.nlp.ims.instance.IInstance;
import sg.edu.nus.comp.nlp.ims.instance.IInstanceExtractor;
import sg.edu.nus.comp.nlp.ims.io.CPlainCorpusResultWriter;
import sg.edu.nus.comp.nlp.ims.io.CResultWriter;
import sg.edu.nus.comp.nlp.ims.io.IResultWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.CLexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.util.CJWNL;
import sg.edu.nus.comp.nlp.ims.util.COpenNLPPOSTagger;
import sg.edu.nus.comp.nlp.ims.util.COpenNLPSentenceSplitter;
import sg.edu.nus.comp.nlp.ims.util.CWordNetSenseIndex;
import sg.edu.nus.comp.nlp.ims.util.ISenseIndex;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DKProContext;

public class WSDTest {
	// default instance extractor class name
	protected static final String INSTANCEEXTRACTOR = CInstanceExtractor.class.getName();
	// default feature extractor class name
	protected static final String FEATUREEXTRACTOR = CFeatureExtractorCombination.class.getName();
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

	public static void main(String[] p_Args) throws Exception {

	    File BASE_DIR = DKProContext.getContext().getWorkspace("ims_wsd").getAbsoluteFile();
		String LIB_DIR = BASE_DIR + "/lib/";
		
		WSDTest tester = new WSDTest();
		File testPath = new File("src/test/resources/plain/test.txt");
		String modelDir = BASE_DIR + "models/";
		String statDir = "target/ims_stat/";
		String saveDir = "target/ims_results/";
		String evaluatorName = CLibLinearEvaluator.class.getName();
//		String writerName = CResultWriter.class.getName();
        String writerName = CPlainCorpusResultWriter.class.getName();

		CJWNL.initial(new FileInputStream(new File(LIB_DIR + "prop.xml")));

		COpenNLPSentenceSplitter.setDefaultModel(LIB_DIR + "EnglishSD.bin.gz");
		COpenNLPPOSTagger.setDefaultModel(LIB_DIR + "tag.bin.gz");
		COpenNLPPOSTagger.setDefaultPOSDictionary(LIB_DIR + "tagdict.txt");

		IEvaluator evaluator = (IEvaluator) Class.forName(evaluatorName).newInstance();
		evaluator.setOptions(new String[]{"-m", modelDir, "-s", statDir});

		ISenseIndex senseIndex = new CWordNetSenseIndex(LIB_DIR + "dict/index.sense");
		evaluator.setSenseIndex(senseIndex);

		IResultWriter writer = (IResultWriter) Class.forName(writerName).newInstance();
		writer.setOptions(new String[] { "-s", saveDir });

		tester.setEvaluator(evaluator);
		tester.setWriter(writer);
		
		tester.setFeatureExtractorName("sg.edu.nus.comp.nlp.ims.feature.CAllWordsFeatureExtractorCombination");
		tester.setCorpusClassName("sg.edu.nus.comp.nlp.ims.corpus.CAllWordsPlainCorpus");

		System.err.println("testing " + testPath.getAbsolutePath());
		tester.test(testPath.getAbsolutePath());
		System.err.println("writing results");
		tester.write();
		tester.clear();
	}
}
