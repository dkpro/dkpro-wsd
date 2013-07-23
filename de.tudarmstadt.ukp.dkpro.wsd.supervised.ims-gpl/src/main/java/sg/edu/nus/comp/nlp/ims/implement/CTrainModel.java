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
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.edu.nus.comp.nlp.ims.classifiers.CLibLinearTrainer;
import sg.edu.nus.comp.nlp.ims.classifiers.IModelTrainer;
import sg.edu.nus.comp.nlp.ims.corpus.ACorpus;
import sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus;
import sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination;
import sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor;
import sg.edu.nus.comp.nlp.ims.instance.CInstanceExtractor;
import sg.edu.nus.comp.nlp.ims.instance.IInstance;
import sg.edu.nus.comp.nlp.ims.instance.IInstanceExtractor;
import sg.edu.nus.comp.nlp.ims.io.CModelWriter;
import sg.edu.nus.comp.nlp.ims.io.IModelWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.*;
import sg.edu.nus.comp.nlp.ims.util.*;

/**
 * main interface of training.
 *
 * @author zhongzhi
 *
 */
public class CTrainModel {

	// models
	protected ArrayList<Object> m_Models = new ArrayList<Object>();
	// model trainer
	protected IModelTrainer m_Trainer = new CLibLinearTrainer();
	// model writer
	protected IModelWriter m_Writer = new CModelWriter();
	// corpus class name
	protected String m_CorpusName = CLexicalCorpus.class.getName();
	// instance extractor class name
	protected String m_InstanceExtractorName = CInstanceExtractor.class.getName();
	// feature extractor class name
	protected String m_FeatureExtractorName = CFeatureExtractorCombination.class
			.getName();
	// cut off parameters
	protected Hashtable<String, Integer> m_CutOffs = new Hashtable<String, Integer>();
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
	 * default constructor
	 */
	public CTrainModel() {
	}

