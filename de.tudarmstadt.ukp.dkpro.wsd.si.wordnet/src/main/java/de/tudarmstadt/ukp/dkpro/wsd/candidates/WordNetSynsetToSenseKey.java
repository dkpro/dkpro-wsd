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
package de.tudarmstadt.ukp.dkpro.wsd.candidates;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.WordNetSynsetSenseInventoryResource;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Converts WordNet synset offsets+POS to sense keys
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
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
                    if (ignorePattern.matcher(fromSense).find() == true) {
                        continue;
                    }
                    toSense = convert(fromSense, r.getWsdItem()
                            .getSubjectOfDisambiguation());
                    if (toSense == null) {
                        if (ignoreUnknownSenses == false) {
                            logger.error(
                                    "Can't convert sense " + fromSense);
                            throw new AnalysisEngineProcessException();
                        }
                        else {
                            logger.debug(
                                    "Can't convert sense " + fromSense);
                        }
                    }
                    else {
                        logger.debug("Converting " + fromSense + " to " + toSense);
                        r.getSenses(i).setId(toSense);
                    }
                }
                r.setSenseInventory(targetSenseInventoryName);
            }
        }
    }

    public String convert(String senseId, String lemma)
    {
        try {
            return ((WordNetSynsetSenseInventoryResource) sourceInventory)
                    .getWordNetSenseKey(senseId, lemma);
        }
        catch (SenseInventoryException e) {
            return null;
        }
    }

    @Override
    public String convert(String senseId)
    {
        return null;
    }

}
