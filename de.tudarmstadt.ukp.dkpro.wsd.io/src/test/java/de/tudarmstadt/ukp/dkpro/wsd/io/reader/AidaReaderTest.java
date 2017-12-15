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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;


public class AidaReaderTest {

	@Test
	public void aidaReaderTest() throws UIMAException, IOException {
		CollectionReader reader = createReader(
                AidaReader.class,
                AidaReader.PARAM_INPUT_PATH, "src/test/resources/aida_test.tsv");

        JCasIterator i = new JCasIterator(reader);

		assertTrue(i.hasNext());
		JCas jCas = i.next();
		testJCas(jCas,0);

		assertTrue(i.hasNext());
		jCas = i.next();
		testJCas(jCas,1);

		assertTrue(i.hasNext());
		jCas = i.next();
		testJCas(jCas,2);

		assertFalse(i.hasNext());

	}

	private void testJCas(JCas jCas, int i) {
		List<String> docIds = new ArrayList<String>();
		docIds.add("1_EU");
		docIds.add("2_Rare");
		docIds.add("3_China");

		assertEquals("src/test/resources/aida_test.tsv", DocumentMetaData.get(jCas).getCollectionId());
		assertTrue(docIds.contains(DocumentMetaData.get(jCas).getDocumentId()));

		if(i==0){
			String euStart = "EU rejects German call to boycott British lamb . \nPeter Blackburn \nBRUSSELS 1996-08-22 \nThe Europ";
			assertTrue(jCas.getDocumentText().startsWith(euStart));
		}

		for(WSDItem wsdItem : JCasUtil.select(jCas, WSDItem.class)){
			assertEquals(wsdItem.getSubjectOfDisambiguation().replaceAll(" ", ""), wsdItem.getCoveredText().replaceAll(" ", ""));
		}


	}
}
