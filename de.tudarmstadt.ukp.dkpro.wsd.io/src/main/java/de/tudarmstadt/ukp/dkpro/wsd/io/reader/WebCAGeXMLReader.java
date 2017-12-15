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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * A collection reader the WebCAGe 1.0 corpus.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class WebCAGeXMLReader
    extends JCasResourceCollectionReader_ImplBase
{
    public static final String DISAMBIGUATION_METHOD_NAME = "WebCAGe";

    private static final String ELEMENT_CORPUS = "corpus";
    private static final String ELEMENT_TEXT = "text";
    private static final String ELEMENT_HEAD = "head";
    private static final String ELEMENT_SAT = "sat";
    private static final String ATTR_LANG = "lang";
    private static final String ATTR_ID = "id";
    private static final String ATTR_SRC = "src";
    // private static final String ATTR_TITLE = "title";
    // private static final String ATTR_AUTHOR = "author";
    // private static final String ATTR_REFS = "refs";
    private static final String ATTR_LEMMA = "lemma";
    private static final String ATTR_LUIDS = "luids";
    private static final String ATTR_POS = "pos";

    private final Logger logger = Logger.getLogger(getClass());

    public static final String PARAM_SENSE_INVENTORY = "senseInventory";
    @ConfigurationParameter(name = PARAM_SENSE_INVENTORY, mandatory = false, description = "The sense inventory used by the answer key", defaultValue = "GermaNet_8.0")
    private String senseInventory;

    protected Element corpus = null;
    protected Iterator<Element> textIterator = null;

    @Override
    @SuppressWarnings("unchecked")
    public void getNext(JCas jCas)
        throws IOException, CollectionException
    {
        if (textIterator == null || textIterator.hasNext() == false) {
            // Open the next file
            Document document;
            SAXReader reader = new SAXReader();
            NullEntityResolver resolver = new NullEntityResolver();
            reader.setEntityResolver(resolver);
            Resource nextFile = nextFile();

            logger.info("Reading " + nextFile.getLocation());
            InputStream is = new BufferedInputStream(nextFile.getInputStream());
            try {
                document = reader.read(is);
            }
            catch (DocumentException e) {
                throw new CollectionException(e);
            }

            // Get metadata from the top two elements
            corpus = document.getRootElement();
            if (corpus.getName().equals(ELEMENT_CORPUS) == false) {
                throw new CollectionException("unknown_element",
                        new Object[] { corpus.getName() });
            }
            textIterator = corpus.elementIterator(ELEMENT_TEXT);
            if (textIterator.hasNext() == false) {
                throw new CollectionException("element_not_found",
                        new Object[] { ELEMENT_TEXT, ELEMENT_CORPUS });
            }
        }
        Element text = textIterator.next();

        setDocumentMetadata(jCas, corpus, text);

        // Process document text
        StringBuffer documentText = processText(jCas, text);
        jCas.setDocumentText(documentText.toString());
    }

    @Override
    public boolean hasNext()
        throws IOException, CollectionException
    {
        return textIterator != null && textIterator.hasNext()
                || super.hasNext();
    }

    @SuppressWarnings("unchecked")
    private StringBuffer processText(JCas jCas, Element text)
        throws CollectionException
    {
        StringBuffer documentText = new StringBuffer();
        int offset = 0;

        // Loop over all nodes to get the document text in order
        for (Iterator<Node> nodeIterator = text.nodeIterator(); nodeIterator
                .hasNext();) {

            Node node = nodeIterator.next();
            String nodeText = node.getText().replace('\n', ' ');
            String nodeName = node.getName();

            // TODO: For now we ignore satellites. We should add support for
            // them.
            if (nodeName == null || nodeName.equals(ELEMENT_SAT)) {
                offset += nodeText.length();
                documentText.append(nodeText);
                continue;
            }

            // If the node is a head, create a LexicalItemConstituent and a
            // WSDItem
            else if (nodeName.equals(ELEMENT_HEAD)) {
                Element head = (Element) node;
                String headId = head.attributeValue(ATTR_ID);
                String lemma = head.attributeValue(ATTR_LEMMA);

                logger.trace("Reading instance " + headId);

                // Skip word forms without a POS
                String pos = head.attributeValue(ATTR_POS);
                if (pos == null) {
                    logger.warn("No POS provided for " + headId + "; skipping");
                    continue;
                }
                try {
                    pos = webCAGePosToPOS(pos).toString();
                }
                catch (IllegalArgumentException e) {
                    logger.warn("Unrecognized POS " + pos + " provided for "
                            + headId + "; skipping");
                    continue;
                }

                // Create the necessary WSDItem and LexicalItemConstituent
                // annotations for this word form
                LexicalItemConstituent c = newLexicalItemConstituent(jCas,
                        headId, ELEMENT_HEAD, offset, nodeText.length());
                WSDItem w = newWsdItem(jCas, headId, offset, nodeText.length(),
                        pos, lemma);
                w.setConstituents(new FSArray(jCas, 1));
                w.setConstituents(0, c);

                // Get an array of lexical unit IDs (LUIDs). LUIDs are found
                // in the luids attribute and are separated with
                // # characters.
                String luids[] = head.attributeValue(ATTR_LUIDS).split("#");
                FSArray senseArray = new FSArray(jCas, luids.length);
                for (int i = 0; i < luids.length; i++) {
                    Sense sense = new Sense(jCas);
                    sense.setId(luids[i].substring(1));
                    sense.setConfidence(1.0);
                    sense.addToIndexes();
                    senseArray.set(i, sense);
                }

                WSDResult wsdResult = new WSDResult(jCas);
                wsdResult.setWsdItem(w);
                wsdResult.setSenses(senseArray);
                wsdResult.setSenseInventory(senseInventory);
                wsdResult.setDisambiguationMethod(DISAMBIGUATION_METHOD_NAME);
                wsdResult.addToIndexes();

            }

            // If the node is any other element, something is wrong
            else if (node.getNodeTypeName().equals("Entity") == false) {
                throw new CollectionException("unknown_element",
                        new Object[] { node.getName() });
            }

            offset += nodeText.length();
            documentText.append(nodeText);
        }
        return documentText;
    }

    /**
     * Sets the metadata of the current document.
     *
     * @param jCas
     * @throws CollectionException
     */
    private void setDocumentMetadata(JCas jCas, Element corpus, Element text)
        throws CollectionException
    {
        DocumentMetaData d = DocumentMetaData.create(jCas);
        String language = corpus.attributeValue(ATTR_LANG);
        if (language == null) {
            throw new CollectionException("required_attribute_missing",
                    new Object[] { ATTR_LANG, ELEMENT_CORPUS });
        }

        String id = text.attributeValue(ATTR_ID);
        if (id == null) {
            throw new CollectionException("required_attribute_missing",
                    new Object[] { ATTR_ID, ELEMENT_TEXT });
        }

        String uri = text.attributeValue(ATTR_SRC);
        if (uri != null) {
            d.setDocumentUri(uri);
        }

        d.setDocumentId(id);
        // d.setDocumentUri(contextFiles[textCount].toURI().toString());
        // d.setCollectionId("WebCAGe_prerelease");
        d.setLanguage(language);
        jCas.setDocumentLanguage(language);
    }

    /**
     * Creates a new LexicalItemConstituent annotation and adds it to the
     * annotation index.
     *
     * @param jCas
     *            The CAS in which to create the annotation.
     * @param id
     *            An identifier for the annotation.
     * @param constituentType
     *            The constituent type (e.g., "head", "satellite").
     * @param offset
     *            The index of the first character of the annotation in the
     *            document.
     * @param length
     *            The length, in characters, of the annotation.
     * @return The new annotation.
     */
    protected LexicalItemConstituent newLexicalItemConstituent(JCas jCas,
            String id, String constituentType, int offset, int length)
    {
        LexicalItemConstituent c = new LexicalItemConstituent(jCas);
        c.setBegin(offset);
        c.setEnd(offset + length);
        c.setConstituentType(constituentType);
        c.setId(id);
        c.addToIndexes();
        return c;
    }

    /**
     * Creates a new WSDItem annotation and adds it to the annotation index.
     *
     * @param jCas
     *            The CAS in which to create the annotation.
     * @param id
     *            An identifier for the annotation.
     * @param offset
     *            The index of the first character of the annotation in the
     *            document.
     * @param length
     *            The length, in characters, of the annotation.
     * @param pos
     *            The part of speech, if known, otherwise null.
     * @param lemma
     *            The lemmatized form, if known, otherwise null.
     * @return The new annotation.
     */
    protected WSDItem newWsdItem(JCas jCas, String id, int offset, int length,
            String pos, String lemma)
    {
        WSDItem w = new WSDItem(jCas);
        w.setBegin(offset);
        w.setEnd(offset + length);
        w.setId(id);
        if (pos == null) {
            w.setPos(null);
        }
        else {
            w.setPos(pos);
        }
        w.setSubjectOfDisambiguation(lemma);
        w.addToIndexes();
        return w;
    }

    protected POS webCAGePosToPOS(String pos)
    {
        if (pos == null) {
            throw new IllegalArgumentException();
        }
        if (pos.equals("a")) {
            return POS.ADJ;
        }
        else if (pos.equals("n")) {
            return POS.NOUN;
        }
        else if (pos.equals("v")) {
            return POS.VERB;
        }
        else {
            throw new IllegalArgumentException("Unrecognized POS: " + pos);
        }
    }
}
