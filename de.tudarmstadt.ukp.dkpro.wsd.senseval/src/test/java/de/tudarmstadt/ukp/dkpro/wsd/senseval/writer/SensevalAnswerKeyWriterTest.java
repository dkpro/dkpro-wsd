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

package de.tudarmstadt.ukp.dkpro.wsd.senseval.writer;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.junit.Test;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.Senseval2LSReader;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.SensevalAnswerKeyReader;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.writer.SensevalAnswerKeyWriter;

public class SensevalAnswerKeyWriterTest
{
    @Test
    public void sensevalAnswerKeyWriterTest()
    throws Exception
    {
        File temp = File.createTempFile("SensevalAnswerKeyWriterTest", ".key");

        CollectionReader reader = createReader(
                Senseval2LSReader.class,
                Senseval2LSReader.PARAM_FILE, "classpath:/senseval/senseval2ls.xml"
                );
        AnalysisEngineDescription answerReader = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, "classpath:/senseval/senseval2ls.key"
        );
        AnalysisEngineDescription answerWriter = createEngineDescription(
                SensevalAnswerKeyWriter.class,
                SensevalAnswerKeyWriter.PARAM_INCLUDE_CONFIDENCE_VALUES, true,
                SensevalAnswerKeyWriter.PARAM_REPLACE_APOSTROPHES, false,
                SensevalAnswerKeyWriter.PARAM_ALGORITHM, "foo",
                SensevalAnswerKeyWriter.PARAM_OUTPUT_FILE, temp
                );

        SimplePipeline.runPipeline(reader,  answerReader, answerWriter);

        // TODO: Do some more sophisticated testing (e.g., test that the
        // contents of the file are what we expected them to be)

        temp.delete();
    }

}
