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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.dom4j.Element;
import org.dom4j.Node;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * Semeval2AWReader reads the XML data sets for the Semeval-2 all-words
 * tasks.  Currently it does not handle component elements.
 * 
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class Semeval2AWReader
	extends SensevalAWReader
{
	private static final String SENTENCE_ELEMENT_NAME = "s";
	private static final String HEAD_ELEMENT_NAME = "head";

	@SuppressWarnings("unchecked")
	@Override
	public void getNext(JCas jCas)
		throws IOException, CollectionException
	{
		int offset = 0;
		String s = "";
		Element text = textIterator.next();

		for (Iterator<Element> sentenceIterator = text.elementIterator(SENTENCE_ELEMENT_NAME); sentenceIterator.hasNext();) {

			Map<String, WSDItem> wsdItems = new HashMap<String, WSDItem>();
			Map<String, LexicalItemConstituent> lics = new HashMap<String, LexicalItemConstituent>();
			Map<String, String> sats = new HashMap<String, String>();
			Element sentence = sentenceIterator.next();
            Sentence sentenceAnnotation = new Sentence(jCas);
            sentenceAnnotation.setBegin(offset);

            // Loop over all nodes to get the document text in order
			for (Iterator<Node> nodeIterator = sentence.nodeIterator(); nodeIterator.hasNext();) {

				Node node = nodeIterator.next();
				String nodeText = node.getText().replace('\n', ' ');
				String nodeName = node.getName();
				
				if (nodeName == null) {
	                offset += nodeText.length();
	                s += nodeText;
	                continue;
				}
				
				// If the node is a satellite, create a LexicalItemConstituent
				if (nodeName.equals(SATELLITE_ELEMENT_NAME)) {
					String id = ((Element)node).attributeValue(ID_ATTRIBUTE_NAME);
					lics.put(id, newLexicalItemConstituent(jCas, id, LIC_TYPE_SATELLITE, offset, nodeText.length()));
				}
				
				// If the node is a head, create a LexicalItemConstituent and a WSDItem
				else if (nodeName.equals(HEAD_ELEMENT_NAME)) {
					Element head = (Element) node;
					String id = head.attributeValue(ID_ATTRIBUTE_NAME);
					String satellites = head.attributeValue(SATELLITES_ATTRIBUTE_NAME);

					lics.put(id, newLexicalItemConstituent(jCas, id, LIC_TYPE_HEAD, offset, nodeText.length()));
					wsdItems.put(id, newWsdItem(jCas, id, LIC_TYPE_HEAD, offset, nodeText.length(), 
							head.attributeValue(POS_ATTRIBUTE_NAME), head.attributeValue(LEMMA_ATTRIBUTE_NAME)));

					if (satellites != null)
						sats.put(id, satellites);
				}
				
				// If the node is any other element, something is wrong
				else if (node.getNodeTypeName().equals("Entity") == false) {
					throw new CollectionException("unknown_element", new Object[]{node.getName()});
				}

				offset += nodeText.length();
				s += nodeText;
			}

			// Add a sentence annotation
			sentenceAnnotation.setEnd(offset);
			sentenceAnnotation.addToIndexes();

			populateLexicalItemConstituents(jCas, wsdItems, lics, sats);
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
