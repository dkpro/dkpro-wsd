/*******************************************************************************
 * Copyright 2013
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
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
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
        output.write(String.format(
                "\n%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%s\n", "POS",
                "test", "gold", "both", "score", "p", "r", "cover", "F1",
                "cluster"));
        output.write(String.format(
                "%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%s\n", "-------",
                "-------", "-------", "-------", "-------", "-------",
                "-------", "-------", "-------", "-------"));
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
        output.write("\n" + numberOfClusteredLexicalItems
                + " lexical items in " + numberOfClusteredInstances
                + " instances were affected by the clustering.\n");
        output.write(improvedInstanceCount
                + " instances (representing "
                + improvedSods.size()
                + " lexical items) had clustered scores better than random clustering.\n");
        if (showImprovedInstances) {
            output.write("Improved instances:\n");
            for (String s : improvedInstances) {
                output.write(s + "\n");
            }
        }
        if (showImprovedSods) {
            output.write("The total clustered score for the following lexical items exceeds the total random score:\n");
            output.write("inc    %inc   lemma\n");
            for (String lemma : clusteredScoreByLemma.keySet()) {
                double clusteredScore = clusteredScoreByLemma.get(lemma);
                double randomClusteredScore = randomClusteredScoreByLemma
                        .get(lemma);
                output.write(String.format("%06.1f %2.4f %s\n", clusteredScore
                        - randomClusteredScore,
                        (clusteredScore - randomClusteredScore)
                                / randomClusteredScore, lemma));
            }
        }
    }

    @Override
    protected void tableHeader(String cellContents)
        throws IOException
    {
    }

    @Override
    protected void tableCell(String cellContents)
        throws IOException
    {
        output.write(cellContents + "\t");
    }

}
