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
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * A collection reader for <a href=
 * "http://nltk.googlecode.com/svn/trunk/nltk_data/packages/corpora/semcor.zip"
 * >NLTK's XML conversion of the SemCor pseudo-SGML</a>.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class SemCorXMLReader
    extends JCasResourceCollectionReader_ImplBase
{
    public static final String DISAMBIGUATION_METHOD_NAME = SemCorXMLReader.class
            .getName();

    private final static Logger logger = Logger.getLogger(SemCorXMLReader.class
            .getName());

    private static final String ELEMENT_CONTEXT = "context";
    private static final String ELEMENT_CONTEXTFILE = "contextfile";
    private static final String ELEMENT_PARAGRAPH = "p";
    private static final String ELEMENT_SENTENCE = "s";
    private static final String ELEMENT_PUNCTUATION = "punc";
    private static final String ELEMENT_WORDFORM = "wf";
    private static final String ATTR_FILENAME = "filename";
    private static final String ATTR_CONCORDANCE = "concordance";
    private static final String ATTR_CMD = "cmd";
    // private static final String ATTR_DC = "dc";
    private static final String ATTR_ID = "id";
    private static final String ATTR_LEMMA = "lemma";
    private static final String ATTR_LEXSN = "lexsn";
    private static final String ATTR_OT = "ot";
    // private static final String ATTR_PARAS = "paras";
    // private static final String ATTR_PN = "pn";
    private static final String ATTR_PNUM = "pnum";
    private static final String ATTR_POS = "pos";
    // private static final String ATTR_RDF = "rdf";
    // private static final String ATTR_SEP = "sep";
    private static final String ATTR_SNUM = "snum";
    private static final String ATTR_WNSN = "wnsn";
    private static final String VAL_DONE = "done";

    public static final String PARAM_SENSE_INVENTORY = "senseInventory";
    @ConfigurationParameter(name = PARAM_SENSE_INVENTORY, mandatory = false, description = "The sense inventory used by the answer key", defaultValue = "WordNet_3.0_sensekey")
    private String senseInventory;

    public static final String PARAM_SKIP_UNDONE = "skipUndone";
    @ConfigurationParameter(name = PARAM_SKIP_UNDONE, mandatory = false, description = "Whether to skip word forms not marked as 'done'", defaultValue = "true")
    private boolean skipUndone;

    public static final String PARAM_SKIP_UNASSIGNABLE = "skipUnassignable";
    @ConfigurationParameter(name = PARAM_SKIP_UNASSIGNABLE, mandatory = false, description = "Whether to skip unassignable word forms (i.e., where the ot attribute is present)", defaultValue = "true")
    private boolean skipUnassignable;

    public static final String PARAM_SKIP_WITHOUT_WNSN = "skipWithoutWnsn";
    @ConfigurationParameter(name = PARAM_SKIP_WITHOUT_WNSN, mandatory = false, description = "Whether to skip word forms where the wnsn attribute does not contain a positive integer", defaultValue = "true")
    private boolean skipWithoutWnsn;

    public static final String PARAM_SKIP_WITHOUT_LEMMA = "skipWithoutLemma";
    @ConfigurationParameter(name = PARAM_SKIP_WITHOUT_LEMMA, mandatory = false, description = "Whether to skip word forms without a lemma", defaultValue = "true")
    private boolean skipWithoutLemma;

    public static final String PARAM_SKIP_WITHOUT_POS = "skipWithoutPos";
    @ConfigurationParameter(name = PARAM_SKIP_WITHOUT_POS, mandatory = false, description = "Whether to skip word forms without a POS tag", defaultValue = "true")
    private boolean skipWithoutPos;

    public static final String PARAM_WRITE_CORE_ANNOTATIONS = "writeCoreAnnotations";
    @ConfigurationParameter(name = PARAM_WRITE_CORE_ANNOTATIONS, mandatory = false, description = "Whether the reader should write core annotations like Token, Lemma, and POS (default=true)", defaultValue = "true")
    private boolean shouldWriteCoreAnnotations;

    private int validWordFormCount;
    private int totalWordFormCount;

    private MappingProvider mappingProvider;

    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);
        validWordFormCount = 0;
        totalWordFormCount = 0;

        // FIXME brown tagset provider is only available from DKPro core 1.6.0 onwards
        // once released, this should be changed back and tagmapping should be removed from the resources here
        mappingProvider = new MappingProvider();
