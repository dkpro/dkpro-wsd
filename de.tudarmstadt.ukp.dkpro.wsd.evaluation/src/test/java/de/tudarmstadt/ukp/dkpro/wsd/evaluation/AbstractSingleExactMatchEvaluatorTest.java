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

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.junit.Test;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.wsd.io.reader.SemCorXMLReader;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.SensevalAnswerKeyReader;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 * 			Andriy Nadolskyy
 */
public class AbstractSingleExactMatchEvaluatorTest
{
	@Test
	public void exactMatchEvaluatorTest()
    throws Exception
	{
        // Corpus reader
        final String corpusDirectory = "src/test/resources/AbstractSingleExactMatchEvaluatorTest/";
        CollectionReader reader = createReader(SemCorXMLReader.class,
                SemCorXMLReader.PARAM_SKIP_WITHOUT_WNSN, true,
                SemCorXMLReader.PARAM_SOURCE_LOCATION, corpusDirectory,
                SemCorXMLReader.PARAM_PATTERNS,
                new String[] { ResourceCollectionReaderBase.INCLUDE_PREFIX
                        + "d00_modified.xml" });
        
        AnalysisEngineDescription testAlgorithmAnswerReader = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, "classpath:/AbstractSingleExactMatchEvaluatorTest/senseval/senseval_test_algorithm.key"
        );
        
        AnalysisEngineDescription backoffAlgorithmAnswerReader1 = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, "classpath:/AbstractSingleExactMatchEvaluatorTest/senseval/senseval_backoff_1.key"
        );
        
        AnalysisEngineDescription backoffAlgorithmAnswerReader2 = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, "classpath:/AbstractSingleExactMatchEvaluatorTest/senseval/senseval_backoff_2.key"
        );
        
        // Produces a TSV file which a spreadsheet or graphing program can use to produce a precision-recall graph
        String backoffAlgorithms[] = new String[2];
        backoffAlgorithms[0] = "classpath:/AbstractSingleExactMatchEvaluatorTest/senseval/senseval_backoff_1.key";
        backoffAlgorithms[1] = "classpath:/AbstractSingleExactMatchEvaluatorTest/senseval/senseval_backoff_2.key";
//        backoffAlgorithms[0] = "classpath:/AbstractSingleExactMatchEvaluatorTest/senseval/senseval_backoff_2.key";
        AnalysisEngineDescription singleExact = createEngineDescription(SingleExactMatchEvaluatorText.class,
        		SingleExactMatchEvaluatorText.PARAM_GOLD_STANDARD_ALGORITHM,
                SemCorXMLReader.DISAMBIGUATION_METHOD_NAME,
                SingleExactMatchEvaluatorText.PARAM_TEST_ALGORITHM, 
                "classpath:/AbstractSingleExactMatchEvaluatorTest/senseval/senseval_test_algorithm.key",
                SingleExactMatchEvaluatorText.PARAM_BACKOFF_ALGORITHMS, backoffAlgorithms,
                SingleExactMatchEvaluatorText.PARAM_OUTPUT_FILE, "target/pr.tsv");
   
        SimplePipeline.runPipeline(
                reader,
                testAlgorithmAnswerReader,
                backoffAlgorithmAnswerReader1,
                backoffAlgorithmAnswerReader2,
                singleExact
        );
        
        /*
         * detailed description of test cases to cover all possibilities is given below
         * 
         *                    111111111122222222
		   instance: 123456789012345678901234567
		   gold    : TTTTTTTTTTTTTTTTTTTTTTTTTTT
		   test    : TTTTTTTTTFFFFFFFFF---------
		   back1   : TTTFFF---TTTFFF---TTTFFF---
		   back2   : TF-TF-TF-TF-TF-TF-TF-TF-TF-
		   
		   "T" means a correct answer, "F" means an incorrect answer, and "-" means a missing answer
		   
		   The confidence scores (the part after the / in the answer key file) should be 1.0 for the 
		   test algorithm, 0.1 for the first backoff algorithm, and 0.01 for the second backoff algorithm.
		   
		   Pipelines with following evaluator configurations should be run to check all possibilities 
		   from the table above:
		   1) Test algorithm only 						(recall = 9/27 = 0.3333)
		   2) Test algorithm + backoff1					(recall = 9/27 + 3/27/10 = 0.3444)
		   3) Test algorithm + backoff2					(recall = 9/27 + 3/27/100 = 0.3344)
		   4) Test algorithm + backoff1 + backoff2		(recall = 9/27 + 3/27/10 + 1/27/100 = 0.3448)
		   5) Test algorithm + backoff2 + backoff1		(recall = 9/27 + 3/27/100 + 1/27/10 = 0.3381)
         */
        
	}
}
