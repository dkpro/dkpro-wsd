/*******************************************************************************
 * Copyright 2013
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
 *
 ******************************************************************************/

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.examples;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stopwordremover.StopWordRemover;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.tokenization.TokenizationStrategy;

/**
 * A TokenizationStrategy for use with the Lesk family of algorithms which
 * lemmatizes and removes stop words from strings. This class is specific to
 * English.
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class EnglishStopLemmatizer
    implements TokenizationStrategy
{
    private AnalysisEngineDescription lemmatizer;
    private final Pattern pattern = Pattern.compile("\\w");
    private AnalysisEngine engine;

    public EnglishStopLemmatizer()
    {
        // Set up stemmer and lemmatizer
        try {
            lemmatizer = createEngineDescription(
                    createEngineDescription(BreakIteratorSegmenter.class),
                    createEngineDescription(StanfordLemmatizer.class),
                    createEngineDescription(
                            StopWordRemover.class,
                            StopWordRemover.PARAM_STOP_WORD_LIST_FILE_NAMES,
                            new String[] { "classpath:/stopwords/stoplist_en.txt" }));
            engine = createEngine(lemmatizer);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a string of words and returns a list of lemmatized forms with
     * non-alphabetic and stop words removed
     *
     * @param text
     * @return
     * @throws Exception
     */
    @Override
    public List<String> tokenize(String text)
    {
        List<String> lemmas = new ArrayList<String>();

        JCas jcas = null;

        try {
            jcas = engine.newJCas();
            jcas.setDocumentLanguage("en");
            jcas.setDocumentText(text);
            engine.process(jcas);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for (Lemma l : select(jcas, Lemma.class)) {
            if (pattern.matcher(l.getValue()).find()) {
                lemmas.add(l.getValue().toLowerCase());
            }
        }

        return lemmas;
    }

}
