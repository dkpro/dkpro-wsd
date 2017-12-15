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

package de.tudarmstadt.ukp.dkpro.wsd.senseval.reader;

import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.Senseval2LSReader;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

public class Senseval2LSReaderTest
{
	@Test
	public void senseval2LSReaderTest()
    throws Exception
	{
		WSDItem w;
		LexicalItemConstituent c;
		CollectionReader reader = createReader(
                Senseval2LSReader.class,
                Senseval2LSReader.PARAM_FILE, "classpath:/senseval/senseval2ls.xml"
                );

        JCasIterator i = new JCasIterator(reader);
		assertTrue(i.hasNext());
		JCas j = i.next();
		assertEquals("Eggs call for food.", j.getDocumentText());
		assertEquals("en", j.getDocumentLanguage());

		w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
		assertEquals("call", w.getCoveredText());
		assertEquals("call.1", w.getId());
		assertEquals("call", w.getSubjectOfDisambiguation());
		assertEquals(POS.VERB.toString(), w.getPos());

		assertEquals(2, w.getConstituents().size());
		c = w.getConstituents(0);
		assertNotNull(c.getConstituentType());
		assertEquals("call", c.getCoveredText());
		assertEquals("call.1", c.getId());
		c = w.getConstituents(1);
		assertNotNull(c.getConstituentType());
		assertEquals("for", c.getCoveredText());
		assertEquals("call_for:1", c.getId());

		w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
		assertEquals("call", w.getCoveredText());

		assertTrue(i.hasNext());
		j = i.next();
		assertEquals("Dave is an animal.", j.getDocumentText());
		assertEquals("en", j.getDocumentLanguage());

		assertFalse(i.hasNext());

		w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
		assertEquals("animal", w.getCoveredText());
		assertEquals("animal.1", w.getId());
		assertEquals(POS.NOUN.toString(), w.getPos());
	}
}
