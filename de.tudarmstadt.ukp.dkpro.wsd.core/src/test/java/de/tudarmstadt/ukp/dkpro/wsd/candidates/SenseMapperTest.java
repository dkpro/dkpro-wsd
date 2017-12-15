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

package de.tudarmstadt.ukp.dkpro.wsd.candidates;

// TODO: Rewrite to eliminate dependency on Senseval reader

public class SenseMapperTest
{
//	@Test
//	public void senseMapperTest()
//    throws Exception
//	{
//		WSDItem w;
//		WSDResult r;
//
//		CollectionReader reader = createReader(
//                Senseval2LSReader.class,
//                Senseval2LSReader.PARAM_FILE, "classpath:/senseval/senseval2ls.xml"
//                );
//        AnalysisEngineDescription answerReader = createEngineDescription(
//                SensevalAnswerKeyReader.class,
//                SensevalAnswerKeyReader.PARAM_FILE, "classpath:/senseval/senseval2ls.key",
//                SensevalAnswerKeyReader.PARAM_SENSE_INVENTORY, "Senseval_sensekey"
//        );
//        AnalysisEngineDescription converter = createEngineDescription(
//				SenseMapper.class,
//				SenseMapper.PARAM_FILE, "classpath:/senseval/index.sense",
//				SenseMapper.PARAM_SOURCE_SENSE_INVENTORY_NAME, "Senseval_sensekey",
//				SenseMapper.PARAM_TARGET_SENSE_INVENTORY_NAME, "WordNet_3.0_synset",
//				SenseMapper.PARAM_KEY_COLUMN, 1,
//				SenseMapper.PARAM_VALUE_COLUMN, 2);
//        AnalysisEngineDescription aggregate = createEngineDescription(
//      		answerReader,
//       		converter
//        );
//        AnalysisEngine engine = createEngine(aggregate);
//
//        JCasIterable i = new JCasIterable(reader, engine);
//		assertTrue(i.hasNext());
//		JCas j = i.next();
//
//		w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
//		r = JCasUtil.selectByIndex(j, WSDResult.class, 0);
//        System.out.println(SensevalAnswerKeyWriter.toSensevalAnswerKey(r));
//		assertEquals(w, r.getWsdItem());
//		assertEquals("01063695", r.getSenses(0).getId());
//		assertEquals("02627934", r.getSenses(1).getId());
//
//		r = JCasUtil.selectByIndex(j, WSDResult.class, -1);
//		assertEquals(w, r.getWsdItem());
//
//		assertTrue(i.hasNext());
//		j = i.next();
//
//		w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
//		r = JCasUtil.selectByIndex(j, WSDResult.class, -1);
//        System.out.println(SensevalAnswerKeyWriter.toSensevalAnswerKey(r));
//		assertEquals(w, r.getWsdItem());
//		assertEquals("00015388", r.getSenses(0).getId());
//		assertEquals("U", r.getSenses(1).getId());
//	}

}
