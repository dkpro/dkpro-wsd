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

package de.tudarmstadt.ukp.dkpro.wsd.io.reader;

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

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

public class WebCAGeXMLReaderTest
{
    public final static String sentence2="Nach dem Unwetter blieb uns nur eine Lösung: Abbruch der Zelte.";
    public final static String sentence3=" Der Ladevorgang einer Internetseite kann in verschiedenen Webbrowsern mit der Escape-Taste unterbrochen werden. In den meisten Dialogfenstern von Windows-Anwendungen entspricht das Drücken der Escape-Taste einem Klick auf die Schaltfläche „Abbrechen“. Ein Druck auf die Escape-Taste bricht auch eine bereits begonnene Eingabe in einem Textfeld ab und stellt den Ursprungszustand wieder her. ";
    public final static String sentence4=" Im Augenblick sind vierzehn Kasinos 24 Stunden am Tag geöffnet, zu Lande oder auf dem Meer, in denen Spielergruppen - weitaus leiser, als man vermuten möchte - ohne Unterbrechung in fensterlosen Sälen unter gleißendem Neonlicht zocken. ";

    @Test
	public void webCAGeXMLReaderTest()
    throws Exception
	{
		WSDItem w;
		WSDResult r;
		final String webCAGeDirectory = "classpath:/webcage";
		LexicalItemConstituent c;
		CollectionReader reader = createReader(
                WebCAGeXMLReader.class,
                WebCAGeXMLReader.PARAM_SOURCE_LOCATION, webCAGeDirectory,
                WebCAGeXMLReader.PARAM_PATTERNS,  new String[] {
                        ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.xml" }
                );

        JCasIterator i = new JCasIterator(reader);
		assertTrue(i.hasNext());
		JCas j = i.next();
        System.out.println(DocumentMetaData.get(j).getDocumentId());
		System.out.println(j.getDocumentText());
		assertTrue(i.hasNext());

		j = i.next();
        System.out.println(j.getDocumentText());
        assertTrue(i.hasNext());
        assertEquals(sentence2, j.getDocumentText());
        assertEquals("de", j.getDocumentLanguage());

        w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
        assertEquals("Abbruch", w.getCoveredText());
        assertEquals("11", w.getId());
        assertEquals("Abbruch", w.getSubjectOfDisambiguation());
        assertEquals(POS.NOUN.toString(), w.getPos());

        j = i.next();
        System.out.println(j.getDocumentText());

		assertEquals(sentence3, j.getDocumentText());
		assertEquals("de", j.getDocumentLanguage());

		w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
		assertEquals("Abbrechen", w.getCoveredText());
		assertEquals("1", w.getId());
		assertEquals("abbrechen", w.getSubjectOfDisambiguation());
		assertEquals(POS.VERB.toString(), w.getPos());

		assertEquals(1, w.getConstituents().size());
		c = w.getConstituents(0);
		assertNotNull(c.getConstituentType());
		assertEquals("Abbrechen", c.getCoveredText());
		assertEquals("1", c.getId());

		r = JCasUtil.selectByIndex(j, WSDResult.class, 0);
		assertEquals(1, r.getSenses().size());
		assertEquals("83332", r.getSenses(0).getId());
//		assertEquals(semCorDirectory, r.getDisambiguationMethod());

		w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
        assertEquals("bricht", w.getCoveredText());
        assertEquals("abbrechen", w.getSubjectOfDisambiguation());
        assertEquals(POS.VERB.toString(), w.getPos());

        assertTrue(i.hasNext());
        j = i.next();
        System.out.println(j.getDocumentText());
        assertEquals(sentence4, j.getDocumentText());

        r = JCasUtil.selectByIndex(j, WSDResult.class, 0);
        assertEquals(1, r.getSenses().size());
        assertEquals("79948", r.getSenses(0).getId());

        w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
        assertEquals("zocken", w.getCoveredText());
        assertEquals("zocken", w.getSubjectOfDisambiguation());
        assertEquals(POS.VERB.toString(), w.getPos());


        assertFalse(i.hasNext());
	}
}
