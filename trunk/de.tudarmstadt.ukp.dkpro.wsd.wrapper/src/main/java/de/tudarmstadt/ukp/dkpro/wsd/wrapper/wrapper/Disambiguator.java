package de.tudarmstadt.ukp.dkpro.wsd.wrapper.wrapper;

import java.io.IOException;
import java.util.List;

public interface Disambiguator {
	
    /**
     * @param inputText The input text.
     *  
     * @return
     *   The list of string with the senses disambiguated in the input text.
     */
	List<String> disambiguate(String inputText) throws IOException;
	
    /**
     * @return The name of the disambiguator.
     */
	String getName();
	
    /**
     * @return Returns a string with the configuration details of this word sense disambiguator.
     */
	String getConfigurationDetails();
	
	

}
