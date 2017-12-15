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
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.wsd.io.reader.SemCorXMLReader;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.SensevalAnswerKeyReader;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class SingleExactMatchEvaluatorTextTest
{
    final double delta = 0.0001;

    @Test
    public void testSingleAnswer() throws UIMAException, IOException
    {
        Result r;

        // All answers are correct
        r = runEvaluation(1, 1);
        assertEquals(1.0, r.p, delta);
        assertEquals(1.0, r.r, delta);
        assertEquals(1.0, r.c, delta);

        // All answers wrong
        r = runEvaluation(2, 1);
        assertEquals(0.0, r.p, delta);
        assertEquals(0.0, r.r, delta);
        assertEquals(1.0, r.c, delta);

        // Half of answers correct
        r = runEvaluation(3, 1);
        assertEquals(1.0, r.p, delta);
        assertEquals(0.5, r.r, delta);
        assertEquals(0.5, r.c, delta);

        // One answer is half-correct; others are correct
        r = runEvaluation(4, 1);
        assertEquals(0.875, r.p, delta);
        assertEquals(0.875, r.r, delta);
        assertEquals(1.0, r.c, delta);

        // All answers are missing
        r = runEvaluation(5, 1);
        assertEquals(0.0, r.p, delta);
        assertEquals(0.0, r.r, delta);
        assertEquals(0.0, r.c, delta);

        // One answer has duplicate half-correct entries
        r = runEvaluation(6, 1);
        assertEquals(1.0, r.p, delta);
        assertEquals(1.0, r.r, delta);
        assertEquals(1.0, r.c, delta);
}

    @Test
    public void testMultipleAnswer() throws UIMAException, IOException
    {
        Result r;

        // All answers are correct
        r = runEvaluation(1, 2);
        assertEquals(1.0, r.p, delta);
        assertEquals(1.0, r.r, delta);
        assertEquals(1.0, r.c, delta);

        // All answers wrong
        r = runEvaluation(2, 2);
        assertEquals(0.0, r.p, delta);
        assertEquals(0.0, r.r, delta);
        assertEquals(1.0, r.c, delta);

        // Half of answers correct
        r = runEvaluation(3, 2);
        assertEquals(1.0, r.p, delta);
        assertEquals(0.5, r.r, delta);
        assertEquals(0.5, r.c, delta);

        // All answers correct
        r = runEvaluation(4, 2);
        assertEquals(1.0, r.p, delta);
        assertEquals(1.0, r.r, delta);
        assertEquals(1.0, r.c, delta);

        // All answers are missing
        r = runEvaluation(5, 2);
        assertEquals(0.0, r.p, delta);
        assertEquals(0.0, r.r, delta);
        assertEquals(0.0, r.c, delta);

        // One answer has duplicate half-correct entries
        r = runEvaluation(6, 2);
        assertEquals(1.0, r.p, delta);
        assertEquals(1.0, r.r, delta);
        assertEquals(1.0, r.c, delta);
}

    public class Result {
        double p, r, c;
        public Result(double p, double r, double c) {
            this.p = p;
            this.r = r;
            this.c = c;
        }
    }

    public Result runEvaluation(int testKey, int goldKey) throws UIMAException, IOException {

        final String goldKeyTemplate = "classpath:/SingleExactMatchEvaluatorTextTest/semcor.gold.%02d.key";
        final String testKeyTemplate = "classpath:/SingleExactMatchEvaluatorTextTest/semcor.test.%02d.key";

        String goldKeyFilename = String.format(goldKeyTemplate, goldKey);
        String testKeyFilename = String.format(testKeyTemplate, testKey);

        File tempFile = File.createTempFile("SingleExactMatchEvaluatorTest.", ".log");
        tempFile.deleteOnExit();
        System.out.println(tempFile.getPath());
        CollectionReader reader = createReader(
                SemCorXMLReader.class,
                SemCorXMLReader.PARAM_SOURCE_LOCATION, "classpath:/semcor",
                SemCorXMLReader.PARAM_PATTERNS,  new String[] {
                        ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.xml" }
                );

        AnalysisEngineDescription goldKeyReader = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, goldKeyFilename
        );
        AnalysisEngineDescription testKeyReader = createEngineDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, testKeyFilename
        );
        AnalysisEngineDescription evaluator = createEngineDescription(
                SingleExactMatchEvaluatorText.class,
                SingleExactMatchEvaluatorText.PARAM_GOLD_STANDARD_ALGORITHM, goldKeyFilename,
                SingleExactMatchEvaluatorText.PARAM_TEST_ALGORITHM, testKeyFilename,
                SingleExactMatchEvaluatorText.PARAM_OUTPUT_FILE, tempFile.getPath()
        );

        SimplePipeline.runPipeline(
                reader,
                goldKeyReader,
                testKeyReader,
                evaluator
        );

        Scanner in = new Scanner(new FileReader(tempFile));
        for (int i = 0; i < 12; i++) {
            in.nextLine();
        }
        for (int i = 0; i < 5; i++) {
            in.next();
        }
        Result r = new Result(in.nextDouble(), in.nextDouble(), in.nextDouble());
        in.close();
        return r;
    }
}
