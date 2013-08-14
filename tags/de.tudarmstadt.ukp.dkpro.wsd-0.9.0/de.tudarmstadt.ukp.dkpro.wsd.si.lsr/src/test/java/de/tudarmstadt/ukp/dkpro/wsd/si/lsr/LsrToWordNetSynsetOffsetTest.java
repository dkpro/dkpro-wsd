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

package de.tudarmstadt.ukp.dkpro.wsd.si.lsr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregate;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.io.reader.Senseval2LSReader;
import de.tudarmstadt.ukp.dkpro.wsd.io.reader.SensevalAnswerKeyReader;
import de.tudarmstadt.ukp.dkpro.wsd.io.writer.SensevalAnswerKeyWriter;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.LsrToWordNetSynsetOffset;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;


public class LsrToWordNetSynsetOffsetTest
{
	@Test
	public void lsrToWordNetSynsetOffsetTest()
    throws Exception
	{
		WSDItem w;
		WSDResult r;

		CollectionReader reader = createCollectionReader(
                Senseval2LSReader.class,
                Senseval2LSReader.PARAM_FILE, "classpath:/senseval/senseval2ls.xml"
                );
        AnalysisEngineDescription answerReader = createPrimitiveDescription(
                SensevalAnswerKeyReader.class,
                SensevalAnswerKeyReader.PARAM_FILE, "classpath:/senseval/senseval2ls_lsr.key",
                SensevalAnswerKeyReader.PARAM_SENSE_INVENTORY, "WordNet_3.0_LSR"
        );
        AnalysisEngineDescription converter = createPrimitiveDescription(
                LsrToWordNetSynsetOffset.class,
                LsrToWordNetSynsetOffset.PARAM_SOURCE_SENSE_INVENTORY_NAME, "WordNet_3.0_LSR",
                LsrToWordNetSynsetOffset.PARAM_TARGET_SENSE_INVENTORY_NAME, "WordNet_3.0_synset"
                );
        AnalysisEngineDescription aggregate = createAggregateDescription(
      		answerReader,
       		converter
        );
        AnalysisEngine engine = createAggregate(aggregate);

        JCasIterable i = new JCasIterable(reader, engine);
		assertTrue(i.hasNext());
		JCas j = i.next();

		w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
		r = JCasUtil.selectByIndex(j, WSDResult.class, 0);
		System.out.println(SensevalAnswerKeyWriter.toSensevalAnswerKey(r));
		assertEquals(w, r.getWsdItem());
		assertEquals("00933420n", r.getSenses(0).getId());
		assertEquals("05638987n", r.getSenses(1).getId());

		r = JCasUtil.selectByIndex(j, WSDResult.class, -1);
		assertEquals(w, r.getWsdItem());

		assertTrue(i.hasNext());
		j = i.next();

		w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
		r = JCasUtil.selectByIndex(j, WSDResult.class, -1);
        System.out.println(SensevalAnswerKeyWriter.toSensevalAnswerKey(r));
		assertEquals(w, r.getWsdItem());
		assertEquals("02937469n", r.getSenses(0).getId());
		assertEquals("U", r.getSenses(1).getId());
	}

}
