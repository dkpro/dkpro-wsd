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

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.lesk.resource;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.SimplifiedAlignedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;

/**
 * A resource for {@link SimplifiedAlignedLesk}.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WSDResourceSimplifiedAlignedLesk
    extends WSDResourceSimplifiedLesk
{
    @Override
    protected void initializeWsdAlgorithm()
    {
        wsdAlgorithm = new SimplifiedAlignedLesk((SenseTaxonomy) inventory,
                overlapStrategy, normalizationStrategy,
                defaultTokenizationStrategy, contextTokenizationStrategy);
    }

}
