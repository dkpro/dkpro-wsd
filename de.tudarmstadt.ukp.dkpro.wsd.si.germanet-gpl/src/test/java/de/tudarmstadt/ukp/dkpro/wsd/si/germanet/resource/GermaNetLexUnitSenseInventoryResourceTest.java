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
package de.tudarmstadt.ukp.dkpro.wsd.si.germanet.resource;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.bindResource;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;

/**
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class GermaNetLexUnitSenseInventoryResourceTest
{

    public static class Annotator extends JCasAnnotator_ImplBase {
        final static String MODEL_KEY = "SenseInventory";
        @ExternalResource(key = MODEL_KEY)
        private SenseInventory inventory;

        @Override
        public void process(JCas aJCas)
            throws AnalysisEngineProcessException
        {
                System.out.println(inventory.getSenseInventoryName());
        }
    }

    @Ignore
    @Test
    public void configureAggregatedExample() throws Exception {
        AnalysisEngineDescription desc = createEngineDescription(Annotator.class);

        bindResource(
                desc,
                Annotator.MODEL_KEY,
                GermaNetLexUnitSenseInventoryResource.class,
                GermaNetLexUnitSenseInventoryResource.PARAM_GERMANET_DIRECTORY, "/home/miller/share/GermaNet/GermaNet_7.0/GN_V70_XML"
        );

        // Check the external resource was injected
        AnalysisEngine ae = createEngine(desc);
        ae.process(ae.newJCas());
    }

}
