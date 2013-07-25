/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.si.resource;

import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetSenseInventoryBase;

/**
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class WordNetSenseInventoryResourceBase
    extends SenseInventoryResourceBase
{
    public static final String PARAM_WORDNET_PROPERTIES_URL = "wordNetPropertiesURL";
    @ConfigurationParameter(name = PARAM_WORDNET_PROPERTIES_URL, description = "The URL of the WordNet properties file", mandatory = true)
    protected String wordNetPropertiesURL;

    public static final String PARAM_SENSE_DESCRIPTION_FORMAT = "senseDescriptionFormat";
    @ConfigurationParameter(name = PARAM_SENSE_DESCRIPTION_FORMAT, description = "A format string specifying how sense descriptions should be printed", mandatory = false)
    protected String senseDescriptionFormat;

    /**
     * Given a lemma and a string representing a synset + part of speech,
     * returns a corresponding sense key.
     *
     * @param senseId
     * @param lemma
     * @return
     * @throws SenseInventoryException
     */
    public String getWordNetSenseKey(String senseId, String lemma)
        throws SenseInventoryException
    {
        return ((WordNetSenseInventoryBase) inventory)
                .synsetOffsetAndPosToSenseKey(senseId, lemma);
    }

    /**
     * Given a WordNet sense key, return a synset offset + POS
     *
     * @param senseId
     * @return
     * @throws SenseInventoryException
     */
    public String getWordNetSynsetOffsetAndPos(String senseKey)
        throws SenseInventoryException
    {
        return ((WordNetSenseInventoryBase) inventory)
                .senseKeyToSynsetOffsetAndPos(senseKey);
    }

    /**
     * Given a WordNet sense key, respond the corresponding synset offset
     *
     * @param senseKey
     * @return
     */
    public long getWordNetSynsetOffset(String senseKey)
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
