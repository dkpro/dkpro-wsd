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
package de.tudarmstadt.ukp.dkpro.wsd.examples;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ExternalResourceDescription;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.RandomSenseBaseline;
import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorIndividualBasic;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseMapper;
import de.tudarmstadt.ukp.dkpro.wsd.evaluation.MultipleExactMatchEvaluator;
import de.tudarmstadt.ukp.dkpro.wsd.io.writer.WSDWriter;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceIndividualBasic;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.Senseval2AWReader;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.SensevalAnswerKeyReader;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.resource.WordNetSenseKeySenseInventoryResource;

/**
 * This class illustrates a pipeline which runs a WSD algorithm on the
 * Senseval-2 English all words test data. It uses the
 * {@link WordNetSenseKeySenseInventoryResource} resource to access WordNet via
 * the extJWNL library.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class Senseval2EnAWExample
{

    public static void main(String[] args)
        throws UIMAException, IOException
    {
        // For our corpus and answer key we will use the Senseval-2 English
        // all-words test data. You need to obtain this data set from the
        // Senseval-2 website. Change the value of the directory variable
        // to point to the location on your filesystem where you stored the
        // data set.
        final String directory = "/home/miller/workspace/de.tudarmstadt.ukp.experiments.tm.wsdcorpora/src/main/resources/senseval-2/english-all-words/test/";
        final String corpus = directory + "eng-all-words.test.xml";
        final String answerkey = directory + "eng-all-words.test.key";

        // This is a collection reader for the documents to be disambiguated.
        // The original corpus contains errors so we instruct the collection
        // reader to ignore them.
        CollectionReader reader = createReader(
                Senseval2AWReader.class, Senseval2AWReader.PARAM_FILE, corpus,
                Senseval2AWReader.PARAM_IGNORE_MISSING_SATELLITES, true);

        // This Senseval data set uses an unpublished pre-release version of
        // WordNet 1.7 as its sense inventory. This pre-release version is
        // lost. Here we use WordNet 1.7 instead, though some of the sense
        // keys are slightly different. You need to create an extJWNL
        // properties file and change the value of the
        // PARAM_WORDNET_PROPERTIES_URL to point to its location on your file
        // system.
        final String wordnetInventoryName = "WordNet_1.7pre_sensekey";
        ExternalResourceDescription wordnet1_7 = createExternalResourceDescription(
                WordNetSenseKeySenseInventoryResource.class,
                WordNetSenseKeySenseInventoryResource.PARAM_WORDNET_PROPERTIES_URL,
                "/home/miller/share/WordNet/WordNet-1.7pre/extjwnl_properties.xml",
                WordNetSenseKeySenseInventoryResource.PARAM_SENSE_INVENTORY_NAME,
                wordnetInventoryName);

        // This AE reads the Senseval-2 answer key. Because the Senseval
        // answer key format doesn't itself indicate what sense inventory is
        // used for the keys, we need to pass this as a configuration parameter.
        // In this case, the keys use sense identifiers which are specific
        // to the Senseval task, so we give it a unique ID.
        final String sensevalInventoryName = "Senseval2_sensekey";
        AnalysisEngineDescription answerReader = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, answerkey,
                SensevalAnswerKeyReader.PARAM_SENSE_INVENTORY,
                sensevalInventoryName);

        // The Senseval-2 sense identifiers are based on (but subtly different
        // from) sense keys from the WordNet 1.7-prerelease.  We therefore
        // use this AE  to convert them to WordNet 1.7-prerelease sense keys. We
        // have a delimited text file providing a mapping between the two
        // sense identifiers, which the SenseMapper annotator reads in and
        // uses to perform the conversion.
        AnalysisEngineDescription convertSensevalToSensekey = createEngineDescription(
                SenseMapper.class, SenseMapper.PARAM_FILE,
                "classpath:/wordnet_senseval.tsv",
                SenseMapper.PARAM_SOURCE_SENSE_INVENTORY_NAME,
                sensevalInventoryName,
                SenseMapper.PARAM_TARGET_SENSE_INVENTORY_NAME,
                wordnetInventoryName, SenseMapper.PARAM_KEY_COLUMN, 2,
                SenseMapper.PARAM_VALUE_COLUMN, 1,
                SenseMapper.PARAM_IGNORE_UNKNOWN_SENSES, true);

        // Here's a resource encapsulating the random sense baseline algorithm.
        ExternalResourceDescription randomBaselineResource = createExternalResourceDescription(
                WSDResourceIndividualBasic.class,
                WSDResourceIndividualBasic.SENSE_INVENTORY_RESOURCE,
                wordnet1_7, WSDResourceIndividualBasic.DISAMBIGUATION_METHOD,
                RandomSenseBaseline.class.getName());

        AnalysisEngineDescription randomBaseline = createEngineDescription(
                WSDAnnotatorIndividualBasic.class,
                WSDAnnotatorIndividualBasic.WSD_ALGORITHM_RESOURCE,
                randomBaselineResource);

        // This AE prints out detailed information on the AEs' sense
        // assignments. It's excluded from the pipeline by default as it
        // produces quite a lot of output.
        @SuppressWarnings("unused")
        AnalysisEngineDescription writer = createEngineDescription(WSDWriter.class);

        // This AE compares the sense assignments of all algorithms against
        // the given gold standard (in this case, the answer key we read in)
        // and computes and prints out useful statistics, such as precision,
        // recall, and coverage.
        AnalysisEngineDescription evaluator = createEngineDescription(
                MultipleExactMatchEvaluator.class,
                MultipleExactMatchEvaluator.PARAM_GOLD_STANDARD_ALGORITHM,
                answerkey);

        // Here we run the pipeline
        SimplePipeline.runPipeline(reader, answerReader,
                convertSensevalToSensekey, randomBaseline,
                // writer,
                evaluator);
    }

}
