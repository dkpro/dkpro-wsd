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
package de.tudarmstadt.ukp.dkpro.wsd.supervised.ims.annotator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithmDocumentTextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorBaseDocumentCollective;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * An abstract class for annotators which call a disambiguation algorithm taking
 * as a parameter the id of the document.
 *
 * @author <a href="mailto:erbs@ukp.informatik.tu-darmstadt.de">Nicolai Erbs</a>
 *
 */
public class ImsWSDAnnotator
extends WSDAnnotatorBaseDocumentCollective
{
    public final static String WSD_ALGORITHM_RESOURCE = "WSDAlgorithmResource";
    @ExternalResource(key = WSD_ALGORITHM_RESOURCE)
    protected WSDAlgorithmDocumentTextBasic wsdMethod;

    @Override
    public void initialize(UimaContext context)
            throws ResourceInitializationException
            {
        super.initialize(context);
        inventory = wsdMethod.getSenseInventory();
            }

    @Override
    protected String getDisambiguationMethod()
            throws SenseInventoryException
            {
        if (disambiguationMethodName != null) {
            return disambiguationMethodName;
        }
        else {
            return wsdMethod.getDisambiguationMethod();
        }
            }

    @Override
    protected Map<WSDItem, Map<String, Double>> getDisambiguation(Collection<WSDItem> wsdItems,
            String documentText)
                    throws SenseInventoryException
                    {
        String senseDisambiguatedText = wsdMethod.getDisambiguation(documentText);
        
        Map<String,String> mapping = getMapping(documentText, senseDisambiguatedText);

        Map<WSDItem, Map<String, Double>> disambiguation = new HashMap<WSDItem, Map<String, Double>>();
        Map<String, Double> senses;
        for(WSDItem wsdItem : wsdItems){
            senses = new HashMap<String, Double>();
            if(mapping.containsKey(wsdItem.getCoveredText())){
                senses.put(mapping.get(wsdItem.getCoveredText()), 1d);
            }
            disambiguation.put(wsdItem,senses);
        }

        return disambiguation;
                    }

    static Map<String, String> getMapping( String documentText, String senseDisambiguatedText) throws SenseInventoryException
    {
        Map<String, String> mapping = new HashMap<String, String>();
        //Todo This mapping should be improved
        String[] words = documentText.replaceAll("\n", "").replaceAll("\\.", " ").replaceAll("  ", " ").split(" ");
        String[] senses = senseDisambiguatedText.replaceAll("\n", "").replaceAll("\\.", " ").replaceAll("  ", " ").split(" ");
//        System.out.println(StringUtils.join(words, ","));
//        System.out.println(StringUtils.join(senses, ","));
        if(words.length != senses.length){
            throw new SenseInventoryException("Mapping from IMS to WSDItems cannot be performed.");
        }
        //Lucy is in the sky with diamonds.
        //1x05x00xx 2x42x03xx in the 1x17x00xx with 1x21x00xx .
        
        for(int n=0;n<words.length;n++){
            if(!words[n].equalsIgnoreCase(senses[n])){
                mapping.put(words[n], senses[n]);
            }
        }

        return mapping;
    }

}