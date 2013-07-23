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

package de.tudarmstadt.ukp.dkpro.wsd.io.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;

import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

public class SemCorXMLReaderTest
{
    public final static String sentence1=" The Fulton_County_Grand_Jury said Friday an investigation of Atlanta 's recent primary_election produced `` no evidence '' that any irregularities took_place . ";
    public final static String sentence2=" The jury further said in term end presentments that the City_Executive_Committee , which had over-all charge of the election , `` deserves the praise and thanks of the City_of_Atlanta '' for the manner in which the election was conducted . ";
    public final static String sentence3=" Committee approval of Gov._Price_Daniel 's `` abandoned property '' act seemed certain Thursday despite the adamant protests of Texas bankers . ";
    public final static String sentence4=" Daniel personally led the fight for the measure , which he had watered_down considerably since its rejection by two previous Legislatures , in a public hearing before the House_Committee_on_Revenue_and_Taxation . ";

	@Test
	public void semCorXMLReaderTest()
    throws Exception
	{
		Sentence s;
		Paragraph p;
		WSDItem w;
		WSDResult r;
		final String semCorDirectory = "classpath:/semcor";
		LexicalItemConstituent c;
		CollectionReader reader = createCollectionReader(
                SemCorXMLReader.class,
                SemCorXMLReader.PARAM_PATH, semCorDirectory,
                SemCorXMLReader.PARAM_PATTERNS,  new String[] {
                        ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.xml" }
                );

		JCasIterable i = new JCasIterable(reader);
		assertTrue(i.hasNext());
		JCas j = i.next();
		System.out.println(j.getDocumentText());

		assertEquals(sentence1 + sentence2, j.getDocumentText());
		assertEquals("en", j.getDocumentLanguage());

		s = JCasUtil.selectByIndex(j, Sentence.class, 0);
		assertEquals(sentence1, s.getCoveredText());
		s = JCasUtil.selectByIndex(j, Sentence.class, -1);
		assertEquals(sentence2, s.getCoveredText());

        p = JCasUtil.selectByIndex(j, Paragraph.class, 0);
        assertEquals(sentence1, p.getCoveredText());
        p = JCasUtil.selectByIndex(j, Paragraph.class, -1);
        assertEquals(sentence2, p.getCoveredText());

		w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
		assertEquals("Fulton_County_Grand_Jury", w.getCoveredText());
		assertEquals("br-a01.p1.s1.w2", w.getId());
		assertEquals("group", w.getSubjectOfDisambiguation());
		assertEquals(POS.NOUN.toString(), w.getPos());

		assertEquals(1, w.getConstituents().size());
		c = w.getConstituents(0);
		assertNotNull(c.getConstituentType());
		assertEquals("Fulton_County_Grand_Jury", c.getCoveredText());
		assertEquals("br-a01.p1.s1.w2", c.getId());

		r = JCasUtil.selectByIndex(j, WSDResult.class, 0);
		assertEquals(1, r.getSenses().size());
		assertEquals("group%1:03:00::", r.getSenses(0).getId());
//		assertEquals(semCorDirectory, r.getDisambiguationMethod());

		w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
        assertEquals("conducted", w.getCoveredText());
        assertEquals("conduct", w.getSubjectOfDisambiguation());
        assertEquals(POS.VERB.toString(), w.getPos());

        assertTrue(i.hasNext());
        j = i.next();
        System.out.println(j.getDocumentText());

        assertEquals(sentence3 + sentence4, j.getDocumentText());
        assertEquals("en", j.getDocumentLanguage());

        s = JCasUtil.selectByIndex(j, Sentence.class, 0);
        assertEquals(sentence3, s.getCoveredText());
        s = JCasUtil.selectByIndex(j, Sentence.class, -1);
        assertEquals(sentence4, s.getCoveredText());

        p = JCasUtil.selectByIndex(j, Paragraph.class, 0);
        assertEquals(sentence3, p.getCoveredText());
        p = JCasUtil.selectByIndex(j, Paragraph.class, -1);
        assertEquals(sentence4, p.getCoveredText());

        w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
        assertEquals("Committee", w.getCoveredText());
        assertEquals("br-a02.p1.s1.w1", w.getId());
        assertEquals("committee", w.getSubjectOfDisambiguation());
        assertEquals(POS.NOUN.toString(), w.getPos());

        assertEquals(1, w.getConstituents().size());
        c = w.getConstituents(0);
        assertNotNull(c.getConstituentType());
        assertEquals("Committee", c.getCoveredText());
        assertEquals("br-a02.p1.s1.w1", c.getId());

        r = JCasUtil.selectByIndex(j, WSDResult.class, 0);
        assertEquals(1, r.getSenses().size());
        assertEquals("committee%1:14:00::", r.getSenses(0).getId());
//      assertEquals(semCorDirectory, r.getDisambiguationMethod());

        w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
        assertEquals("House_Committee_on_Revenue_and_Taxation", w.getCoveredText());
        assertEquals("group", w.getSubjectOfDisambiguation());
        assertEquals(POS.NOUN.toString(), w.getPos());

        assertFalse(i.hasNext());
	}
}
