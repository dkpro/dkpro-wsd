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

import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * SenseConverter converts all senses from a given source sense inventory to
 * those of a target sense inventory.  Sense IDs are modified in accordance with
 * the abstract convert() method.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public abstract class SenseConverter
	extends JCasAnnotator_ImplBase
{

    public final static String SOURCE_SENSE_INVENTORY_RESOURCE = "SourceSenseInventory";
    @ExternalResource(key = SOURCE_SENSE_INVENTORY_RESOURCE, mandatory = false)
    protected SenseInventory sourceInventory;

    public final static String TARGET_SENSE_INVENTORY_RESOURCE = "TargetSenseInventory";
    @ExternalResource(key = TARGET_SENSE_INVENTORY_RESOURCE, mandatory = false)
    protected SenseInventory targetInventory;

    public static final String PARAM_IGNORE_REGEX = "Ignore_Regex";
	@ConfigurationParameter(name=PARAM_IGNORE_REGEX,
			mandatory=false,
			description="A regular expression matching the senses to ignore",
			defaultValue="^(P|PROPERNOUN|U|UNCLASSIFIABLE)$"
	)
	protected String ignoreRegex;

	public static final String PARAM_SOURCE_SENSE_INVENTORY_NAME = "SourceSenseInventoryName";
	@ConfigurationParameter(name=PARAM_SOURCE_SENSE_INVENTORY_NAME,
			mandatory=true,
			description="The name of the sense inventory representation to convert from.  In some cases it may also be necessary to pass the sense inventory itself as a resource.")
	protected String sourceSenseInventoryName;

	public static final String PARAM_TARGET_SENSE_INVENTORY_NAME = "TargetSenseInventoryName";
	@ConfigurationParameter(name=PARAM_TARGET_SENSE_INVENTORY_NAME,
			mandatory=true,
			description="The name of the sense inventory representation to convert to.  In some cases it may also be necessary to pass the sense inventory itself as a resource.")
	protected String targetSenseInventoryName;

	public static final String PARAM_IGNORE_UNKNOWN_SENSES = "Ignore_Unknown_Senses";
	@ConfigurationParameter(name=PARAM_IGNORE_UNKNOWN_SENSES,
			mandatory=false,
			description="If true, unknown senses are ignored and left unchanged.  If false, unknown senses trigger an exception.",
			defaultValue="false"
	)
	protected boolean ignoreUnknownSenses;

	protected Pattern ignorePattern;

	@Override
	public void initialize(UimaContext context)
		throws ResourceInitializationException
	{
		super.initialize(context);

		ignorePattern =  Pattern.compile(ignoreRegex);
	}

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
                        toSense = convert(fromSense);
                    }
                    catch (SenseInventoryException e) {
                        getLogger().error("Can't convert sense " + fromSense);
                        if (ignoreUnknownSenses == false) {
                            throw new AnalysisEngineProcessException(e);
                        }
                    }
        			if (toSense != null) {
                        getLogger().debug("Converting " + fromSense + " to "
                                + toSense);
                        r.getSenses(i).setId(toSense);
                    }
        		}
        		r.setSenseInventory(targetSenseInventoryName);
        	}
        }
	}

	public abstract String convert(String senseId) throws SenseInventoryException;
}
