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

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sg.edu.nus.comp.nlp.ims.classifiers.IEvaluator;
import sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus;
import sg.edu.nus.comp.nlp.ims.corpus.ICorpus;
import sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination;
import sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor;
import sg.edu.nus.comp.nlp.ims.instance.CInstanceExtractor;
import sg.edu.nus.comp.nlp.ims.instance.IInstance;
import sg.edu.nus.comp.nlp.ims.instance.IInstanceExtractor;
import sg.edu.nus.comp.nlp.ims.io.IResultWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.CLexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;

/**
 * Test thread.
 * @author zhongzhi
 *
 */
public class CTestThread extends Thread {

	// thread pool
	protected ExecutorService m_ThreadPool = Executors.newFixedThreadPool(10);
	// Socket connection
	protected Socket m_Connection = null;
	// name of instance extractor
	protected String m_InstanceExtractorName = CInstanceExtractor.class.getName();
	// name of feature extractor
	protected String m_FeatureExtractorName = CFeatureExtractorCombination.class.getName();
	// name of corpus
	protected String m_CorpusName = CLexicalCorpus.class.getName();
	// end symbol of an input stream
	protected String m_EndSymbol = null;
	// evaluator
	protected IEvaluator m_Evaluator;
	// result writer
	protected IResultWriter m_Writer;
	// test lexelts
	protected Hashtable <String, ILexelt> m_Lexelts = new Hashtable<String, ILexelt>();
	// input corpus
	protected ICorpus m_Corpus = null;
	// instance lexelt ids
	protected Hashtable <String, ArrayList<String>> m_InstanceLexeltIDs = null;
	// thread counter
	protected CountDownLatch m_CountDown = null;
	// results
	protected ArrayList<Object> m_Results = new ArrayList<Object>();
	// input stream reader
	protected Reader m_In;
	// output stream
	protected PrintStream m_Out;
	// connection reader
	protected BufferedReader m_ConnReader;

