/**
 *
 */
package de.tudarmstadt.ukp.dkpro.wsd.si;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public abstract class SenseInventoryBase
    implements SenseInventory
{
    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        List<String> senses = new ArrayList<String>();
        for (POS pos : POS.values()) {
            senses.addAll(getSenses(sod, pos));
        }
        return senses;
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        int maxUseCount = Integer.MIN_VALUE;
        String mostFrequentSense = null;
        for (String sense : getSenses(sod)) {
            int useCount = getUseCount(sense);
            if (maxUseCount < useCount) {
                maxUseCount = useCount;
                mostFrequentSense = sense;
            }
        }
        return mostFrequentSense;
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        int maxUseCount = Integer.MIN_VALUE;
        String mostFrequentSense = null;
        for (String sense : getSenses(sod, pos)) {
            int useCount = getUseCount(sense);
            if (maxUseCount < useCount) {
                maxUseCount = useCount;
                mostFrequentSense = sense;
            }
        }
        return mostFrequentSense;
    }
}
