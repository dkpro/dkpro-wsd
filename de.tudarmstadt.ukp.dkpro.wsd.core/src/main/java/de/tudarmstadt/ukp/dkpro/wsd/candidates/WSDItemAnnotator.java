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

package de.tudarmstadt.ukp.dkpro.wsd.candidates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.Level;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathException;
import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathFactory;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

// FIXME already discussed with NE which changes are necessary here
/**
 * This annotator takes a feature path as a parameter.
 *
 * In case of overlapping annotations and a resolveOverlaps parameter set to true:
 * a)
 * xxxx yyyy zzzz
 *      yyyy
 * We only add the larger one (xxxx yyyy zzzz) as a candidate .
 *
 * b)
 * xxxx yyyy
 *      yyyy zzzz
 * We add a merged candiate (xxxx yyyy zzzz).
 *
 * @author zesch
 */
public class WSDItemAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_FEATURE_PATH = "FeaturePath";
    @ConfigurationParameter(name=PARAM_FEATURE_PATH, mandatory=true)
    private String featurePath;

    public static final String PARAM_RESOLVE_OVERLAPS = "ResolveOverlaps";
    @ConfigurationParameter(name=PARAM_RESOLVE_OVERLAPS, mandatory=true, defaultValue="false")
    private boolean resolveOverlaps; // setting this true might not make too much sense with NGrams :)

    public static final String PARAM_ALLOW_DUPLICATES = "AllowDuplicates";
    @ConfigurationParameter(name=PARAM_ALLOW_DUPLICATES, mandatory=true, defaultValue="true")
    private boolean allowDuplicates;

    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
        getContext().getLogger().log(Level.FINE, "Entering " + this.getClass().getSimpleName());

        // get the candidates according to the parameters
        List<Candidate> candidates = new ArrayList<Candidate>();

        try {
            for (Entry<AnnotationFS, String> entry: FeaturePathFactory.select(jcas.getCas(), featurePath)) {
                candidates.add(new Candidate(entry.getValue(), entry.getKey().getBegin(), entry.getKey().getEnd()));
            }
        } catch (FeaturePathException e) {
            throw new AnalysisEngineProcessException(e);
        }

        List<Candidate> resolvedCandidates;
        if (resolveOverlaps) {
            resolvedCandidates = resolveOverlappingCandidates(candidates);
        }
        else {
            resolvedCandidates = candidates;
        }

        AnnotationIndex<Annotation> candidateIndex = jcas.getAnnotationIndex(WSDItem.type);
        for (Candidate candidate : resolvedCandidates) {
            WSDItem item = new WSDItem(jcas);
            item.setBegin(candidate.begin);
            item.setEnd(candidate.end);
            item.setSubjectOfDisambiguation(candidate.term);

            // do not allow duplicates
            if (!candidateIndex.contains(item) || allowDuplicates) {
            	item.addToIndexes(jcas);
            }
        }
    }


    private List<Candidate> resolveOverlappingCandidates(List<Candidate> candidates) {
        List<Candidate> resolvedCandidates = new ArrayList<Candidate>();

        boolean finished = false;
        while (!finished && !candidates.isEmpty()) {
            if (candidates.size() == 1) {
                resolvedCandidates.add(candidates.get(0));
                finished = true;
                continue;
            }

            Candidate firstCandidate = candidates.get(0);

            Overlap overlap = getOverlappingCandidate(firstCandidate, candidates.subList(1, candidates.size()));

            if (overlap == null) {
                // if there is no overlap, add the firstCandidate to the list of resolved cases
                resolvedCandidates.add(firstCandidate);
                // shorten candidate list
                candidates = candidates.subList(1, candidates.size());
            }
            else {
                // resolve
                Candidate resolvedCandidate = resolve(firstCandidate, overlap.candidate);

                if (resolvedCandidate != null) {
                    // Add the resolved candidate at the position of the overlap and shorten list.
                    candidates.set(overlap.offset, resolvedCandidate);
                    candidates = candidates.subList(1, candidates.size());
                }
                else {
                    // just shorten the list => remove the candidate that caused the error
                    candidates = candidates.subList(1, candidates.size());
                }
            }
        }

        return resolvedCandidates;
    }

    /**
     * @param candidate
     * @param candidateList
     * @return Returns an Overlap object containing the first candidate from candidateList overlapping with candidate. Or null, if no overlap was found.
     */
    private Overlap getOverlappingCandidate(Candidate candidate, List<Candidate> candidateList) {
        int i = 1;
        for (Candidate c : candidateList) {
            if (overlaps(c, candidate)) {
                return new Overlap(c,i);
            }
            i++;
        }
        return null;
    }

    /**
     * @param c1
     * @param c2
     * @return True, if c1 and c2 overlap.
     */
    private boolean overlaps(Candidate c1, Candidate c2) {
        if ((c1.begin == c2.begin || c1.end == c2.end) ||
            (c1.begin < c2.begin && c1.end > c2.end) ||
            (c1.begin > c2.begin && c1.end < c2.end) ||
            (c1.begin < c2.begin && c1.end > c2.begin) ||
            (c2.begin < c1.begin && c2.end > c1.begin)) {

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Resolve cases:
     * a)
     * Equal candidates. Return one.
     *
     * b)
     * xxxx yyyy zzzz
     *      yyyy
     * We only add the larger one (xxxx yyyy zzzz) as a candidate.
     *
     * c)
     * xxxx yyyy
     *      yyyy zzzz
     * We add a merged candiate (xxxx yyyy zzzz).
     *
     *
     * @param firstCandidate
     * @param secondCandidate
     * @return The resolved candidate or null.
     */
    private Candidate resolve(Candidate c1, Candidate c2) {
        // case a
        if (c1.begin == c2.begin && c1.end == c2.end) {
            getContext().getLogger().log(Level.FINEST, "Resolve case a");
            return c1;
        }
        // case b(1)
        else if (c1.begin <= c2.begin && c1.end >= c2.end) {
            getContext().getLogger().log(Level.FINEST, "Resolve case b1");
            return c1;
        }
        // case b(2)
        else if (c1.begin >= c2.begin && c1.end <= c2.end) {
            getContext().getLogger().log(Level.FINEST, "Resolve case b2");
            return c2;
        }
        // case c(1)
        else if (c1.begin < c2.begin && c1.end > c2.begin) {
            getContext().getLogger().log(Level.FINEST, "Resolve case c1");
            int mismatch = c2.begin - c1.begin;
            if (mismatch < c2.term.length()) {
                String term = c1.term.substring(0,mismatch) + c2.term;
                return new Candidate(term, c1.begin, c2.end);
            }
            else {
                return null;
            }
        }
        // case c(2)
        else if (c2.begin < c1.begin && c2.end > c1.begin) {
            getContext().getLogger().log(Level.FINEST, "Resolve case c2");
            int mismatch = c1.begin - c2.begin;
            if (mismatch < c1.term.length()) {
                String term = c2.term.substring(0,mismatch) + c1.term;
                return new Candidate(term, c2.begin, c1.end);
            }
            else {
                return null;
            }
        }
        else {
            getContext().getLogger().log(Level.WARNING, "Reached unexpected case when resolving overlaps.");
            getContext().getLogger().log(Level.WARNING, c1.toString());
            getContext().getLogger().log(Level.WARNING, c2.toString());
            return null;
        }
    }

    private class Candidate {
        private final String term;
        private final int begin;
        private final int end;
        public Candidate(String term, int begin, int end) {
            super();
            this.term = term;
            this.begin = begin;
            this.end = end;
        }
        @Override
		public String toString() {
            return term + " (" + begin + " - " + end + ")";
        }

    }

    private class Overlap {
        private final Candidate candidate;
        private final int offset;
        public Overlap(Candidate candidate, int offset) {
            super();
            this.candidate = candidate;
            this.offset = offset;
        }
        @Override
		public String toString() {
            return candidate.toString() + " #:" + offset;
        }
    }
}