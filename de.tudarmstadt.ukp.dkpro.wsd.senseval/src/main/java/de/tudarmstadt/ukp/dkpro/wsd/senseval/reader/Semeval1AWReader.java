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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.dom4j.Element;
import org.dom4j.Node;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * Semeval1AWReader reads the XML data sets for the Semeval-1 all-words
 * tasks.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class Semeval1AWReader
	extends SensevalAWReader
{
    private static final String SENTENCE_ELEMENT_NAME = "sentence";
	private static final String HEAD_ELEMENT_NAME = "instance";

	@SuppressWarnings("unchecked")
	@Override
	public void getNext(JCas jCas)
		throws IOException, CollectionException
	{
		int offset = 0, numSentences = 0;
		String s = "";
		Element text = textIterator.next();

		for (Iterator<Element> sentenceIterator = text
				.elementIterator(SENTENCE_ELEMENT_NAME); sentenceIterator
				.hasNext();) {

			Element sentence = sentenceIterator.next();
			Sentence sentenceAnnotation = new Sentence(jCas);
			sentenceAnnotation.setBegin(offset);

			for (Iterator<Node> nodeIterator = sentence.nodeIterator(); nodeIterator
					.hasNext();) {

				Node node = nodeIterator.next();
				String nodeText = node.getText().replace('\n', ' ');

				// If the node is a head, create a LexicalItemConstituent and a
				// WSDItem
				if (node.getName() != null && node.getName().equals(HEAD_ELEMENT_NAME)) {
					Element head = (Element) node;
					String id = head.attributeValue(ID_ATTRIBUTE_NAME);

					LexicalItemConstituent c = newLexicalItemConstituent(jCas,
							id, LIC_TYPE_HEAD, offset, nodeText.length());
					WSDItem w = newWsdItem(jCas, id, LIC_TYPE_HEAD, offset,
							nodeText.length(), head
									.attributeValue(POS_ATTRIBUTE_NAME), head
									.attributeValue(LEMMA_ATTRIBUTE_NAME));
					w.setConstituents(new FSArray(jCas, 1));
					w.setConstituents(0, c);
				}
				else if (node.getName() != null ){
					throw new CollectionException("unknown_element",
							new Object[] { node.getName() });
				}

				offset += nodeText.length();
				s += nodeText;
			}

			sentenceAnnotation.setEnd(offset);
			sentenceAnnotation.addToIndexes();
			numSentences++;
		}

		// The Semeval-1 DTD requires each text to have at least one sentence
		if (numSentences == 0) {
            throw new CollectionException("element_not_found", new Object[] {
                    SENTENCE_ELEMENT_NAME, TEXT_ELEMENT_NAME });
		}

		jCas.setDocumentText(s);

		try {
            setDocumentMetadata(jCas, text.attributeValue(ID_ATTRIBUTE_NAME));
        }
        catch (URISyntaxException e) {
            throw new IOException(e);
        }

		textCount++;
	}
}
