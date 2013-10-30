package de.tudarmstadt.ukp.dkpro.wsd.wrapper.wrapper;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stopwordremover.StopWordRemover;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.WSDItemAnnotator;
import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;


public abstract class Disambiguator_ImplBase implements Disambiguator {

	/**
	 * Default is English ("en").
	 */
	private String language = "en";

	/**
	 * Default is Token ("Token.class.getName()").
	 */
	private String featurePath = Token.class.getName();

	/**
	 * Default is true.
	 */
	private boolean filterStopwords = true;
	
	private AnalysisEngine disambiguationEngine = null;

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public void setFilterStopwords(boolean filterStopwords) {
		this.filterStopwords = filterStopwords;
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

	protected abstract AnalysisEngineDescription createDisambiguationEngine() throws ResourceInitializationException;

	public AnalysisEngine getDisambiguationEngine()
			throws IOException
			{
		if (disambiguationEngine == null) {
			try {
				disambiguationEngine = createEngine(createDisambiguationEngine());
			}
			catch (ResourceInitializationException e) {
				throw new IOException(e);
			}
		}

		return disambiguationEngine;
			}

	public String getConfigurationDetails() {

		StringBuilder sb = new StringBuilder();
		
		sb.append("Disambiguation" + "\t" + getName() + "\n");
		sb.append("Language" + "\t" + language + "\n");
		sb.append("Feature path" + "\t" + featurePath + "\n");
		sb.append("Filter stopwords" + "\t" + filterStopwords + "\n");

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
				bestSenses.add(sense.getId());
				bestConfidence = sense.getConfidence();
			}
			else if (sense.getConfidence() == bestConfidence) {
				bestSenses.add(sense.getId());
			}
		}
		return bestSenses;
	}

	protected AnalysisEngineDescription getPreprocessingEngineDescritpion() throws ResourceInitializationException{

		List<AnalysisEngineDescription> preprocessing = new ArrayList<AnalysisEngineDescription>();
		if(featurePath.equals(Token.class.getName())){
			preprocessing.add(
					createEngineDescription(
							StanfordSegmenter.class));
			
			if(filterStopwords){
				preprocessing.add(
						createEngineDescription(StopWordRemover.class,
								StopWordRemover.PARAM_STOP_WORD_LIST_FILE_NAMES, new String[]{
							"[*]classpath:/stopwords/punctuation.txt",
							"[de]classpath:/stopwords/german_stopwords.txt",
							"[en]classpath:/stopwords/english_stopwords.txt"}));
			}

			preprocessing.add(
					createEngineDescription(
							WSDItemAnnotator.class,
							WSDItemAnnotator.PARAM_FEATURE_PATH, featurePath));

		}
		return createEngineDescription(preprocessing.toArray(new AnalysisEngineDescription[preprocessing.size()]));
	}





}
