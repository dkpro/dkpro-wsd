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

package de.tudarmstadt.ukp.dkpro.wsd.evaluation;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

//TODO: Rewrite with Chunk

/**
 * A {@link AbstractSingleExactMatchEvaluator} with HTML output.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class SingleExactMatchEvaluatorHTML
    extends AbstractSingleExactMatchEvaluator
{

    public static final String PARAM_OPEN_IN_BROWSER = "openInBrowser";
    @ConfigurationParameter(name = PARAM_OPEN_IN_BROWSER, mandatory = false, description = "Whether to open the output file in the system Web browser", defaultValue = "false")
    protected boolean openInBrowser;

    @Override
    protected void beginFile(String fileTitle)
        throws IOException
    {
        output.write("<html>");
        output.newLine();
        output.write("<head>");
        output.newLine();
        output.write("\t<title>" + fileTitle + "</title>");
        output.newLine();
        output.write("\t<style type='text/css'>");
        output.newLine();
        output.write("td { text-align: right }");
        output.newLine();
        output.write("\t</style>");
        output.newLine();
        output.write("</head>");
        output.newLine();
        output.write("<body>");
        output.newLine();
        output.write("<h1>" + fileTitle + "</h1>");
        output.newLine();
    }

    @Override
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        super.collectionProcessComplete();
        if (openInBrowser == true) {
            try {
                java.awt.Desktop.getDesktop().browse(
                        java.net.URI.create("file://" + outputFilename));
            }
            catch (IOException e) {
                throw new AnalysisEngineProcessException(e);
            }
        }
    }

    @Override
    protected void endFile()
        throws IOException
    {
        output.write("</html>");
        output.newLine();
    }

    @Override
    protected void beginTableRow()
        throws IOException
    {
        output.write("\t<tr>");
        output.newLine();
    }

    @Override
    protected void endTableRow()
        throws IOException
    {
        output.write("\t</tr>");
        output.newLine();
    }

    @Override
    protected void endTable()
        throws IOException
    {
        output.write("</table>");
        output.newLine();
    }

    @Override
    protected void beginDocument(String documentTitle)
        throws IOException
    {
        output.write("<h2>" + documentTitle + "</h2>");
        output.newLine();

        output.write("<p>");
        output.write("Test algorithm         : " + testAlgorithm + "<br />");
        output.newLine();
        output.write("Gold standard algorithm: " + goldStandardAlgorithm
                + "<br />");
        output.newLine();
        output.write("Backoff algorithms     : ");
        if (backoffAlgorithms != null) {
            for (String backoffAlgorithm : backoffAlgorithms) {
                output.write(backoffAlgorithm + " ");
            }
        }
        output.write("<br />");
        output.newLine();
        output.write("</p>");
        output.newLine();
    }

    @Override
    protected void tableHeader(String cellContents)
        throws IOException
    {
        output.write("\t\t<th>" + cellContents.replaceAll("([./])", "$1&shy;")
                + "</th>");
        output.newLine();
    }

    @Override
    protected void beginTable()
        throws IOException
    {
        output.write("<table>");
        output.newLine();
    }

    @Override
    protected void endDocument()
        throws IOException
    {
    }

    @Override
    protected void tableCell(String cellContents)
        throws IOException
    {
        output.write("<td>" + cellContents + "</td>");
    }

    @Override
    protected void paragraph(String text)
        throws IOException
    {
        output.write("<p>" + text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</p>");
        output.newLine();
    }

}
