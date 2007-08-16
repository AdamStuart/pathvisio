// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.naming.OperationNotSupportedException;

import org.pathvisio.debug.Logger;
import org.pathvisio.debug.StopWatch;

public class DBConnectorDerbyServer extends AbstractDBConnector {
	String host;
	int port;
	
	public DBConnectorDerbyServer(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public Connection createConnection(String dbName) throws Exception {
		Properties sysprop = System.getProperties();
		sysprop.setProperty("derby.storage.tempDirectory", System.getProperty("java.io.tmpdir"));
		sysprop.setProperty("derby.stream.error.file", File.createTempFile("derby",".log").toString());
		
		Class.forName("org.apache.derby.jdbc.ClientDriver");
		
		StopWatch timer = new StopWatch();
		timer.start();
		
		String url = "jdbc:derby://" + host + ":" + port + "/" + dbName;
		Logger.log.trace("Connecting to database: " + url);
		Connection con = DriverManager.getConnection(url);
		Logger.log.trace("Connected");
		return con;
	}

	public Connection createConnection(String dbName, int props) throws Exception {
		return createConnection(dbName);
	}

	public String finalizeNewDatabase(String dbName) throws Exception {
		//Creating database not supported
		throw new OperationNotSupportedException("Can't create new database on server");
	}

}
