/*******************************************************************************
 * Copyright 2013
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

package de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.PerformanceLinkInformationReader.TABLE;

/**
 * The inventory for a LinkDatabase
 *
 * @author nico.erbs@gmail.com
 *
 */
public class LinkDatabaseInventory
	implements LinkDatabase
{

	private final PerformanceLinkInformationReader reader;

	private final static Logger logger = Logger
			.getLogger(LinkDatabaseInventory.class.getName());

	public LinkDatabaseInventory(String host, String database)
	{
		reader = new PerformanceLinkInformationReader(host, database);
	}

	@Override
	public String getSenseDescription(String senseId)
		throws SenseInventoryException
	{
		try {
			if (reader.isLinkAnchor(senseId, null, null, 0)) {
				return senseId;
			}
			else {
				return "";
			}
		}
		catch (Exception e) {
			throw new SenseInventoryException(e);
		}
	}

    @Override
    public POS getPos(String senseId)
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
	public Map<String, List<String>> getSenseInventory()
		throws SenseInventoryException
	{
        throw new UnsupportedOperationException();
	}

	@Override
	public String getSenseInventoryName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public List<String> getSenses(String sod)
		throws SenseInventoryException
	{
		logger.info("Getting senses for " + sod);
		List<String> senses = new ArrayList<String>();
		try {
			for (long sense : reader.getLinkTargets(sod, null, null, 0)
					.keySet()) {
				senses.add(reader.getName(TABLE.senses, sense));
			}
		}
		catch (Exception e) {
			throw new SenseInventoryException(e);
		}
		return senses;
	}

	@Override
	public Map<String, Double> getWeightedSenses(String sod)
		throws SenseInventoryException
	{
		Map<String, Double> senses = new HashMap<String, Double>();
		try {
			long total = reader.getNumberOfLinkTargets(sod, null, null, 0);
			Map<Long, Long> targets = reader.getLinkTargets(sod, null, null, 0);
			for (long sense : targets.keySet()) {
				senses.put(reader.getName(TABLE.senses, sense),
						targets.get(sense) / (double) total);
			}
		}
		catch (Exception e) {
			throw new SenseInventoryException(e);
		}
		return senses;
	}

	@Override
	public List<String> getSenses(String sod, POS pos)
		throws SenseInventoryException
	{
		return getSenses(sod);
	}

	@Override
	public String getMostFrequentSense(String sod)
		throws SenseInventoryException, UnsupportedOperationException
	{
		try {
			return reader.getName(TABLE.senses,
					reader.getMostFrequentLinkTarget(sod, null, null, 0));
		}
		catch (SQLException e) {
			throw new SenseInventoryException(e);
		}
		catch (Exception e) {
			throw new SenseInventoryException(e);
		}
	}

	@Override
	public String getMostFrequentSense(String sod, POS pos)
		throws SenseInventoryException, UnsupportedOperationException
	{
		try {
			return reader.getName(TABLE.senses,
					reader.getMostFrequentLinkTarget(sod, null, null, 0));
		}
		catch (SQLException e) {
			throw new SenseInventoryException(e);
		}
		catch (Exception e) {
			throw new SenseInventoryException(e);
		}
	}

	@Override
	public List<String> getIncomingLinks(String target)
		throws SenseInventoryException, UnsupportedOperationException
	{
		try {
			return reader.getLinkSources(null, null, target, 0l);
		}
		catch (SQLException e) {
			throw new SenseInventoryException(e);
		}
		catch (Exception e) {
			throw new SenseInventoryException(e);
		}
	}

	@Override
	public int getNumberOfSenses()
	{
		return reader.getNumberOfSenses();
	}
}
