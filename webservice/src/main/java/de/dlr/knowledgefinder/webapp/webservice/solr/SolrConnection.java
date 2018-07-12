/*******************************************************************************
 * Copyright 2016 DLR - German Aerospace Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.dlr.knowledgefinder.webapp.webservice.solr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Enum SolrConnection.
 */
public enum SolrConnection {

	/** The instance. */
	INSTANCE;

	private final Logger logger = LoggerFactory.getLogger(SolrConnection.class);

	/** The solr client. */
	private SolrClient solrClient;
	private Properties properties;
	private String username;
	private String password;
	private boolean credentialsSet = true;

	private void createConnection() throws IOException {
		properties = new Properties();
		InputStream input = SolrConnection.class.getClassLoader().getResourceAsStream("webservice.properties");
		properties.load(input);

		String scheme = properties.getProperty("solr.scheme");
		String host = properties.getProperty("solr.host");
		String port = properties.getProperty("solr.port");
		String core = properties.getProperty("solr.core");

		String urlServer = scheme + "://" + host + ":" + port + "/" + core;
		
		logger.info("Connecting to: " + urlServer);
		System.out.print(urlServer);

		solrClient = new HttpSolrClient.Builder(urlServer).build();
	}

	/**
	 * Gets the server.
	 *
	 * @return the server
	 * @throws IOException the IO exception
	 */
	public SolrClient getClient() throws IOException {
		if (INSTANCE.solrClient == null)
			createConnection();
		return INSTANCE.solrClient;
	}
	
	
	private void createRequestAuthentification() throws IOException {
		properties = new Properties();
		InputStream input = SolrConnection.class.getClassLoader().getResourceAsStream("webservice.properties");
		properties.load(input);

		username = properties.getProperty("solr.username");
		password = properties.getProperty("solr.password");
				
		if (username != null && password != null) {
			logger.info("Authenticate as user: " + username);
		} else {
			credentialsSet = false;
		}
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 * @throws IOException the IO exception
	 */
	public String getUsername() throws IOException {
		if (INSTANCE.credentialsSet && INSTANCE.username == null)
			createRequestAuthentification();
		return INSTANCE.username;
	}
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 * @throws IOException the IO exception
	 */
	public String getPassword() throws IOException {
		if (INSTANCE.credentialsSet && INSTANCE.password == null)
			createRequestAuthentification();
		return INSTANCE.password;
	}
}