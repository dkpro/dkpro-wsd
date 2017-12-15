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

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.annotator.WSDAnnotatorBase;
import de.tudarmstadt.ukp.dkpro.wsd.evaluation.AbstractWSDEvaluator;
import de.tudarmstadt.ukp.dkpro.wsd.resource.WSDResourceDocumentTextBasic;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.resource.LsrSenseInventoryResource;
import de.tudarmstadt.ukp.dkpro.wsd.supervised.ims.ImsWsdDisambiguator;
import de.tudarmstadt.ukp.dkpro.wsd.supervised.ims.annotator.ImsWSDAnnotator;
import de.tudarmstadt.ukp.dkpro.wsd.supervised.ims.resource.ImsWsdDisambiguatorResource;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

public class ImsWSDAnnotatorTest extends AbstractWSDEvaluator
{

    @Test
    public void testMapping() throws SenseInventoryException
    {
      String input = "Lucy is in the sky with diamonds. She lives in a yellow submarine.\n\nShe has starnge hair.";
      String output = "1x05x00xx 2x42x03xx in the 1x17x00xx with 1x21x00xx .\nShe 2x42x08xx in a 5x00x00xchromaticx00 1x06x00xx .\nShe 2x40x00xx starnge 1x08x00xx .";
      Map<String,String> mapping = ImsWSDAnnotator.getMapping(input, output);
      assertNotNull(mapping);
      assertEquals(9, mapping.size());
      assertEquals("1x21x00xx", mapping.get("diamonds"));
      
    }

    @Test
    @Ignore
    public void testDisambiguation() throws AnalysisEngineProcessException, ResourceInitializationException
    {        
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


        AnalysisEngine imsAnnotator = createEngine(
                ImsWSDAnnotator.class,
                ImsWSDAnnotator.WSD_ALGORITHM_RESOURCE, imsResource,
                WSDAnnotatorBase.PARAM_SET_SENSE_DESCRIPTIONS, false);

        JCas jcas = imsAnnotator.newJCas();       
        jcas.setDocumentLanguage("en");
        jcas.setDocumentText("Lucy is in the sky with diamonds. She lives in a yellow submarine.\n She has starnge hair.");
        DocumentMetaData dmd = DocumentMetaData.create(jcas);
        dmd.setDocumentId("Test");

        assertNotNull(jcas);
        //Lucy is in the sky with diamonds.
        //1x05x00xx 2x42x03xx in the 1x17x00xx with 1x21x00xx .

        WSDItem wsdItem = new WSDItem(jcas, 0, 4);
        wsdItem.addToIndexes();

        wsdItem = new WSDItem(jcas, 5, 7);
        wsdItem.addToIndexes();

        wsdItem = new WSDItem(jcas, 8, 10);
        wsdItem.addToIndexes();

        imsAnnotator.process(jcas);  

        assertEquals(3,JCasUtil.select(jcas, WSDItem.class).size());

        for(WSDItem item : JCasUtil.select(jcas, WSDItem.class)){
            assertTrue(getWSDResults(jcas, item).size()<=1);
            for(WSDResult wsdResult : getWSDResults(jcas, item)){
                if(item.getCoveredText().equals("Lucy")){
                    assertEquals("1x05x00xx", wsdResult.getBestSense().getId());
                    assertEquals(wsdResult.getBestSense().getConfidence(),1d,.00001);
                }
                if(item.getCoveredText().equals("in")){
                    assertNull(wsdResult.getBestSense());
                }
            }
        }
    }
}
