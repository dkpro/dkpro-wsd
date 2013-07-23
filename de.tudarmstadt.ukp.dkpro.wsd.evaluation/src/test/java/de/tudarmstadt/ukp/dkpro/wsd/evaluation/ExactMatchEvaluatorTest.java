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

package de.tudarmstadt.ukp.dkpro.wsd.evaluation;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.junit.Test;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.wsd.io.reader.Senseval2LSReader;
import de.tudarmstadt.ukp.dkpro.wsd.io.reader.SensevalAnswerKeyReader;

/**
 * @author	Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 */
public class ExactMatchEvaluatorTest
{
	@Test
	public void exactMatchEvaluatorTest()
    throws Exception
	{

		CollectionReader reader = createCollectionReader(
                Senseval2LSReader.class,
                Senseval2LSReader.PARAM_FILE, "classpath:/senseval/senseval2ls.xml"
                );
        AnalysisEngineDescription goldAnswerReader = createPrimitiveDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, "classpath:/senseval/senseval2ls.key"
        );
        AnalysisEngineDescription testAnswerReader = createPrimitiveDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, "classpath:/senseval/senseval2ls_test.key"
        );
        AnalysisEngineDescription exactMatchEvaluator = createPrimitiveDescription(
                MultipleExactMatchEvaluator.class,
                MultipleExactMatchEvaluator.PARAM_GOLD_STANDARD_ALGORITHM, "classpath:/senseval/senseval2ls.key"
        );

        SimplePipeline.runPipeline(
                reader,
                goldAnswerReader,
                testAnswerReader,
                exactMatchEvaluator
        );
	}
}
