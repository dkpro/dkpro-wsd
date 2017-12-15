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

package de.tudarmstadt.ukp.dkpro.wsd.si.wordnet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * A sense inventory for WordNet, based on extJWNL. Sense keys are used as sense
 * IDs.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WordNetSenseKeySenseInventory
    extends WordNetSenseKeySenseInventoryBase
{

    public WordNetSenseKeySenseInventory(URL propertiesURL)
        throws JWNLException, IOException
    {
        wn = Dictionary.getInstance(propertiesURL.openStream());
    }

    public WordNetSenseKeySenseInventory(InputStream propertiesStream)
        throws JWNLException, IOException
    {
        wn = Dictionary.getInstance(propertiesStream);
    }

    @Override
    public String getWordNetSynsetAndPos(String senseId)
        throws SenseInventoryException
    {
        return senseKeyToSynsetOffsetAndPos(senseId);
    }
}
