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
package de.tudarmstadt.ukp.dkpro.wsd.wrapper;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Ignore;

import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorBase;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceDocumentTextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.resource.LsrSenseInventoryResource;
import de.tudarmstadt.ukp.dkpro.wsd.supervised.ims.ImsWsdDisambiguator;
import de.tudarmstadt.ukp.dkpro.wsd.supervised.ims.annotator.ImsWSDAnnotator;
import de.tudarmstadt.ukp.dkpro.wsd.supervised.ims.resource.ImsWsdDisambiguatorResource;

/**
 * This disambiguator uses WordNet as the sense inventory and returns
 * the highest ranked sense by IMS
 *
 * @author nico.erbs@gmail.com
 *
 */
public class ImsDisambiguator
    extends Disambiguator_ImplBase
{

    @Override
    @Ignore
    protected AnalysisEngineDescription createDisambiguationEngine()
        throws ResourceInitializationException
    {

        List<AnalysisEngineDescription> components = new ArrayList<AnalysisEngineDescription>();

        ExternalResourceDescription wordnet = createExternalResourceDescription(
                LsrSenseInventoryResource.class,
                LsrSenseInventoryResource.PARAM_RESOURCE_NAME,"wordnet",
                LsrSenseInventoryResource.PARAM_RESOURCE_LANGUAGE,"en"
                );

        ExternalResourceDescription imsResource = createExternalResourceDescription(
                ImsWsdDisambiguatorResource.class,
                WSDResourceDocumentTextBasic.SENSE_INVENTORY_RESOURCE, wordnet,
                WSDResourceDocumentTextBasic.DISAMBIGUATION_METHOD,
                ImsWsdDisambiguator.class.getName());

        
        AnalysisEngineDescription imsAnnotator = createEngineDescription(
                ImsWSDAnnotator.class,
                ImsWSDAnnotator.WSD_ALGORITHM_RESOURCE, imsResource,
                WSDAnnotatorBase.PARAM_SET_SENSE_DESCRIPTIONS, false);

        components.add(imsAnnotator);

        return createEngineDescription(components
                .toArray(new AnalysisEngineDescription[components.size()]));
    }

}
