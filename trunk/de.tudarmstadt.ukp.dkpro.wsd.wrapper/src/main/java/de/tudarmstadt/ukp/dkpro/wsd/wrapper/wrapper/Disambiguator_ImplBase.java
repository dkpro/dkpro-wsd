package de.tudarmstadt.ukp.dkpro.wsd.wrapper.wrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;


public abstract class Disambiguator_ImplBase implements Disambiguator {

    /**
     * Default is English ("en").
     */
    private String language = "en";

	private AnalysisEngine disambiguationEngine = null;

    public void setLanguage(String language) {
		this.language = language;
	}

    public List<String> disambiguate(String inputText) throws IOException {

        JCas jcas = null;
        try {
            jcas = getDisambiguationEngine().newJCas();
            jcas.setDocumentText(inputText);
            jcas.setDocumentLanguage(language);

            getDisambiguationEngine().process(jcas);
        }
        catch (ResourceInitializationException e) {
            throw new RuntimeException(e);
        }
        catch (AnalysisEngineProcessException e) {
            throw new RuntimeException(e);
        }

//      Extract senses
        List<String> senses = new ArrayList<String>();
        for(WSDResult wsdResult : JCasUtil.select(jcas, WSDResult.class)){
        	senses.addAll(getBestSenses(wsdResult.getSenses()));
        }
        
        return senses;
    }
    
    protected abstract AnalysisEngine createDisambiguationEngine() throws ResourceInitializationException;

    public AnalysisEngine getDisambiguationEngine()
            throws IOException
        {
            if (disambiguationEngine == null) {
                try {
                	disambiguationEngine = createDisambiguationEngine();
                }
                catch (ResourceInitializationException e) {
                    throw new IOException(e);
                }
            }

            return disambiguationEngine;
        }

	public String getConfigurationDetails() {
		StringBuilder sb = new StringBuilder();
		//TODO: Add configuration
		return sb.toString();
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}
	
    /**
     * Returns the senses with the highest non-zero confidence score. (Copied from AbstractWSDEvaluator)
     *
     * @param senseArray senses of wsdResult
     * @return Set of best sense (can be more than one if multiple ones have the highest score)
     */
    protected Set<String> getBestSenses(FSArray senseArray)
    {
        Set<String> bestSenses = new HashSet<String>();
        if (senseArray == null) {
            return bestSenses;
        }
        double bestConfidence = Double.MIN_VALUE;
        for (Sense sense : JCasUtil.select(senseArray, Sense.class)) {
            if (sense.getConfidence() > bestConfidence && sense.getConfidence() > 0) {
                bestSenses.clear();
                bestSenses.add(sense.getDescription());
                bestConfidence = sense.getConfidence();
            }
            else if (sense.getConfidence() == bestConfidence) {
                bestSenses.add(sense.getDescription());
            }
        }
        return bestSenses;
    }


}
