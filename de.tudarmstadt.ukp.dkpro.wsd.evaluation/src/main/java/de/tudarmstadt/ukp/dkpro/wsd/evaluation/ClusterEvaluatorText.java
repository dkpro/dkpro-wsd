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

/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.evaluation;

import java.io.IOException;

/**
 * A {@link AbstractClusterEvaluator} with plain text output
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class ClusterEvaluatorText
    extends AbstractClusterEvaluator
{

    @Override
    protected void beginFile(String fileTitle)
        throws IOException
    {
    }

    @Override
    protected void endFile()
        throws IOException
    {
    }

    @Override
    protected void beginTableRow()
        throws IOException
    {
    }

    @Override
    protected void endTableRow()
        throws IOException
    {
        output.newLine();
    }

    @Override
    protected void endTable()
        throws IOException
    {
    }

    @Override
    protected void beginTable()
        throws IOException
    {
        output.newLine();
    }

    @Override
    protected void beginDocument(String documentTitle)
        throws IOException
    {
        output.write("EVALUATION\n");
        output.write("==========\n\n");

        output.write("Test algorithm         : " + testAlgorithm + "\n");
        output.write("Gold standard algorithm: " + goldStandardAlgorithm + "\n");
        output.write("Clustering scheme      : " + clusterUrl + "\n");
    }

    @Override
    protected void endDocument()
        throws IOException
    {
    }

    @Override
    protected void tableHeader(String cellContents)
        throws IOException
    {
        output.write(cellContents + "\t");
    }

    @Override
    protected void tableCell(String cellContents)
        throws IOException
    {
        output.write(cellContents + "\t");
    }

    @Override
    protected void paragraph(String text)
        throws IOException
    {
        output.newLine();
        output.write(text);
        output.newLine();
    }
}
