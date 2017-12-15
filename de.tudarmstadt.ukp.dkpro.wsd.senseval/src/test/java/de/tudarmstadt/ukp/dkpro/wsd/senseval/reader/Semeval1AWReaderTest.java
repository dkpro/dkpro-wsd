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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.Semeval1AWReader;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

public class Semeval1AWReaderTest
{
	@Test
	public void semeval1AWReaderTest()
    throws Exception
	{
		Sentence s;
		WSDItem w;
		LexicalItemConstituent c;
		CollectionReader reader = createReader(
                Semeval1AWReader.class,
                Semeval1AWReader.PARAM_FILE, "classpath:/senseval/semeval1aw.xml"
                );

		JCasIterator i = new JCasIterator(reader);
		assertTrue(i.hasNext());
		JCas j = i.next();
		assertEquals(" Eggs looked like food . ", j.getDocumentText());
		assertEquals("en", j.getDocumentLanguage());

		s = JCasUtil.selectByIndex(j, Sentence.class, 0);
		assertEquals(" Eggs looked like food . ", s.getCoveredText());
		s = JCasUtil.selectByIndex(j, Sentence.class, -1);
		assertEquals(" Eggs looked like food . ", s.getCoveredText());

		w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
		assertEquals("looked", w.getCoveredText());
		assertEquals("d001.s001.t001", w.getId());
		assertEquals("look", w.getSubjectOfDisambiguation());
		assertEquals(POS.VERB.toString(), w.getPos());

		assertEquals(1, w.getConstituents().size());
		c = w.getConstituents(0);
		assertNotNull(c.getConstituentType());
		assertEquals("looked", c.getCoveredText());
		assertEquals("d001.s001.t001", c.getId());

		w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
		assertEquals("food", w.getCoveredText());

		assertEquals(1, w.getConstituents().size());
		c = w.getConstituents(0);
		assertNotNull(c.getConstituentType());
		assertEquals("food", c.getCoveredText());
		assertEquals("d001.s001.t002", c.getId());

		assertTrue(i.hasNext());
		j = i.next();
		assertEquals(" Hello .  He is good . ", j.getDocumentText());
		assertEquals("en", j.getDocumentLanguage());

		assertFalse(i.hasNext());
	}
}
