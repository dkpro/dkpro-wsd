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

import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.SimplifiedExpandedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.SimplifiedExtendedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.AbstractLexicalExpander;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.AbstractLexicalExpander.Expansion;

/**
 * A resource for {@link SimplifiedExtendedLesk}.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WSDResourceSimplifiedExpandedLesk
    extends WSDResourceSimplifiedLesk
{
    public final static String PARAM_N_EXPANSIONS = "nExpansions";
    @ConfigurationParameter(name = PARAM_N_EXPANSIONS, mandatory = true)
    protected String nExpansions;

    public final static String PARAM_A_EXPANSIONS = "aExpansions";
    @ConfigurationParameter(name = PARAM_A_EXPANSIONS, mandatory = true)
    protected String aExpansions;

    public final static String PARAM_V_EXPANSIONS = "vExpansions";
    @ConfigurationParameter(name = PARAM_V_EXPANSIONS, mandatory = true)
    protected String vExpansions;

    public final static String PARAM_R_EXPANSIONS = "rExpansions";
    @ConfigurationParameter(name = PARAM_R_EXPANSIONS, mandatory = true)
    protected String rExpansions;

    @Override
    protected void initializeWsdAlgorithm()
    {
        wsdAlgorithm = new SimplifiedExpandedLesk(inventory,
                overlapStrategy, normalizationStrategy,
                (AbstractLexicalExpander) defaultTokenizationStrategy, (AbstractLexicalExpander) contextTokenizationStrategy);
        ((SimplifiedExpandedLesk) wsdAlgorithm).setExpansions(new Expansion(aExpansions), new Expansion(nExpansions), new Expansion(rExpansions), new Expansion(vExpansions));
    }

}
