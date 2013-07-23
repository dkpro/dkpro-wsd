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
package de.tudarmstadt.ukp.dkpro.wsd.examples;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.util.JCasUtil.select;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.stopwordremover.StopWordRemover;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.wsd.algorithms.lesk.util.tokenization.TokenizationStrategy;

/**
 * A TokenizationStrategy for use with the Lesk family of algorithms which
 * lemmatizes and removes stop words from strings.  This class is specific to
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

	public EnglishStopLemmatizer() {
		// Set up stemmer and lemmatizer
		try {
		lemmatizer = createAggregateDescription(
				createPrimitiveDescription(BreakIteratorSegmenter.class),
				// TODO: Replace TreeTagger with a different tagger/lemmatizer
				//				createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class),
				createPrimitiveDescription(StopWordRemover.class,
						StopWordRemover.PARAM_STOP_WORD_LIST_FILE_NAMES,
						new String[] { "classpath:/stopwords/stoplist_en.txt" }));
		engine = createPrimitive(lemmatizer);
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
