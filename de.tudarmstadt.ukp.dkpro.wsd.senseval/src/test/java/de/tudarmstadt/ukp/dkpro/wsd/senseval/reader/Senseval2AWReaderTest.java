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
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.senseval.reader.Senseval2AWReader;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

public class Senseval2AWReaderTest
{
    @Test
    public void senseval2AWReaderTest()
        throws Exception
    {
        WSDItem w;
        LexicalItemConstituent c;
        CollectionReader reader = createReader(
                Senseval2AWReader.class, Senseval2AWReader.PARAM_FILE,
                "classpath:/senseval/senseval2aw.xml");

        JCasIterator i = new JCasIterator(reader);
        assertTrue(i.hasNext());
        JCas j = i.next();
        assertEquals(
                " He thought on the spur of the moment , as a matter of fact . ",
                j.getDocumentText());
        assertEquals("en", j.getDocumentLanguage());

        w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
        assertEquals("thought", w.getCoveredText());
        assertEquals("d000.s014.t014", w.getId());
        assertEquals("thought", w.getSubjectOfDisambiguation());
        // Formerly if no lemma attribute was available, we simply didn't set
        // the subject of disambiguation.
        // assertNull(w.getSubjectOfDisambiguation());
        assertNull(w.getPos());

        assertEquals(1, w.getConstituents().size());
        c = w.getConstituents(0);
        assertNotNull(c.getConstituentType());
        assertEquals("thought", c.getCoveredText());
        assertEquals("d000.s014.t014", c.getId());

        w = JCasUtil.selectByIndex(j, WSDItem.class, -1);
        assertEquals("matter", w.getCoveredText());

        assertEquals(5, w.getConstituents().size());
        c = w.getConstituents(0);
        assertNotNull(c.getConstituentType());
        assertEquals("matter", c.getCoveredText());
        assertEquals("d000.s015.t002", c.getId());

        c = w.getConstituents(4);
        assertNotNull(c.getConstituentType());
        assertEquals("fact", c.getCoveredText());
        assertEquals("d000.s015.t004", c.getId());

        assertFalse(i.hasNext());
    }
}
