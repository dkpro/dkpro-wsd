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

package de.tudarmstadt.ukp.dkpro.wsd.io.writer;

import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import de.tudarmstadt.ukp.dkpro.wsd.type.Sense;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult;

/**
 * WSDWriter outputs all the WSDResults in a CAS in human-readable format.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class WSDWriter extends JCasConsumer_ImplBase {

	private final static int idLength = 15;
	private final static int contextLength = 75;
	private final static String ellipsis = "…";
	private final static String beginItemMarkup = ">";
	private final static String endItemMarkup = "<";
	private final static boolean shortNames = false;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String document = jCas.getDocumentText();
		for(WSDResult result : JCasUtil.select(jCas, WSDResult.class)){
			WSDItem item = result.getWsdItem();
			int leftContextLength = max(0, (contextLength - (item.getEnd() - item.getBegin())) / 2);
			int rightContextLength = max(0, contextLength - (item.getEnd() - item.getBegin()) - leftContextLength);
			int leftContextBegin = max(0, item.getBegin() - leftContextLength - 1);
			int digitsRequired = (int)log10(document.length()) + 1;
			String senseInventoryName = result.getSenseInventory();
			String disambiguationMethodName = result.getDisambiguationMethod();

			if (shortNames) {
				disambiguationMethodName = shorten(disambiguationMethodName);
			}

			System.out.format("[%" + idLength + "s] [%" + digitsRequired +"d,%" + digitsRequired + "d] [%s] ", item.getId(), item.getBegin(), item.getEnd(), item.getPos());
			if (leftContextBegin > 0) {
                System.out.print(ellipsis);
            }

			if (leftContextLength > 0 && leftContextBegin != item.getBegin()) {
				System.out.print(document.substring(max(0, item.getBegin() - leftContextLength - 1), max(0, item.getBegin() - 1)) + ' ');
			}

			System.out.print(beginItemMarkup + item.getCoveredText() + endItemMarkup);

			if (item.getEnd() != document.length()) {
				int rightContextEnd = min(document.length(), item.getEnd() + 1 + rightContextLength);
				System.out.print(document.substring(item.getEnd(), min(document.length(), item.getEnd() + 1 + rightContextLength)));
				if (rightContextEnd != document.length()) {
                    System.out.print(ellipsis);
                }
			}

			System.out.println();

			FSArray senses = result.getSenses();
			for (int i=0; i < senses.size(); i++) {
				Sense s = (Sense) senses.get(i);
				System.out.format("\t%01.5f\t%s %s/%s (%s)\n", s.getConfidence(),
						disambiguationMethodName, senseInventoryName, s.getId(), s.getDescription());
			}
		}

	}

	/**
	 * Returns the last element of a dotted-label string.
	 *
     * @param s	A dotted-label string.
     * @return	The last element of s.
	 */
	private static String shorten(String s) {
		if (s.lastIndexOf('.') > 0) {
            return s.substring(s.lastIndexOf('.') + 1);
        }
        else {
            return s;
        }
	}

}
