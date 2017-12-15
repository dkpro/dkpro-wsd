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

package de.tudarmstadt.ukp.dkpro.wsd.senseval.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Writes all WSDResults to Senseval answer key format
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class SensevalAnswerKeyWriter
    extends JCasConsumer_ImplBase
{

    public static final String PARAM_REPLACE_APOSTROPHES = "replaceApostrophes";
    @ConfigurationParameter(name = PARAM_REPLACE_APOSTROPHES, mandatory = false, description = "Replace sense ID apostrophes with hyphens as required by the Senseval specifications", defaultValue = "false")
    protected boolean replaceApostrophes;

    public static final String PARAM_INCLUDE_CONFIDENCE_VALUES = "includeConfidenceValues";
    @ConfigurationParameter(name = PARAM_INCLUDE_CONFIDENCE_VALUES, mandatory = false, description = "Include confidence values with the sense IDs", defaultValue = "true")
    protected boolean includeConfidenceValues;

    public static final String PARAM_ALGORITHM = "algorithm";
    @ConfigurationParameter(name = PARAM_ALGORITHM, mandatory = true, description = "The algorithm whose results to output")
    protected String algorithm;

    public static final String PARAM_OUTPUT_FILE = "outputFilename";
    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = false, description = "The output file.  If unset, output goes to standard output.")
    protected String outputFilename;
    protected BufferedWriter output;

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        for (WSDResult r : JCasUtil.select(aJCas, WSDResult.class)) {
            try {
                if (!algorithm.equals(r.getDisambiguationMethod())) {
                    continue;
                }
                output.write(toSensevalAnswerKey(r, replaceApostrophes, includeConfidenceValues));
                output.newLine();
            }
            catch (IOException e) {
                throw new AnalysisEngineProcessException(e);
            }
        }
    }

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        super.collectionProcessComplete();
        try {
            output.close();
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        if (outputFilename != null) {
            try {
                output = new BufferedWriter(new FileWriter(outputFilename));
            }
            catch (IOException e) {
                throw new ResourceInitializationException(e);
            }
        }
        else {
            output = new BufferedWriter(new OutputStreamWriter(System.out));
        }
    }

    public static String toSensevalAnswerKey(WSDResult r,
            boolean replaceApostrophes, boolean includeConfidenceValues)
    {
        String s = new String(r.getWsdItem().getSubjectOfDisambiguation() + " "
                + r.getWsdItem().getId());
        for (int i = 0; i < r.getSenses().size(); i++) {
            if (replaceApostrophes) {
                s += " " + r.getSenses(i).getId().replace('\'', '-');
            }
            else {
                s += " " + r.getSenses(i).getId();
            }
            if (includeConfidenceValues) {
                s += "/" + r.getSenses(i).getConfidence();
            }
        }
        if (r.getComment() != null) {
            s += " !! " + r.getComment();
        }
        return s;
    }

    public static String toSensevalAnswerKey(WSDResult r)
    {
        return toSensevalAnswerKey(r, false, true);
    }
}
