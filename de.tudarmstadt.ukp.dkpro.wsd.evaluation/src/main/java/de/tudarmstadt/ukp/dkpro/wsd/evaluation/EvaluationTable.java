/*******************************************************************************
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tudarmstadt.ukp.dkpro.wsd.evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

// TODO: Rewrite with Chunk

/**
 * Prints a plain text table showing WSD algorithm accuracy
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public abstract class EvaluationTable
    extends AbstractWSDEvaluator
{
    public static final String PARAM_OUTPUT_FILE = "outputFilename";
    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = false, description = "The output file.  If unset, output goes to standard output.")
    protected String outputFilename;
    protected BufferedWriter output;

    private final Logger logger = Logger.getLogger(getClass());

    protected enum SenseType
    {
        GOLD, TESTNA, GOLDNA, CORRECT, INCORRECT
    }

    protected final String NA = "N/A";

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        if (outputFilename != null) {
            try {
                output = new BufferedWriter(new FileWriter(outputFilename));
            }
            catch (IOException e) {
                throw new ResourceInitializationException(e);
            }
        }
        else {
            output = new BufferedWriter(new OutputStreamWriter(System.out));
        }
        try {
            beginFile(this.getClass().getSimpleName());
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    class WSDItemComparator
        implements Comparator<WSDItem>
    {

        @Override
        public int compare(WSDItem arg0, WSDItem arg1)
        {
            if (arg0 == null || arg1 == null || arg0.getId() == null
                    || arg1.getId() == null) {
                return 0;
            }
            return arg0.getId().compareTo(arg1.getId());
        }

    }

    class StringComparator
        implements Comparator<String>
    {

        @Override
        public int compare(String arg0, String arg1)
        {
            if (arg0 == null || arg1 == null) {
                return 0;
            }
            return arg0.compareTo(arg1);
        }
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        super.process(aJCas);
        Table<WSDItem, String, FSArray> testResults = TreeBasedTable.create(
                new WSDItemComparator(), new StringComparator());

        for (WSDResult r : JCasUtil.select(aJCas, WSDResult.class)) {
            if (r.getWsdItem() == null) {
                logger.info("skipping " + r.getDisambiguationMethod()
                        + " result for \"" + r.getCoveredText()
                        + "\" because it has no instance");
                continue;
            }
            if (r.getSenses() == null) {
                logger.info("skipping " + r.getDisambiguationMethod()
                        + " result for " + r.getWsdItem().getId()
                        + " because no senses are assigned");
                continue;
            }
            if (ignoreResult(r)) {
                logger.info(goldStandardAlgorithm + " result for "
                        + r.getWsdItem().getId()
                        + " matches the ignore pattern");
            }
            else {
                testResults.put(r.getWsdItem(),
                        r.getDisambiguationMethod(),
                        r.getSenses());
            }
        }

        try {
            beginDocument("Document");
            beginTable(testResults.rowKeySet().size(), testResults
                    .columnKeySet().size());
            beginTableRow();
            tableHeader("instance");
            tableHeader(goldStandardAlgorithm);
            for (String testAlgorithm : testResults.columnKeySet()) {
                if (!testAlgorithm.equals(goldStandardAlgorithm)) {
                    tableHeader(testAlgorithm);
                }
            }
            endTableRow();
            for (WSDItem wsdItem : testResults.rowKeySet()) {
                if (maxItemsAttempted >= 0 && numItemsAttempted++ >= maxItemsAttempted) {
                    break;
                }
                FSArray goldResults = testResults.get(wsdItem,
                        goldStandardAlgorithm);
                beginTableRow();
                tableHeaderInstance(wsdItem);
                tableCellGoldResults(goldResults);
                for (String testAlgorithm : testResults.columnKeySet()) {
                    if (!testAlgorithm.equals(goldStandardAlgorithm)) {
                        tableCellTestResults(goldResults,
                                testResults.get(wsdItem, testAlgorithm));
                    }
                }
                endTableRow();
            }
            endTable();
            endDocument();
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    protected void tableCellGoldResults(FSArray senses)
        throws IOException
    {
        if (senses == null || senses.size() == 0) {
            tableCellSenseArray(senses, SenseType.GOLDNA, null);
        }
        else {
            tableCellSenseArray(senses, SenseType.GOLD, null);
        }
    }

    protected void tableCellTestResults(FSArray goldSenseArray,
            FSArray testSenseArray)
        throws IOException
    {
        Set<Sense> bestTestSenses = null;
        SenseType senseType;

        if (goldSenseArray == null) {
            senseType = SenseType.GOLDNA;
        }
        else if (testSenseArray == null || testSenseArray.size() == 0) {
            senseType = SenseType.TESTNA;
        }
        else {
            senseType = SenseType.CORRECT;
            Set<String> goldSenseIds = new TreeSet<String>();
            for (Sense s : JCasUtil.select(goldSenseArray, Sense.class)) {
                goldSenseIds.add(s.getId());
            }
            bestTestSenses = getBestSenses(testSenseArray);
            for (Sense s : bestTestSenses) {
                if (!goldSenseIds.contains(s.getId())) {
                    senseType = SenseType.INCORRECT;
                    break;
                }
            }
        }
        tableCellSenseArray(testSenseArray, senseType, bestTestSenses);
    }

    protected abstract void beginFile(String fileTitle)
        throws IOException;

    protected abstract void endFile()
        throws IOException;

    protected abstract void beginTableRow()
        throws IOException;

    protected abstract void endTableRow()
        throws IOException;

    protected abstract void endTable()
        throws IOException;

    protected abstract void beginTable(int numRows, int numColumns)
        throws IOException;

    protected abstract void beginDocument(String documentTitle)
        throws IOException;

    protected abstract void endDocument()
        throws IOException;

    protected abstract void tableHeader(String cellContents)
        throws IOException;

    protected abstract void tableCellSenseArray(FSArray senses,
            SenseType senseType, Collection<Sense> highlightSense)
        throws IOException;

    protected abstract void tableCellSense(Sense sense, SenseType senseType)
        throws IOException;

    protected abstract void tableCell(String cellContents, SenseType senseType)
        throws IOException;

    protected abstract void tableHeaderInstance(WSDItem wsdItem)
        throws IOException;

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        try {
            endFile();
            output.close();
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

    }

}
