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

package de.tudarmstadt.ukp.dkpro.wsd.lesk.resource;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.SimplifiedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NormalizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.OverlapStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.TokenizationStrategy;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceContextPOS;

/**
 * A resource for {@link SimplifiedLesk}.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class WSDResourceSimplifiedLesk
    extends WSDResourceContextPOS
{

    public final static String PARAM_OVERLAP_STRATEGY = "overlapStrategyClass";
    @ConfigurationParameter(name = PARAM_OVERLAP_STRATEGY, mandatory = true)
    protected String overlapStrategyClass;

    public final static String PARAM_NORMALIZATION_STRATEGY = "normalizationStrategyClass";
    @ConfigurationParameter(name = PARAM_NORMALIZATION_STRATEGY, mandatory = true)
    protected String normalizationStrategyClass;

    public final static String PARAM_TOKENIZATION_STRATEGY = "tokenizationStrategyClass";
    @ConfigurationParameter(name = PARAM_TOKENIZATION_STRATEGY, mandatory = true, description = "The default tokenization strategy to be used")
    protected String defaultTokenizationStrategyClass;

    public final static String PARAM_CONTEXT_TOKENIZATION_STRATEGY = "contextTokenizationStrategyClass";
    @ConfigurationParameter(name = PARAM_CONTEXT_TOKENIZATION_STRATEGY, mandatory = false, description = "The tokenization strategy to be used for the context only; overrides the default tokenization strategy")
    protected String contextTokenizationStrategyClass;

    public final static String PARAM_PREFILTER_SOD = "prefilterSod";
    @ConfigurationParameter(name = PARAM_PREFILTER_SOD, mandatory = false, description = "Whether to use filter the subject of disambiguation from the untokenized contexts and sense descriptions", defaultValue = "false")
    protected String prefilterSod;

    public final static String PARAM_POSTFILTER_SOD = "postfilterSod";
    @ConfigurationParameter(name = PARAM_POSTFILTER_SOD, mandatory = false, description = "Whether to use filter the subject of disambiguation from the tokenized contexts and sense descriptions", defaultValue = "false")
    protected String postfilterSod;

    // TODO: Implement the following
    // public final static String PARAM_FORWARD_CONTEXT_SIZE =
    // "ForwardContextSize";
    // @ConfigurationParameter(name = PARAM_FORWARD_CONTEXT_SIZE, mandatory =
    // false, defaultValue = "-1",
    // description =
    // "The number of words of forward context to consider, or -1 for all")
    // protected int forwardContextSize;
    //
    // public final static String PARAM_BACKWARD_CONTEXT_SIZE =
    // "BackwardContextSize";
    // @ConfigurationParameter(name = PARAM_BACKWARD_CONTEXT_SIZE, mandatory =
    // false, defaultValue = "-1",
    // description =
    // "The number of words of backward context to consider, or -1 for all")
    // protected int backwardContextSize;

    protected OverlapStrategy overlapStrategy;
    protected TokenizationStrategy defaultTokenizationStrategy;
    protected TokenizationStrategy contextTokenizationStrategy;
    protected NormalizationStrategy normalizationStrategy;

    @SuppressWarnings("unchecked")
    protected void initializeStrategies()
        throws ResourceInitializationException
    {
        Class<OverlapStrategy> osClass;
        Class<NormalizationStrategy> nsClass;
        Class<TokenizationStrategy> dtsClass;
        Class<TokenizationStrategy> ctsClass;
        try {
            osClass = (Class<OverlapStrategy>) Class
                    .forName(overlapStrategyClass);
            nsClass = (Class<NormalizationStrategy>) Class
                    .forName(normalizationStrategyClass);
            dtsClass = (Class<TokenizationStrategy>) Class
                    .forName(defaultTokenizationStrategyClass);
            overlapStrategy = osClass.newInstance();
            normalizationStrategy = nsClass.newInstance();
            defaultTokenizationStrategy = dtsClass.newInstance();
            contextTokenizationStrategy = defaultTokenizationStrategy;
        }
        catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
        if (contextTokenizationStrategyClass != null) {
            try {
                ctsClass = (Class<TokenizationStrategy>) Class
                        .forName(contextTokenizationStrategyClass);
                contextTokenizationStrategy = ctsClass.newInstance();
            }
            catch (Exception e) {
                throw new ResourceInitializationException(e);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        initializeStrategies();

        return true;
    }

    @Override
    public void afterResourcesInitialized() throws ResourceInitializationException
    {
        super.afterResourcesInitialized();
        initializeWsdAlgorithm();
        ((SimplifiedLesk) wsdAlgorithm).setPrefilterSod(Boolean
                .valueOf(prefilterSod));
        ((SimplifiedLesk) wsdAlgorithm).setPostfilterSod(Boolean
                .valueOf(postfilterSod));
    }

    protected void initializeWsdAlgorithm()
    {
        wsdAlgorithm = new SimplifiedLesk(inventory, overlapStrategy,
                normalizationStrategy, defaultTokenizationStrategy,
                contextTokenizationStrategy);
    }
}