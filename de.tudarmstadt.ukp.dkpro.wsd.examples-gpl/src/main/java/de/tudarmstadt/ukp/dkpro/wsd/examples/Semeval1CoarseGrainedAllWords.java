/*******************************************************************************
 * Copyright 2013
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

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.examples;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.MostFrequentSenseBaseline;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.SimplifiedExtendedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.normalization.SecondObjects;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.overlap.PairedOverlap;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseConfidenceNormalizer;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseMapper;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.WordNetSenseKeyToSynset;
import de.tudarmstadt.ukp.dkpro.wsd.evaluation.SingleExactMatchEvaluatorText;
import de.tudarmstadt.ukp.dkpro.wsd.io.reader.Semeval1AWReader;
import de.tudarmstadt.ukp.dkpro.wsd.io.reader.SensevalAnswerKeyReader;
import de.tudarmstadt.ukp.dkpro.wsd.io.writer.WSDWriter;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceIndividualPOS;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceSimplifiedExtendedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceSimplifiedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.WordNetSynsetSenseInventoryResource;
import de.tudarmstadt.ukp.dkpro.wsd.wsdannotators.WSDAnnotatorContextPOS;
import de.tudarmstadt.ukp.dkpro.wsd.wsdannotators.WSDAnnotatorIndividualPOS;

/**
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class Semeval1CoarseGrainedAllWords
{
    public static void main(String[] args)
        throws UIMAException, IOException
    {

        final String directory = "classpath:/semeval-1/english-coarse-grained-all-words/";
        final String corpus = directory + "eng-coarse-all-words.xml";
        final String answerkey = directory + "dataset21.test.key";

        CollectionReader reader = createCollectionReader(
                Semeval1AWReader.class, Semeval1AWReader.PARAM_FILE, corpus);

        AnalysisEngineDescription answerReader = createPrimitiveDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, answerkey,
                SensevalAnswerKeyReader.PARAM_SENSE_INVENTORY,
                "Semeval1_sensekey");

        ExternalResourceDescription wordnet2_1 = createExternalResourceDescription(
                WordNetSynsetSenseInventoryResource.class,
                WordNetSynsetSenseInventoryResource.PARAM_WORDNET_PROPERTIES_URL,
                "/home/miller/share/WordNet/WordNet-2.1/wordnet_properties.xml",
                WordNetSynsetSenseInventoryResource.PARAM_SENSE_INVENTORY_NAME,
                "WordNet_2.1_synset",
                WordNetSynsetSenseInventoryResource.PARAM_SENSE_DESCRIPTION_FORMAT,
                "%w");

        ExternalResourceDescription simplifiedExtendedLeskResource = createExternalResourceDescription(
                WSDResourceSimplifiedExtendedLesk.class,
                WSDResourceSimplifiedExtendedLesk.SENSE_INVENTORY_RESOURCE, wordnet2_1,
                WSDResourceSimplifiedExtendedLesk.PARAM_NORMALIZATION_STRATEGY,
                SecondObjects.class.getName(),
                WSDResourceSimplifiedExtendedLesk.PARAM_OVERLAP_STRATEGY,
                PairedOverlap.class.getName(),
                WSDResourceSimplifiedExtendedLesk.PARAM_TOKENIZATION_STRATEGY,
                EnglishStopLemmatizer.class.getName(),
                WSDResourceSimplifiedExtendedLesk.PARAM_POSTFILTER_SOD, "false");

        AnalysisEngineDescription simplifiedExtendedLesk = createPrimitiveDescription(
                WSDAnnotatorContextPOS.class,
                WSDAnnotatorContextPOS.WSD_METHOD_CONTEXT,
                simplifiedExtendedLeskResource,
                WSDAnnotatorContextPOS.PARAM_CONTEXT_ANNOTATION,
                DocumentAnnotation.class.getName());

        ExternalResourceDescription simplifiedLeskResource = createExternalResourceDescription(
                WSDResourceSimplifiedLesk.class,
                WSDResourceSimplifiedLesk.SENSE_INVENTORY_RESOURCE, wordnet2_1,
                WSDResourceSimplifiedLesk.PARAM_NORMALIZATION_STRATEGY,
                SecondObjects.class.getName(),
                WSDResourceSimplifiedLesk.PARAM_OVERLAP_STRATEGY,
                PairedOverlap.class.getName(),
                WSDResourceSimplifiedLesk.PARAM_TOKENIZATION_STRATEGY,
                EnglishStopLemmatizer.class.getName());

        AnalysisEngineDescription simplifiedLesk = createPrimitiveDescription(
                WSDAnnotatorContextPOS.class,
                WSDAnnotatorContextPOS.WSD_METHOD_CONTEXT,
                simplifiedLeskResource,
                WSDAnnotatorContextPOS.PARAM_CONTEXT_ANNOTATION,
                Sentence.class.getName());

//        ExternalResourceDescription degreeCentralityResource = createExternalResourceDescription(
//                WSDResourceDegreeCentrality.class,
//                WSDResourceDegreeCentrality.SENSE_INVENTORY_RESOURCE, wordnet2_1,
//                WSDResourceDegreeCentrality.PARAM_MINIMUM_DEGREE, "1",
//                WSDResourceDegreeCentrality.PARAM_SEARCH_DEPTH, "2");

//        AnalysisEngineDescription degreeCentrality = createPrimitiveDescription(
//                WSDAnnotatorCollectivePOS.class,
//                WSDAnnotatorCollectivePOS.WSD_ALGORITHM_RESOURCE,
//                degreeCentralityResource,
//                WSDAnnotatorCollectivePOS.PARAM_CONTEXT_ANNOTATION, Sentence.class.getName());

        // Here's a resource encapsulating the most frequent sense baseline
        // algorithm.
        ExternalResourceDescription mfsBaselineResource = createExternalResourceDescription(
                WSDResourceIndividualPOS.class,
                WSDResourceIndividualPOS.SENSE_INVENTORY_RESOURCE, wordnet2_1,
                WSDResourceIndividualPOS.DISAMBIGUATION_METHOD,
                MostFrequentSenseBaseline.class.getName());

        AnalysisEngineDescription mfsBaseline = createPrimitiveDescription(
                WSDAnnotatorIndividualPOS.class,
                WSDAnnotatorIndividualPOS.WSD_ALGORITHM_RESOURCE,
                mfsBaselineResource);

        AnalysisEngineDescription writer = createPrimitiveDescription(WSDWriter.class);

        AnalysisEngineDescription evaluator = createPrimitiveDescription(
                SingleExactMatchEvaluatorText.class,
                SingleExactMatchEvaluatorText.PARAM_GOLD_STANDARD_ALGORITHM, answerkey,
                SingleExactMatchEvaluatorText.PARAM_TEST_ALGORITHM, SimplifiedExtendedLesk.class.getName(),
                SingleExactMatchEvaluatorText.PARAM_BACKOFF_ALGORITHM, MostFrequentSenseBaseline.class.getName());

        AnalysisEngineDescription senseWeightNormalizer = createPrimitiveDescription(
                SenseConfidenceNormalizer.class);

        AnalysisEngineDescription convertSensevalToSensekey = createPrimitiveDescription(
                SenseMapper.class, SenseMapper.PARAM_FILE,
                "classpath:/WordNet/wordnet_senseval.tsv",
                SenseMapper.PARAM_SOURCE_SENSE_INVENTORY_NAME, "Semeval1_sensekey",
                SenseMapper.PARAM_TARGET_SENSE_INVENTORY_NAME, "WordNet_2.1_sensekey",
                SenseMapper.PARAM_KEY_COLUMN, 2,
                SenseMapper.PARAM_VALUE_COLUMN, 1,
                SenseMapper.PARAM_IGNORE_UNKNOWN_SENSES, true);

        AnalysisEngineDescription convertSensekeyToSynset = createPrimitiveDescription(
                WordNetSenseKeyToSynset.class,
                WordNetSenseKeyToSynset.SOURCE_SENSE_INVENTORY_RESOURCE,
                wordnet2_1,
//                WordNetSenseKeyToSynset.PARAM_INDEX_SENSE_FILE,
//                "classpath:/WordNet/WordNet_2.1/dict/index.sense",
                SenseMapper.PARAM_SOURCE_SENSE_INVENTORY_NAME, "WordNet_2.1_sensekey",
                SenseMapper.PARAM_TARGET_SENSE_INVENTORY_NAME, "WordNet_2.1_synset",
                SenseMapper.PARAM_IGNORE_UNKNOWN_SENSES, false);

        SimplePipeline.runPipeline(reader,
                answerReader,
                convertSensevalToSensekey,
                convertSensekeyToSynset,
                mfsBaseline,
//                simplifiedLesk,
//                senseWeightNormalizer,
              simplifiedExtendedLesk,
//              degreeCentrality,
//              writer,
                evaluator);
    }

}
