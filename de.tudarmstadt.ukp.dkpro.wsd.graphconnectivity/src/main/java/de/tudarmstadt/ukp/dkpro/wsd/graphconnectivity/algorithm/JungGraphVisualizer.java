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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 * Handles visualizations for the graph connectivity algorithms
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class JungGraphVisualizer
    implements GraphVisualizer
{
    private Layout<String, UnorderedPair<String>> layout;
    private VisualizationViewer<String, UnorderedPair<String>> vv;
    private int animationWidth = 1280;
    private int animationHeight = 720;
    private long animationDelay = 250;
    protected VertexPainter vertexPainter;
    private boolean interactive = false;
    private final JungMouseListener mouseListener = new JungMouseListener();
    private Transformer<String, String> vertexToolTipTransformer;

    class JungMouseListener
        implements MouseListener
    {

        public boolean clicked = false;

        @Override
        public void mouseClicked(MouseEvent arg0)
        {
            clicked = true;
        }

        @Override
        public void mouseEntered(MouseEvent arg0)
        {
        }

        @Override
        public void mouseExited(MouseEvent arg0)
        {
        }

        @Override
        public void mousePressed(MouseEvent arg0)
        {
        }

        @Override
        public void mouseReleased(MouseEvent arg0)
        {
        }

    }

    @Override
    public void initialize(final Graph<String, UnorderedPair<String>> graph)
    {
        // KKLayout tries to fit the graph within a circle. Other nice layouts
        // include ISOMLayout and FRLayout.
        layout = new KKLayout<String, UnorderedPair<String>>(graph);
        layout.setSize(new Dimension(animationWidth, animationHeight));
        Relaxer relaxer = new VisRunner((IterativeContext) layout);
        relaxer.stop();
        relaxer.prerelax();

        Layout<String, UnorderedPair<String>> staticLayout = new StaticLayout<String, UnorderedPair<String>>(
                graph, layout);

        vv = new VisualizationViewer<String, UnorderedPair<String>>(
                staticLayout, new Dimension(animationWidth, animationHeight));
        vv.getRenderContext().setVertexLabelTransformer(
                new VertexLabelTransformer());

        vv.getRenderer().getVertexLabelRenderer()
                .setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.setForeground(Color.black);
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPainter);
        vv.addMouseListener(mouseListener);
        vv.setVertexToolTipTransformer(vertexToolTipTransformer);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);

        try {
            Thread.sleep(animationDelay);
        }
        catch (InterruptedException e) {
            // Not particularly catastrophic
            e.printStackTrace();
        }

    }

    @Override
    public void setAnimationDimensions(int width, int height)
    {
        animationWidth = width;
        animationHeight = height;
    }

    @Override
    public void setInteractive(boolean interactive)
    {
        this.interactive = interactive;
    }

    @Override
    public void setAnimationDelay(long ms)
    {
        animationDelay = ms;
    }

    @Override
    public void animate(final Graph<String, UnorderedPair<String>> graph,
            UnorderedPair<String> edge, String vertex1, String vertex2)
    {
        vv.getRenderContext().getPickedVertexState().clear();
        vv.getRenderContext().getPickedEdgeState().clear();
        vv.getRenderContext().getPickedVertexState().pick(vertex1, true);
        vv.getRenderContext().getPickedVertexState().pick(vertex2, true);
        vv.getRenderContext().getPickedEdgeState().pick(edge, true);
        layout.initialize();

        Relaxer relaxer = new VisRunner((IterativeContext) layout);
        relaxer.stop();
        relaxer.prerelax();
        StaticLayout<String, UnorderedPair<String>> staticLayout = new StaticLayout<String, UnorderedPair<String>>(
                graph, layout);
        LayoutTransition<String, UnorderedPair<String>> lt = new LayoutTransition<String, UnorderedPair<String>>(
                vv, vv.getGraphLayout(), staticLayout);
        Animator animator = new Animator(lt);
        animator.start();
        vv.repaint();
        try {
            Thread.sleep(animationDelay);
        }
        catch (InterruptedException e) {
            // Not particularly catastrophic
            e.printStackTrace();
        }

        // TODO: This is probably an unidiomatic way of checking for a button
        // press. It should be replaced with better event handling code.
        if (interactive == true) {
            while (mouseListener.clicked == false) {
                try {
                    Thread.sleep(250);
                }
                catch (InterruptedException e) {
                    // Not particularly catastrophic
                    e.printStackTrace();
                }
            }
            mouseListener.clicked = false;
        }
    }

    /**
     * Determines how vertex (i.e., sense) labels will be visualized
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    protected static class VertexLabelTransformer
        implements Transformer<String, String>
    {

        @Override
        public String transform(String s)
        {
            return s;
        }
    }

    /**
     * Associate a unique color with each subject of disambiguation
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    protected class VertexPainter
        implements Transformer<String, Paint>
    {

        private final Color[] colors;
        Map<String, Color> colorMap = new HashMap<String, Color>();

        /**
         * Generate an array of unique colours
         *
         * @param num_colors
         *            Number of colours to generate
         */
        public VertexPainter(int num_colors)
        {
            colors = new Color[num_colors];
            for (int i = 0; i < num_colors; i++) {
                colors[i] = Color.getHSBColor((float) i / num_colors, 0.75f,
                        0.75f);
            }
        }

        /**
         * Permanently highlights the given vertex label
         *
         * @param label
         */
        public void highlight(String label)
        {
            colorMap.put(label, colorMap.get(label).brighter());
        }

        /**
         * Sets the colour for a vertex label
         *
         * @param label
         *            The label whose colour to set
         * @param index
         *            The index of the colour from the colour map
         */
        public void setColor(String label, int index)
        {
            colorMap.put(label, colors[index]);
        }

        @Override
        public Paint transform(String label)
        {
            Color c = colorMap.get(label);
            return (c == null) ? Color.gray : c;
        }
    }

    @Override
    public void initializeColorMap(int numberOfColors)
    {
        vertexPainter = new VertexPainter(numberOfColors);
    }

    @Override
    public void setColor(String label, int colorIndex)
    {
        vertexPainter.setColor(label, colorIndex);
    }

    @Override
    public void refresh()
    {
        vv.getRenderContext().getPickedVertexState().clear();
        vv.getRenderContext().getPickedEdgeState().clear();
        vv.repaint();
    }

    @Override
    public void highlight(String label)
    {
        vertexPainter.highlight(label);
    }

    @Override
    public void setVertexToolTipTransformer(
            Transformer<String, String> vertexToolTipTransformer)
    {
        this.vertexToolTipTransformer = vertexToolTipTransformer;
    }

}
