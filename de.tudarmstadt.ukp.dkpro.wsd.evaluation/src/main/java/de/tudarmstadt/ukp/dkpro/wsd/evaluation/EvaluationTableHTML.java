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
import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

//TODO: Rewrite with Chunk

/**
 * Prints an HTML table showing WSD algorithm accuracy
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class EvaluationTableHTML
    extends EvaluationTable
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
        output.write("\t\t td." + SenseType.TESTNA
                + " { background-color: LightGray; }");
        output.newLine();
        output.write("\t\t td." + SenseType.GOLDNA
                + " { background-color: White; }");
        output.newLine();
        output.write("\t\t td." + SenseType.CORRECT
                + " { background-color: LightGreen; }");
        output.newLine();
        output.write("\t\t td." + SenseType.INCORRECT
                + " { background-color: LightPink; }");
        output.newLine();
        output.write("\t\t td." + SenseType.GOLD
                + " { background-color: Gold; }");
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
    protected void beginTable(int numRows, int numColumns)
        throws IOException
    {
        output.write("<table>");
        output.newLine();
    }

    @Override
    protected void beginDocument(String documentTitle)
        throws IOException
    {
        output.write("<h2>" + documentTitle + "</h2>");
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
    public void collectionProcessComplete()
        throws AnalysisEngineProcessException
    {
        try {
            output.write("</html>");
            output.newLine();
            output.close();
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

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
    protected void tableCell(String cellContents, SenseType senseIdClass)
        throws IOException
    {
        output.write("\t\t<td>" + cellContents + "</td>");
        output.newLine();
    }

    @Override
    protected void tableCellSenseArray(FSArray senses, SenseType senseIdClass,
            Collection<Sense> highlightSense)
        throws IOException
    {
        output.write("\t\t<td class='" + senseIdClass + "'>");
        if (senses == null || senses.size() == 0) {
            output.write(NA);
        }
        else {
            boolean firstSense = true;
            for (Sense s : JCasUtil.select(senses, Sense.class)) {
                if (firstSense == false) {
                    output.write("<br />");
                }
                if (highlightSense != null && !highlightSense.contains(s)) {
                    output.write("<span style='color: #888'>");
                }
                output.write(String.format("%.2f", s.getConfidence()) + "\t"
                        + s.getId());
                if (highlightSense != null && !highlightSense.contains(s)) {
                    output.write("</span>");
                }
                firstSense = false;
            }
        }
        output.write("</td>");
        output.newLine();
    }

    @Override
    protected void tableHeaderInstance(WSDItem wsdItem)
        throws IOException
    {
        output.write("\t\t<th>" + wsdItem.getId() + "<br />"
                + wsdItem.getSubjectOfDisambiguation() + "/" + wsdItem.getPos()
                + "</th>");
        output.newLine();
    }

    @Override
    protected void tableCellSense(Sense sense, SenseType senseType)
        throws IOException
    {
        output.write("\t\t<td class='" + senseType + "'>"
                + ((sense == null) ? NA : sense.getId()) + "</td>");
        output.newLine();
    }

    @Override
    protected void endFile()
        throws IOException
    {
        output.write("</html>");
        output.newLine();
    }

    @Override
    protected void endDocument()
        throws IOException
    {
    }

}
