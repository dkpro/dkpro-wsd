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

package de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.PerformanceLinkInformationReader.TABLE;

/**
 * A high-performace writer to the LinkDatabase
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class PerformanceLinkInformationWriter{

	private final static Logger logger = Logger
			.getLogger(PerformanceLinkInformationWriter.class.getName());

	private final String database;
	private final String host;

	private final PerformanceLinkInformationReader reader;

	public PerformanceLinkInformationWriter(String host, String database) {
		this.host = host;
		this.database = database;
		reader = new PerformanceLinkInformationReader(host, database);
		MySqlConnection.createConnection(host,database);

	}

	public void addLink(String anchor, String source, String sense){
		addLink(anchor, source, sense, 1);
	}

	public void closeDbConnection() {
		MySqlConnection.closeConnection();
	}

	public void addLinkDocCount(String anchor, long number) {
		logger.error("Not implemented!");
	}

	public void addId(TABLE table, String name) throws SQLException{
		if(MySqlConnection.conn == null){
			MySqlConnection.createConnection(host,database);
		}
		PreparedStatement stmt = null;
		try{
			stmt = MySqlConnection.conn.prepareStatement(
			"INSERT into " + table.name() + " (name) values (?)");

			stmt.setString(1, name);

			stmt.execute();
		}
		finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore

				stmt = null;
			}
		}

	}

	public void addLink(String anchor, String source, String sense,
			Integer counter){
		if(MySqlConnection.conn == null) {
			MySqlConnection.createConnection(host,database);
		}
		PreparedStatement stmt = null;

		try {
			//check and add IDs
			long anchorId = checkId(TABLE.anchors, anchor);
			long sourceId = checkId(TABLE.senses, source);
			long senseId = checkId(TABLE.senses, sense);

			//add links
			stmt = MySqlConnection.conn.prepareStatement(
					"INSERT into links (anchorId,sourceId,senseId,counter)" +
			" values (?,?,?,?) ON DUPLICATE KEY UPDATE counter = counter + ?");
			stmt.setLong(1, anchorId);
			stmt.setLong(2, sourceId);
			stmt.setLong(3, senseId);
			stmt.setInt(4, counter);
			stmt.setInt(5, counter);
			stmt.execute();
//			logger.log(Level.INFO,stmt.toString());


		}
		catch (SQLException ex){
			// handle any errors
			logger.warn("SQLException: " + ex.getMessage());
			logger.warn("SQLState: " + ex.getSQLState());
			logger.warn("VendorError: " + ex.getErrorCode());
//			logger.log(Level.WARNING, stmt.toString());
		}
		finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore

				stmt = null;
			}
		}


	}

	private long checkId(TABLE table, String name) throws SQLException {
		long id = reader.getId(table, name);
		if(id == -1){
			addId(table, name);
			id =  reader.getId(table, name);
		}
		return id;
	}
}
