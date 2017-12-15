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

package de.tudarmstadt.ukp.dkpro.wsd.candidates;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * SenseConfidenceNormalizer normalizes all the weights (confidence values) of
 * senses in the WSDResults so that they sum to 1.0
 */
public class SenseConfidenceNormalizer
    extends JCasAnnotator_ImplBase
{
    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        for (WSDResult r : JCasUtil.select(aJCas, WSDResult.class)) {
            r.normalize();
        }
    }

}
