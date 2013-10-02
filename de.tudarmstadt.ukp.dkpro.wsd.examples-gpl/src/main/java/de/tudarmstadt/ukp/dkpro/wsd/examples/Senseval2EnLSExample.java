/*******************************************************************************
 * Copyright 2013
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
 *
 ******************************************************************************/

package de.tudarmstadt.ukp.dkpro.wsd.examples;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.wsd.algorithms.MostFrequentSenseBaseline;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.SimplifiedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.normalization.SecondObjects;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.overlap.PairedOverlap;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseMapper;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.WordNetSenseKeyToSynset;
import de.tudarmstadt.ukp.dkpro.wsd.evaluation.SingleExactMatchEvaluatorText;
import de.tudarmstadt.ukp.dkpro.wsd.io.reader.Senseval2LSReader;
import de.tudarmstadt.ukp.dkpro.wsd.io.reader.SensevalAnswerKeyReader;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceIndividualPOS;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceSimplifiedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.LsrToWordNetSynsetOffset;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.LsrSenseInventoryResource;
import de.tudarmstadt.ukp.dkpro.wsd.wsdannotators.WSDAnnotatorContextPOS;
import de.tudarmstadt.ukp.dkpro.wsd.wsdannotators.WSDAnnotatorIndividualPOS;

/**
 * This class illustrates a pipeline which runs various WSD baselines on the
 * Senseval-2 English Lexical Sample training data. It uses the
 * {@link LsrSenseInventoryResource} resource to access WordNet via the DKPro
 * LSR library.
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class Senseval2EnLSExample
{

    public static void main(String[] args)
        throws UIMAException, IOException
    {
        // Set this to a negative value to disambiguate all items in the corpus.
        final int maxItemsToAttempt = 50;

        // For our corpus and answer key we will use the Senseval-2 English
        // lexical sample training data. You need to obtain this data set from
        // the Senseval-2 website. Change the value of the directory variable
        // to point to the location on your filesystem where you stored the
        // data set.
        final String directory = "/home/miller/workspace/de.tudarmstadt.ukp.experiments.tm.wsdcorpora/src/main/resources/senseval-2/english-lex-sample/train/";
        final String corpus = directory + "eng-lex-sample.train.xml";
        final String answerkey = directory + "eng-lex-sample.train.key";

        // This is a collection reader for the documents to be disambiguated.
        CollectionReader reader = createReader(
                Senseval2LSReader.class, Senseval2LSReader.PARAM_FILE, corpus);

        // This AE reads the Senseval-2 answer key. Because the Senseval
        // answer key format doesn't itself indicate what sense inventory is
        // used for the keys, we need to pass this as a configuration parameter.
        // Senseval uses an identifier scheme which is similar to (but not the
        // same as) WordNet sense keys, so let's call it "Senseval2_sensekey".
        final String sensevalInventoryName = "Senseval2_sensekey";
        AnalysisEngineDescription answerReader = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, answerkey,
                SensevalAnswerKeyReader.PARAM_SENSE_INVENTORY,
                "Senseval2_sensekey");

        // The Senseval-2 sense identifiers are based on (but subtly different
        // from) sense keys from the WordNet 1.7-prerelease.  We therefore
        // use this AE  to convert them to WordNet 1.7-prerelease sense keys. We
        // have a delimited text file providing a mapping between the two
        // sense identifiers, which the SenseMapper annotator reads in and
        // uses to perform the conversion.
        final String wordnet17SenseKeyInventoryName = "WordNet_1.7pre_sensekey";
        AnalysisEngineDescription convertSensevalToSensekey = createEngineDescription(
                SenseMapper.class, SenseMapper.PARAM_FILE,
                "classpath:/wordnet_senseval.tsv",
                SenseMapper.PARAM_SOURCE_SENSE_INVENTORY_NAME,
                sensevalInventoryName,
                SenseMapper.PARAM_TARGET_SENSE_INVENTORY_NAME,
                wordnet17SenseKeyInventoryName, SenseMapper.PARAM_KEY_COLUMN,
                2, SenseMapper.PARAM_VALUE_COLUMN, 1,
                SenseMapper.PARAM_IGNORE_UNKNOWN_SENSES, true);

        // The WSD baseline algorithms we will be using need to select senses
        // from a sense inventory. We will use JLSR as an interface to
        // the WordNet 1.7 prerelease. For this to work you will need
        // to have the WordNet 1.7 prerelease installed on your local system,
        // and to have an appropriately configured WordNet properties file and
        // DKPro LSR resources.xml file.
        ExternalResourceDescription wordnet1_7 = createExternalResourceDescription(
                LsrSenseInventoryResource.class,
                LsrSenseInventoryResource.PARAM_RESOURCE_NAME, "wordnet17",
                LsrSenseInventoryResource.PARAM_RESOURCE_LANGUAGE, "en");

        // WordNet 1.7-prerelease sense keys are not unique identifiers for
        // WordNet synsets (that is, multiple sense keys map to the same synset)
        // so we use another annotator to convert them to strings comprised of
        // the WordNet synset offset plus part of speech. These strings uniquely
        // identify WordNet senses. You need to change the value of the
        // PARAM_INDEX_SENSE_FILE to point to the location of the WordNet
        // index.sense file on your file system.
        final String wordnet17SynsetInventoryName = "WordNet_1.7pre_synset";
        AnalysisEngineDescription convertSensekeyToSynset = createEngineDescription(
                WordNetSenseKeyToSynset.class,
                WordNetSenseKeyToSynset.PARAM_INDEX_SENSE_FILE,
                "/home/miller/share/WordNet/WordNet-1.7/dict/index.sense",
                SenseMapper.PARAM_SOURCE_SENSE_INVENTORY_NAME,
                wordnet17SenseKeyInventoryName,
                SenseMapper.PARAM_TARGET_SENSE_INVENTORY_NAME,
                wordnet17SynsetInventoryName,
                SenseMapper.PARAM_IGNORE_UNKNOWN_SENSES, true);

        // The sense identifiers returned by JLSR are also proprietary, so we
        // use this AE to convert them to strings comprised of the
        // WordNet 1.7-prerelease synset offset plus part of speech.
        AnalysisEngineDescription convertLSRtoSynset = createEngineDescription(
                LsrToWordNetSynsetOffset.class,
                LsrToWordNetSynsetOffset.PARAM_SOURCE_SENSE_INVENTORY_NAME,
                "WordNet_3.0_LSR",
                LsrToWordNetSynsetOffset.PARAM_TARGET_SENSE_INVENTORY_NAME,
                wordnet17SynsetInventoryName);

        // Here's a resource encapsulating the most frequent sense baseline
        // algorithm, which we bind to the JLSR sense inventory.
        ExternalResourceDescription mfsBaselineResource = createExternalResourceDescription(
                WSDResourceIndividualPOS.class,
                WSDResourceIndividualPOS.SENSE_INVENTORY_RESOURCE, wordnet1_7,
                WSDResourceIndividualPOS.DISAMBIGUATION_METHOD,
                MostFrequentSenseBaseline.class.getName());

        // And here we create an analysis engine, and bind to it the
        // most frequent sense baseline resource.
        AnalysisEngineDescription mfsBaseline = createEngineDescription(
                WSDAnnotatorIndividualPOS.class,
                WSDAnnotatorIndividualPOS.WSD_ALGORITHM_RESOURCE,
                mfsBaselineResource,
                WSDAnnotatorIndividualPOS.PARAM_MAXIMUM_ITEMS_TO_ATTEMPT,
                maxItemsToAttempt);

        // Here's a resource encapsulating the simplified Lesk algorithm (that
        // is, a Lesk-like algorithm where the word's definitions are compared
        // with the context in which the word appears.) The algorithm takes
        // three parameters: a tokenization strategy (that is, how to split a
        // string representing a context or a definition into tokens), an
        // overlap strategy (that is, how to compute the overlap between two
        // collections of tokens), and a a normalization strategy (that is, how
        // to normalize the value returned by the overlap measure).
        ExternalResourceDescription simplifiedLeskResource = createExternalResourceDescription(
                WSDResourceSimplifiedLesk.class,
                WSDResourceSimplifiedLesk.SENSE_INVENTORY_RESOURCE, wordnet1_7,
                WSDResourceSimplifiedLesk.PARAM_NORMALIZATION_STRATEGY,
                SecondObjects.class.getName(),
                WSDResourceSimplifiedLesk.PARAM_OVERLAP_STRATEGY,
                PairedOverlap.class.getName(),
                WSDResourceSimplifiedLesk.PARAM_TOKENIZATION_STRATEGY,
                EnglishStopLemmatizer.class.getName());

        // Next we create the analysis engine for the Lesk algorithm
        AnalysisEngineDescription simplifiedLesk = createEngineDescription(
                WSDAnnotatorContextPOS.class,
                WSDAnnotatorContextPOS.WSD_METHOD_CONTEXT,
                simplifiedLeskResource,
                WSDAnnotatorContextPOS.PARAM_MAXIMUM_ITEMS_TO_ATTEMPT,
                maxItemsToAttempt);

        // This AE compares the sense assignments of the SimplifiedLesk
        // algorithm against the given gold standard (in this case, the answer
        // key we read in) and computes and prints out useful statistics, such
        // as precision, recall, and coverage.
        AnalysisEngineDescription evaluator = createEngineDescription(
                SingleExactMatchEvaluatorText.class,
                SingleExactMatchEvaluatorText.PARAM_GOLD_STANDARD_ALGORITHM,
                answerkey, SingleExactMatchEvaluatorText.PARAM_TEST_ALGORITHM,
                SimplifiedLesk.class.getName(),
                SingleExactMatchEvaluatorText.PARAM_BACKOFF_ALGORITHM,
                MostFrequentSenseBaseline.class.getName(),
                SingleExactMatchEvaluatorText.PARAM_MAXIMUM_ITEMS_TO_ATTEMPT,
                maxItemsToAttempt);

        // Here we run the pipeline
        SimplePipeline.runPipeline(reader, answerReader,
                convertSensevalToSensekey, convertSensekeyToSynset,
                mfsBaseline, simplifiedLesk, convertLSRtoSynset, evaluator);
    }

}
