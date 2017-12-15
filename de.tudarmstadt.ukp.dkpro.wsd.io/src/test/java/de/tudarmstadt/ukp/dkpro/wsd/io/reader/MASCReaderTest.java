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
import static org.junit.Assert.assertNull;
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

public class MASCReaderTest
{

    public final static String sentence2 = "But I told the third person that I am only telling you once.";

    @Test
    public void mascReaderTest()
    throws Exception
    {
        WSDItem w;
        WSDResult r;
        final String MASCDirectory = "classpath:/masc";
        CollectionReader reader = createReader(
                MASCReader.class,
                MASCReader.PARAM_IGNORE_TIES, true,
                MASCReader.PARAM_SOURCE_LOCATION, MASCDirectory,
                MASCReader.PARAM_PATTERNS,  new String[] {
                        ResourceCollectionReaderBase.INCLUDE_PREFIX + "*/*-wn.xml" }
                );

        JCasIterator i = new JCasIterator(reader);
        assertTrue(i.hasNext());
        JCas j = i.next();
        assertEquals("en", j.getDocumentLanguage());
        System.out.println(DocumentMetaData.get(j).getDocumentId());
        System.out.println(j.getDocumentText());

        w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
        assertEquals("tell_wn_n0", w.getId());
        assertEquals(POS.VERB.toString(), w.getPos());
        assertEquals("tell", w.getSubjectOfDisambiguation());

        assertTrue(i.hasNext());
        j = i.next();
        w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
        assertNull(w);

        assertTrue(i.hasNext());
        j = i.next();
        assertEquals(sentence2, j.getDocumentText());
        w = JCasUtil.selectByIndex(j, WSDItem.class, 0);
        assertEquals(1, w.getConstituents().size());

        LexicalItemConstituent c = w.getConstituents(0);
        assertNotNull(c.getConstituentType());
        assertEquals("telling", c.getCoveredText());
        assertEquals("tell_wn_n2", c.getId());

        r = JCasUtil.selectByIndex(j, WSDResult.class, 0);
        assertEquals(1, r.getSenses().size());
        assertEquals("tell%2:32:04::", r.getSenses(0).getId());

        assertFalse(i.hasNext());

    }
}
