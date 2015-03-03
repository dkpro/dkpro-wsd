/*******************************************************************************
 * Copyright 2015
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

package de.tudarmstadt.ukp.dkpro.wsd.si.lsr.resource;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.ResourceLoaderException;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.LsrSenseInventory;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;

/**
 * A resource wrapping {@link LsrSenseInventory}
 *
 * @author Torsten Zesch <zesch@ukp.informatik.tu-darmstadt.de>
 *
 */
public class LsrSenseInventoryResource
    extends SenseInventoryResourceBase
{
    public static final String PARAM_RESOURCE_LANGUAGE = "resourceLanguage";
    @ConfigurationParameter(name = PARAM_RESOURCE_LANGUAGE, description = "The language of the lexical-semantic resource", mandatory = true)
    private String resourceLanguage;

    public static final String PARAM_RESOURCE_NAME = "lsrName";
    @ConfigurationParameter(name = PARAM_RESOURCE_NAME, description = "The name of the lexical-semantic resource", mandatory = true)
    private String lsrName;

    /**
     * Returns the underlying LexicalSemanticResource object.
     *
     * @return
     */
    public LexicalSemanticResource getUnderlyingResource() {
        return ((LsrSenseInventory)inventory).getUnderlyingResource();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        try {
            inventory = new LsrSenseInventory(lsrName, resourceLanguage);
        }
        catch (ResourceLoaderException e) {
            throw new ResourceInitializationException(e);
        }

        return true;
    }
}
