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

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmDocumentTextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * An abstract class for annotators which call a disambiguation algorithm taking
 * as a parameter the id of the document.
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 *
 */
public class WSDAnnotatorDocumentCollectiveBasic
    extends WSDAnnotatorBaseDocumentCollective
{
    public final static String WSD_ALGORITHM_RESOURCE = "WSDAlgorithmResource";
    @ExternalResource(key = WSD_ALGORITHM_RESOURCE)
    protected WSDAlgorithmDocumentTextBasic wsdMethod;

    @Override
    public void initialize(UimaContext context)
    throws ResourceInitializationException
    {
        super.initialize(context);
        inventory = wsdMethod.getSenseInventory();
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
    protected Map<WSDItem, Map<String, Double>> getDisambiguation(Collection<WSDItem> wsdItems,
            String documentText)
        throws SenseInventoryException
    {
        String senseDisambiguatedText = wsdMethod.getDisambiguation(documentText);
        return null;
    }

}