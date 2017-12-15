/**
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
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
 */
package de.tudarmstadt.ukp.dkpro.wsd.supervised.ims;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import net.didion.jwnl.JWNLException;
import sg.edu.nus.comp.nlp.ims.classifiers.CLibLinearEvaluator;
import sg.edu.nus.comp.nlp.ims.classifiers.IEvaluator;
import sg.edu.nus.comp.nlp.ims.corpus.ACorpus;
import sg.edu.nus.comp.nlp.ims.corpus.CAllWordsPlainCorpus;
import sg.edu.nus.comp.nlp.ims.feature.CAllWordsFeatureExtractorCombination;
import sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor;
import sg.edu.nus.comp.nlp.ims.instance.CInstanceExtractor;
import sg.edu.nus.comp.nlp.ims.instance.IInstance;
import sg.edu.nus.comp.nlp.ims.instance.IInstanceExtractor;
import sg.edu.nus.comp.nlp.ims.io.CPlainCorpusInlineWriter;
import sg.edu.nus.comp.nlp.ims.io.IResultWriter;
import sg.edu.nus.comp.nlp.ims.lexelt.CLexelt;
import sg.edu.nus.comp.nlp.ims.lexelt.ILexelt;
import sg.edu.nus.comp.nlp.ims.util.CJWNL;
import sg.edu.nus.comp.nlp.ims.util.COpenNLPPOSTagger;
import sg.edu.nus.comp.nlp.ims.util.COpenNLPSentenceSplitter;
import sg.edu.nus.comp.nlp.ims.util.CWordNetSenseIndex;
import sg.edu.nus.comp.nlp.ims.util.ISenseIndex;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmDocumentTextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

public class ImsWsdDisambiguator
implements WSDAlgorithmDocumentTextBasic
{
    private static String BASE_DIR;
    private static String LIB_DIR;

    // default instance extractor class name
    protected static final String INSTANCEEXTRACTOR = CInstanceExtractor.class.getName();
    // default feature extractor class name
    protected static final String FEATUREEXTRACTOR = CAllWordsFeatureExtractorCombination.class.getName();
    // default corpus class name
    protected static final String CORPUS = CAllWordsPlainCorpus.class.getName();
    // evaluator
    protected IEvaluator m_Evaluator = new CLibLinearEvaluator();
    // writer
    protected IResultWriter m_Writer = new CPlainCorpusInlineWriter();
    // results
    protected ArrayList<Object> m_Results = new ArrayList<Object>();
    // instance extractor class name
    protected String m_InstanceExtractorName = INSTANCEEXTRACTOR;
    // feature extractor class name
    protected String m_FeatureExtractorName = FEATUREEXTRACTOR;
    // corpus class name
    protected String m_CorpusName = CORPUS;

    SenseInventory inventory;


    public ImsWsdDisambiguator(SenseInventory inventory) throws JWNLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        this.inventory = inventory;
        init();
    }

    private void init() throws JWNLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        BASE_DIR = "src/main/resources/ims/";
        LIB_DIR = BASE_DIR + "lib/";

        String modelDir = BASE_DIR + "models/";
        String statDir = "target/ims_stat/";
        String saveDir = "target/ims_results/";
        String evaluatorName = CLibLinearEvaluator.class.getName();
        String writerName = CPlainCorpusInlineWriter.class.getName();

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

        setEvaluator(evaluator);
        setWriter(writer);

        setFeatureExtractorName("sg.edu.nus.comp.nlp.ims.feature.CAllWordsFeatureExtractorCombination");
        setCorpusClassName("sg.edu.nus.comp.nlp.ims.corpus.CAllWordsPlainCorpus");

    }

    public void test(String text)
            throws SenseInventoryException
            {
        IInstanceExtractor instExtractor;
        IFeatureExtractor featExtractor;
        ACorpus corpus;
        try {
            instExtractor = (IInstanceExtractor) Class.forName(this.m_InstanceExtractorName).newInstance();
            featExtractor = (IFeatureExtractor) Class.forName(this.m_FeatureExtractorName).newInstance();
            corpus = (ACorpus) Class.forName(this.m_CorpusName).newInstance();
        }
        catch (InstantiationException e) {
            throw new SenseInventoryException(e);
        }
        catch (IllegalAccessException e) {
            throw new SenseInventoryException(e);
        }
        catch (ClassNotFoundException e) {
            throw new SenseInventoryException(e);
        }

        corpus.setSplit(false);
        corpus.setTokenized(false);
        corpus.setPOSTagged(false);
        corpus.setLemmatized(false);
        try {
            corpus.load(new StringReader(text));
        }
        catch (Exception e) {
            throw new SenseInventoryException(e);
        }

        if (this.m_Writer != null && CPlainCorpusInlineWriter.class.isInstance(this.m_Writer)) {
            ((CPlainCorpusInlineWriter)this.m_Writer).setCorpus(corpus);
        }
        instExtractor.setCorpus(corpus);
        instExtractor.setFeatureExtractor(featExtractor);

        Hashtable<String, ILexelt> lexelts = new Hashtable<String, ILexelt>();
        while (instExtractor.hasNext()) {
            IInstance instance = instExtractor.next();
            String lexeltID = instance.getLexeltID();
            if (!lexelts.containsKey(lexeltID)) {
                lexelts.put(lexeltID, new CLexelt(lexeltID));
            }
            lexelts.get(lexeltID).addInstance(instance);
        }
        ArrayList<String> lexeltIDs = new ArrayList<String>();
        lexeltIDs.addAll(lexelts.keySet());
        Collections.sort(lexeltIDs);

        for (String lexeltID : lexeltIDs) {
            System.err.println(lexeltID);
            Object lexelt = lexelts.remove(lexeltID);
            try {
                this.m_Results.add(this.m_Evaluator.evaluate(lexelt));
            }
            catch (Exception e) {
                throw new SenseInventoryException(e);
            }
        }
            }

    public void setEvaluator(IEvaluator p_Evaluator) {
        this.m_Evaluator = p_Evaluator;
    }

    public void setWriter(IResultWriter p_Writer) {
        this.m_Writer = p_Writer;
    }

    public void setCorpusClassName(String p_Name) {
        this.m_CorpusName = p_Name;
    }

    public void setInstanceExtractorName(String p_Name) {
        this.m_InstanceExtractorName = p_Name;
    }

    public void setFeatureExtractorName(String p_Name) {
        this.m_FeatureExtractorName = p_Name;
    }

    public void write()
            throws SenseInventoryException
            {
        try {
            this.m_Writer.write(this.m_Results);
        }
        catch (IOException e) {
            throw new SenseInventoryException(e);
        }
            }

    public void clear() {
        this.m_Results.clear();
    }

    @Override
    public String getDisambiguation(String documentText)
        throws SenseInventoryException
    {
        test(documentText);
        String disambiguation = this.m_Writer.toString(this.m_Results);
        clear();

        return disambiguation;
    }

    @Override
    public String getDisambiguationMethod()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public SenseInventory getSenseInventory()
    {
        return inventory;
    }

    @Override
    public void setSenseInventory(SenseInventory senseInventory)
    {
        this.inventory = senseInventory;
    }
}