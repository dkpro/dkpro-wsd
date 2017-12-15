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


import static org.junit.Assert.*;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.WSDItemAnnotator;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

public class WSDItemAnnotatorTest {

	private final String text = "This is any text.";

	@Test
	public void testProcessJCasTokens() throws AnalysisEngineProcessException, ResourceInitializationException {
        AnalysisEngine engine = AnalysisEngineFactory.createEngine(
        		WSDItemAnnotator.class,
        		WSDItemAnnotator.PARAM_FEATURE_PATH, Token.class.getName());
 
        JCas aJCas = engine.newJCas();
        aJCas.setDocumentText(text);
        
        Token token = new Token(aJCas,0,2);
        token.addToIndexes();
        engine.process(aJCas);
        WSDItem wsdItem = JCasUtil.selectSingle(aJCas, WSDItem.class);
        assertNotNull(wsdItem);
        
        assertEquals(0, wsdItem.getBegin());
        assertEquals(2, wsdItem.getEnd());
	}

}
