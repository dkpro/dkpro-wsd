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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmCollectiveSequentialBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * An annotator which calls a {@link WSDAlgorithmCollectiveSequentialBasic}
 * disambiguation algorithm on a collection of {@link WSDItem}s.
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 *
 */
public class WSDAnnotatorCollectiveSequentialBasic
    extends WSDAnnotatorBase
{

    public final static String WSD_ALGORITHM_RESOURCE = "WSDAlgorithmResource";
    @ExternalResource(key = WSD_ALGORITHM_RESOURCE)
    protected WSDAlgorithmCollectiveSequentialBasic wsdMethod;

    public final static String PARAM_SEQUENCE_ANNOTATION = "sequenceAnnotation";
    @ConfigurationParameter(name = PARAM_SEQUENCE_ANNOTATION, mandatory = true, description = "The annotation type determining the sequence")
    protected String sequenceAnnotation;

    protected Class<Annotation> sequenceAnnotationClass;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        inventory = wsdMethod.getSenseInventory();

        try {
            sequenceAnnotationClass = (Class<Annotation>) Class
                    .forName(sequenceAnnotation);
        }
        catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        try {
            List<Collection<WSDItem>> wsdItems = new LinkedList<Collection<WSDItem>>();
            for (Annotation sequencePart : JCasUtil.select(aJCas,
                    sequenceAnnotationClass)) {
                wsdItems.add(JCasUtil.selectCovered(aJCas, WSDItem.class,
                        sequencePart));
            }

            Map<WSDItem, Map<String, Double>> disambiguationResults = getDisambiguation(wsdItems);
            for (WSDItem wsdItem : disambiguationResults.keySet()) {
                if (maxItemsAttempted >= 0
                        && numItemsAttempted++ >= maxItemsAttempted) {
                    return;
                }
                setWSDItem(aJCas, wsdItem, disambiguationResults.get(wsdItem));
            }
        }
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }

    }

    protected Map<WSDItem, Map<String, Double>> getDisambiguation(
            List<Collection<WSDItem>> wsdItems)
        throws SenseInventoryException
    {
        List<Collection<String>> subjectsOfDisambiguation = new ArrayList<Collection<String>>();
        List<String> sods4Part;
        for (Collection<WSDItem> wsdItems4Part : wsdItems) {
            sods4Part = new ArrayList<String>();
            for (WSDItem wsdItem : wsdItems4Part) {
                sods4Part.add(wsdItem.getSubjectOfDisambiguation());
            }
            subjectsOfDisambiguation.add(sods4Part);
        }
        List<Map<String, Map<String, Double>>> resultsByToken = wsdMethod
                .getDisambiguation(subjectsOfDisambiguation);
        if (resultsByToken.size() != wsdItems.size()) {
            throw new SenseInventoryException("");
        }
        Map<WSDItem, Map<String, Double>> resultsByWSDItem = new HashMap<WSDItem, Map<String, Double>>();
        Iterator<Map<String, Map<String, Double>>> resultsByTokenIterator = resultsByToken
                .iterator();
        Map<String, Map<String, Double>> sequencePartResultsByToken;
        for (Collection<WSDItem> sequencePartWsdItems : wsdItems) {
            sequencePartResultsByToken = resultsByTokenIterator.next();
            for (WSDItem wsdItem : sequencePartWsdItems) {
                Map<String, Double> senseMap = sequencePartResultsByToken
                        .get(wsdItem.getSubjectOfDisambiguation());
                if (senseMap != null) {
                    resultsByWSDItem.put(wsdItem, senseMap);
                }
            }
        }
        return resultsByWSDItem;
    }

    @Override
    protected String getDisambiguationMethod()
        throws SenseInventoryException
    {
        if (disambiguationMethodName != null) {
            return disambiguationMethodName;
        }
        else {
            return wsdMethod.getDisambiguationMethod();
        }
    }

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        super.collectionProcessComplete();
        wsdMethod.collectionProcessComplete();
    }

}