	/**
	 * train model with given xml and key
	 *
	 * @param p_XmlFile
	 *            train xml file
	 * @param p_KeyFile
	 *            train key file
	 * @throws Exception
	 *             train exception
	 */
	public void train(String p_XmlFile, String p_KeyFile) throws Exception {
		Reader xmlReader = new InputStreamReader(new FileInputStream(p_XmlFile));
		BufferedReader keyReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(p_KeyFile)));
		this.train(xmlReader, keyReader);
		xmlReader.close();
		keyReader.close();
	}

	/**
	 * train model with given xml and key
	 *
	 * @param p_XmlReader
	 *            train xml file reader
	 * @param p_KeyReader
	 *            train key file reader
	 * @throws Exception
	 *             train exception
	 */
	public void train(Reader p_XmlReader, BufferedReader p_KeyReader)
			throws Exception {
		StringTokenizer tokenizer = null;
		Hashtable<String, String[]> tags = new Hashtable<String, String[]>();
		String id;
		String line;
		while ((line = p_KeyReader.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			tokenizer.nextToken();
			id = tokenizer.nextToken();
			String[] ss = new String[tokenizer.countTokens()];
			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				ss[i++] = tokenizer.nextToken();
			}
			tags.put(id, ss);
		}

		ACorpus corpus = (ACorpus) Class.forName(this.m_CorpusName)
				.newInstance();
		IFeatureExtractor featExtractor = (IFeatureExtractor) Class.forName(
				this.m_FeatureExtractorName).newInstance();
		IInstanceExtractor instExtractor = (IInstanceExtractor) Class.forName(
				this.m_InstanceExtractorName).newInstance();
		if (this.m_Delimiter != null) {
			corpus.setDelimiter(this.m_Delimiter);
		}
		corpus.setSplit(this.m_Split);
		corpus.setTokenized(this.m_Tokenized);
		corpus.setPOSTagged(this.m_POSTagged);
		corpus.setLemmatized(this.m_Lemmatized);

		corpus.load(p_XmlReader);
		instExtractor.setCorpus(corpus);
		instExtractor.setFeatureExtractor(featExtractor);
		Hashtable<String, ILexelt> lexelts = new Hashtable<String, ILexelt>();
		while (instExtractor.hasNext()) {
			IInstance instance = instExtractor.next();
			String lexeltID = instance.getLexeltID();
			id = instance.getID();
			if (!lexelts.containsKey(lexeltID)) {
				lexelts.put(lexeltID, new CLexelt(lexeltID));
			}
			if (tags.containsKey(id)) {
				for (String tag : tags.get(id)) {
					instance.setTag(tag);
				}
			} else {
				throw new Exception("cannot find tag for instance " + id);
			}
			lexelts.get(lexeltID).addInstance(instance, true);
		}
		for (String lexeltID : lexelts.keySet()) {
			System.err.println(lexeltID);
			ILexelt lexelt = lexelts.get(lexeltID);
			ArrayList<IFeatureSelector> selectors = new ArrayList<IFeatureSelector>();
			int s2 = 0, c2 = 0, p2 = 0;
			if (this.m_CutOffs.containsKey("s2")) {
				s2 = this.m_CutOffs.get("s2");
			}
			if (s2 > 1) {
				selectors.add(new CSurroundingWordFeatureSelector(s2));
			}
			if (this.m_CutOffs.containsKey("c2")) {
				c2 = this.m_CutOffs.get("c2");
			}
			if (c2 > 1) {
				selectors.add(new CCollocationFeatureSelector(c2));
			}
			if (this.m_CutOffs.containsKey("p2")) {
				p2 = this.m_CutOffs.get("p2");
			}
			if (p2 > 1) {
				selectors.add(new CPOSFeatureSelector(p2));
			}

			IFeatureSelector mixselector = new CFeatureSelectorCombination(selectors);
			lexelt.getStatistic().select(mixselector);
			Object model = this.m_Trainer.train(lexelt);
			System.err.println("done");
			this.m_Models.add(model);
		}
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
	 * set model trainer
	 *
	 * @param p_ModelTrainer
	 *            model trainer
	 */
	public void setModelTrainer(IModelTrainer p_ModelTrainer) {
		this.m_Trainer = p_ModelTrainer;
	}

	/**
	 * set model writer
	 *
	 * @param p_ModelWriter
	 *            model writer
	 */
	public void setModelWriter(IModelWriter p_ModelWriter) {
		this.m_Writer = p_ModelWriter;
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
	 * set cut off
	 *
	 * @param p_Key
	 *            key name
	 * @param p_Value
	 *            value
	 */
	public void setCutOff(String p_Key, int p_Value) {
		this.m_CutOffs.put(p_Key, p_Value);
	}

	/**
	 * get models
	 *
	 * @return models
	 */
	public ArrayList<Object> getModels() {
		return this.m_Models;
	}

	/**
	 * clear the generated models
	 */
	public void clear() {
		this.m_Models.clear();
	}

	/**
	 * write models to disk
	 *
	 * @throws IOException
	 *             exception while save model
	 */
	public void write() throws IOException {
		for (Object modelInfo : this.m_Models) {
			this.m_Writer.write(modelInfo);
		}
	}

	/**
	 * @param p_Args
	 *            arguments
	 */
	public static void main(String[] p_Args) {
		try {
			String generalOptions = "Usage: train.xml train.key saveDir\n"
				+ "\t-i class name of Instance Extractor(default sg.edu.nus.comp.nlp.ims.instance.CInstanceExtractor)\n"
				+ "\t-f class name of Feature Extractor(default sg.edu.nus.comp.nlp.ims.feature.CMixedFeatureExtractor)\n"
				+ "\t-c class name of Corpus(default sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus)\n"
				+ "\t-t class name of Trainer(default sg.edu.nus.comp.nlp.ims.classifiers.CLibLinearTrainer)\n"
				+ "\t-m class name of Model Writer(default sg.edu.nus.comp.nlp.ims.io.CModelWriter)\n"
				+ "\t-algorithm svm(default) or naivebayes\n"
				+ "\t-s2 cut off for surrounding word(default 0)\n"
				+ "\t-c2 cut off for collocation(default 0)\n"
				+ "\t-p2 cut off for pos(default 0)\n"
				+ "\t-split 1/0 whether the corpus is sentence splitted(default 0)\n"
				+ "\t-ssm path of sentence splitter model\n"
				+ "\t-token 1/0 whether the corpus is tokenized(default 0)\n"
				+ "\t-pos 1/0 whether the pos tag is provided in corpus(default 0)\n"
				+ "\t-ptm path of pos tagger model\n"
				+ "\t-dict path of dictionary for opennlp POS tagger\n"
				+ "\t-tagdict path of tagdict for opennlp POS tagger\n"
				+ "\t-lemma 1/0 whether the lemma is provided in the corpus(default 0)\n"
				+ "\t-prop path of prop.xml for JWNL\n"
				+ "\t-type type of train.xml\n"
				+ "\t\tdirectory train all xml files under directory trainPath\n"
				+ "\t\tlist train all xml files listed in file trainPath\n"
				+ "\t\tfile(default) train file trainPath\n";
			CArgumentManager argmgr = new CArgumentManager(p_Args);
			CTrainModel trainModel = new CTrainModel();
			String type = "file";
			if (argmgr.has("type")) {
				type = argmgr.get("type");
			}
			if (argmgr.size() != 3) { // check arguments
				throw new IllegalArgumentException(generalOptions);
			}
			if (!argmgr.has("prop")) {
				System.err.println("prop.xml file for JWNL has not been set.");
				throw new IllegalArgumentException(generalOptions);
			}
			CJWNL.initial(new FileInputStream(argmgr.get("prop")));
			File trainXmlDir = new File(argmgr.get(0));
			File trainKeyDir = new File(argmgr.get(1));

			// set model writer
			String writerName = CModelWriter.class.getName();
			if (argmgr.has("w")) {
				writerName = argmgr.get("w");
			}
			IModelWriter writer = (IModelWriter) Class.forName(writerName)
					.newInstance();
			writer.setOptions(new String[] { "-m", argmgr.get(2) });

			// set sentence splitter
			if (argmgr.has("split")) {
				if (Integer.parseInt(argmgr.get("split")) == 1) {
					trainModel.setSplit(true);
				}
			}
			if (argmgr.has("ssm")) {
				COpenNLPSentenceSplitter.setDefaultModel(argmgr.get("ssm"));
			}

			if (argmgr.has("token")) {
				if (Integer.parseInt(argmgr.get("token")) == 1) {
					trainModel.setTokenized(true);
				}
			}

			// set pos tagger
			if (argmgr.has("pos")) {
				if (Integer.parseInt(argmgr.get("pos")) == 1) {
					trainModel.setPOSTagged(true);
					trainModel.setTokenized(true);
				}
			}
			if (argmgr.has("ptm")) {
				COpenNLPPOSTagger.setDefaultModel(argmgr.get("ptm"));
			}
			if (argmgr.has("dict")) {
				COpenNLPPOSTagger.setDefaultDictionary(argmgr.get("dict"));
			}
			if (argmgr.has("tagdict")) {
				COpenNLPPOSTagger
						.setDefaultPOSDictionary(argmgr.get("tagdict"));
			}

			if (argmgr.has("lemma")) {
				if (Integer.parseInt(argmgr.get("lemma")) == 1) {
					trainModel.setLemmatized(true);
					trainModel.setTokenized(true);
				}
			}

			// set trainer
			String trainerName = CLibLinearTrainer.class.getName();
			IModelTrainer trainer = null;
			if (argmgr.has("t")) {
				trainerName = argmgr.get("t");
			}
			trainer = (IModelTrainer) Class.forName(trainerName).newInstance();

			trainModel.setModelWriter(writer);
			trainModel.setModelTrainer(trainer);
			if (argmgr.has("i")) {
				trainModel.setInstanceExtractorName(argmgr.get("i"));
			}
			if (argmgr.has("f")) {
				trainModel.setFeatureExtractorName(argmgr.get("f"));
			}
			if (argmgr.has("c")) {
				trainModel.setCorpusClassName(argmgr.get("c"));
			}
			if (argmgr.has("s2")) {
				int s2 = Integer.parseInt(argmgr.get("s2"));
				trainModel.setCutOff("s2", s2);
			}
			if (argmgr.has("c2")) {
				int c2 = Integer.parseInt(argmgr.get("c2"));
				trainModel.setCutOff("c2", c2);
			}
			if (argmgr.has("p2")) {
				int p2 = Integer.parseInt(argmgr.get("p2"));
				trainModel.setCutOff("p2", p2);
			}
			ArrayList<File> trainXmlList = new ArrayList<File>();
			ArrayList<File> trainKeyList = new ArrayList<File>();
			Pattern xmlPattern = Pattern.compile("([^\\/]*)\\.xml$");
			Matcher matcher = null;
			if (type.equalsIgnoreCase("list")) { // in file
				String line;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(trainXmlDir)));
				while ((line = reader.readLine()) != null) {
					trainXmlList.add(new File(line));
				}
				reader.close();
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(trainKeyDir)));
				while ((line = reader.readLine()) != null) {
					trainKeyList.add(new File(line));
				}
				reader.close();
				if (trainXmlList.size() != trainKeyList.size()) {
					throw new Exception(
							"Error: the numbers of xml files and key files do not match.");
				}
			} else if (type.equalsIgnoreCase("directory")) {
				if (!trainXmlDir.exists() || !trainXmlDir.isDirectory()
						|| !trainKeyDir.exists() || !trainKeyDir.isDirectory()) {
					throw new Exception("Error: cannot find directory "
							+ trainXmlDir.getName() + " or "
							+ trainKeyDir.getName() + "!\n");
				}
				File[] trainXmlFiles = trainXmlDir.listFiles();
				for (File xmlFile : trainXmlFiles) {
					matcher = xmlPattern.matcher(xmlFile.getAbsolutePath());
					if (matcher.find()) {
						File keyFile = new File(trainKeyDir + "/"
								+ matcher.group(1) + ".key");
						if (!keyFile.exists() || !keyFile.isFile()) {
							throw new Exception(
									"Error: cannot find key file for "
											+ xmlFile.getAbsolutePath());
						}
						trainXmlList.add(xmlFile);
						trainKeyList.add(keyFile);
					}
				}
			} else {
				trainXmlList.add(trainXmlDir);
				trainKeyList.add(trainKeyDir);
			}
			for (int i = 0; i < trainXmlList.size(); i++) {
				File xmlFile = trainXmlList.get(i);
				File keyFile = trainKeyList.get(i);
				System.err.println(xmlFile.getAbsolutePath());
				trainModel.train(xmlFile.getAbsolutePath(), keyFile
						.getAbsolutePath());
				trainModel.write();
				trainModel.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
