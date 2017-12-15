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
package de.tudarmstadt.ukp.dkpro.wsd.wsi.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.internal.util.XMLUtils;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.jsoup.Jsoup;

import com.ibm.icu.text.CharsetDetector;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasResourceCollectionReader_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.wsd.wsi.type.WSITopic;

/**
 * Reader for the AMBIENT,MORESQUE and SemEval 2013 Task 11 WSI Datasets
 *
 * @author zorn
 *
 */
public class AMBIENTReader
    extends JCasResourceCollectionReader_ImplBase
{

    /**
     *
     */
    public static final String PARAM_FILE = "File";
    @ConfigurationParameter(name = PARAM_FILE, mandatory = true)
    private String fileName;

    /**
     * Set this to true if the reader should attempt to download the webpage, clean it and append
     * the content to the snippet
     *
     */
    public static final String PARAM_DOWNLOAD_HTML = "downloadHTML";
    @ConfigurationParameter(name = PARAM_DOWNLOAD_HTML, mandatory = false, defaultValue = "false")
    private boolean downloadHTML;
    /**
     * List of Results (Webpages with assigned Topics/Subtopics)
     */
    protected List<Result> results = new ArrayList<Result>();
    // Pointer to the current result
    protected int pointer;

    Map<Integer, String> topics = new TreeMap<Integer, String>();

    class Result
    {
        SubTopic subTopic;
        String id;
        String url;
        String text;
        Integer topic;

    }

    List<SubTopic> subtopics;

    class SubTopic
    {

        Integer topic;
        Integer subTopic;
        String description;
    }

    protected Map<Integer, String> readTopics(String file)
        throws IOException
    {
        final TreeMap<Integer, String> topics = new TreeMap<Integer, String>();
        final BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null) {
            final String[] tok = line.split("\t");
            try {
                topics.put(Integer.parseInt(tok[0]), tok[1].replaceAll("_", " "));
            }
            catch (final NumberFormatException e) {

            }
            line = br.readLine();
        }
        br.close();
        return topics;
    }

    protected List<SubTopic> readSubtopics(String file)
        throws IOException
    {
        final List<SubTopic> _results = new LinkedList<AMBIENTReader.SubTopic>();
        final BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        line = br.readLine();

        while (line != null) {
            try {
                final String[] tok = line.split("\t");
                final String[] tok1 = tok[0].split("\\.");
                final int top = Integer.parseInt(tok1[0]);
                final int subtop = Integer.parseInt(tok1[1]);
                final SubTopic subTopic = new SubTopic();
                subTopic.topic = top;
                subTopic.subTopic = subtop;
                subTopic.description = tok[1];
                _results.add(subTopic);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            line = br.readLine();
        }
        br.close();

        return _results;
    }

    protected List<Result> readResults(String file)
        throws IOException
    {
        final List<Result> _results = new ArrayList<AMBIENTReader.Result>();
        final BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null) {
            try {
                final String[] tok = line.split("\t");
                final String[] tok1 = tok[0].split("\\.");

                final int top = Integer.parseInt(tok1[0]);

                final Result result = new Result();
                // result.subTopic = getSubtopic(top, subtop);
                result.topic = top;
                result.id = tok[0];
                result.url = tok[1];
                result.text = tok[2] + tok[3];
                _results.add(result);
            }
            catch (final Exception e) {

            }
            line = br.readLine();
        }
        br.close();

        return _results;

    }

    private SubTopic getSubtopic(int top, int subtop)
    {
        for (final SubTopic s : this.subtopics) {
            if (s.topic == top && s.subTopic == subtop) {
                return s;
            }
        }
        System.out.println("subtopic " + top + "." + subtop + " not found");
        return null;
    }

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        try {
            this.topics = readTopics(fileName + "/topics.txt");
            this.results = readResults(fileName + "/results.txt");

        }
        catch (final IOException e) {
            throw new ResourceInitializationException(e);
        }

    }

    private void readSTRel(String file)
        throws IOException
    {
        final BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        line = br.readLine();
        while (line != null) {
            try {
                final String[] tok = line.split("\t");
                final String[] tok1 = tok[0].split("\\.");

                final int top = Integer.parseInt(tok1[0]);
                final int subtop = Integer.parseInt(tok1[1]);
                boolean found = false;
                // result.subTopic =getSubtopic(top,subtop);
                for (final Result result : this.results) {
                    if (result.id.equals(tok[1])) {

                        result.subTopic = getSubtopic(top, subtop);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("could not assign subtopic for " + line);
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            line = br.readLine();
        }
        br.close();
    }

    @Override
    public boolean hasNext()

    {
        return this.pointer < this.results.size() - 1;
    }

    @Override
    public void getNext(JCas jCas)
        throws IOException, CollectionException
    {

        final Result result = this.results.get(this.pointer++);
        // jCas.setDocumentText(result.text);
        try {
            setDocumentMetadata(jCas, "" + result.id, result.url);

            final WSITopic wsiTopic = new WSITopic(jCas);
            wsiTopic.setBegin(0);
            wsiTopic.setEnd(result.text.length());
            wsiTopic.setId(result.id);
            wsiTopic.setSubjectOfDisambiguation(this.topics.get(result.topic));
            wsiTopic.addToIndexes();
            if (downloadHTML == true) {
                downloadHTMLPage(jCas, result);
            }
        }
        catch (final Exception e) {

            // wsiTopic.setBegin(0);
            // wsiTopic.setEnd(result.text.length());
            //
            // wsiTopic.setSubjectOfDisambiguation("" + result.id);
            // wsiTopic.addToIndexes();
        }

        // System.out.println("" + this.pointer + ":" + result.text);

    }

    private void downloadHTMLPage(JCas jCas, final Result result)
    {
        try {
            URL inputURL = new URL(URLDecoder.decode(result.url, "UTF-8"));
            URLConnection conn = inputURL.openConnection();
            conn.setReadTimeout(30000);
            InputStream is = conn.getInputStream();

            String text;

            CharsetDetector detector = new CharsetDetector();
            detector.enableInputFilter(true);

            try {
                text = IOUtils.toString(detector.getReader(is, null));

            }
            catch (Exception e) {
                text = IOUtils.toString(is);
            }
            StringBuffer cleanedText = new StringBuffer(Jsoup.parse(text).text());

            int index = XMLUtils.checkForNonXmlCharacters(cleanedText.toString(), false);
            while (index > -1) {
                cleanedText.delete(index, index + 1);
                index = XMLUtils.checkForNonXmlCharacters(cleanedText.toString(), false);
            }
            if (StringUtils.isAsciiPrintable(cleanedText.toString())) {
                jCas.setDocumentText(result.text + " " + cleanedText.toString());
            }
            else {
                jCas.setDocumentText(result.text);
            }
        }
        catch (Exception e) {
            getLogger().warn(
                    "Connection to " + result.url + " timed out/failed, using snippet only");
            e.printStackTrace();
            getLogger().warn(e);
            jCas.setDocumentText(result.text);
        }
    }

    public int addSentence(JCas jCas, int offset, final String nodeValue)
    {
        final Sentence sentenceAnnotation = new Sentence(jCas);
        sentenceAnnotation.setBegin(offset);

        offset += nodeValue.length();
        sentenceAnnotation.setEnd(offset);
        sentenceAnnotation.addToIndexes();
        return offset;
    }

    /**
     * Sets the metadata of the current document.
     *
     * @param jCas
     * @param documentId
     *            An identifier for the current document.
     * @param url
     * @throws URISyntaxException
     */
    protected void setDocumentMetadata(JCas jCas, String documentId, String url)
        throws URISyntaxException
    {
        final DocumentMetaData docMetaData = DocumentMetaData.create(jCas);
        docMetaData.setDocumentId(documentId);
        docMetaData.setDocumentUri(url);
        docMetaData.setCollectionId(new File(this.fileName.toString()).getName()
                .replace(".xml", ""));
        docMetaData.setLanguage("en");
        jCas.setDocumentLanguage("en");
    }

    @Override
    public Progress[] getProgress()
    {
        return new Progress[] {};
    }

}
