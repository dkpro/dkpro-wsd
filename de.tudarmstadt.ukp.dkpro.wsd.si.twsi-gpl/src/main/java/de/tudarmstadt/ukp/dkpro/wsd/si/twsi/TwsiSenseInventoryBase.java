/**
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudarmstadt.ukp.dkpro.wsd.si.twsi;

import java.io.File;
import java.io.IOException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.langtech.substituter.settings.Configuration;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;

/**
 * An sense inventory for the Turk Bootstrap Word Sense Inventory
 *
 * @author <a href="mailto:baer@ukp.informatik.tu-darmstadt.de">Daniel Bär</a>
 *
 */
public class TwsiSenseInventoryBase
    implements TwsiSenseInventory
{
    List<String[]> substitutions;
    File twsiConfigFile;

    public TwsiSenseInventoryBase(String twsiConfigFilename)
        throws SenseInventoryException
    {
        twsiConfigFile = new File(twsiConfigFilename);
        Configuration.setConfigFile(twsiConfigFilename, false);

        try {
            substitutions = getSubstitutions(new File(
                    Configuration.substitutionsFile));
        }
        catch (IOException e) {
            throw new SenseInventoryException(e);
        }
    }

    @SuppressWarnings("unused")
    private TwsiSenseInventoryBase()
        throws SenseInventoryException
    {
        throw new SenseInventoryException(
                "No TWSI configuration file specified");
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        String mfs = null;
        int count = -1;

        for (String[] substitution : substitutions) {
            if (substitution[1].equals(sod)
                    && Integer.parseInt(substitution[3]) > count) {
                mfs = substitution[0];
                count = Integer.parseInt(substitution[3]);
            }
        }

        return mfs;
    }

    @Override
    public POS getPos(String senseId)
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        throw new UnsupportedAddressTypeException();
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        List<String> synonyms = new ArrayList<String>();

        for (String[] substitution : substitutions) {
            if (substitution[0].equals(senseId)) {
                synonyms.add(substitution[2]);
            }
        }

        return StringUtils.join(synonyms, ", ");
    }
    
    @Override
    public String getSenseDescription(String senseId, boolean seclang) throws SenseInventoryException {
		return getSenseDescription(senseId);
	}

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        Map<String, List<String>> inventory = new HashMap<String, List<String>>();

        for (String[] substitution : substitutions) {
            String word = substitution[1];
            String sense = substitution[0];

            if (inventory.containsKey(word)) {
                List<String> ids = inventory.get(word);
                if (!ids.contains(sense)) {
                    ids.add(sense);
                    inventory.put(word, ids);
                }
            }
            else {
                List<String> ids = new ArrayList<String>();
                ids.add(sense);

                inventory.put(word, ids);
            }
        }

        return inventory;
    }

    @Override
    public String getSenseInventoryName()
    {
        return this.getClass().getName();
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        List<String> senses = new ArrayList<String>();

        for (String[] substitution : substitutions) {
            if (substitution[1].equals(sod)
                    && !senses.contains(substitution[0])) {
                senses.add(substitution[0]);
            }
        }

        return senses;
    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException
    {
        throw new UnsupportedAddressTypeException();
    }

    public List<String[]> getSubstitutions(File file)
        throws IOException
    {
        List<String[]> substitutions = new ArrayList<String[]>();

        for (String line : FileUtils.readLines(file)) {
            substitutions.add(line.split("\t"));
        }

        return substitutions;
    }

    @Override
    public File getConfigFile()
    {
        return twsiConfigFile;
    }

    @Override
    public int getUseCount(String senseId)
        throws SenseInventoryException
    {
        throw new UnsupportedAddressTypeException();
    }
}
