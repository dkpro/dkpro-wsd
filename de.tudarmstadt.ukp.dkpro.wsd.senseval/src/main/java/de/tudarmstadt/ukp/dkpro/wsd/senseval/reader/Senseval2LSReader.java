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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * Senseval2LSReader reads the XML data sets for the Senseval-2 lexical sample
 * tasks.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class Senseval2LSReader
    extends SensevalReader
{
    protected static final String LEXELT_ELEMENT_NAME = "lexelt";
    protected static final String INSTANCE_ELEMENT_NAME = "instance";
    protected static final String ANSWER_ELEMENT_NAME = "answer";
    protected static final String CONTEXT_ELEMENT_NAME = "context";
    protected static final String HEAD_ELEMENT_NAME = "head";

    protected static final String POS_ATTRIBUTE_UNKNOWN_VALUE = "unk";
    protected static final String ITEM_ATTRIBUTE_NAME = "item";
    protected static final String DOCSRC_ATTRIBUTE_NAME = "docsrc";
    protected static final String TOPIC_ATTRIBUTE_NAME = "topic";
    protected static final String INSTANCE_ATTRIBUTE_NAME = "instance";
    protected static final String SENSEID_ATTRIBUTE_NAME = "senseid";

    public static final String PARAM_GUESS_POS = "guessPOS";
    @ConfigurationParameter(name = PARAM_GUESS_POS, mandatory = false, description = "Attempt to guess the POS from the item attribute of the lexelt element", defaultValue = "true")
    protected boolean guessPOS;

    public static final String PARAM_GUESS_LEMMA = "guessLemma";
    @ConfigurationParameter(name = PARAM_GUESS_LEMMA, mandatory = false, description = "Attempt to guess the lemma from the item attribute of the lexelt element", defaultValue = "true")
    protected boolean guessLemma;

    private String lexeltPOS;
    private String lexeltLemma;
    private static final Pattern lexeltPattern = Pattern.compile("(.*)\\.(.)$");
    private Element lexelt;
    private Iterator<Element> lexeltIterator, instanceIterator;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        numTexts = corpus.elements(LEXELT_ELEMENT_NAME).size();
        if (numTexts < 1) {
            throw new ResourceInitializationException("element_not_found",
                    new Object[] { LEXELT_ELEMENT_NAME, CORPUS_ELEMENT_NAME });
        }

        // Find the first <instance> of the first <lexelt>
        lexeltIterator = corpus.elementIterator(LEXELT_ELEMENT_NAME);
        lexelt = lexeltIterator.next();
        lexeltPOS = getLexeltPOS(lexelt.attributeValue(ITEM_ATTRIBUTE_NAME));
        lexeltLemma = getLexeltLemma(lexelt.attributeValue(ITEM_ATTRIBUTE_NAME));
        instanceIterator = lexelt.elementIterator(INSTANCE_ELEMENT_NAME);
    }

    private String getLexeltLemma(String item)
    {
        if (guessLemma == true) {
            Matcher m = lexeltPattern.matcher(item);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    private String getLexeltPOS(String item)
    {
        String pos = lexelt.attributeValue(POS_ATTRIBUTE_NAME);
        if ((pos == null || pos.equals(POS_ATTRIBUTE_UNKNOWN_VALUE))
                && guessPOS == true) {
            Matcher m = lexeltPattern.matcher(item);
            if (m.find()) {
                pos = m.group(2);
            }
        }
        return pos;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getNext(JCas jCas)
        throws IOException, CollectionException
    {
        // If there are no more <instance>s in this <lexelt>, get the next
        // <lexelt>
        if (instanceIterator.hasNext() == false) {
            lexelt = lexeltIterator.next();
            lexeltPOS = getLexeltPOS(lexelt.attributeValue(ITEM_ATTRIBUTE_NAME));
            lexeltLemma = getLexeltLemma(lexelt
                    .attributeValue(ITEM_ATTRIBUTE_NAME));
            textCount++;
            instanceIterator = lexelt.elementIterator(INSTANCE_ELEMENT_NAME);
        }

        Element instance = instanceIterator.next();
        Element context = instance.element(CONTEXT_ELEMENT_NAME);
        int offset = 0;
        String s = "";
        Map<String, WSDItem> wsdItems = new HashMap<String, WSDItem>();
        Map<String, LexicalItemConstituent> lics = new HashMap<String, LexicalItemConstituent>();
        Map<String, String> sats = new HashMap<String, String>();

        // Loop over all nodes to get the document text in order
        for (Iterator<Node> nodeIterator = context.nodeIterator(); nodeIterator
                .hasNext();) {

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
                String id = ((Element) node).attributeValue(ID_ATTRIBUTE_NAME);
                lics.put(
                        id,
                        newLexicalItemConstituent(jCas, id, LIC_TYPE_SATELLITE,
                                offset, nodeText.length()));
            }

            // If the node is a head, create a LexicalItemConstituent and a
            // WSDItem
            else if (nodeName.equals(HEAD_ELEMENT_NAME)) {
                String id = instance.attributeValue(ID_ATTRIBUTE_NAME);
                String satellites = ((Element) node)
                        .attributeValue(SATELLITES_ATTRIBUTE_NAME);

                lics.put(
                        id,
                        newLexicalItemConstituent(jCas, id, LIC_TYPE_HEAD,
                                offset, nodeText.length()));
                wsdItems.put(
                        id,
                        newWsdItem(jCas, id, LIC_TYPE_HEAD, offset,
                                nodeText.length(), lexeltPOS, lexeltLemma));

                if (satellites != null) {
                    sats.put(id, satellites);
                }
            }

            // If the node is any other element, something is wrong
            else if (node.getNodeTypeName().equals("Entity") == false) {
                throw new CollectionException("unknown_element",
                        new Object[] { node.getName() });
            }

            offset += nodeText.length();
            s += nodeText;
        }

        populateLexicalItemConstituents(jCas, wsdItems, lics, sats);

        jCas.setDocumentText(s);

        try {
            setDocumentMetadata(jCas,
                    instance.attributeValue(ID_ATTRIBUTE_NAME));
        }
        catch (URISyntaxException e) {
            throw new IOException(e);
        }

    }

    @Override
    public boolean hasNext()
        throws IOException, CollectionException
    {
        if (instanceIterator.hasNext()) {
            return true;
        }
        else {
            return lexeltIterator.hasNext();
        }
    }

}
