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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.util.LinkIdentification;

/**
 * A high-performance reader from a LinkDatabase
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class PerformanceLinkInformationReader {

	private final static Logger logger = Logger
			.getLogger(PerformanceLinkInformationReader.class.getName());

	private final String database;
	private final String host;

	public static enum TABLE{anchors,senses,links,unique_anchors};
	public static enum COLUMN{id,anchorId,sourceId,senseId,counter,name}

	public PerformanceLinkInformationReader(String host,String database){
		this.database = database;
		this.host = host;
		MySqlConnection.createConnection(host,database);
	}

	public PerformanceLinkInformationReader(String host,String database, String user, String password){
		this.database = database;
		this.host = host;
		MySqlConnection.createConnection(host,database, user, password);
	}
	
	public int getLinkCount(String anchor, String source, String target, long min_count) throws Exception {
		int sum=0;
		for(LinkIdentification link : query(anchor,source,target,min_count)){
			sum += link.getCount();
		}
		return sum;
	}

	public int getLinkCount(Long anchor, Long source, Long target, long min_count) throws Exception {
		int sum=0;
		for(LinkIdentification link : query(anchor,source,target,min_count)){
			sum += link.getCount();
		}
		return sum;
	}

	public Iterator<LinkIdentification> getLinkAnchorIterator(String anchor, String source, String target, long min_count) throws Exception {
		return query(anchor,source,target,min_count).iterator();
	}

	public List<String> getLinkSources(String anchor, String source, String target, long min_count) throws Exception {
		List<String> sources = new ArrayList<String>();
		for(LinkIdentification link : query(anchor,source,target,min_count)){
			sources.add(getName(TABLE.senses, link.getSourceId()));
		}
		return sources;
	}

	public boolean isLinkAnchor(String anchor, String source, String target, long min_count) throws Exception {
		return !query(anchor,source,target,min_count).isEmpty();
	}

	public boolean isSourcePage(String anchor, String source, String target, long min_count) throws Exception {
		return !query(anchor,source,target,min_count).isEmpty();
	}

	public boolean isTargetPage(String anchor, String source, String target, long min_count) throws Exception {
		return !query(anchor,source,target,min_count).isEmpty();
	}

	public Map<Long,Long> getLinkTargets(String anchor, String source, String target, long min_count) throws Exception {
		List<LinkIdentification> linkInformation = query(anchor,source,target,min_count);
		HashMap<Long,Long> linkTargets = new HashMap<Long,Long>();
		for(LinkIdentification info : linkInformation){
			long counter = 0;
			if(linkTargets.containsKey(info.getSenseId())){
				counter = linkTargets.get(info.getSenseId());
			}
			linkTargets.put(info.getSenseId(), counter + info.getCount());
		}
		return linkTargets;
	}

	public Map<Long,Long> getLinkTargets(Long anchor, Long source, Long target, long min_count) throws Exception{
		List<LinkIdentification> linkInformation = query(anchor,source,target,min_count);
		HashMap<Long,Long> linkTargets = new HashMap<Long,Long>();
		for(LinkIdentification info : linkInformation){
			long counter = 0;
			if(linkTargets.containsKey(info.getSenseId())){
				counter = linkTargets.get(info.getSenseId());
			}
			linkTargets.put(info.getSenseId(), counter + info.getCount());
		}
		return linkTargets;
	}

	public Long getMostFrequentLinkTarget(String anchor, String source, String target, long min_count) throws Exception {
		List<LinkIdentification> linkInformation = query(anchor,source,target,min_count);
		Long mostFrequentTarget = null;
		long frequency = 0;
		for(LinkIdentification info : linkInformation){
			if(info.getCount() > frequency){
				frequency = info.getCount();
				mostFrequentTarget = info.getSenseId();
			}
		}
		return mostFrequentTarget;
	}

	public long getNumberOfLinkTargets(String anchor, String source, String target, long min_count) throws Exception {
		List<LinkIdentification> LinkIdentification = query(anchor,source,target,min_count);
		long counter = 0;
		for(LinkIdentification info : LinkIdentification){
			counter += info.getCount();
		}
		return counter;
	}


	private List<LinkIdentification> query(String anchor, String source, String target, long minCount) throws Exception{
		if(MySqlConnection.conn == null || MySqlConnection.conn.isClosed()){
			MySqlConnection.createConnection(host,database);
		}

		//get IDs for parameters
		//return empty list if one of them is not found

		Long anchorId = null;
		Long sourceId = null;
		Long targetId = null;

		if(anchor != null){
			anchorId = getId(TABLE.anchors,anchor);
			if(anchorId == -1){
				return new ArrayList<LinkIdentification>();
			}
		}

		if(source != null){
			sourceId = getId(TABLE.senses,source);
			if(sourceId == -1){
				return new ArrayList<LinkIdentification>();
			}
		}

		if(target != null){
			targetId = getId(TABLE.senses,target);
			if(targetId == -1){
				return new ArrayList<LinkIdentification>();
			}
		}
		return query(anchorId,sourceId,targetId,minCount);
	}

	private List<LinkIdentification> query(Long anchor, Long source, Long target, long minCount) throws Exception{
		List<LinkIdentification> links = new ArrayList<LinkIdentification>();

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			if(MySqlConnection.conn == null || MySqlConnection.conn.isClosed()){
				MySqlConnection.createConnection(host,database);
			}

			//get IDs for parameters
			//return empty list if one of them is not found



			HashMap<COLUMN, Long> restrictions = new HashMap<COLUMN,Long>();
			//fill parameters into map
			if(anchor != null){
				if(anchor == -1){
					return links;
				}
				restrictions.put(COLUMN.anchorId, anchor);
			}

			if(source != null){
				if(source == -1){
					return links;
				}
				restrictions.put(COLUMN.sourceId, source);
			}

			if(target != null){
				if(target == -1){
					return links;
				}
				restrictions.put(COLUMN.senseId, target);
			}


			stmt = MySqlConnection.conn.prepareStatement(createStatement(restrictions));

			int counter = 1;
			for(COLUMN key : restrictions.keySet()){
				stmt.setLong(counter++, restrictions.get(key));
			}
			stmt.setLong(counter, minCount);


			logger.debug(stmt.toString());

			stmt.execute();
			rs = stmt.getResultSet ();

			//fill LinkIdentification from result set
			while(rs.next()){
				LinkIdentification info = new LinkIdentification();
				info.setAnchorId(rs.getLong(COLUMN.anchorId.toString()));
				info.setSourceId(rs.getLong(COLUMN.sourceId.toString()));
				info.setSenseId(rs.getLong(COLUMN.senseId.toString()));
				info.setCount(rs.getLong(COLUMN.counter.toString()));
				links.add(info);
			}

		}
		catch (SQLException ex){
			// handle any errors
			logger.warn("SQLException2: " + ex.getMessage());
			logger.warn("SQLState: " + ex.getSQLState());
			logger.warn("VendorError: " + ex.getErrorCode());
		}
		finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { } // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore

				stmt = null;
			}
		}
		return links;

	}

	public long getId(TABLE table, String name) throws SQLException {
		if(MySqlConnection.conn == null || MySqlConnection.conn.isClosed()){
			MySqlConnection.createConnection(host,database);
		}

		PreparedStatement stmt = MySqlConnection.conn.prepareStatement("SELECT * FROM " + table.name() + " WHERE name=?");

		stmt.setString(1, name);
		ResultSet rs = null;
		long id = -1;
		try{
			stmt.execute();
			rs = stmt.getResultSet ();

			//if there is no such an id return -1
			if(!rs.next()){
				return -1;
			}

			//get the id from the result set
			id= rs.getLong(COLUMN.id.toString());
			//TODO solve uppercase problem
			//			if (rs.next()){
			//				logger.log(Level.SEVERE,"Id is probably not unique");
			//				throw new SQLException();
			//			}
		}
		finally{
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { } // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore

				stmt = null;
			}
		}

		return id;
	}

	public String getName(TABLE table, Long id) throws SQLException {
		if(id == null){
			return null;
		}

		if(MySqlConnection.conn == null || MySqlConnection.conn.isClosed()){
			MySqlConnection.createConnection(host,database);
		}

		PreparedStatement stmt = MySqlConnection.conn.prepareStatement("SELECT * FROM " + table.name() + " WHERE id=?");

		stmt.setLong(1, id);
		ResultSet rs = null;
		String name;
		try{
			stmt.execute();
			rs = stmt.getResultSet();

			//if there is no such an id return null
			if(!rs.next()){
				return null;
			}

			//get the name from the result set
			name= rs.getString(COLUMN.name.toString());
		}
		finally{
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { } // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore

				stmt = null;
			}
		}

		return name;
	}

	private String createStatement(HashMap<COLUMN, Long> restrictions) {
		String statement = "SELECT * FROM links l WHERE ";

		Iterator<COLUMN> keyIt = restrictions.keySet().iterator();
		COLUMN nextKey;
		while(keyIt.hasNext()){
			nextKey = keyIt.next();
			statement += nextKey.toString() + "= ?";
			statement += " AND ";
		}
		statement += " "+COLUMN.counter+" > ?";

		//		System.out.println(statement);
		return statement;
	}

	public void closeDbConnection() {
		MySqlConnection.closeConnection();
	}

	public int getNumberOfSenses() {
		// TODO Auto-generated method stub
		return 3600000;
	}

}
