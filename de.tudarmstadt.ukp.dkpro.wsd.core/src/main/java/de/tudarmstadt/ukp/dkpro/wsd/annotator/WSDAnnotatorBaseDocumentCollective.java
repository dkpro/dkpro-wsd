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

package de.tudarmstadt.ukp.dkpro.wsd.annotator;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * An abstract class for annotators which call a disambiguation algorithm taking
 * as a parameter the text in the document so that the algorithm
 * can disambiguate them all simultaneously.
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 *
 */
public abstract class WSDAnnotatorBaseDocumentCollective
extends WSDAnnotatorBase
{

    protected Class<Annotation> contextClass;

    private final static Logger logger = Logger
            .getLogger(WSDAnnotatorBaseDocumentCollective.class.getName());

    protected abstract Map<WSDItem, Map<String, Double>> getDisambiguation(
            Collection<WSDItem> wsdItems, String documentText)
                    throws SenseInventoryException;

    @Override
    public void process(JCas aJCas)
            throws AnalysisEngineProcessException
            {

        try{
            logger.debug("Entering annotator WSDAnnotatorBaseDocumentCollective.");
            Map<WSDItem, Map<String, Double>> disambiguationResults = getDisambiguation(JCasUtil
                    .select(aJCas, WSDItem.class), aJCas.getDocumentText());
            for (WSDItem wsdItem : disambiguationResults.keySet()) {
                if (maxItemsAttempted >= 0 && numItemsAttempted++ >= maxItemsAttempted) {
                    return;
                }
                setWSDItem(aJCas, wsdItem,
                        disambiguationResults.get(wsdItem));
            }
        }
        catch (SenseInventoryException e) {
            throw new AnalysisEngineProcessException(e);
        }

            }
}