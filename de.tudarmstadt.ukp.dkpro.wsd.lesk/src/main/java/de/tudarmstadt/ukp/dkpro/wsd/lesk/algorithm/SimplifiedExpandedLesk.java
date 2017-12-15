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

package de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm;

import java.util.Map;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmContextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmContextPOS;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NormalizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.OverlapStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.AbstractLexicalExpander;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.AbstractLexicalExpander.Expansion;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * A version of {@link SimplifiedLesk} whose tokenizer performs lexical
 * expansions
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class SimplifiedExpandedLesk
    extends SimplifiedLesk
    implements WSDAlgorithmContextBasic, WSDAlgorithmContextPOS
{

    private final Logger logger = Logger.getLogger(getClass());

    protected Expansion aExpansion, nExpansion, rExpansion, vExpansion;

    public SimplifiedExpandedLesk(SenseInventory inventory,
            OverlapStrategy overlapStrategy,
            NormalizationStrategy normalizationStrategy,
            AbstractLexicalExpander senseTokenizationStrategy,
            AbstractLexicalExpander contextTokenizationStrategy)
    {
        super(inventory, overlapStrategy, normalizationStrategy,
                senseTokenizationStrategy, contextTokenizationStrategy);
    }

    public void setExpansions(Expansion a, Expansion n, Expansion r, Expansion v)
    {
        logger.info("Setting a expansions: " + a);
        logger.info("Setting n expansions: " + n);
        logger.info("Setting r expansions: " + r);
        logger.info("Setting v expansions: " + v);
        aExpansion = a;
        nExpansion = n;
        rExpansion = r;
        vExpansion = v;
    }

    protected void setNumberOfExpansions(Expansion expansion)
    {
        logger.debug("Setting number of expansions to " + expansion);
        ((AbstractLexicalExpander) senseTokenizationStrategy).setNumberOfExpansions(expansion);
        ((AbstractLexicalExpander) contextTokenizationStrategy).setNumberOfExpansions(expansion);
    }

    @Override
    public Map<String, Double> getDisambiguation(String sod, POS sodPos,
            String context)
        throws SenseInventoryException
    {
        logger.debug("Disambiguating " + sod + "/" + sodPos);
        if (sodPos.equals(POS.NOUN)) {
            setNumberOfExpansions(nExpansion);
        }
        else if (sodPos.equals(POS.VERB)) {
            setNumberOfExpansions(vExpansion);
        }
        else if (sodPos.equals(POS.ADJ)) {
            setNumberOfExpansions(aExpansion);
        }
        else {
            setNumberOfExpansions(rExpansion);
        }

        // Use the Lesk algorithm to find the index of the best sense
        return super.getDisambiguation(sod, sodPos, context);
    }

}
