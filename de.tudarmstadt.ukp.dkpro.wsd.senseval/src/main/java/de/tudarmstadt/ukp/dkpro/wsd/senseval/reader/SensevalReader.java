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
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * SensevalReader is the abstract base class for all collection readers which
 * read the XML data sets for the Senseval and Semeval all-words and lexical
 * sample tasks.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public abstract class SensevalReader
    extends JCasCollectionReader_ImplBase
{
    public static final String LIC_TYPE_HEAD = "head";
    public static final String LIC_TYPE_SATELLITE = "satellite";

    public static final String PARAM_IGNORE_MISSING_SATELLITES = "ignoreMissingSatellites";
    @ConfigurationParameter(name = PARAM_IGNORE_MISSING_SATELLITES, mandatory = false, description = "Whether to ignore missing satellites", defaultValue = "false")
    protected boolean ignoreMissingSatellites;

    public static final String PARAM_FILE = "fileName";
    @ConfigurationParameter(name = PARAM_FILE, mandatory = true, description = "The Semeval/Senseval XML file to read")
    protected String fileName;
    protected URL fileURL;

    protected static final String CORPUS_ELEMENT_NAME = "corpus";
    protected static final String SATELLITE_ELEMENT_NAME = "sat";

    protected static final String ID_ATTRIBUTE_NAME = "id";
    protected static final String LANG_ATTRIBUTE_NAME = "lang";
    protected static final String POS_ATTRIBUTE_NAME = "pos";
    protected static final String SATELLITES_ATTRIBUTE_NAME = "sats";
    protected static final String LEMMA_ATTRIBUTE_NAME = "lemma";

    protected Element corpus;
    protected int textCount, numTexts;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        textCount = 0;

        // EntityResolver resolver = new EntityResolver() {
        // public InputSource resolveEntity(String publicId, String systemId) {
        // try {
        // URL url;
        // if (publicId == null) {
        // url = ResourceUtils.resolveLocation(systemId, this, null);
        // }
        // else {
        // url = ResourceUtils.resolveLocation(publicId, this, null);
        // }
        // return new InputSource(url.openStream());
        // } catch (IOException e) {
        // e.printStackTrace();
        // return null;
        // }
        // }
        // };
        Document documentCollection = null;
        SAXReader reader = new SAXReader();

        // TODO: We can't figure out how to get the XML parser to read DTDs in
        // all cases (i.e., whether they are in a directory or in a JAR) so the
        // following code just forces the SAXReader to ignore DTDs.  This is
        // not an optimal solution as it prevents the XML files from being
        // validated.
        EntityResolver resolver = new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) {
                return new InputSource(new StringReader(""));
            }
        };
        reader.setEntityResolver(resolver);

        InputStream is = null;
        try {
            fileURL = ResourceUtils.resolveLocation(fileName, this, context);
            is = fileURL.openStream();
            // The following line fails on Jenkins but not locally
            // documentCollection = reader.read(fileURL.getFile());
            documentCollection = reader.read(is);
        }
        catch (DocumentException e) {
            throw new ResourceInitializationException(e);
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        finally {
            IOUtils.closeQuietly(is);
        }

        // Get the (root) corpus element so we can iterate over its elements
        corpus = documentCollection.getRootElement();
        if (corpus.getName().equals(CORPUS_ELEMENT_NAME) == false) {
            throw new ResourceInitializationException("unknown_element",
                    new Object[] { corpus.getName() });
        }
    }

    @Override
    public Progress[] getProgress()
    {
        return new Progress[] { new ProgressImpl(textCount, numTexts,
                Progress.ENTITIES) };
    }

    /**
     * Sets the metadata of the current document.
     *
     * @param jCas
     * @param documentId
     *            An identifier for the current document.
     * @throws URISyntaxException
     */
    protected void setDocumentMetadata(JCas jCas, String documentId)
        throws URISyntaxException
    {
        DocumentMetaData docMetaData = DocumentMetaData.create(jCas);
        docMetaData.setDocumentId(documentId);
        docMetaData.setDocumentUri(fileURL.toURI().toString() + "#"
                + documentId);
        docMetaData.setCollectionId(fileURL.toURI().toString());
        docMetaData.setLanguage(corpus.attributeValue(LANG_ATTRIBUTE_NAME));
        jCas.setDocumentLanguage(corpus.attributeValue(LANG_ATTRIBUTE_NAME));
    }

    /**
     * Sets the constituents of a WSDItem.
     *
     * @param jCas
     * @param wsdItems
     *            A map of WSDItem identifiers to WSDItems.
     * @param lics
     *            A map of LexicalItemConstituent identifiers to
     *            LexicalItemConstituents.
     * @param sats
     *            A map of WSDItem identifiers (the heads) to strings containing
     *            a space-separated list of WSDItem identifiers (the
     *            satellites).
     */
    protected void populateLexicalItemConstituents(JCas jCas,
            Map<String, WSDItem> wsdItems,
            Map<String, LexicalItemConstituent> lics, Map<String, String> sats)
        throws CollectionException
    {
        // For each WSDItem, populate its array of lexical constituents
        for (String headId : wsdItems.keySet()) {
            WSDItem wsdItemAnnotation = wsdItems.get(headId);

            // Case 1: The head has no satellites
            if (!sats.containsKey(headId)) {
                wsdItemAnnotation.setConstituents(new FSArray(jCas, 1));
                wsdItemAnnotation.setConstituents(0, lics.get(headId));
                continue;
            }

            // Case 2: The head has a list of satellites
            String satIds[] = sats.get(headId).split(" ");
            int numSatellites = 0;

            for (String satId : satIds) {
                if (lics.get(satId) == null) {
                    if (ignoreMissingSatellites) {
                        getLogger().warn("Can't find satellite " + satId);
                    }
                    else {
                        getLogger().error("Can't find satellite " + satId);
                        throw new CollectionException();
                    }
                }
                else {
                    numSatellites++;
                }
            }

            wsdItemAnnotation.setConstituents(new FSArray(jCas,
                    1 + numSatellites));
            wsdItemAnnotation.setConstituents(0, lics.get(headId));

            int i = 1;
            for (String satId : satIds) {
                if (lics.get(satId) != null) {
                    wsdItemAnnotation.setConstituents(i++, lics.get(satId));
                }
            }
        }
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
     * @param constituentType
     *            A string representing the constituent type (e.g., "head",
     *            "satellite").
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
    protected WSDItem newWsdItem(JCas jCas, String id, String constituentType,
            int offset, int length, String pos, String lemma)
    {
        WSDItem w = new WSDItem(jCas);
        w.setBegin(offset);
        w.setEnd(offset + length);
        w.setId(id);
        if (pos == null) {
            w.setPos(null);
        }
        else {
            w.setPos(sensevalPosToPOS(pos).toString());
        }
        w.setSubjectOfDisambiguation(lemma);
        w.addToIndexes();
        return w;
    }

    protected POS sensevalPosToPOS(String pos)
    {
        if (pos == null) {
            return null;
        }
        if (pos.equals("a")) {
            return POS.ADJ;
        }
        else if (pos.equals("r")) {
            return POS.ADV;
        }
        else if (pos.equals("v")) {
            return POS.VERB;
        }
        else if (pos.equals("n")) {
            return POS.NOUN;
        }
        else {
            return null;
        }
    }
}
