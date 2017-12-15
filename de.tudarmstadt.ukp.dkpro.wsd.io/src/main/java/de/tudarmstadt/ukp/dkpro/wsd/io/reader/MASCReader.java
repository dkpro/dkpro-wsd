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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.jaxen.SimpleNamespaceContext;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * A collection reader for the WordNet senses in the MASC corpus.
 *
 * @author <a href="mailto:cholakov@ukp.informatik.tu-darmstadt.de">Kostadin Cholakov</a>
 */

public class MASCReader
    extends JCasResourceCollectionReader_ImplBase
{

    public static final String PARAM_SENSE_INVENTORY = "senseInventory";
    @ConfigurationParameter(name = PARAM_SENSE_INVENTORY, mandatory = false, description = "The sense inventory used by the answer key", defaultValue = "WordNet_3.0_sensekey")
    private String senseInventory;

    public static final String PARAM_IAA = "interannotatorAgreement";
    @ConfigurationParameter(name = PARAM_IAA, mandatory = false, description = "The way annotatons disagreements are resolved", defaultValue = "majority_vote")
    private String interannotatorAgreement;

    public static final String PARAM_IGNORE_TIES = "ignoreTies";
    @ConfigurationParameter(name = PARAM_IGNORE_TIES, mandatory = true, description = "Ignore cases where no majority annotation is available")
    private boolean ignoreTies;

    public static final String PARAM_OUTPUT_ANN = "outputAnnotation";
    @ConfigurationParameter(name = PARAM_OUTPUT_ANN, mandatory = false, description = "Output the annotations to a specific file", defaultValue = "")
    private String outputAnnotation;

    public static final String DISAMBIGUATION_METHOD_NAME = "masc";

    private static final String LIC_TYPE_HEAD = "head";
    private static final String LANG = "en";

    private static final String ELEMENT_A = "a";
    private static final String ELEMENT_F = "f";
    private static final String ELEMENT_LINK = "link";
    private static final String ELEMENT_NODE = "node";
    private static final String ELEMENT_GRAPH = "graph";
    private static final String ELEMENT_REGION = "region";

    private static final String ATTR_ID = "xml:id";
    private static final String ATTR_ANCHORS = "anchors";
    private static final String ATTR_VALUE = "value";

    protected HashMap<String, String> targetItems2sentences = new HashMap<String, String>();
    protected HashMap<String, List<String>> targetItems2senses = new HashMap<String, List<String>>();
    protected Document document = null;
    protected Iterator<Element> nodeIterator = null;
    protected Element root = null;
    protected String pos = null;
    protected String lemma = null;

    private final Logger logger = Logger.getLogger(getClass());

    @SuppressWarnings("unchecked")
    @Override
    public void getNext(JCas jCas)
        throws IOException, CollectionException
    {
        if (nodeIterator == null || nodeIterator.hasNext() == false) {
            Resource nextFile = nextFile();
            String fileLocation = nextFile.getLocation();
            int offset = fileLocation.indexOf(":") + 1; // ignore the initial
                                                        // "file:" or
                                                        // "classpath:" prefix
                                                        // of the location
            int extLength = 7; // ignore the extension "-wn.xml"
            String baseName = fileLocation.substring(offset,
                    fileLocation.length() - extLength);
            logger.info("Processing " + fileLocation);
            // get the target word
            String targetItem = baseName
                    .substring(baseName.lastIndexOf("/") + 1);

            setPosLemma(targetItem);

            String txtFileLocation = baseName + ".txt";
            logger.info("Raw corpus file: " + txtFileLocation);

            // open the txt file containing the raw corpus for this targetItem
            File txtFile = new File(txtFileLocation);
            String corpus;
            Scanner scanner;
            try {
                scanner = new Scanner(txtFile);
            }
            catch (FileNotFoundException fne) { // txt file is on the classpath
                InputStream is = this.getClass().getResourceAsStream(
                        txtFileLocation);
                if (is == null) {
                    System.out.println("File " + txtFileLocation
                            + " not found! Skipping...");
                    return;
                }
                scanner = new Scanner(is);
            }

            try {
                scanner.useDelimiter("\\Z");
                corpus = scanner.next();
                scanner.close();
            }
            catch (NoSuchElementException e) {
                System.out.println("File " + txtFileLocation
                        + " might be empty! Skipping...");
                return;
            }

            // open the file containing WordNet annotations
            SAXReader reader = new SAXReader();
            NullEntityResolver resolver = new NullEntityResolver();
            reader.setEntityResolver(resolver);
            InputStream is = new BufferedInputStream(nextFile.getInputStream());
            try {
                document = reader.read(is);
            }
            catch (DocumentException e) {
                throw new CollectionException(e);
            }

            root = document.getRootElement();
            if (root.getName().equals(ELEMENT_GRAPH) == false) {
                throw new CollectionException("unknown_element",
                        new Object[] { root.getName() });
            }

            Iterator<Element> regionIterator = root
                    .elementIterator(ELEMENT_REGION);
            if (regionIterator.hasNext() == false) {
                throw new CollectionException("element_not_found",
                        new Object[] { ELEMENT_REGION, root });
            }

            targetItems2sentences = mapTargetItems2sentences(regionIterator,
                    corpus);

            nodeIterator = root.elementIterator(ELEMENT_NODE);
            if (nodeIterator.hasNext() == false) {
                throw new CollectionException("element_not_found",
                        new Object[] { ELEMENT_NODE, root });
            }
        }

        Element node = nodeIterator.next();
        String documentText = processNode(jCas, node, root);

        // if no tie between annotators is discovered
        if (documentText != null) {
            setDocumentMetadata(jCas, node);
            jCas.setDocumentText(documentText);
        }
        else {
            jCas.reset();
        }

        // after all files are processed, output the annotation stats if oANN
        // specified
        if (!hasNext() && !outputAnnotation.equals("")) {
            logger.info("Outputting annotation info to " + outputAnnotation);
            outputAnnotations();
        }
    }

    @Override
    public boolean hasNext()
        throws IOException, CollectionException
    {
        return nodeIterator != null && nodeIterator.hasNext()
                || super.hasNext();
    }

    /**
     * Sets the metadata of the current document.
     *
     * @param jCas
     * @throws CollectionException
     */
    private void setDocumentMetadata(JCas jCas, Element node)
        throws CollectionException
    {
        DocumentMetaData d = DocumentMetaData.create(jCas);
        String id = node.valueOf("@" + ATTR_ID);
        if (id == null) {
            throw new CollectionException("required_attribute_missing",
                    new Object[] { ATTR_ID, ELEMENT_NODE });
        }
        d.setDocumentId(id);
        d.setLanguage(LANG);
        jCas.setDocumentLanguage(LANG);
    }

    @SuppressWarnings("unchecked")
    private String processNode(JCas jCas, Element node, Element root)
    {
        String sentence = null;

        String id = node.valueOf("@" + ATTR_ID);
        Element link = node.element(ELEMENT_LINK);
        String targets = link.valueOf("@targets"); // the ID of the region
                                                   // element this node points
                                                   // to

        String[] tokens = targetItems2sentences.get(targets).split("<sep>");
        int begin = Integer.parseInt(tokens[1]);
        int end = Integer.parseInt(tokens[2]);

        // get the annotation element "a" which points to the node which is
        // being processed
        XPath pathToA = createXPath("//masc:" + ELEMENT_A + "[@ref='" + id
                + "']");
        List<Node> as = pathToA.selectNodes(root);
        Node a = as.get(0);

        // Get all senses specified for the item
        // XPath does not work, even when one specifies the absolute path to the
        // element containing the sense;
        // only when the root is changed, global search "//" works; weird!
        document.setRootElement((Element) a);
        XPath pathToSenses = createXPath("//masc:" + ELEMENT_F
                + "[@name='sense']");
        List<Node> senseNodes = pathToSenses.selectNodes(a);

        HashSet<String> senses = resolveIAA(senseNodes, lemma + "-" + pos + "_"
                + id);

        // if the sense annotation is OK, create WSDItem and WSDResult
        if (senses != null) {
            LexicalItemConstituent c = createLexicalItemConstituent(jCas, lemma
                    + "_" + id, LIC_TYPE_HEAD, begin, end);
            WSDItem w = createWsdItem(jCas, lemma + "_" + id, begin, end, pos,
                    lemma);
            w.setConstituents(new FSArray(jCas, 1));
            w.setConstituents(0, c);

            FSArray senseArray = new FSArray(jCas, senses.size());
            List<String> sensesList = new ArrayList<String>(senses);
            for (int i = 0; i < sensesList.size(); i++) {
                String nextSense = sensesList.get(i);

                if (nextSense.contains(" ") || nextSense.length() == 1) {
                    document.setRootElement(root); // set back the XML root to
                                                   // the original root node
                    return null;
                }

                Sense sense = new Sense(jCas);
                sense.setId(nextSense);
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

            sentence = tokens[0]; // set the context of the target item to the
                                  // sentence in which this item occurs
        }

        document.setRootElement(root); // set back the XML root to the original
                                       // root node

        return sentence;
    }

    @SuppressWarnings("unchecked")
    private HashSet<String> resolveIAA(List<Node> senseNodes, String id)
    {
        HashSet<String> senseSet = new HashSet<String>();
        List<String> senseList = new ArrayList<String>(senseNodes.size());
        for (Node node : senseNodes) {
            String sense = node.valueOf("@" + ATTR_VALUE);
            senseSet.add(sense);

            Element e = (Element) node;
            Element parent = e.getParent();
            List<Element> children = parent.elements();
            sense += "#" + children.get(1).attributeValue(ATTR_VALUE);
            senseList.add(sense);
        }
        targetItems2senses.put(id, senseList);

        if (interannotatorAgreement.equals("first_sense")) {
            senseSet.clear();
            String firstSense = senseNodes.get(0).valueOf("@" + ATTR_VALUE);
            senseSet.add(firstSense);
            return senseSet;
        }

        // all annotators agree on a single sense or the user want all senses
        // back
        if (senseSet.size() == 1
                || interannotatorAgreement.equals("all_senses")) {
            return senseSet;
        }

        senseSet.clear();

        if (interannotatorAgreement.equals("majority_vote")) {
            HashMap<String, Integer> senseFreq = new HashMap<String, Integer>();
            for (Node node : senseNodes) {
                String nextSense = node.valueOf("@" + ATTR_VALUE);
                Integer count = senseFreq.get(nextSense);
                senseFreq.put(nextSense, count == null ? 1 : count + 1);
            }

            List<Integer> values = new ArrayList<Integer>(senseFreq.values());
            Collections.sort(values); // sort in ascending order
            int max = values.get(values.size() - 1);
            int second = values.get(values.size() - 2);

            // there is a tie
            if (max == second && ignoreTies) {
                return null;
            }
            else {
                for (String key : senseFreq.keySet()) {
                    int count = senseFreq.get(key);
                    if (count == max) {
                        senseSet.add(key);
                        return senseSet;
                    }
                }
            }
        }
        else if (interannotatorAgreement.matches(".*\\d+")) {
            for (Node node : senseNodes) {
                String sense = node.valueOf("@" + ATTR_VALUE);
                Element e = (Element) node;
                Element parent = e.getParent();
                List<Element> children = parent.elements();
                for (Element child : children) {
                    if (child.attributeValue(ATTR_VALUE).equals(
                            interannotatorAgreement)) {
                        senseSet.add(sense);
                        return senseSet;
                    }
                }
            }
        }

        return senseSet;
    }

    // lemma and POS are encoded in the file name
    // set them only once for all target items specified in the XML
    private void setPosLemma(String targetItem)
    {
        int separatorIndex = targetItem.lastIndexOf("-");
        String posEncoded = targetItem.substring(separatorIndex + 1);
        pos = getPOS(posEncoded).toString();
        lemma = targetItem.substring(0, separatorIndex);

    }

    protected LexicalItemConstituent createLexicalItemConstituent(JCas jCas,
            String id, String constituentType, int begin, int end)
    {
        LexicalItemConstituent c = new LexicalItemConstituent(jCas);
        c.setBegin(begin);
        c.setEnd(end);
        c.setConstituentType(constituentType);
        c.setId(id);
        c.addToIndexes();
        return c;
    }

    protected WSDItem createWsdItem(JCas jCas, String id, int begin, int end,
            String pos, String lemma)
    {
        WSDItem w = new WSDItem(jCas);
        w.setBegin(begin);
        w.setEnd(end);
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

    /**
     * Maps each target item to the sentence (document) in which this item
     * occurs in the corpus. Maps each target item to its start and end indexes
     * with respect to the sentence (document) this item occurs in.
     *
     * @param regionIterator
     *            iterates over the region XML elements which contain the start
     *            and end indexes, with respect to the corpus, of each sentence
     *            (document) and each target item
     * @param corpus
     *            the corpus which is being processed
     * @return a mapping of item IDs to the sentence (document) which this item
     *         occurs in and to the start and end indexes of the item with
     *         respect to that sentence (document)
     */
    private HashMap<String, String> mapTargetItems2sentences(
            Iterator<Element> regionIterator, String corpus)
    {
        HashMap<String, String> mappings = new HashMap<String, String>();

        int idOffset = 4;
        int sentenceId = 0;
        String sentence = "";
        int sentenceStart = 0;
        while (regionIterator.hasNext()) {
            Element region = regionIterator.next();
            String id = region.valueOf("@" + ATTR_ID);

            // all IDs start with wn_r, followed by a digit; that is why,
            // idOffset is set to 4
            int idNumber = Integer.parseInt(id.substring(idOffset));

            String anchors = region.valueOf("@" + ATTR_ANCHORS);
            String[] tokens = anchors.split("\\s+");
            int begin = Integer.parseInt(tokens[0]);
            int end = Integer.parseInt(tokens[1]);

            /*
             * The XML uses the same type of region elements to specify the
             * anchors for the target items and the sentences those items occur
             * in. In order to distinguish items from sentences, one has to look
             * at the order the region elements are specified, and their IDs.
             * First, the region for the sentence is specified, and then the
             * regions for each occurrence of the target item in this sentence
             * are given. Also, the numerical ID of the sentence region is
             * greater than that of the IDs of the region elements which specify
             * the target items in this sentence. For example, for a
             * two-sentence corpus and a target item "history" which occurs
             * twice in the first sentence and once in the second sentence, the
             * elements and the IDs are specified as follows: <region
             * xml:id="wn_r2" anchors="0 167"> <region xml:id="wn_r0"
             * anchors="20 27"> <region xml:id="wn_r1" anchors="127 134">
             * <region xml:id="wn_r4" anchors="169 217"> <region xml:id="wn_r3"
             * anchors="189 196">
             */

            // check if a new sentence is specified by this region element
            if (idNumber > sentenceId) {
                sentenceId = idNumber;
                sentence = corpus.substring(begin, end);
                sentenceStart = begin;
            }
            else {
                // otherwise the element specifies an item
                // get the start index of the item with respect to the sentence
                int itemStart = begin - sentenceStart;
                int itemLength = end - begin;
                int itemEnd = itemStart + itemLength; // get the end index of
                                                      // this item with respect
                                                      // to the sentence
                String value = sentence + "<sep>" + itemStart + "<sep>"
                        + itemEnd;
                mappings.put(id, value);
            }
        }

        return mappings;
    }

    // specifies a namespace, so that XPath expressions work correctly with
    // dom4j
    private XPath createXPath(String xpathExpr)
    {
        HashMap<String, String> namespaceURIMap = new HashMap<String, String>();
        namespaceURIMap.put("masc", "http://www.xces.org/ns/GrAF/1.0/");

        XPath path = DocumentHelper.createXPath(xpathExpr);
        path.setNamespaceContext(new SimpleNamespaceContext(namespaceURIMap));

        return path;
    }

    // no adverbs yet in the corpus
    protected POS getPOS(String pos)
    {
        if (pos == null) {
            throw new IllegalArgumentException();
        }
        if (pos.equals("j")) {
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

    private void outputAnnotations()
    {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(
                    outputAnnotation));
            List<String> keys = new ArrayList<String>(
                    targetItems2senses.keySet());
            Collections.sort(keys);
            for (String id : keys) {
                out.write(id);
                for (String sense : targetItems2senses.get(id)) {
                    out.write("\t" + sense);
                }
                out.newLine();
            }
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
