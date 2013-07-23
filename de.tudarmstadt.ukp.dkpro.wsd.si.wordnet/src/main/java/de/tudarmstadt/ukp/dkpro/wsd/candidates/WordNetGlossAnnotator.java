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

package de.tudarmstadt.ukp.dkpro.wsd.candidates;

import java.io.File;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * Populates the sense descriptions with their corresponding WordNet synonyms
 * and/or glosses.  This annotator was written as a stopgap measure and should
 * be superseded by a new hierarchy of annotators (see TODO comments for
 * further details)
 *
 * @author	Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 */
public class WordNetGlossAnnotator
	extends JCasAnnotator_ImplBase
{

	public static final String PARAM_JWNL_PROPERTIES_FILE = "JWNL_Properties_File";
	@ConfigurationParameter(name=PARAM_JWNL_PROPERTIES_FILE,
			mandatory=true,
			description="The JWNL properties file"
	)
	private File jwnlPropertiesFile;

	public static final String PARAM_OVERWRITE = "Overwrite";
	@ConfigurationParameter(name=PARAM_OVERWRITE,
			mandatory=false,
			description="If true, overwrite existing sense descriptions",
			defaultValue="true"
	)
	private boolean overwrite;

	public static final String PARAM_INCLUDE_SYNONYMS = "Include_Synonyms";
	@ConfigurationParameter(name=PARAM_INCLUDE_SYNONYMS,
			mandatory=false,
			description="If true, add the synonyms to the sense descriptions",
			defaultValue="true"
	)
	private boolean includeSynonyms;

	public static final String PARAM_INCLUDE_GLOSS = "Include_Gloss";
	@ConfigurationParameter(name=PARAM_INCLUDE_GLOSS,
			mandatory=false,
			description="If true, add the gloss to the sense descriptions",
			defaultValue="true"
	)
	private boolean includeGloss;

	// TODO: consider that multiword synonyms use underscores instead of spaces
	// TODO: make this thing generic part of sense inventory
	@Override
	public void process(JCas aJCas)
		throws AnalysisEngineProcessException
	{
        for (WSDResult r : JCasUtil.select(aJCas, WSDResult.class)) {
    		for (int i = 0; i < r.getSenses().size(); i++) {
    			Sense sense = r.getSenses(i);
    			WSDItem wsdItem = r.getWsdItem();
    			if (sense.getDescription() != null && overwrite == false) {
					continue;
				}
    			try {
    				System.out.println("looking up " + sense.getId() + "/" + wsdItem.getPos());
					Synset synset = Dictionary.getInstance().getSynsetAt(stringToPOS(wsdItem.getPos()), Integer.valueOf(sense.getId()));
					String description = new String();
					if (includeSynonyms == true) {
						description += synset.getWords().get(0).getLemma() + " ";
						for (int j = 1; j < synset.getWords().size(); j++) {
							description += synset.getWords().get(j).getLemma() + " ";
						}
					}
					if (includeGloss == true) {
						description += synset.getGloss();
					}
					sense.setDescription(description);
				}
				catch (NumberFormatException e) {
					throw new AnalysisEngineProcessException(e);
				}
				catch (JWNLException e) {
					throw new AnalysisEngineProcessException(e);
				}
    		}
        }
	}

	private POS stringToPOS(String s) throws AnalysisEngineProcessException {
		if (s.equals("N")) {
			return POS.NOUN;
		}
		else if (s.equals("V")) {
			return POS.VERB;
		}
		else if (s.equals("ADV")) {
			return POS.ADVERB;
		}
		else if (s.equals("ADJ")) {
			return POS.ADJECTIVE;
		}

		throw new AnalysisEngineProcessException();
	}
}
