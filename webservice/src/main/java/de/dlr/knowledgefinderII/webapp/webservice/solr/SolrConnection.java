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
package de.dlr.knowledgefinderII.webapp.webservice.solr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
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

	/** The solr server. */
	private SolrServer solrServer;
	private Properties properties;

	private void createConnection() throws IOException {
		properties = new Properties();
		InputStream input = SolrConnection.class.getClassLoader().getResourceAsStream("webservice.properties");
		properties.load(input);

		String scheme = properties.getProperty("solr.scheme");
		String host = properties.getProperty("solr.host");
		String port = properties.getProperty("solr.port");
		String core = properties.getProperty("solr.core");
		String username = properties.getProperty("solr.username");
		String password = properties.getProperty("solr.password");

		String urlServer = scheme + "://" + host + ":" + port + "/" + core;
		CloseableHttpClient client = null;
		
		// .setParser( new org.apache.solr.client.solrj.impl.XMLResponseParser()
		
		if (username != null && password != null) {
		    CredentialsProvider credsProvider = new BasicCredentialsProvider();
		    credsProvider.setCredentials(
		            new AuthScope(host, Integer.parseInt(port)),
		            new UsernamePasswordCredentials(username, password));
		    client = HttpClients.custom()
		            .setDefaultCredentialsProvider(credsProvider).build();
		}
		
		
		logger.info("Connecting to: " + urlServer);
		System.out.print(urlServer);

		solrServer = new HttpSolrServer(urlServer, client);
	}

	/**
	 * Gets the server.
	 *
	 * @return the server
	 * @throws IOException the IO exception
	 */
	public SolrServer getServer() throws IOException {
		if (INSTANCE.solrServer == null)
			createConnection();
		return INSTANCE.solrServer;
	}
}