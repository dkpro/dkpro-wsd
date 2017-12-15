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

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * An abstract annotator which calls the disambiguation algorithm once for each
 * {@link WSDItem} in the document.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class WSDAnnotatorBaseIndividual
	extends WSDAnnotatorBase
{

	protected abstract Map<String, Double> getDisambiguation(JCas aJCas,
			WSDItem wsdItem)
		throws SenseInventoryException;

	@Override
	public void process(JCas aJCas)
		throws AnalysisEngineProcessException
	{

		try {

			for (WSDItem wsdItem : JCasUtil.select(aJCas, WSDItem.class)) {
                if (maxItemsAttempted >= 0 && numItemsAttempted++ >= maxItemsAttempted) {
                    return;
                }
				setWSDItem(aJCas, wsdItem, getDisambiguation(aJCas, wsdItem));
			}
		}
		catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}