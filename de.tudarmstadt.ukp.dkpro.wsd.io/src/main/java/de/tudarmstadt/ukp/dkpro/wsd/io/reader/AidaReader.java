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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * A collection reader for <a href=
 * "https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/yago-naga/aida/downloads/"
 * >AIDA</a> based on <a href= "http://www.cnts.ua.ac.be/conll2003/ner/">CoNLL</a>.
 *
 * @author Nicolai Erbs
 */
public class AidaReader
    extends JCasCollectionReader_ImplBase
{
    public static final String PARAM_SENSE_INVENTORY = "senseInventory";
    @ConfigurationParameter(name = PARAM_SENSE_INVENTORY, mandatory = false, description = "The sense inventory used by the answer key", defaultValue = "WIKIPEDIA_EN")
    private String senseInventory;

    public static final String PARAM_INPUT_PATH = "inputPath";
    @ConfigurationParameter(name = PARAM_INPUT_PATH, mandatory = true, description = "Input path for dataset")
    private String inputPath;

    public static final String DISAMBIGUATION_METHOD_NAME = "Aida";
    public static final String NIL_DISAMBIGUATION_RESULT = "NIL";
    public static final String NIL_DISAMBIGUATION_AIDA = "--NME--";
    public static final Object SENSE_INVENTORY = "WIKIPEDIA_EN";

    private static final String DOCSTART = "-DOCSTART- ";
    private static final String FIELD_SEPERATOR = "\t";
    private static final String NEWLINE = System.getProperty("line.separator");

    private Map<String, String[]> corpus;

    private Iterator<String> docIterator;
    private int counter;

    @Override
    public void initialize(final UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        String content;
        try {
            content = FileUtils.readFileToString(new File(inputPath));
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }

        corpus = new TreeMap<String, String[]>();

        String docId;
        String docText;
        for (String doc : content.split(DOCSTART)) {
            // don't do anything before first document starts
            if (doc.length() == 0) {
                continue;
            }

            docId = doc.substring(0, doc.indexOf(")")).replaceAll("\\(", "")
                    .trim().replaceAll(" ", "_");
            docText = doc.substring(doc.indexOf("\n") + 1).trim();
            corpus.put(docId, docText.split(NEWLINE));
        }

        docIterator = corpus.keySet().iterator();
        counter = 0;
    }

    @Override
    public void getNext(JCas jCas)
        throws IOException, CollectionException
    {
        // get the content
        String documentId = docIterator.next();
        String[] lines = corpus.get(documentId);
        counter++;

        setDocumentMetadata(jCas, documentId, inputPath);

        // add text and annotations
        boolean lastIsWsdItem = false;
        String lastNE = null;
        String lastAidaResult = null;
        StringBuffer documentText = new StringBuffer();
        String neMark;
        String[] lineParts;
        for (String line : lines) {
            // System.out.println(line.length() + "\t" + line);
            if (line.length() == 0) {

                // If the document stops with a named entity, add it
                if (lastIsWsdItem) {
                    addAnnotations(jCas, documentText, lastNE, lastAidaResult);
                    lastIsWsdItem = false;
                    lastNE = null;
                    lastAidaResult = null;
                }

                documentText.append(NEWLINE);
            }
            else {
                lineParts = line.split(FIELD_SEPERATOR);
                documentText.append(lineParts[0] + " ");
                neMark = lineParts.length > 1 ? lineParts[1] : "x";
                // System.out.println(documentId + "\t" + line + "\t" + neMark +
                // "\t" + (lineParts.length>1));
                if (neMark.equalsIgnoreCase("B")) {
                    if (lastIsWsdItem) {
                        addAnnotations(jCas, documentText, lastNE,
                                lastAidaResult);
                    }
                    lastIsWsdItem = true;
                    lastNE = lineParts[2];
                    lastAidaResult = lineParts[3]
                            .equalsIgnoreCase(NIL_DISAMBIGUATION_AIDA) ? NIL_DISAMBIGUATION_RESULT
                            : lineParts[3];
                }
                else if (lastIsWsdItem && neMark.equalsIgnoreCase("x")) {
                    addAnnotations(jCas, documentText, lastNE, lastAidaResult);
                    lastIsWsdItem = false;
                    lastNE = null;
                    lastAidaResult = null;
                }

                // else if(lastIsWsdItem && neMark.equalsIgnoreCase("I")){
                // //do not add WSDItem
                // //lastIsWsdItem = true;
                // //no need to do anything
                // }
                // else if(!lastIsWsdItem && neMark.equalsIgnoreCase("x")){
                // //do not add WSDItem
                // //lastIsWsdItem = false;
                // //no need to do anything
                // }
                // else if(!lastIsWsdItem && neMark.equalsIgnoreCase("I")){
                // //do not add WSDItem
                // // lastIsWsdItem = true;
                // //A NE cannot continue if the previous wasn't a NE
                // throw new CollectionException();
                // }
                // else{
                // //There shouldn't be any case like this
                // throw new CollectionException();
                // }

            }
        }

        jCas.setDocumentText(documentText.toString());
    }

    private void addAnnotations(JCas jCas, StringBuffer documentText,
            String namedEntity, String aidaResult)
    {

        int offset = documentText.lastIndexOf(namedEntity);
        int length = namedEntity.length();
        if (offset == -1) {
            offset = documentText
                    .lastIndexOf(namedEntity.replaceAll("'", " '"));
            length += StringUtils.countMatches(namedEntity, "'");
        }
        // add WSDItem
        WSDItem wsdItem = getWsdItem(jCas, offset, length, namedEntity);

        // Get sense tags
        FSArray senseArray = new FSArray(jCas, 1);
        Sense sense = new Sense(jCas);
        sense.setId(StringEscapeUtils.unescapeJava(aidaResult));
        sense.setConfidence(1.0);
        sense.addToIndexes();
        senseArray.set(0, sense);

        WSDResult wsdResult = new WSDResult(jCas);
        wsdResult.setWsdItem(wsdItem);
        wsdResult.setSenses(senseArray);
        wsdResult.setSenseInventory(senseInventory);
        wsdResult.setDisambiguationMethod(DISAMBIGUATION_METHOD_NAME);
        wsdResult.addToIndexes();
    }

    /**
     * Sets the metadata of the current document.
     *
     * @param jCas
     * @throws CollectionException
     */
    private void setDocumentMetadata(JCas jCas, String documentId,
            String collectionId)
        throws CollectionException
    {
        DocumentMetaData dmd = DocumentMetaData.create(jCas);

        dmd.setDocumentId(documentId);
        dmd.setDocumentTitle(documentId);
        // d.setDocumentUri(contextFiles[textCount].toURI().toString());
        dmd.setCollectionId(collectionId);
        dmd.setLanguage("en");
        jCas.setDocumentLanguage("en");
    }

    /**
     * Creates a new WSDItem annotation and adds it to the annotation index.
     *
     * @param jCas
     *            The CAS in which to create the annotation.
     * @param offset
     *            The index of the first character of the annotation in the
     *            document.
     * @param length
     *            The length, in characters, of the annotation.
     * @param namedEntity
     *            The lemmatized form, if known, otherwise null.
     * @return The new annotation.
     */
    protected WSDItem getWsdItem(JCas jCas, int offset, int length,
            String namedEntity)
    {
        WSDItem wsdItem = new WSDItem(jCas);
        wsdItem.setBegin(offset);
        wsdItem.setEnd(offset + length);
        wsdItem.setId(namedEntity);
        wsdItem.setSubjectOfDisambiguation(namedEntity);
        wsdItem.addToIndexes();
        return wsdItem;
    }

    @Override
    public Progress[] getProgress()
    {
        return new Progress[] { new ProgressImpl(counter, corpus.size(),
                "documents") };
    }

    @Override
    public boolean hasNext()
        throws IOException, CollectionException
    {
        return docIterator.hasNext();
    }
}