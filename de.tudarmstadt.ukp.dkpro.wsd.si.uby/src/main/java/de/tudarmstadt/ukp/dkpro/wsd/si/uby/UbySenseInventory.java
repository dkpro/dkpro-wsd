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
package de.tudarmstadt.ukp.dkpro.wsd.si.uby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseAlignment;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseDictionary;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryBase;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import de.tudarmstadt.ukp.lmf.api.Uby;
import de.tudarmstadt.ukp.lmf.model.core.LexicalEntry;
import de.tudarmstadt.ukp.lmf.model.core.Lexicon;
import de.tudarmstadt.ukp.lmf.model.core.Sense;
import de.tudarmstadt.ukp.lmf.model.core.TextRepresentation;
import de.tudarmstadt.ukp.lmf.model.enums.EPartOfSpeech;
import de.tudarmstadt.ukp.lmf.model.enums.ESenseAxisType;
import de.tudarmstadt.ukp.lmf.model.meta.Frequency;
import de.tudarmstadt.ukp.lmf.model.multilingual.SenseAxis;
import de.tudarmstadt.ukp.lmf.model.semantics.MonolingualExternalRef;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseExample;
import de.tudarmstadt.ukp.lmf.model.semantics.SenseRelation;
import de.tudarmstadt.ukp.lmf.model.semantics.Synset;
import de.tudarmstadt.ukp.lmf.model.semantics.SynsetRelation;
import de.tudarmstadt.ukp.lmf.transform.DBConfig;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * A sense inevntory for UBY
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class UbySenseInventory extends SenseInventoryBase
    implements SenseTaxonomy, SenseDictionary, SenseAlignment
{
    protected Uby uby;
    protected Lexicon lexicon;
    protected boolean allowMultiLingualAlignments = false;
    private final static SiPosToUbyPos siPosToUbyPos = new SiPosToUbyPos();
    private final static UbyPosToSiPos ubyPosToSiPos = new UbyPosToSiPos();
    private final Log logger = LogFactory.getLog(getClass());

    // Variables and cache for sense descriptions
    private String senseDescriptionFormat = "%w; %d";
    private final Map<String, CachedSense> senses = new HashMap<String, CachedSense>();

    /**
     * Returns the underlying {@link Uby} object.
     *
     * @return the underlying {@link Uby} object
     */
    public Uby getUnderlyingResource() {
        return uby;
    }

    public UbySenseInventory(DBConfig dbConfig)
        throws SenseInventoryException
    {
        try {
            uby = new Uby(dbConfig);
        }
        catch (IllegalArgumentException e) {
            throw new SenseInventoryException(e);
        }
    }

    public void setSenseDescriptionFormat(String format)
    {
        if (format == null) {
            senseDescriptionFormat = "%d";
        }
        else {
            senseDescriptionFormat = format;
        }
    }

    /**
     * Filter all queries by the given lexicon
     *
     * @param lexiconName
     *            The name of the lexicon to filter on, or null if no filter
     *            should be applied.
     * @throws SenseInventoryException
     */
    public void setLexicon(String lexiconName)
        throws SenseInventoryException
    {
        if (lexiconName == null) {
            this.lexicon = null;
            return;
        }

        Lexicon lexicon;
        try {
            lexicon = uby.getLexiconByName(lexiconName);
        }
        catch (IllegalArgumentException e) {
            throw new SenseInventoryException(e);
        }
        if (this.lexicon != null) {
            // Flush the sense cache
            senses.clear();
        }
        this.lexicon = lexicon;
    }

    /**
     * Determines whether {@link #getSenseAlignments(String)} should also return
     * alignments to senses in other languages.
     *
     * @param allow
     */
    public void setAllowMultilingualAlignments(boolean allow)
    {
        this.allowMultiLingualAlignments = allow;
        flushSenseAlignmentCache();
    }

    private void flushSenseAlignmentCache()
    {
        if (senses == null) {
            return;
        }
        for (CachedSense s : senses.values()) {
            s.alignments = null;
        }
    }

    /**
     *
     * @param url
     *            Host_to_the_database/database_name
     * @param jdbc_driver_class
     *            The jdbc driver class using to access database
     * @param db_vendor
     * @param user
     *            Password for accessing the database
     * @param password
     *            Database name
     * @param showSQL
     *            If true all SQL queries are printed on the console
     * @throws SenseInventoryException
     */
    public UbySenseInventory(String url, String jdbc_driver_class,
            String db_vendor, String user, String password, boolean showSQL)
        throws SenseInventoryException
    {
        this(new DBConfig(url, jdbc_driver_class, db_vendor, user, password,
                showSQL));
    }

    @SuppressWarnings("unused")
    private UbySenseInventory()
    {
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
        return getSenses(sod, null);
    }

    /**
     * Get a list of Uby lexical entries for a given lemma and part of speech.
     * Because our POS tags are more coarse-grained than the ones used by Uby,
     * we need to call Uby's getLexicalEntries() multiple times and merge the
     * results.
     *
     * @param lemma
     * @param pos
     * @return
     */
    protected List<LexicalEntry> getLexicalEntriesByPOS(String lemma, POS pos)
    {
        if (pos == null) {
            return uby.getLexicalEntries(lemma, null, lexicon);
        }
        List<LexicalEntry> entries = new ArrayList<LexicalEntry>();
        for (EPartOfSpeech ubyPOS : siPosToUbyPos.transform(pos)) {
            entries.addAll(uby.getLexicalEntries(lemma, ubyPOS, lexicon));
        }
        return entries;
    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        List<LexicalEntry> entries = getLexicalEntriesByPOS(
                sod.replace('_', ' '), pos);
        List<String> senses = new ArrayList<String>();
        for (LexicalEntry lexicalEntry : entries) {
            for (Sense sense : lexicalEntry.getSenses()) {
                senses.add(sense.getId());
            }
        }
        return senses;
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return getMostFrequentSense(sod, null);
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        // TODO: Implement a cache as this operation is expensive

        List<LexicalEntry> entries = getLexicalEntriesByPOS(
                sod.replace('_', ' '), pos);
        Sense mostFrequentSense = null;
        int maxFrequency = Integer.MIN_VALUE;
        for (LexicalEntry lexicalEntry : entries) {
            for (Sense sense : lexicalEntry.getSenses()) {
                int senseFrequency = 0;
                boolean foundFrequencies = false;

                // Sum frequencies over all corpora and generators
                for (Frequency frequency : sense.getFrequencies()) {
                    senseFrequency += frequency.getFrequency();
                    foundFrequencies = true;
                }
                if (foundFrequencies && senseFrequency > maxFrequency) {
                    maxFrequency = senseFrequency;
                    mostFrequentSense = sense;
                }
            }
        }

        if (mostFrequentSense != null) {
            return mostFrequentSense.getId();
        }
        else {
            return null;
        }
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        CachedSense sense = getSense(senseId);
        String description = senseDescriptionFormat.replace("%d",
                sense.getDefinition());
        description = description.replace("%e", sense.getExamples().toString());
        description = description.replace("%w", sense.getSynonyms().toString());
        return description;
    }

    @Override
    public POS getPos(String senseId)
        throws SenseInventoryException
    {
        CachedSense sense = getSense(senseId);
        return sense.getPos();
    }

    @Override
    public int getUseCount(String senseId)
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSenseInventoryName()
    {
        if (lexicon == null) {
            return "Uby";
        }
        else {
            return "Uby_" + lexicon.getName();
        }
    }

    @Override
    public UndirectedGraph<String, UnorderedPair<String>> getUndirectedGraph()
        throws SenseInventoryException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUndirectedGraph(
            UndirectedGraph<String, UnorderedPair<String>> graph)
        throws SenseInventoryException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getSenseNeighbours(String senseId)
        throws SenseInventoryException, UnsupportedOperationException
    {
        return getSense(senseId).getNeighbours();
    }

    /**
     * Transforms a POS enum to a Uby POS
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    public static class SiPosToUbyPos
        implements Transformer<POS, EPartOfSpeech[]>
    {
        protected final EPartOfSpeech UbyNounPOS[] = { EPartOfSpeech.noun,
                EPartOfSpeech.nounCommon, EPartOfSpeech.nounProper,
                EPartOfSpeech.nounProperFirstName,
                EPartOfSpeech.nounProperLastName };
        protected final EPartOfSpeech UbyVerbPOS[] = { EPartOfSpeech.verb,
                EPartOfSpeech.verbAuxiliary, EPartOfSpeech.verbMain,
                EPartOfSpeech.verbModal };
        protected final EPartOfSpeech UbyAdjectivePOS[] = { EPartOfSpeech.adjective };
        protected final EPartOfSpeech UbyAdverbPOS[] = { EPartOfSpeech.adverb };

        @Override
        public EPartOfSpeech[] transform(POS pos)
        {
            if (pos == null) {
                return null;
            }

            switch (pos) {
            case NOUN:
                return UbyNounPOS;
            case VERB:
                return UbyVerbPOS;
            case ADJ:
                return UbyAdjectivePOS;
            case ADV:
                return UbyAdverbPOS;
            }

            return null;
        }
    }

    /**
     * Transforms a Uby POS to a POS enum
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    public static class UbyPosToSiPos
        implements Transformer<EPartOfSpeech, POS>
    {
        @Override
        public POS transform(EPartOfSpeech pos)
        {
            if (pos == null) {
                return null;
            }

            switch (pos) {
            case noun:
            case nounCommon:
            case nounProper:
            case nounProperFirstName:
            case nounProperLastName:
                return POS.NOUN;
            case verb:
            case verbAuxiliary:
            case verbMain:
            case verbModal:
                return POS.VERB;
            case adjective:
                return POS.ADJ;
            case adverb:
                return POS.ADV;
            default:
                return null;
            }
        }
    }

    @Override
    public Set<String> getSenseExamples(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getExamples();
    }

    @Override
    public Set<String> getSenseWords(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getSynonyms();
    }

    @Override
    public String getSenseDefinition(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getDefinition();
    }

    /**
     * Given a Uby sense ID, return the sense ID used by the underlying lexicon
     *
     * @param senseId
     * @return the sense ID used by the underlying lexicon
     * @throws SenseInventoryException
     */
    public String getLexiconSenseId(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getLexiconSenseId();
    }

    /**
     * Given a Uby sense ID, return the synset ID used by the underlying lexicon
     *
     * @param senseId
     * @return the synset ID used by the underlying lexicon
     * @throws SenseInventoryException
     */
    public String getLexiconSynsetId(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getLexiconSynsetId();
    }

    /**
     * Returns a set of alignments for the given sense
     *
     * @param senseId
     *            The ID of the sense whose alignments should be found
     * @return A (possibly empty) set of sense IDs for aligned senses
     *
     * @throws SenseInventoryException
     */
    @Override
    public Set<String> getSenseAlignments(String senseId)
        throws SenseInventoryException
    {
        return getSense(senseId).getAlignments();
    }

    private CachedSense getSense(String senseId)
        throws SenseInventoryException
    {
        CachedSense s = senses.get(senseId);
        if (s == null) {
            s = new CachedSense(senseId);
            senses.put(senseId, s);
        }
        return s;
    }

    private class CachedSense
        implements CachedDictionarySense, CachedTaxonomySense,
        CachedAlignedSense
    {
        private final String id;
        private final Sense sense;
        private final String definition;
        private final Synset synset;
        private final POS pos;
        private final int useCount;
        private Set<String> examples;
        private Set<String> words;
        private Set<String> neighbours;
        private Set<String> alignments;
        private String lexiconSenseId = null;
        private String lexiconSynsetId = null;
        private boolean foundLexiconSenseId = false;
        private boolean foundLexiconSynsetId = false;

        @SuppressWarnings("unused")
        private CachedSense()
        {
            id = null;
            sense = null;
            definition = null;
            synset = null;
            pos = null;
            useCount = 0;
        }

        public int getUseCount()
        {
            return useCount;
        }

        public POS getPos()
        {
            return pos;
        }

        public String getLexiconSenseId()
        {
            if (foundLexiconSenseId) {
                return lexiconSenseId;
            }
            foundLexiconSenseId = true;

            List<MonolingualExternalRef> externalReferences = sense
                    .getMonolingualExternalRefs();
            if (externalReferences == null || externalReferences.isEmpty()) {
                return null;
            }
            if (externalReferences.size() > 1) {
                logger.warn("Sense " + id
                        + " has more than one external reference");
            }

            MonolingualExternalRef externalReference = externalReferences
                    .get(0);
            if (externalReference == null) {
                return null;
            }

            lexiconSenseId = externalReference.getExternalReference();
            return lexiconSenseId;
        }

        public String getLexiconSynsetId()
        {
            if (foundLexiconSynsetId) {
                return lexiconSynsetId;
            }
            foundLexiconSynsetId = true;

            if (synset == null) {
                return null;
            }

            List<MonolingualExternalRef> externalReferences = synset
                    .getMonolingualExternalRefs();
            if (externalReferences == null || externalReferences.isEmpty()) {
                return null;
            }
            if (externalReferences.size() > 1) {
                logger.warn("Synset for sense " + id
                        + " has more than one external reference");
            }

            MonolingualExternalRef externalReference = externalReferences
                    .get(0);
            if (externalReference == null) {
                return null;
            }

            lexiconSynsetId = externalReference.getExternalReference();
            return lexiconSynsetId;
        }

        public CachedSense(String senseId)
            throws SenseInventoryException
        {
            id = senseId;
            try {
                sense = uby.getSenseById(senseId);
                synset = sense.getSynset();
                pos = ubyPosToSiPos.transform(sense.getLexicalEntry().getPartOfSpeech());

                // Sum frequencies over all corpora and generators
                int senseFrequency = 0;
                for (Frequency frequency : sense.getFrequencies()) {
                    senseFrequency += frequency.getFrequency();
                }
                useCount = senseFrequency;
            }
            catch (IllegalArgumentException e) {
                throw new SenseInventoryException(e);
            }
            definition = constructDefinition();
        }

        private String constructDefinition()
        {
            String definition = sense.getDefinitionText();
            if (definition != null && definition.length() > 0) {
                return definition;
            }
            if (synset == null) {
                return "";
            }
            definition = synset.getDefinitionText();
            if (definition != null && definition.length() > 0) {
                return definition;
            }
            return "";
        }

        /**
         * Returns a set of alignments for the given sense
         *
         * @throws SenseInventoryException
         */
        @Override
        public Set<String> getAlignments()
            throws SenseInventoryException
        {
            if (alignments != null) {
                return alignments;
            }

            List<SenseAxis> alignedSenses = uby.getSenseAxesBySense(sense);
            alignments = new HashSet<String>(alignedSenses.size());
            for (SenseAxis axis : alignedSenses) {
                if (allowMultiLingualAlignments == false
                        && axis.getSenseAxisType() != ESenseAxisType.monolingualSenseAlignment) {
                    continue;
                }
                Sense alignedSense = sense.equals(axis.getSenseOne()) ? axis
                        .getSenseTwo() : axis.getSenseOne();
                alignments.add(alignedSense.getId());
            }
            return alignments;
        }

        @Override
        public Set<String> getNeighbours()
            throws SenseInventoryException
        {
            if (neighbours != null) {
                return neighbours;
            }

            neighbours = new HashSet<String>();

            List<SenseRelation> senseRelations = sense.getSenseRelations();

            if (senseRelations != null && !senseRelations.isEmpty()) {
                for (SenseRelation senseRelation : senseRelations) {
                    Sense target = senseRelation.getTarget();
                    if (target != null) {
                        neighbours.add(target.getId());
                    }
                }
            }

            if (synset != null) {
                List<SynsetRelation> synsetRelations = synset
                        .getSynsetRelations();

                if (synsetRelations != null && !synsetRelations.isEmpty()) {
                    for (SynsetRelation synsetRelation : synsetRelations) {
                        Synset target = synsetRelation.getTarget();
                        if (target != null) {
                            for (Sense neighbour : target.getSenses()) {
                                neighbours.add(neighbour.getId());
                            }
                        }
                    }
                }
            }

            return neighbours;
        }

        @Override
        public Set<String> getSynonyms()
            throws SenseInventoryException
        {
            if (words != null) {
                return words;
            }

            words = new HashSet<String>();

            // Add this sense's lemma to the set
            words.add(sense.getLexicalEntry().getLemmaForm());

            // First thing to check: Are we part of a synset? If so, we can
            // find the synonyms through the synset.
            if (synset != null) {
                for (Sense s : synset.getSenses()) {
                    words.add(s.getLexicalEntry().getLemmaForm());
                }
            }

            // Second thing to check: See if we have any sense relations of type
            // "SYNONYM":
            for (SenseRelation senseRelation : sense.getSenseRelations()) {
                if (senseRelation.getRelName().equals("SYNONYM")
                        && senseRelation.getFormRepresentation() != null) {
                    words.add(senseRelation.getFormRepresentation()
                            .getWrittenForm());
                }
            }

            return words;
        }

        @Override
        public Set<String> getExamples()
            throws SenseInventoryException
        {
            if (examples != null) {
                return examples;
            }

            examples = new HashSet<String>();
            List<SenseExample> exampleList = sense.getSenseExamples();
            for (SenseExample example : exampleList) {
                List<TextRepresentation> textRepresentations = example
                        .getTextRepresentations();
                for (TextRepresentation textRepresentation : textRepresentations) {
                    examples.add(textRepresentation.getWrittenText());
                }
            }
            return examples;
        }

        @Override
        public String getDefinition()
            throws SenseInventoryException
        {
            return definition;
        }
    }

}
