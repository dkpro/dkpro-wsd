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
package de.tudarmstadt.ukp.dkpro.wsd.wsi.annotator;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.WSDException;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

public class WSIAnnotator
    extends JCasAnnotator_ImplBase
{
    public static final String SENSE_INDUCTION_RESOURCE = "InductionAlgorithm";
    @ExternalResource(key = SENSE_INDUCTION_RESOURCE)
    SenseInductionResourceBase wsi;
    public static final String PARAM_INVENTORY_FILE = "InventoryFile";
    @ConfigurationParameter(name = PARAM_INVENTORY_FILE, mandatory = true, defaultValue = "clusters.json")
    private String inventoryFile;

    private FileWriter outFile;

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {

        final DocumentMetaData documentMetaData = DocumentMetaData.get(aJCas);
        final String docId = documentMetaData.getDocumentId();
        final String collectionId = documentMetaData.getCollectionId();
        // final String term = collectionId.replace(".n", "");
        String term = null;
        for (final WSDItem wsdItem : select(aJCas, WSDItem.class)) {
            term = wsdItem.getId();
        }
        if (term == null) {
            return;
        }
        try {
            this.wsi.induceSenses(term);
        }
        catch (WSDException e) {
            throw new AnalysisEngineProcessException(e);
        }

    }

    @Override
    public void batchProcessComplete()
        throws AnalysisEngineProcessException
    {
        super.batchProcessComplete();
        try {
            this.wsi.writeInventory(inventoryFile);
            this.outFile.close();

        }
        catch (final IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

}
