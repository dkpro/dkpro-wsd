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

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.MostFrequentSenseBaseline;
import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorIndividualBasic;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceIndividualBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.LinkDatabaseInventoryResource;

/**
 * This disambiguator uses the LinkDatabase as the sense inventory and returns
 * the most frequent sense
 *
 * @author nico.erbs@gmail.com
 *
 */
public class LinkDatabaseMFSDisambiguator
    extends Disambiguator_ImplBase
{

    @Override
    protected AnalysisEngineDescription createDisambiguationEngine()
        throws ResourceInitializationException
    {

        List<AnalysisEngineDescription> components = new ArrayList<AnalysisEngineDescription>();

        ExternalResourceDescription linkDatabase = createExternalResourceDescription(
                LinkDatabaseInventoryResource.class,
                LinkDatabaseInventoryResource.PARAM_RESOURCE_HOST, "localhost",
                LinkDatabaseInventoryResource.PARAM_RESOURCE_DATABASE,
                "linkdatabase_wikipedia_en_20100615",
                LinkDatabaseInventoryResource.PARAM_SENSE_INVENTORY_NAME,
                "LinkDatabase_20100615");

        ExternalResourceDescription mfsBaselineResource = createExternalResourceDescription(
                WSDResourceIndividualBasic.class,
                WSDResourceIndividualBasic.SENSE_INVENTORY_RESOURCE,
                linkDatabase, WSDResourceIndividualBasic.DISAMBIGUATION_METHOD,
                MostFrequentSenseBaseline.class.getName());

        AnalysisEngineDescription mfsBaseline = createEngineDescription(
                WSDAnnotatorIndividualBasic.class,
                WSDAnnotatorIndividualBasic.WSD_ALGORITHM_RESOURCE,
                mfsBaselineResource);

        components.add(mfsBaseline);

        return createEngineDescription(components
                .toArray(new AnalysisEngineDescription[components.size()]));
    }

}
