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

package de.tudarmstadt.ukp.dkpro.wsd.tackbp.annotator;

import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ExternalResource;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmDocumentDependentBasic;
import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorBaseDocumentDependent;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * An annotator which calls a {@link WSDAlgorithmDocumentDependentBasic}
 * disambiguation algorithm once for each {@link WSDItem} in the document.
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 *
 */
public class WSDAnnotatorDocumentDependentBasic
    extends WSDAnnotatorBaseDocumentDependent
{

    public final static String WSD_ALGORITHM_RESOURCE = "WSDAlgorithmResource";
    @ExternalResource(key = WSD_ALGORITHM_RESOURCE)
    protected WSDAlgorithmDocumentDependentBasic wsdMethod;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        inventory = wsdMethod.getSenseInventory();
    }

    @Override
    protected Map<String, Double> getDisambiguation(JCas aJCas, String sod)
        throws SenseInventoryException
    {
        return wsdMethod.getDisambiguation(DocumentMetaData.get(aJCas)
                .getDocumentId(), sod);
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