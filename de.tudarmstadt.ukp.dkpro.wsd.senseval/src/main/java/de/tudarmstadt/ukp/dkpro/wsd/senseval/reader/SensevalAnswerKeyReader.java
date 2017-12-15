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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * SensevalAnswerKeyReader reads in a Senseval answer key file and creates a
 * WSDresult annotation for each answer set contained therein. The answer key
 * file format is as follows:
 *
 * <p>
 * &lt;lexical_sample_answer&gt; ::= &lt;lexelt&gt; &lt;instance_id&gt;
 * &lt;sense_tag_list&gt; [!! &lt;comment&gt;]
 * </p>
 * <p>
 * &lt;all_words_answer&gt; ::= &lt;text_id&gt; &lt;head_id&gt;
 * &lt;sense_tag_list&gt; [!! &lt;comment&gt;]
 * </p>
 * <p>
 * &lt;lexelt&gt; ::= lexical element (as defined in the lex-sample evaluation
 * data)
 * </p>
 * <p>
 * &lt;instance_id&gt; ::= instance id (as defined in the lex-sample evaluation
 * data)
 * </p>
 * <p>
 * &lt;text_id&gt; ::= text id (as defined in the all-words evaluation data)
 * </p>
 * <p>
 * &lt;head_id&gt; ::= head id (as defined in the all-words evaluation data)
 * </p>
 * <p>
 * &lt;sense_tag_list&gt; ::= &lt;weighted_list&gt; | &lt;unweighted_list&gt;
 * </p>
 * <p>
 * &lt;weighted_list&gt; ::= &lt;senseid&gt;[/&lt;weight&gt;]
 * {&lt;senseid&gt;[/&lt;weight&gt;]}
 * </p>
 * <p>
 * &lt;unweighted_list&gt; ::= &lt;senseid&gt; {&lt;senseid&gt;}
 * </p>
 * <p>
 * &lt;senseid&gt; ::= senseid from the lexicon for the task, with apostrophes
 * replaced by hyphens
 * </p>
 * <p>
 * &lt;weight&gt; ::= integer or positive real number
 * </p>
 * <p>
 * &lt;comment&gt; ::= text string
 * </p>
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class SensevalAnswerKeyReader
    extends JCasAnnotator_ImplBase
{
    private final static Logger logger = Logger
            .getLogger(SensevalAnswerKeyReader.class.getName());

    public static final String PARAM_FILE = "fileName";
    @ConfigurationParameter(name = PARAM_FILE, mandatory = true, description = "The Semeval/Senseval answer key to read")
    private String fileName;

    public static final String PARAM_SENSE_INVENTORY = "senseInventory";
    @ConfigurationParameter(name = PARAM_SENSE_INVENTORY, mandatory = false, description = "The sense inventory used by the answer key", defaultValue = "Senseval_sensekey")
    private String senseInventory;

    public static final String PARAM_NORMALIZE_CONFIDENCE = "normalizeConfidence";
    @ConfigurationParameter(name = PARAM_NORMALIZE_CONFIDENCE , mandatory = false, description = "Wether to normalize confidence values", defaultValue = "false")
    private boolean normalizeConfidence;

    private Map<String, String> answerKey;

    /**
     * @param context
     * @throws ResourceInitializationException
     */
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        answerKey = new TreeMap<String, String>();

        // Read the answer key file into a collection indexed by instance/head
        // ID

        BufferedReader bufRead = null;
        InputStream is = null;
        try {

            URL url = ResourceUtils.resolveLocation(fileName, this, context);
            is = url.openStream();
            String content = IOUtils.toString(is, "UTF-8");

            bufRead = new BufferedReader(new StringReader(content));
            int numLines = 0;
            String line; // String that holds current file line
            while ((line = bufRead.readLine()) != null) {
                numLines++;
                String wsdItemId = line.split("[ \\t]+")[1];
                if (answerKey.put(wsdItemId, line) != null) {
                    logger.warn(fileName + ":" + numLines
                            + ": Duplicate answer key for " + wsdItemId);
                }
            }
            logger.info(fileName + ": Read " + answerKey.size()
                    + " unique answers");
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        finally {
            IOUtils.closeQuietly(bufRead);
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {

        String key;
        for (WSDItem wsdItem : JCasUtil.select(aJCas, WSDItem.class)) {
            if ((key = answerKey.get(wsdItem.getId())) != null) {
                WSDResult wsdResult = sensevalAnswerKeytoWSDResult(aJCas, key,
                        wsdItem, normalizeConfidence);
                wsdResult.setSenseInventory(senseInventory);
                wsdResult.setDisambiguationMethod(fileName);
                wsdResult.addToIndexes();
                answerKey.remove(wsdItem.getId());
            }
            else {
                logger.warn(fileName + ": No answer key for " + wsdItem.getId());
            }
        }
        for (String k : answerKey.keySet()) {
            logger.warn(fileName + ": No instance for answer key item " + k);
        }
    }

    /**
     * convert from Senseval answer key format
     *
     * @param jCas
     * @param answerKey
     *            A string containing a line from a Senseval answer key file
     * @param w
     *            The WSD item the answer key applies to, or null if this is not
     *            known.
     */
    public static WSDResult sensevalAnswerKeytoWSDResult(JCas jCas,
            String answerKey, WSDItem w, boolean normalizeConfidence)
    {
        Pattern commentPattern = Pattern
                .compile("^(.*)([\\t ]+!![\\t ])+(.*)$");
        Matcher commentMatcher = commentPattern.matcher(answerKey);
        String[] senses;

        WSDResult r = new WSDResult(jCas);

        if (commentMatcher.find()) {
            r.setComment(commentMatcher.group(3));
            answerKey = commentMatcher.group(1);
        }

        senses = answerKey.split("[ \\t]+");

        // Extract lexelt/text ID and instance/head ID
        if (senses.length < 3) {
            throw new IllegalArgumentException(
                    "syntax error in Senseval answer key");
        }
        // String lexelt = senses[0]; // Not used
        String instanceId = senses[1];

        // Extract sense tags
        FSArray senseArray = new FSArray(jCas, senses.length - 2);
        for (int i = 2; i < senses.length; i++) {
            Sense sense = new Sense(jCas);
            String senseParts[] = senses[i].split("/");
            if (senseParts.length == 2) {
                sense.setConfidence(Double.parseDouble(senseParts[1]));
            }
            else if (senseParts.length == 1) {
                sense.setConfidence(0.0);
            }
            else {
                throw new IllegalArgumentException(
                        "syntax error in Senseval answer key");
            }

            sense.setId(senseParts[0]);
            sense.addToIndexes();
            senseArray.set(i - 2, sense);
        }
        r.setSenses(senseArray);

        if (w != null) {
            r.setWsdItem(w);
        }
        else {
            boolean found = false;
            // Find WSDItem corresponding to the instance/head ID
            for (WSDItem wsdItem : JCasUtil.select(jCas, WSDItem.class)) {
                if (wsdItem.getId().equals(instanceId)) {
                    r.setWsdItem(wsdItem);
                    found = true;
                    break;
                }
            }
            if (found == false) {
                throw new IllegalArgumentException("instance/head ID '"
                        + instanceId + "' not found");
            }
        }

        if (normalizeConfidence) {
            r.normalize();
        }

        return r;
    }

}
