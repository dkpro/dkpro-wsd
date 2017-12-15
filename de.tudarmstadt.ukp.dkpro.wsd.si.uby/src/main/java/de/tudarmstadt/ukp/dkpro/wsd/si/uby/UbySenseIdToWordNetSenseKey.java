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

package de.tudarmstadt.ukp.dkpro.wsd.si.uby;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.dkpro.wsd.candidates.SenseConverter;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.uby.resource.UbySenseInventoryResource;

/**
 * Converts all Uby sense IDs to WordNet sense keys.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class UbySenseIdToWordNetSenseKey
    extends SenseConverter
{
    private static final Pattern sensePattern = Pattern.compile("\\[POS: ([^]]+)\\] (.+)");

    @Override
    public String convert(String senseId)
    {
        String senseKey;
        try {
            senseKey = ((UbySenseInventoryResource) sourceInventory)
                    .getLexiconSenseId(senseId);
        }
        catch (SenseInventoryException e) {
            return null;
        }

        if (senseKey == null) {
            return null;
        }

        Matcher m = sensePattern.matcher(senseKey);
        if (m.find()) {
            return m.group(2);
        }

        return null;
    }
}
