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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

public class WSDAnnotatorTinyContextBasic
    extends WSDAnnotatorContextBasic
{
    @Override
    protected Map<String, Double> getDisambiguation(JCas aJCas,
            WSDItem wsdItem, Annotation contextAnnotation)
        throws SenseInventoryException
    {
        // TODO: Currently this just passes the covered text as the context.
        // It might be better to pass a collection of annotations (for example,
        // lemmas)
    	String context = aJCas.getDocumentText();
		if(wsdItem.getEnd() != 0){
			context = context.substring(Math.max(0, wsdItem.getBegin() - 1000),Math.min(context.length(),wsdItem.getEnd()+1000));
		}

        return wsdMethod.getDisambiguation(
                wsdItem.getSubjectOfDisambiguation(), context);
    }
}
