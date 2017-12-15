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

package de.tudarmstadt.ukp.dkpro.wsd.resource;

import org.apache.log4j.Logger;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.wsd.algorithm.WSDAlgorithm;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventory;

/**
 * A resource wrapping algorithms of type {@link WSDAlgorithm}
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class WSDResourceBasic
    extends WSDResourceBase
    implements WSDAlgorithm
{
    private final static Logger logger = Logger
            .getLogger(WSDResourceBasic.class.getName());

    public final static String DISAMBIGUATION_METHOD = "disambiguationMethod";
    @ConfigurationParameter(name = DISAMBIGUATION_METHOD, mandatory = false, description = "The class name of the disambiguation method to instantiate, or null to use the default disambiguation method for this resource.")
    protected String disambiguationMethod;

    @SuppressWarnings("unchecked")
    @Override
    public void afterResourcesInitialized()
        throws ResourceInitializationException
    {
        super.afterResourcesInitialized();

        if (disambiguationMethod != null) {
            logger.debug("Initializing custom disambiguation method "
                    + disambiguationMethod);
            try {
                Class<? extends WSDAlgorithm> wsdAlgoClass;
                wsdAlgoClass = (Class<WSDAlgorithm>) Class
                        .forName(disambiguationMethod);
                wsdAlgorithm = wsdAlgoClass
                        .getConstructor(SenseInventory.class).newInstance(
                                inventory);
            }
            catch (Exception e) {
                logger.error("Can't instantiate method " + disambiguationMethod);
                e.printStackTrace();
                throw new ResourceInitializationException(e);
            }
        }
    }
}