	/**
	 * initial with a socket connection
	 * @throws IOException initial error
	 */
	protected void initSocket() throws IOException {
		String line;
		this.m_ConnReader = new BufferedReader(new InputStreamReader(this.m_Connection.getInputStream()));
		StringBuilder builder = new StringBuilder();
		System.err.print("INPUT:");
		while ((line = this.m_ConnReader.readLine()) != null && !line.equals(this.m_EndSymbol)) {
			builder.append(line);
			System.err.println(line);
			builder.append("\n");
		}
		while ((line = this.m_ConnReader.readLine()) != null && !line.equals(this.m_EndSymbol)) {
			System.err.println(line);
			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.countTokens() < 2) {
				throw new IOException("the format of the lexelt information part is wrong.");
			}
			String id = tokenizer.nextToken();
			ArrayList<String> lexeltIDs = new ArrayList<String>();
			while (tokenizer.hasMoreTokens()) {
				lexeltIDs.add(tokenizer.nextToken());
			}
			if (this.m_InstanceLexeltIDs == null) {
				this.m_InstanceLexeltIDs = new Hashtable<String, ArrayList<String>>();
			}
			this.m_InstanceLexeltIDs.put(id, lexeltIDs);
		}
		this.m_In = new StringReader(builder.toString());
		this.m_Out = new PrintStream(this.m_Connection.getOutputStream());
	}

	/**
	 * constructor for socket
	 * @param p_Connection connection
	 * @param p_ResultWriter result writer
	 * @param p_Evaluator evaluator
	 * @param p_EndSymbol end symbol
	 * @param p_Options options
	 * @throws IOException io exception
	 */
	public CTestThread(Socket p_Connection, IEvaluator p_Evaluator, IResultWriter p_ResultWriter, String p_EndSymbol, String[] p_Options) throws IOException {
		this.m_Evaluator = p_Evaluator;
		this.m_Writer = p_ResultWriter;
		this.m_EndSymbol = p_EndSymbol;
		this.m_Connection = p_Connection;
		this.setOptions(p_Options);
	}

	/**
	 * constructor with some parameters
	 * @param p_In corpus
	 * @param p_IDs instance lexelt ids
	 * @param p_Out output
	 * @param p_Evaluator evaluator
	 * @param p_ResultWriter writer
	 * @param p_Options options
	 */
	public CTestThread(Reader p_In, Hashtable <String, ArrayList<String>> p_IDs, PrintStream p_Out, IEvaluator p_Evaluator, IResultWriter p_ResultWriter, String[] p_Options) {
		this.m_Evaluator = p_Evaluator;
		this.m_Writer = p_ResultWriter;
		this.m_In = p_In;
		this.m_InstanceLexeltIDs = p_IDs;
		this.m_Out = p_Out;
		this.setOptions(p_Options);
	}

	/**
	 * set options
	 * @param p_Options options
	 */
	public void setOptions(String[] p_Options) {
		if (p_Options != null) {
			this.m_InstanceExtractorName = p_Options[0];
			this.m_FeatureExtractorName = p_Options[1];
			this.m_CorpusName = p_Options[2];
		}
	}

	/**
	 * load data
	 * @throws Exception error
	 */
	protected void load() throws Exception {
		if (this.m_Connection != null) {
			this.initSocket();
		}
		IInstanceExtractor instExtractor = (IInstanceExtractor) Class.forName(this.m_InstanceExtractorName).newInstance();
		IFeatureExtractor featExtractor = (IFeatureExtractor) Class.forName(this.m_FeatureExtractorName).newInstance();
		this.m_Corpus = (ICorpus) Class.forName(this.m_CorpusName).newInstance();

		this.m_Corpus.load(this.m_In);
		instExtractor.setCorpus(this.m_Corpus);
		instExtractor.setFeatureExtractor(featExtractor);
		while (instExtractor.hasNext()) {
			IInstance instance = instExtractor.next();
			String lexeltID = instance.getLexeltID();
			if (m_InstanceLexeltIDs != null) {
				if (m_InstanceLexeltIDs.containsKey(instance.getID())) {
					ArrayList<String> ids = m_InstanceLexeltIDs.get(instance.getID());
					for (int i = 0;i < ids.size();i++) {
						lexeltID = ids.get(i);
						if (!this.m_Lexelts.containsKey(lexeltID)) {
							this.m_Lexelts.put(lexeltID, new CLexelt(lexeltID));
						}
						this.m_Lexelts.get(lexeltID).addInstance(instance);
					}
				} else {
					throw new IllegalArgumentException("instance \"" + instance.getID() + "\" is not defined in lexelt file.");
				}
			} else {
				if (!this.m_Lexelts.containsKey(lexeltID)) {
					this.m_Lexelts.put(lexeltID, new CLexelt(lexeltID));
				}
				this.m_Lexelts.get(lexeltID).addInstance(instance);
			}
		}
	}

	/**
	 * test instances
	 * @throws InterruptedException error
	 */
	protected void test() throws InterruptedException {
		ArrayList <String> lexeltIDs = new ArrayList<String>();
		lexeltIDs.addAll(m_Lexelts.keySet());
		Collections.sort(lexeltIDs);
		this.m_CountDown = new CountDownLatch(lexeltIDs.size());
		for (String lexeltID:lexeltIDs) {
			ILexelt lexelt = m_Lexelts.get(lexeltID);
			LexeltThread thread = new LexeltThread(lexelt);
			this.m_ThreadPool.execute(thread);
		}
		this.m_CountDown.await();
	}

	/**
	 * collection results
	 */
	protected void collect() {
		StringBuilder builder = new StringBuilder();
		for (Object result:this.m_Results) {
			builder.append(this.m_Writer.toString(result));
		}
		this.m_Out.print(builder.toString());
		this.m_Out.flush();
	}

	/**
	 * exit main thread
	 * @throws IOException error
	 */
	protected void exit() throws IOException {
		if (this.m_Connection != null) {
			this.m_Connection.close();
			this.m_ConnReader.close();
		}
	}

	/**
	 * a thread to test one lexelt
	 * @author zhongzhi
	 *
	 */
	protected class LexeltThread extends Thread {
		// test lexelt
		protected ILexelt lexelt;
		/**
		 * constructor
		 * @param p_Lexelt input test lexelt
		 */
		public LexeltThread(ILexelt p_Lexelt) {
			lexelt = p_Lexelt;
		}

		/**
		 * run thread
		 */
		public void run() {
			try {
				Object result = m_Evaluator.evaluate(lexelt);
				synchronized (m_Results) {
					m_Results.add(result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			m_CountDown.countDown();
		}
	}

	/**
	 * get a formatted date time
	 * @return a formatted date time
	 */
    protected String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			System.err.println("loading data" + this.getDateTime());
			this.load();
			System.err.println("testing" + this.getDateTime());
			this.test();
			System.err.println("collecting data" + this.getDateTime());
			this.collect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				this.exit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
