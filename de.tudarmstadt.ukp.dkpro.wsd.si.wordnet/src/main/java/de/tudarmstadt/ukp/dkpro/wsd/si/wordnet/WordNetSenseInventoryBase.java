/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.commons.collections15.Transformer;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseDictionary;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * Abstract class for WordNet-like sense inventories, where synset IDs are used
 * as sense IDs.
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public abstract class WordNetSenseInventoryBase
    implements SenseDictionary, SenseTaxonomy
{

    protected Dictionary wn;
    // private final Log logger = LogFactory.getLog(getClass());

    protected UndirectedGraph<String, UnorderedPair<String>> undirectedWNGraph = null;

    protected final SiPosToWordNetPos siPosToWordNetPos = new SiPosToWordNetPos();

    protected String senseDescriptionFormat = "%w; %d";
    protected final static Pattern glossPattern = Pattern
            .compile("(.*?)(; \".*)");

    @Override
    public void setUndirectedGraph(
            UndirectedGraph<String, UnorderedPair<String>> graph)
        throws SenseInventoryException, UnsupportedOperationException
    {
        undirectedWNGraph = graph;
    }

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        List<String> senses = new ArrayList<String>();

        for (Object pos : net.sf.extjwnl.data.POS.getAllPOS()) {
            senses.addAll(getSenses(sod, (net.sf.extjwnl.data.POS) pos));
        }
        return senses;
    }

    /**
     * Given a synset offset, lemma, and POS, returns a corresponding sense key.
     *
     * @param senseId
     * @param lemma
     * @param POS
     * @return
     * @throws SenseInventoryException
     */
    public String getWordNetSenseKey(long offset, String lemma, POS pos)
        throws SenseInventoryException
    {
        return getWordNetSenseKey(offset, lemma,
                siPosToWordNetPos.transform(pos));
    }

    /**
     * Given a synset offset, lemma, and POS, returns a corresponding sense key.
     *
     * @param senseId
     * @param lemma
     * @param POS
     * @return
     * @throws SenseInventoryException
     */
    protected String getWordNetSenseKey(long offset, String lemma,
            net.sf.extjwnl.data.POS pos)
        throws SenseInventoryException
    {
        Synset s;
        try {
            s = wn.getSynsetAt(pos, offset);
        }
        catch (JWNLException e) {
            throw new SenseInventoryException(e);
        }
        for (Word w : s.getWords()) {
            if (w.getLemma().equals(lemma)) {
                return w.getSenseKey();
            }
        }
        throw new SenseInventoryException("No sense key for " + offset + "/"
                + pos);
    }

    /**
     * Given a lemma and a string representing a synset + part of speech,
     * returns a corresponding sense key.
     *
     * @param senseId
     * @param lemma
     * @return
     * @throws SenseInventoryException
     */
    abstract public String getWordNetSenseKey(String senseId, String lemma)
        throws SenseInventoryException;

    abstract protected List<String> getSenses(String sod,
            net.sf.extjwnl.data.POS pos)
        throws SenseInventoryException;

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException
    {
        return getSenses(sod, siPosToWordNetPos.transform(pos));
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        CachedWordNetSense sense = getSense(senseId);
        if (senseDescriptionFormat == null) {
            return sense.synset.getGloss();
        }
        String description = senseDescriptionFormat.replace("%d",
                sense.getDefinition());
        description = description.replace("%e", sense.getExamples().toString());
        description = description.replace("%w", sense.getSynonyms().toString());
        return description;
    }

    // /**
    // * Retrieves a sense from the cache, or creates one and adds it to the
    // cache
    // * if it's not already there
    // *
    // * @param senseId
    // * @return
    // * @throws SenseInventoryException
    // */
    // protected CachedWordNetSense getSense(String senseId)
    // throws SenseInventoryException
    // {
    // CachedWordNetSense s = senses.get(senseId);
    // if (s == null) {
    // s = new CachedWordNetSense(senseId);
    // senses.put(senseId, s);
    // }
    // return s;
    // }
    abstract protected CachedWordNetSense getSense(String senseId)
        throws SenseInventoryException;

    /**
     * Sets the format of the string to be returned by the {@link
     * getSenseDescription()} method. The following printf-style format
     * specifiers are recognized:
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
     * A class for the textual information associated with a WordNet synset
     *
     * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
     *
     */
    abstract protected class CachedWordNetSense
        implements CachedDictionarySense, CachedTaxonomySense
    {
        protected final String id;
        protected Synset synset;
        protected String definition;
        protected Set<String> examples;
        protected Set<String> synonyms;
        protected Set<String> neighbours;

        public CachedWordNetSense(String senseId)
            throws SenseInventoryException
        {
            id = senseId;
        }

        @Override
        public Set<String> getExamples()
            throws SenseInventoryException
        {
            if (examples != null) {
                return examples;
            }
            setDefinitionAndExamples();
            return examples;
        }

        @Override
        public String getDefinition()
            throws SenseInventoryException
        {
            if (definition != null) {
                return definition;
            }
            setDefinitionAndExamples();
            return definition;
        }

        abstract protected void setDefinitionAndExamples()
            throws SenseInventoryException;
    }

    /**
     * Get a set of example sentences for the given sense. If no examples exist,
     * an empty collection is returned.
     *
     * @param senseId
     *            The ID of the sense to examine.
     * @return A set of example sentences for the given sense.
     * @throws SenseInventoryException
     */
    @Override
    public Set<String> getSenseExamples(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getExamples();
    }

    /**
     * Get a set of lemmas for the given sense.
     *
     * @param senseId
     *            The ID of the sense to examine.
     * @return The set of lemmas for the given sense
     * @throws SenseInventoryException
     */
    @Override
    public Set<String> getSenseWords(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getSynonyms();
    }

    @Override
    public Set<String> getSenseNeighbours(String senseId)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return getSense(senseId).getNeighbours();
    }

    /**
     * Get the definition of the given sense.
     *
     * @param senseId
     *            The ID of the sense to examine.
     * @return The definition of the given sense.
     * @throws SenseInventoryException
     */
    @Override
    public String getSenseDefinition(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getDefinition();
    }

    /**
     * Returns "WordNet_x.y", where x.y is the version of WordNet as specified
     * by the "number" attribute of the &lt;version&gt; element of the WordNet
     * properties file. Note that extJWNL may refuse to access the WordNet
     * database when the "number" attribute is set to earlier versions.
     * Therefore if you want to use an early version of WordNet (e.g., 1.7) it
     * may be necessary to edit the properties file and increase the value of
     * the "number" attribute. Unfortunately, this means that the version
     * reported by this method will be false.
     */
    @Override
    public String getSenseInventoryName()
    {
        return "WordNet_" + wn.getVersion().getNumber();
    }

    /**
     * Transforms a POS enum to a WordNet POS
     *
     * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
     *
     */
    protected static class SiPosToWordNetPos
        implements Transformer<POS, net.sf.extjwnl.data.POS>
    {
        @Override
        public net.sf.extjwnl.data.POS transform(POS pos)
        {
            switch (pos) {
            case NOUN:
                return net.sf.extjwnl.data.POS.NOUN;
            case VERB:
                return net.sf.extjwnl.data.POS.VERB;
            case ADJ:
                return net.sf.extjwnl.data.POS.ADJECTIVE;
            case ADV:
                return net.sf.extjwnl.data.POS.ADVERB;
            }

            return null;
        }
    }

}
