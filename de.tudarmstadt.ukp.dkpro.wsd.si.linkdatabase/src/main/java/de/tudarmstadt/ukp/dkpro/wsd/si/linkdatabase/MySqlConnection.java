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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A class storing the current connection to a LinkDatabase
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class MySqlConnection {
	static Connection conn = null;
	
	static void createConnection(String host,String database){
		createConnection(host, database, "student", "student");
	}

	static void createConnection(String host,String database, String user, String password){
		// Loading driver
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			conn= DriverManager.getConnection(
			"jdbc:mysql://" + host + "/"+database+"?user="+user+"&password="+password);
		}
		catch ( ClassNotFoundException cnfex ) {
			cnfex.printStackTrace();
		}
		catch ( SQLException sqlex ) {
			sqlex.printStackTrace();
		}
		catch ( Exception excp ) {
			excp.printStackTrace();
		}
	}

	public static void closeConnection() {
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	

}
