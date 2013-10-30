package de.tudarmstadt.ukp.dkpro.wsd.wrapper.wrapper;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.algorithms.MostFrequentSenseBaseline;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceIndividualBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.LinkDatabaseInventoryResource;
import de.tudarmstadt.ukp.dkpro.wsd.wsdannotators.WSDAnnotatorIndividualBasic;

public class LinkDatabaseMFSDisambiguator extends Disambiguator_ImplBase {
	
	
	@Override
	protected AnalysisEngineDescription createDisambiguationEngine()
			throws ResourceInitializationException {
		
		List<AnalysisEngineDescription> components = new ArrayList<AnalysisEngineDescription>();
		
		components.add(getPreprocessingEngineDescritpion());
		
        ExternalResourceDescription linkDatabase = createExternalResourceDescription(
                LinkDatabaseInventoryResource.class,
                LinkDatabaseInventoryResource.PARAM_RESOURCE_HOST, "localhost",
                LinkDatabaseInventoryResource.PARAM_RESOURCE_DATABASE, "linkdatabase_wikipedia_en_20100615",
                LinkDatabaseInventoryResource.PARAM_SENSE_INVENTORY_NAME, "LinkDatabase_20100615");

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

		
		return createEngineDescription(components.toArray(new AnalysisEngineDescription[components.size()]));
	}
	


}
