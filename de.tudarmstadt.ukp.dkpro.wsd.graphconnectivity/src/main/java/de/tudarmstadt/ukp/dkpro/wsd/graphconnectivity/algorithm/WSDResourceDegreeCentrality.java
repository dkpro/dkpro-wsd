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
package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.algorithm;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceCollectivePOS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;

/**
 * A resource for {@link DegreeCentralityWSD}.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WSDResourceDegreeCentrality
    extends WSDResourceCollectivePOS
{
    public final static String PARAM_SEARCH_DEPTH = "searchDepth";
    @ConfigurationParameter(name = PARAM_SEARCH_DEPTH, mandatory = true, description = "The maximum search depth for the depth-first search")
    protected String searchDepth;

    public final static String PARAM_MINIMUM_DEGREE = "minDegree";
    @ConfigurationParameter(name = PARAM_MINIMUM_DEGREE, mandatory = false, description = "The degree score below which the algorithm will not attempt a sense assignment", defaultValue = "1")
    protected String minDegree;

    public final static String GRAPH_VISUALIZER_RESOURCE = "GraphVisualizer";
    @ExternalResource(key = GRAPH_VISUALIZER_RESOURCE, mandatory = false)
    protected GraphVisualizer graphVisualizer;

    @Override
    public void afterResourcesInitialized() throws ResourceInitializationException
    {
        super.afterResourcesInitialized();
        wsdAlgorithm = new DegreeCentralityWSD((SenseTaxonomy) inventory);
        ((DegreeCentralityWSD) wsdAlgorithm).setSearchDepth(Integer
                .valueOf(searchDepth));
        ((DegreeCentralityWSD) wsdAlgorithm).setMinDegree(Integer
                .valueOf(minDegree));
        ((DegreeCentralityWSD) wsdAlgorithm).setGraphVisualizer(graphVisualizer);
    }

}
