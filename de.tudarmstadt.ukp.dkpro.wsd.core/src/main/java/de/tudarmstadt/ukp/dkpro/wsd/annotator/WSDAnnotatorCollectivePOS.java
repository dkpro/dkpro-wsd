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
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ExternalResource;

import de.tudarmstadt.ukp.dkpro.wsd.Pair;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmCollectivePOS;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * An annotator for algorithms of type {@link WSDAlgorithmCollectivePOS}
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WSDAnnotatorCollectivePOS
	extends WSDAnnotatorBaseCollective
{
	public final static String WSD_ALGORITHM_RESOURCE = "WSDAlgorithmResource";
	@ExternalResource(key = WSD_ALGORITHM_RESOURCE)
	protected WSDAlgorithmCollectivePOS wsdMethod;

	@Override
	public void initialize(UimaContext context)
		throws ResourceInitializationException
	{
		super.initialize(context);
        inventory = wsdMethod.getSenseInventory();
	}

	@Override
	protected Map<WSDItem, Map<String, Double>> getDisambiguation(
			Collection<WSDItem> wsdItems)
		throws SenseInventoryException
	{
		List<Pair<String, POS>> subjectsOfDisambiguation = new ArrayList<Pair<String, POS>>();
		for (WSDItem wsdItem : wsdItems) {
			subjectsOfDisambiguation.add(new Pair<String, POS>(wsdItem
					.getSubjectOfDisambiguation(),
					POS.valueOf(wsdItem.getPos())));
		}
		Map<Pair<String, POS>, Map<String, Double>> resultsByToken = wsdMethod
				.getDisambiguation(subjectsOfDisambiguation);
		Map<WSDItem, Map<String, Double>> resultsByWSDItem = new HashMap<WSDItem, Map<String, Double>>();
		for (WSDItem wsdItem : wsdItems) {
		    Pair<String, POS> p = new Pair<String, POS>(wsdItem.getSubjectOfDisambiguation(), POS.valueOf(wsdItem.getPos()));
            Map<String, Double> senseMap = resultsByToken.get(p);
			if (senseMap != null) {
				resultsByWSDItem.put(wsdItem, senseMap);
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

}
