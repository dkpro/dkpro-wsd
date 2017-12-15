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

package de.tudarmstadt.ukp.dkpro.wsd.resource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmCollectiveSequentialBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * A resource wrapping algorithms of type
 * {@link WSDAlgorithmCollectiveSequentialBasic}
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 *
 */
public class WSDResourceCollectiveSequentialBasic
    extends WSDResourceBasic
    implements WSDAlgorithmCollectiveSequentialBasic
{

    public final static String PARAM_DAMPING_FACTOR = "DampingFactor";
    @ConfigurationParameter(name = PARAM_DAMPING_FACTOR, mandatory = true)
    protected double dampingFactor;

    public final static String PARAM_PAGERANK_ALPHA = "PagerankAlpha";
    @ConfigurationParameter(name = PARAM_PAGERANK_ALPHA, mandatory = true)
    protected double pagerankAlpha;

    @Override
    public List<Map<String, Map<String, Double>>> getDisambiguation(
            Collection<Collection<String>> sods)
        throws SenseInventoryException
    {
        return ((WSDAlgorithmCollectiveSequentialBasic) wsdAlgorithm)
                .getDisambiguation(sods);
    }

    @Override
    public void afterResourcesInitialized() throws ResourceInitializationException
    {
        super.afterResourcesInitialized();
        ((WSDAlgorithmCollectiveSequentialBasic) wsdAlgorithm)
                .setDampingFactor(dampingFactor);
        ((WSDAlgorithmCollectiveSequentialBasic) wsdAlgorithm)
                .setPagerankAlpha(pagerankAlpha);
    }

    @Override
    public void collectionProcessComplete()
    {
        ((WSDAlgorithmCollectiveSequentialBasic) wsdAlgorithm)
                .collectionProcessComplete();

    }

    @Override
    public void setDampingFactor(double dampingFactor)
    {
        ((WSDAlgorithmCollectiveSequentialBasic) wsdAlgorithm)
                .setDampingFactor(dampingFactor);
    }

    @Override
    public void setPagerankAlpha(double pagerankAlpha)
    {
        ((WSDAlgorithmCollectiveSequentialBasic) wsdAlgorithm)
                .setPagerankAlpha(pagerankAlpha);
    }
}