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

import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ExternalResource;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmContextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * An annotator which calls a {@link WSDAlgorithmContextBasic} disambiguation
 * algorithm once for each {@link WSDItem} in the document.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WSDAnnotatorContextBasic
    extends WSDAnnotatorBaseContext
{
    public final static String WSD_METHOD_CONTEXT = "WSDAlgorithmContext";
    @ExternalResource(key = WSD_METHOD_CONTEXT)
    protected WSDAlgorithmContextBasic wsdMethod;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        inventory = wsdMethod.getSenseInventory();
    }

    @Override
    protected Map<String, Double> getDisambiguation(JCas aJCas,
            WSDItem wsdItem, Annotation context)
        throws SenseInventoryException
    {
        // TODO: Currently this just passes the covered text as the context.
        // It might be better to pass a collection of annotations (for example,
        // lemmas)
        return wsdMethod.getDisambiguation(
                wsdItem.getSubjectOfDisambiguation(), context.getCoveredText());
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
}
