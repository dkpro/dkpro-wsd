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

import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import edu.uci.ics.jung.graph.Graph;

/**
 * A resource wrapping {@link GraphVisualizer}
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class GraphVisualizerResource
    extends Resource_ImplBase
    implements GraphVisualizer
{
    public static final String PARAM_ANIMATION_DELAY = "animationDelay";
    @ConfigurationParameter(name = PARAM_ANIMATION_DELAY, description = "The minimum delay (in milliseconds) between animation steps", mandatory = false)
    protected String animationDelay;

    public static final String PARAM_FRAME_WIDTH = "frameWidth";
    @ConfigurationParameter(name = PARAM_FRAME_WIDTH, description = "The width (in pixels) of the animation frame", mandatory = false)
    protected String frameWidth;

    public static final String PARAM_FRAME_HEIGHT = "frameHeight";
    @ConfigurationParameter(name = PARAM_FRAME_HEIGHT, description = "The height (in pixels) of the animation frame", mandatory = false)
    protected String frameHeight;

    public static final String PARAM_INTERACTIVE = "interactive";
    @ConfigurationParameter(name = PARAM_INTERACTIVE, description = "Whether the visualization should be interactive", mandatory = false)
    protected String interactive;

    public GraphVisualizer graphVisualizer;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }
        graphVisualizer = new JungGraphVisualizer();
        if (animationDelay != null) {
            setAnimationDelay(Long.valueOf(animationDelay));
        }
        if (frameHeight != null && frameWidth != null) {
            setAnimationDimensions(Integer.valueOf(frameWidth),
                    Integer.valueOf(frameHeight));
        }
        if (interactive != null) {
            setInteractive(Boolean.valueOf(interactive));
        }
        return true;
    }

    @Override
    public void initialize(Graph<String, UnorderedPair<String>> graph)
    {
        graphVisualizer.initialize(graph);
    }

    @Override
    public void setAnimationDimensions(int width, int height)
    {
        graphVisualizer.setAnimationDimensions(width, height);
    }

    @Override
    public void setAnimationDelay(long ms)
    {
        graphVisualizer.setAnimationDelay(ms);
    }

    @Override
    public void animate(Graph<String, UnorderedPair<String>> graph,
            UnorderedPair<String> edge, String vertex1, String vertex2)
    {
        graphVisualizer.animate(graph, edge, vertex1, vertex2);
    }

    @Override
    public void initializeColorMap(int numberOfColors)
    {
        graphVisualizer.initializeColorMap(numberOfColors);
    }

    @Override
    public void setColor(String label, int colorIndex)
    {
        graphVisualizer.setColor(label, colorIndex);
    }

    @Override
    public void refresh()
    {
        graphVisualizer.refresh();
    }

    @Override
    public void highlight(String label)
    {
        graphVisualizer.highlight(label);
    }

    @Override
    public void setInteractive(boolean interactive)
    {
        graphVisualizer.setInteractive(interactive);
    }

    @Override
    public void setVertexToolTipTransformer(
            Transformer<String, String> vertexToolTipTransformer)
    {
        graphVisualizer.setVertexToolTipTransformer(vertexToolTipTransformer);
    }

}
