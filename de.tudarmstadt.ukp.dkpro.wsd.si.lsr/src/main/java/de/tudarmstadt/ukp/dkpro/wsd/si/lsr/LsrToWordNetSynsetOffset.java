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

package de.tudarmstadt.ukp.dkpro.wsd.si.lsr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity.PoS;
import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseConverter;

/**
 * Converts all sense IDs in LSR sense key format to WordNet synset
 * offsets.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class LsrToWordNetSynsetOffset
	extends SenseConverter
{
	private static final Pattern sensePattern = Pattern.compile("#(\\d+)\\|.*---(.*)");

	@Override
	public String convert(String senseId)
	{
		Matcher m = sensePattern.matcher(senseId);
		if (m.find()) {
		    char pos;
		    if (m.group(2).equals(PoS.n.toString())) {
                pos = 'n';
            }
            else if (m.group(2).equals(PoS.v.toString())) {
                pos = 'v';
            }
            else if (m.group(2).equals(PoS.adj.toString())) {
                pos = 'a';
            }
            else if (m.group(2).equals(PoS.adv.toString())) {
                pos = 'r';
            }
            else {
                throw new IllegalArgumentException("Unknown part of speech for sense " + senseId);
            }
			return String.format("%08d%c", Integer.valueOf(m.group(1)), pos);
		}
		else {
			return null;
		}
	}
}
