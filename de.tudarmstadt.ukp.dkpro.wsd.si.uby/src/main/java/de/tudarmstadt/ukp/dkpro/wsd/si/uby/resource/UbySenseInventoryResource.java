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
package de.tudarmstadt.ukp.dkpro.wsd.si.uby.resource;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.resource.SenseInventoryResourceBase;
import de.tudarmstadt.ukp.dkpro.wsd.si.uby.UbySenseInventory;
import de.tudarmstadt.ukp.lmf.api.Uby;

/**
 * A resource wrapping {@link UbySenseInventory}
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public class UbySenseInventoryResource
    extends SenseInventoryResourceBase
{
    public static final String PARAM_UBY_DATABASE_URL = "ubyUrl";
    @ConfigurationParameter(name = PARAM_UBY_DATABASE_URL, description = "URL for the Uby database", mandatory = true)
    protected String ubyUrl;

    public static final String PARAM_UBY_JDBC_DRIVER_CLASS = "ubyJdbcDriverClass";
    @ConfigurationParameter(name = PARAM_UBY_JDBC_DRIVER_CLASS, description = "JDBC driver class to access the Uby database", mandatory = true)
    protected String ubyJdbcDriverClass;

    public static final String PARAM_UBY_DB_VENDOR = "ubyDbVendor";
    @ConfigurationParameter(name = PARAM_UBY_DB_VENDOR, description = "DB vendor for Uby database", mandatory = true)
    protected String ubyDbVendor;

    public static final String PARAM_UBY_USER = "ubyUser";
    @ConfigurationParameter(name = PARAM_UBY_USER, description = "Username for Uby database", mandatory = true)
    protected String ubyUser;

    public static final String PARAM_UBY_PASSWORD = "ubyPassword";
    @ConfigurationParameter(name = PARAM_UBY_PASSWORD, description = "Password for Uby database", mandatory = true)
    protected String ubyPassword;

    public static final String PARAM_UBY_SHOW_SQL = "ubyShowSql";
    @ConfigurationParameter(name = PARAM_UBY_SHOW_SQL, description = "Print Uby SQL queries to the console", mandatory = false, defaultValue = "false")
    protected String ubyShowSql;

    public static final String PARAM_UBY_LEXICON = "ubyLexicon";
    @ConfigurationParameter(name = PARAM_UBY_LEXICON, description = "Lexicon to use with Uby; if null all available lexicons will be used", mandatory = false)
    protected String ubyLexicon;

    public static final String PARAM_SENSE_DESCRIPTION_FORMAT = "senseDescriptionFormat";
    @ConfigurationParameter(name = PARAM_SENSE_DESCRIPTION_FORMAT, description = "A format string specifying how sense descriptions should be printed", mandatory = false)
    protected String senseDescriptionFormat;

    public final static String PARAM_ALLOW_MULTILINGUAL_ALIGNMENTS = "allowMultilingualAlignments";
    @ConfigurationParameter(name = PARAM_ALLOW_MULTILINGUAL_ALIGNMENTS, mandatory = false, description = "Whether to return multilingual alignments", defaultValue = "false")
    protected String allowMultilingualAlignments;

    /**
     * Returns the underlying {@link Uby} object.
     *
     * @return the underlying {@link Uby} object
     */
    public Uby getUnderlyingResource() {
        return ((UbySenseInventory) inventory).getUnderlyingResource();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
            Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        Boolean ubyShowSql = Boolean.valueOf(this.ubyShowSql);
        try {
            inventory = new UbySenseInventory(ubyUrl, ubyJdbcDriverClass,
                    ubyDbVendor, ubyUser, ubyPassword, ubyShowSql);
            ((UbySenseInventory) inventory).setLexicon(ubyLexicon);
        }
        catch (SenseInventoryException e) {
            throw new ResourceInitializationException(e);
        }
        if (senseDescriptionFormat != null) {
            ((UbySenseInventory) inventory)
                    .setSenseDescriptionFormat(senseDescriptionFormat);
        }
        ((UbySenseInventory) inventory).setAllowMultilingualAlignments(Boolean
                .valueOf(allowMultilingualAlignments));

        return true;
    }

    public String getLexiconSenseId(String senseId)
        throws SenseInventoryException
    {
        return ((UbySenseInventory) inventory).getLexiconSenseId(senseId);
    }

    public String getLexiconSynsetId(String senseId)
        throws SenseInventoryException
    {
        return ((UbySenseInventory) inventory).getLexiconSynsetId(senseId);
    }
}