//        mappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/de/tudarmstadt/ukp/dkpro/" +
//                "core/api/lexmorph/tagset/en-brown-pos.map");
        mappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/tagmapping/en-brown-pos.map");
        mappingProvider.setDefault(MappingProvider.BASE_TYPE, POS.class.getName());
        mappingProvider.setDefault("pos.tagset", "default");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getNext(JCas jCas)
        throws IOException, CollectionException
    {
        try {
            mappingProvider.configure(jCas.getCas());
        }
        catch (AnalysisEngineProcessException e) {
            throw new CollectionException(e);
        }

        // Open the next file
        Document document;
        SAXReader reader = new SAXReader();
        NullEntityResolver resolver = new NullEntityResolver();
        reader.setEntityResolver(resolver);
        InputStream is = new BufferedInputStream(nextFile().getInputStream());
        try {
            document = reader.read(is);
        }
        catch (DocumentException e) {
            throw new CollectionException(e);
        }

        // Get metadata from the top two elements
        Element contextFile = document.getRootElement();
        if (contextFile.getName().equals(ELEMENT_CONTEXTFILE) == false) {
            throw new CollectionException("unknown_element",
                    new Object[] { contextFile.getName() });
        }
        Iterator<Element> contextIterator = contextFile
                .elementIterator(ELEMENT_CONTEXT);
        if (contextIterator.hasNext() == false) {
            throw new CollectionException("element_not_found", new Object[] {
                    ELEMENT_CONTEXT, ELEMENT_CONTEXTFILE });
        }
        Element context = contextIterator.next();
        setDocumentMetadata(jCas, contextFile, context);
        String documentId = context.attributeValue(ATTR_FILENAME);
        logger.debug("Found context filename: " + documentId);

        // Process document text
        StringBuffer documentText = processParagraphs(jCas, context, documentId);
        if (documentText.length() == 0) {
            documentText = processSentences(jCas, context, 0, documentId);
        }
        jCas.setDocumentText(documentText.toString());
        logger.info("Read " + validWordFormCount
                + " valid word forms; skipped "
                + (totalWordFormCount - validWordFormCount));
    }

    @SuppressWarnings("unchecked")
    private StringBuffer processParagraphs(JCas jCas, Element element,
            String idPrefix)
        throws CollectionException
    {
        StringBuffer paragraphText = new StringBuffer();
        for (Iterator<Element> paragraphIterator = element
                .elementIterator(ELEMENT_PARAGRAPH); paragraphIterator
                .hasNext();) {
            Element paragraph = paragraphIterator.next();
            String paragraphId = paragraph.attributeValue(ATTR_PNUM);
            Paragraph paragraphAnnotation = new Paragraph(jCas);
            paragraphAnnotation.setBegin(paragraphText.length());

            paragraphText.append(processSentences(jCas, paragraph,
                    paragraphText.length(), idPrefix + ".p" + paragraphId));

            paragraphAnnotation.setEnd(paragraphText.length());
            paragraphAnnotation.addToIndexes();
        }
        return paragraphText;
    }

    @SuppressWarnings("unchecked")
    private StringBuffer processSentences(JCas jCas, Element element,
            int offset, String idPrefix)
        throws CollectionException
    {
        StringBuffer sentenceText = new StringBuffer();
        for (Iterator<Element> sentenceIterator = element
                .elementIterator(ELEMENT_SENTENCE); sentenceIterator.hasNext();) {
            Element sentence = sentenceIterator.next();
            Sentence sentenceAnnotation = new Sentence(jCas);
            sentenceAnnotation.setBegin(offset);
            String sentenceId = sentence.attributeValue(ATTR_SNUM);
            int wordFormCount = 0;

            for (Iterator<Node> nodeIterator = sentence.nodeIterator(); nodeIterator
                    .hasNext();) {
                Node node = nodeIterator.next();
                String nodeText = node.getText().replace('\n', ' ');
                int oldOffset = offset;
                offset += nodeText.length();
                sentenceText.append(nodeText);

                if (node.getName() == null) {
                    continue;
                }

                if (node.getName().equals(ELEMENT_PUNCTUATION)) {
                    logger.trace("Found punctuation " + node.getText());
                    continue;
                }

                if (node.getName().equals(ELEMENT_WORDFORM) == false) {
                    throw new CollectionException("unknown_element",
                            new Object[] { node.getName() });
                }

                // Find or construct a unique ID for this word form
                wordFormCount++;
                totalWordFormCount++;
                Element wordForm = (Element) node;
                String wordFormId = wordForm.attributeValue(ATTR_ID);
                if (wordFormId == null) {
                    wordFormId = idPrefix + ".s" + sentenceId + ".w"
                            + wordFormCount;
                }
                logger.trace("Found wf id: " + wordFormId);

                String lemma = wordForm.attributeValue(ATTR_LEMMA);
                String pos = wordForm.attributeValue(ATTR_POS);

                // write DKPro Core annotations Token, Lemma, and POS
                if (shouldWriteCoreAnnotations) {
                    Lemma lemmaAnno = null;
                    if (lemma != null) {
                        lemmaAnno = new Lemma(jCas, offset, oldOffset + nodeText.length());
                        lemmaAnno.setValue(lemma);
                        lemmaAnno.addToIndexes();
                    }

                    de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS posAnno = null;
                    if (pos != null) {
                        Type posTag = mappingProvider.getTagType(pos);
                        posAnno = (de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS) jCas.getCas().createAnnotation(posTag, oldOffset, oldOffset + nodeText.length());
                        posAnno.setPosValue(pos);
                        posAnno.addToIndexes();
                    }

                    Token tokenAnno = new Token(jCas, oldOffset, oldOffset + nodeText.length());
                    tokenAnno.setLemma(lemmaAnno);
                    tokenAnno.setPos(posAnno);
                    tokenAnno.addToIndexes();
                }

                // Skip <wf> elements which are not marked as "done"
                if (skipUndone == true
                        && wordForm.attributeValue(ATTR_CMD).equals(VAL_DONE) == false) {
                    logger.debug("Skipping wf " + wordFormId
                            + ": not marked as 'done'");
                    continue;
                }

                // Skip <wf> elements for which semantic tags could not be
                // assigned
                if (skipUnassignable == true
                        && wordForm.attributeValue(ATTR_OT) != null) {
                    logger.debug("Skipping wf " + wordFormId + ": ot="
                            + wordForm.attributeValue(ATTR_OT));
                    continue;
                }

                // Find the number of valid sense tags for this word form.
                // Tags with a wnsn attribute value of "0"
                // (or "-1" according to some specifications) could not be
                // mapped and so are skipped.
                String wnsn = wordForm.attributeValue(ATTR_WNSN);
                if (skipWithoutWnsn == true && wnsn == null) {
                    logger.debug("Skipping wf " + wordFormId + ": no wnsn");
                    continue;
                }
                int totalValidWf = 0;
                String wnsns[] = wnsn.split(";");
                for (String s : wnsns) {
                    if (isValidWnsn(s)) {
                        totalValidWf++;
                    }
                }
                if (skipWithoutWnsn == true && totalValidWf == 0) {
                    logger.debug("Skipping wf " + wordFormId + ": wnsn="
                            + wordForm.attributeValue(ATTR_WNSN));
                    continue;
                }

                // Skip word forms without a lemma
                if (skipWithoutLemma == true && lemma == null) {
                    logger.warn("Sipping wf " + wordFormId + ": no lemma");
                    continue;
                }

                // Skip word forms without a POS
                if (skipWithoutPos == true && pos == null) {
                    logger.warn("Skipping " + wordFormId + ": no pos");
                    continue;
                }
                try {
                    pos = semCorPosToPOS(pos).toString();
                }
                catch (IllegalArgumentException e) {
                    logger.warn("Skipping wf " + wordFormId
                            + ": unrecognized pos=" + pos);
                    continue;
                }

                // Create the necessary WSDItem and LexicalItemConstituent
                // annotations for this word form
                LexicalItemConstituent c = newLexicalItemConstituent(jCas,
                        wordFormId, ELEMENT_WORDFORM, oldOffset,
                        nodeText.length());
                WSDItem w = newWsdItem(jCas, wordFormId, oldOffset,
                        nodeText.length(), pos, lemma);
                w.setConstituents(new FSArray(jCas, 1));
                w.setConstituents(0, c);

                // Get an array of sense tags. Sense tags are found
                // in the lexsn attribute and are separated with
                // semicolons. Sometimes the head_word field contains
                // a superfluous character in parentheses which must
                // be removed. (These quirks are not documented in
                // the SemCor file format specification.)
                String lexsns[] = wordForm.attributeValue(ATTR_LEXSN)
                        .replaceAll("\\(.\\)", "").split(";");
                FSArray senseArray = new FSArray(jCas, totalValidWf);
                int validWfCount = 0;
                for (int i = 0; i < lexsns.length; i++) {
                    if (isValidWnsn(wnsns[i])) {
                        Sense sense = new Sense(jCas);
                        sense.setId(lemma + "%" + lexsns[i]);
                        sense.setConfidence(1.0);
                        sense.addToIndexes();
                        senseArray.set(validWfCount++, sense);
                    }
                }

                WSDResult wsdResult = new WSDResult(jCas, oldOffset, oldOffset + nodeText.length());
                wsdResult.setWsdItem(w);
                wsdResult.setSenses(senseArray);
                wsdResult.setSenseInventory(senseInventory);
                wsdResult.setDisambiguationMethod(DISAMBIGUATION_METHOD_NAME);
                wsdResult.addToIndexes();
            }

            sentenceAnnotation.setEnd(offset);
            sentenceAnnotation.addToIndexes();
        }
        return sentenceText;
    }

    private boolean isValidWnsn(String wnsn)
    {
        return wnsn != null && wnsn.equals("0") == false
                && wnsn.equals("-1") == false;
    }

    /**
     * Sets the metadata of the current document.
     *
     * @param jCas
     * @throws CollectionException
     */
    private void setDocumentMetadata(JCas jCas, Element contextFile,
            Element context)
        throws CollectionException
    {
        DocumentMetaData d = DocumentMetaData.create(jCas);
        String documentId = context.attributeValue(ATTR_FILENAME);
        if (documentId == null) {
            throw new CollectionException("required_attribute_missing",
                    new Object[] { ATTR_FILENAME, ELEMENT_CONTEXT });
        }

        String collectionId = contextFile.attributeValue(ATTR_CONCORDANCE);
        if (collectionId == null) {
            throw new CollectionException("required_attribute_missing",
                    new Object[] { ATTR_CONCORDANCE, ELEMENT_CONTEXTFILE });
        }

        d.setDocumentId(documentId);
        // d.setDocumentUri(contextFiles[textCount].toURI().toString());
        d.setCollectionId(collectionId);
        d.setLanguage("en");
        jCas.setDocumentLanguage("en");
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
        logger.debug("Adding wsdItem " + id);
        validWordFormCount++;
        return w;
    }

    protected POS semCorPosToPOS(String pos)
    {
        if (pos == null) {
            throw new IllegalArgumentException();
        }
        if (pos.startsWith("JJ")) {
            return POS.ADJ;
        }
        else if (pos.startsWith("RB")) {
            return POS.ADV;
        }
        else if (pos.startsWith("VB") || pos.equals("MD")) {
            return POS.VERB;
        }
        else if (pos.startsWith("NN")) {
            return POS.NOUN;
        }
        else {
            throw new IllegalArgumentException("Unrecognized POS: " + pos);
        }
    }

}
