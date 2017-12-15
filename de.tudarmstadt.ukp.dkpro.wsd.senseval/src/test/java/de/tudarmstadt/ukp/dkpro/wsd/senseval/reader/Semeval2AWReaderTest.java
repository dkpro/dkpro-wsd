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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.Semeval2AWReader;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

public class Semeval2AWReaderTest
{
	@Test
	public void semeval2AWReaderTest()
    throws Exception
	{
		Sentence s;
		WSDItem w;
		LexicalItemConstituent c;
		CollectionReader reader = createReader(
                Semeval2AWReader.class,
                Semeval2AWReader.PARAM_FILE, "classpath:/senseval/semeval2aw.xml"
                );

		JCasIterator i = new JCasIterator(reader);
		assertTrue(i.hasNext());
		JCas j = i.next();
		assertEquals(" Eggs looked up like food . ", j.getDocumentText());
		assertEquals("en", j.getDocumentLanguage());

		s = JCasUtil.selectByIndex(j, Sentence.class, 0);
		assertEquals(" Eggs looked up like food . ", s.getCoveredText());
		s = JCasUtil.selectByIndex(j, Sentence.class, -1);
		assertEquals(" Eggs looked up like food . ", s.getCoveredText());

		w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
		assertEquals("looked", w.getCoveredText());
		assertEquals("en1.s001.t3", w.getId());
		assertEquals("look up", w.getSubjectOfDisambiguation());
		assertNull(w.getPos());

		FSArray constituents = w.getConstituents();
		assertNotNull(constituents);
		assertEquals(2, w.getConstituents().size());
		c = w.getConstituents(0);
		assertNotNull(c.getConstituentType());
		assertEquals("looked", c.getCoveredText());
		assertEquals("en1.s001.t3", c.getId());
		c = w.getConstituents(1);
		assertNotNull(c.getConstituentType());
		assertEquals("up", c.getCoveredText());
		assertEquals("en1.s001.t4", c.getId());

		w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
		assertEquals("food", w.getCoveredText());

		assertEquals(1, w.getConstituents().size());
		c = w.getConstituents(0);
		assertNotNull(c.getConstituentType());
		assertEquals("food", c.getCoveredText());
		assertEquals("en1.s001.t6", c.getId());

		assertTrue(i.hasNext());
		j = i.next();
		assertEquals(" Hello .  He is good . ", j.getDocumentText());
		assertEquals("en", j.getDocumentLanguage());

		assertFalse(i.hasNext());
	}
}
