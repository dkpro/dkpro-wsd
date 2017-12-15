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

import org.apache.commons.collections15.Transformer;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import edu.uci.ics.jung.graph.Graph;

/**
 * A visualizer for graphs used in graph connectivity WSD algorithms
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
interface GraphVisualizer
{
    /**
     * Initialize the visualization frame
     *
     * @param graph
     *            The graph to display
     */
    void initialize(
            final Graph<String, UnorderedPair<String>> graph);
    /**
     * Sets the dimensions (in pixels) of the animation visualization frame.
     *
     * @param width
     * @param height
     */
    void setAnimationDimensions(int width, int height);

    /**
     * Determines whether the graph is interactive.
     *
     * @param interactive
     */
    void setInteractive(boolean interactive);

    /**
     * Sets the minimum delay (in milliseconds) between animation steps.
     *
     * @param ms
     */
    void setAnimationDelay(long ms);


    /**
     * Animate the addition of an edge to the graph
     *
     * @param graph
     *            The graph to display
     * @param edge
     *            The new edge
     * @param vertex1
     *            One of the edge's vertices
     * @param vertex2
     *            The other of the edge's vertices
     */
    void animate(final Graph<String, UnorderedPair<String>> graph,
            UnorderedPair<String> edge, String vertex1, String vertex2);

    /**
     * Generate a map of numberOfColors distinct colours
     *
     * @param numberOfColors
     */
    void initializeColorMap(int numberOfColors);

    /**
     * Sets the colour for a vertex label
     *
     * @param label The label whose colour to set
     * @param colorIndex The index of the colour from the colour map
     */
    void setColor(String label, int colorIndex);

    /**
     * Refreshes the visualization display
     *
     */
    void refresh();

    /**
     * Permanently highlights the given vertex label
     *
     * @param label
     */
    void highlight(String label);

    /**
     * Sets the vertex tooltip Transformer
     */
    void setVertexToolTipTransformer(Transformer<String,String> vertexToolTipTransformer);
}
