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
package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.candidates;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseConverter;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.resource.WordNetSenseInventoryResourceBase;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Converts WordNet synset offsets+POS to sense keys
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WordNetSynsetToSenseKey
    extends SenseConverter
{
    private final Logger logger = Logger.getLogger(getClass());

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        String fromSense, toSense;
        for (WSDResult r : JCasUtil.select(aJCas, WSDResult.class)) {
            if (r.getSenseInventory().equals(sourceSenseInventoryName)) {
                for (int i = 0; i < r.getSenses().size(); i++) {
                    fromSense = r.getSenses(i).getId();
                    toSense = null;
                    if (ignorePattern.matcher(fromSense).find() == true) {
                        continue;
                    }
                    try {
                        toSense = convert(fromSense, r.getWsdItem()
                                .getSubjectOfDisambiguation());
                    }
                    catch (SenseInventoryException e) {
                        logger.error("Can't convert sense " + fromSense);
                        if (ignoreUnknownSenses == false) {
                            throw new AnalysisEngineProcessException(e);
                        }
                    }
                    if (toSense != null) {
                        logger.debug("Converting " + fromSense + " to "
                                + toSense);
                        r.getSenses(i).setId(toSense);
                    }
                }
                r.setSenseInventory(targetSenseInventoryName);
            }
        }
    }

    public String convert(String senseId, String lemma) throws SenseInventoryException
    {
        return ((WordNetSenseInventoryResourceBase) sourceInventory)
                .getWordNetSenseKey(senseId, lemma);
    }

    @Override
    public String convert(String senseId)
    {
        return null;
    }

}
