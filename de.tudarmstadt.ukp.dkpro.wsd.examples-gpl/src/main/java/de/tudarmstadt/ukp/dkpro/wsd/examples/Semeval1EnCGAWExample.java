/**
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
 */
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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.MostFrequentSenseBaseline;
import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorContextPOS;
import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorIndividualPOS;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseMapper;
import de.tudarmstadt.ukp.dkpro.wsd.evaluation.EvaluationTableHTML;
import de.tudarmstadt.ukp.dkpro.wsd.evaluation.SingleExactMatchEvaluatorHTML;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.SimplifiedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.resource.WSDResourceSimplifiedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NoNormalization;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.PairedOverlap;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceIndividualPOS;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.Semeval1AWReader;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.SensevalAnswerKeyReader;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.resource.WordNetSenseKeySenseInventoryResource;

/**
 * A sample pipeline which disambiguates a subset of the Semeval-1 English
 * coarse-grained all-words data set, and opens the results in a web browser.
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class Semeval1EnCGAWExample
{
    public static void main(String[] args)
        throws UIMAException, IOException
    {
        // Set this to a negative value to disambiguate all items in the corpus.
        final int maxItemsToAttempt = 50;

        // For our corpus and answer key we will use the SemEval-2007 English
        // coarse-grained all-words test data. You need to obtain this data set
        // from the SemEval-2007 website.  Because the sense keys referenced in
        // the answer key do not match those from WordNet 2.1, it is also
        // a good idea to patch answer key using the patch files we provide in
        // the {@link de.tudarmstadt.ukp.dkpro.wsd.senseval} module.  Then
        // change the value of the directory variable below to point to the
        // location on your filesystem where you stored the patched data set.
        final String directory = "/home/miller/share/corpora/semeval-1/task07/";
        final String corpus = directory + "test/eng-coarse-all-words.xml";
        final String answerkey = directory + "key/dataset21.test.key";

        // A reader for the SemEval-2007 English coarse-grained all-words corpus
        CollectionReader reader = createReader(
                Semeval1AWReader.class, Semeval1AWReader.PARAM_FILE, corpus);

        // A resource for WordNet 2.1. You need to create an extJWNL properties
        // file and change the value of the PARAM_WORDNET_PROPERTIES_URL to
        // point to its location on your file system.
        final String wordnetInventoryName = "WordNet_2.1_sensekey";
        ExternalResourceDescription wordnet21 = createExternalResourceDescription(
                WordNetSenseKeySenseInventoryResource.class,
                WordNetSenseKeySenseInventoryResource.PARAM_WORDNET_PROPERTIES_URL,
                "/home/miller/share/WordNet/WordNet-2.1/extjwnl_properties.xml",
                WordNetSenseKeySenseInventoryResource.PARAM_SENSE_INVENTORY_NAME,
                wordnetInventoryName);

        // A reader for the gold standard answer key
        final String semEvalInventoryName = "SemEval1_sensekey";
        AnalysisEngineDescription answerReader = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, answerkey,
                SensevalAnswerKeyReader.PARAM_SENSE_INVENTORY,
                semEvalInventoryName);

        // The SemEval sense identifiers are based on (but subtly different
        // from) sense keys from WordNet 2.1.  We therefore use this AE to
        // convert them to WordNet 2.1 sense keys. We have a delimited text
        // file providing a mapping between the two sense identifiers, which
        // the SenseMapper annotator reads in and uses to perform the
        // conversion.
        AnalysisEngineDescription convertSensevalToSensekey = createEngineDescription(
                SenseMapper.class, SenseMapper.PARAM_FILE,
                "classpath:/wordnet_senseval.tsv",
                SenseMapper.PARAM_SOURCE_SENSE_INVENTORY_NAME,
                semEvalInventoryName,
                SenseMapper.PARAM_TARGET_SENSE_INVENTORY_NAME,
                wordnetInventoryName, SenseMapper.PARAM_KEY_COLUMN, 2,
                SenseMapper.PARAM_VALUE_COLUMN, 1,
                SenseMapper.PARAM_IGNORE_UNKNOWN_SENSES, true);

        // Create a resource for the simplified Lesk algorithm.  We bind our
        // WordNet sense inventory to it, and we specify some parameters such
        // as what strategy the algorithm uses for tokenizing the text and
        // how it computes the word overlaps.
        ExternalResourceDescription simplifiedLeskResource = createExternalResourceDescription(
                WSDResourceSimplifiedLesk.class,
                WSDResourceSimplifiedLesk.SENSE_INVENTORY_RESOURCE, wordnet21,
                WSDResourceSimplifiedLesk.PARAM_NORMALIZATION_STRATEGY,
                NoNormalization.class.getName(),
                WSDResourceSimplifiedLesk.PARAM_OVERLAP_STRATEGY,
                PairedOverlap.class.getName(),
                WSDResourceSimplifiedLesk.PARAM_TOKENIZATION_STRATEGY,
                EnglishStopLemmatizer.class.getName()
                );

        // Create an annotator for the WSD algorithm.  We bind the simplified
        // Lesk resource to it, and specify some further parameters, such as
        // how much text to pass it (in this case, a sentence) and how to
        // post-process the disambiguation confidence scores
        AnalysisEngineDescription simplifiedLesk = createEngineDescription(
                WSDAnnotatorContextPOS.class,
                WSDAnnotatorContextPOS.WSD_METHOD_CONTEXT,
                simplifiedLeskResource,
                WSDAnnotatorContextPOS.PARAM_CONTEXT_ANNOTATION,
                Sentence.class.getName(),
                WSDAnnotatorContextPOS.PARAM_NORMALIZE_CONFIDENCE, true,
                WSDAnnotatorContextPOS.PARAM_BEST_ONLY, false,
                WSDAnnotatorContextPOS.PARAM_MAXIMUM_ITEMS_TO_ATTEMPT,
                maxItemsToAttempt);

        // Create a resource for the most frequent sense baseline
        ExternalResourceDescription mfsBaselineResource = createExternalResourceDescription(
                WSDResourceIndividualPOS.class,
                WSDResourceIndividualPOS.SENSE_INVENTORY_RESOURCE, wordnet21,
                WSDResourceIndividualPOS.DISAMBIGUATION_METHOD,
                MostFrequentSenseBaseline.class.getName());

        // Create an annotator for the MFS baseline
        AnalysisEngineDescription mfsBaseline = createEngineDescription(
                WSDAnnotatorIndividualPOS.class,
                WSDAnnotatorIndividualPOS.WSD_ALGORITHM_RESOURCE,
                mfsBaselineResource,
                WSDAnnotatorIndividualPOS.PARAM_MAXIMUM_ITEMS_TO_ATTEMPT,
                maxItemsToAttempt);

        // Output the raw sense assignments to HTML.  You should change the
        // value of the PARAM_OUTPUT_FILE configuration parameter to point to
        // some writable location on your filesystem.
        AnalysisEngineDescription writer = createEngineDescription(
                EvaluationTableHTML.class,
                EvaluationTableHTML.PARAM_GOLD_STANDARD_ALGORITHM, answerkey,
                EvaluationTableHTML.PARAM_OUTPUT_FILE,
                "/tmp/WSDWriterHTML.html",
                EvaluationTableHTML.PARAM_OPEN_IN_BROWSER, true,
                EvaluationTableHTML.PARAM_MAXIMUM_ITEMS_TO_ATTEMPT,
                maxItemsToAttempt);

        // Compute some useful statistics (precision, recall, etc.) and
        // output them to HTML. You should change the
        // value of the PARAM_OUTPUT_FILE configuration parameter to point to
        // some writable location on your filesystem.
        AnalysisEngineDescription evaluator = createEngineDescription(
                SingleExactMatchEvaluatorHTML.class,
                SingleExactMatchEvaluatorHTML.PARAM_GOLD_STANDARD_ALGORITHM,
                answerkey, SingleExactMatchEvaluatorHTML.PARAM_TEST_ALGORITHM,
                SimplifiedLesk.class.getName(),
                SingleExactMatchEvaluatorHTML.PARAM_BACKOFF_ALGORITHM,
                MostFrequentSenseBaseline.class.getName(),
                SingleExactMatchEvaluatorHTML.PARAM_OPEN_IN_BROWSER, true,
                SingleExactMatchEvaluatorHTML.PARAM_OUTPUT_FILE,
                "/tmp/WSDWriterHTML_evaluator.html",
                SingleExactMatchEvaluatorHTML.PARAM_MAXIMUM_ITEMS_TO_ATTEMPT,
                maxItemsToAttempt);

        // String all the components into a pipeline
        SimplePipeline.runPipeline(reader,
                answerReader,
                convertSensevalToSensekey,
                mfsBaseline,
                simplifiedLesk,
                writer,
                evaluator);
    }

}
