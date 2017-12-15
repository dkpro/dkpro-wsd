/**
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.dkpro.wsd.si.germanet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseDictionary;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryBase;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import de.tuebingen.uni.sfs.germanet.api.Example;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Graphs;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan
 *         Miller</a>
 *
 */
public class GermaNetLexUnitSenseInventory
    extends SenseInventoryBase
    implements SenseDictionary, SenseTaxonomy
{
    private final Log logger = LogFactory.getLog(getClass());

    private final GermaNet gnet;
    protected final SiPosToGermaNetPos siPosToWordNetPos = new SiPosToGermaNetPos();
    protected UndirectedGraph<String, UnorderedPair<String>> undirectedGnetGraph = null;
    protected String senseDescriptionFormat = "%w; %d";
    protected GermaNetPosToSiPos germaNetPosToSiPos = new GermaNetPosToSiPos();

    /**
     * Returns the underlying {@link GermaNet} object.
     *
     * @return the underlying {@link GermaNet} object
     */
    public GermaNet getUnderlyingResource()
    {
        return gnet;
    }

    public GermaNetLexUnitSenseInventory(String gnetDirectory)
        throws FileNotFoundException, XMLStreamException, IOException
    {
        File gnetFile = new File(gnetDirectory);
        gnet = new GermaNet(gnetFile);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory#getSenseInventory()
     */
    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory#getSenses(java.lang.String
     * )
     */
    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        List<LexUnit> lexUnits = gnet.getLexUnits(sod);
        List<String> senses = new ArrayList<String>(lexUnits.size());
        for (LexUnit lexUnit : lexUnits) {
            senses.add(Integer.toString(lexUnit.getId()));
        }
        return senses;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory#getSenses(java.lang.String
     * , de.tudarmstadt.ukp.dkpro.wsd.si.POS)
     */
    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        List<LexUnit> lexUnits = gnet.getLexUnits(sod,
                siPosToWordNetPos.transform(pos));
        List<String> senses = new ArrayList<String>(lexUnits.size());
        for (LexUnit lexUnit : lexUnits) {
            senses.add(Integer.toString(lexUnit.getId()));
        }
        return senses;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory#getMostFrequentSense(java
     * .lang.String)
     */
    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory#getMostFrequentSense(java
     * .lang.String, de.tudarmstadt.ukp.dkpro.wsd.si.POS)
     */
    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory#getSenseDescription(java
     * .lang.String)
     */
    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        String description = senseDescriptionFormat.replace("%d",
                getSenseDefinition(senseId));
        description = description.replace("%e", getSenseExamples(senseId)
                .toString());
        description = description.replace("%w", getSenseWords(senseId)
                .toString());
        return description;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory#getSenseInventoryName()
     */
    @Override
    public String getSenseInventoryName()
    {
        return gnet.getDir();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy#getUndirectedGraph()
     */
    @Override
    public UndirectedGraph<String, UnorderedPair<String>> getUndirectedGraph()
        throws SenseInventoryException, UnsupportedOperationException
    {
        if (undirectedGnetGraph != null) {
            return undirectedGnetGraph;
        }

        logger.info("Creating sense graph for " + gnet.numLexUnits()
                + " lexical units...");
        int lexUnitCount = 0;
        undirectedGnetGraph = new UndirectedSparseGraph<String, UnorderedPair<String>>();

        String sourceLexUnitId, targetLexUnitId;
        for (LexUnit sourceLexUnit : gnet.getLexUnits()) {
            if (++lexUnitCount % 1000 == 0) {
                logger.debug("Processed " + lexUnitCount + " lexical units...");
            }
            sourceLexUnitId = Integer.toString(sourceLexUnit.getId());
            Set<LexUnit> targetLexUnits = new HashSet<LexUnit>(
                    sourceLexUnit.getRelatedLexUnits());
            targetLexUnits.addAll(sourceLexUnit.getSynset().getLexUnits());
            for (Synset synset : sourceLexUnit.getSynset().getRelatedSynsets()) {
                targetLexUnits.addAll(synset.getLexUnits());
            }
            for (LexUnit targetLexUnit : targetLexUnits) {
                targetLexUnitId = Integer.toString(targetLexUnit.getId());
                UnorderedPair<String> e = new UnorderedPair<String>(
                        sourceLexUnitId, targetLexUnitId);
                if (!undirectedGnetGraph.containsEdge(e)) {
                    undirectedGnetGraph.addEdge(e, sourceLexUnitId,
                            targetLexUnitId);
                }
            }
        }

        logger.info("# vertices = " + undirectedGnetGraph.getVertexCount()
                + "; # edges = " + undirectedGnetGraph.getEdgeCount());

        undirectedGnetGraph = Graphs
                .unmodifiableUndirectedGraph(undirectedGnetGraph);
        return undirectedGnetGraph;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy#setUndirectedGraph(edu.
     * uci.ics.jung.graph.UndirectedGraph)
     */
    @Override
    public void setUndirectedGraph(
            UndirectedGraph<String, UnorderedPair<String>> graph)
        throws SenseInventoryException, UnsupportedOperationException
    {
        undirectedGnetGraph = graph;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy#getSenseNeighbours(java
     * .lang.String)
     */
    @Override
    public Set<String> getSenseNeighbours(String senseId)
        throws SenseInventoryException, UnsupportedOperationException
    {
        LexUnit sourceLexUnit = gnet.getLexUnitByID(Integer.valueOf(senseId));
        if (sourceLexUnit == null) {
            throw new SenseInventoryException("invalid LexUnit ID " + senseId);
        }
        Set<LexUnit> targetLexUnits = new HashSet<LexUnit>(
                sourceLexUnit.getRelatedLexUnits());
        targetLexUnits.addAll(sourceLexUnit.getSynset().getLexUnits());
        for (Synset synset : sourceLexUnit.getSynset().getRelatedSynsets()) {
            targetLexUnits.addAll(synset.getLexUnits());
        }
        Set<String> targetLexUnitIds = new HashSet<String>(
                targetLexUnits.size());
        for (LexUnit targetLexUnit : targetLexUnits) {
            targetLexUnitIds.add(Integer.toString(targetLexUnit.getId()));
        }
        return targetLexUnitIds;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseDictionary#getSenseExamples(java
     * .lang.String)
     */
    @Override
    public Set<String> getSenseExamples(String senseId)
        throws SenseInventoryException
    {
        LexUnit lexUnit = gnet.getLexUnitByID(Integer.valueOf(senseId));
        if (lexUnit == null) {
            throw new SenseInventoryException("invalid LexUnit ID " + senseId);
        }
        List<Example> lexUnitExamples = lexUnit.getExamples();
        Set<String> senseExamples = new HashSet<String>(lexUnitExamples.size());
        for (Example example : lexUnitExamples) {
            senseExamples.add(example.getText());
        }
        return senseExamples;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseDictionary#getSenseWords(java.lang
     * .String)
     */
    @Override
    public Set<String> getSenseWords(String senseId)
        throws SenseInventoryException
    {
        LexUnit lexUnit = gnet.getLexUnitByID(Integer.valueOf(senseId));
        if (lexUnit == null) {
            throw new SenseInventoryException("invalid LexUnit ID " + senseId);
        }
        return new HashSet<String>(lexUnit.getSynset().getAllOrthForms());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.tudarmstadt.ukp.dkpro.wsd.si.SenseDictionary#getSenseDefinition(java
     * .lang.String)
     */
    @Override
    public String getSenseDefinition(String senseId)
        throws SenseInventoryException
    {
        LexUnit lexUnit = gnet.getLexUnitByID(Integer.valueOf(senseId));
        if (lexUnit == null) {
            throw new SenseInventoryException("invalid LexUnit ID " + senseId);
        }
        return lexUnit.getSynset().getParaphrases().toString();
    }

    @Override
    public POS getPos(String senseId)
        throws SenseInventoryException
    {
        LexUnit lexUnit = gnet.getLexUnitByID(Integer.valueOf(senseId));
        if (lexUnit == null) {
            throw new SenseInventoryException("invalid LexUnit ID " + senseId);
        }
        return germaNetPosToSiPos.transform(lexUnit.getWordCategory());
    }

    /**
     * Sets the format of the string to be returned by the
     * {@link de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory#getSenseDescription(java .lang.String)}
     * method. The following printf-style format specifiers are recognized:
     *
     * <dl>
     * <dt>%d</dt>
     * <dd>the sense's definition</dd>
     * <dt>%w</dt>
     * <dd>the sense's lemmas</dd>
     * <dt>%e</dt>
     * <dd>the sense's example sentences</dd>
     * </dl>
     *
     * Setting the format string to null instructs the sense inventory to return
     * the default description returned by extJWNL.
     *
     * @param format
     *            A format string as described in the format string syntax.
     */
    public void setSenseDescriptionFormat(String format)
    {
        senseDescriptionFormat = format;
    }

    /**
     * Transforms a POS enum to a GermaNet POS
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan
     *         Miller</a>
     *
     */
    protected static class SiPosToGermaNetPos
        implements Transformer<POS, WordCategory>
    {
        @Override
        public WordCategory transform(POS pos)
        {
            switch (pos) {
            case NOUN:
                return WordCategory.nomen;
            case VERB:
                return WordCategory.verben;
            case ADJ:
                return WordCategory.adj;
            default:
                return null;
            }
        }
    }

    /**
     * Transforms a GermaNet POS into a POS enum
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan
     *         Miller</a>
     *
     */
    protected static class GermaNetPosToSiPos
        implements Transformer<WordCategory, POS>
    {
        @Override
        public POS transform(WordCategory pos)
        {
            switch (pos) {
            case nomen:
                return POS.NOUN;
            case verben:
                return POS.VERB;
            case adj:
                return POS.ADJ;
            default:
                return null;
            }
        }
    }

    @Override
    public int getUseCount(String senseId)
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }
}
