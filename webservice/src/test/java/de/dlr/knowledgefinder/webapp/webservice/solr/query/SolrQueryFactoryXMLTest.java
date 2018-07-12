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
package de.dlr.knowledgefinder.webapp.webservice.solr.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXParseException;

public class SolrQueryFactoryXMLTest {

	private SolrQueryFactoryXML factory;
	private String testPath;
	private String configFile;
	private Map<String, Object> expectedContext = new HashMap<>();

	@Before
	public void setup() {
		testPath = createPath();
		configFile = "config.xml";

		expectedContext.put("query", "*:*");
		expectedContext.put("sort", new SolrQuery.SortClause("id",
				SolrQuery.ORDER.asc));
		expectedContext.put("filterQuery", new String[0]);
		expectedContext.put("fields", new String[] { "id" });
		expectedContext.put("hlFields", new String[] { "*:*" });
		expectedContext.put("facetLimit", -1);
		expectedContext.put("appendQuery", "*:*");
		expectedContext.put("allowedFields", new String[] { "test", "foo",
				"bar", "me", "foobar" });
		expectedContext.put("allowedFacets",
				new String[] { "test", "me", "foo" });
		expectedContext.put("valid", true);
	}

	public void checkAsserts() {
		assertThat(factory.getDefaultQuery(), is(expectedContext.get("query")));
		assertThat(factory.getDefaultSort(), is(expectedContext.get("sort")));
		assertThat(factory.getDefaultFilterQueries(),
				is(expectedContext.get("filterQuery")));
		assertThat(factory.getDefaultFields(),
				is(expectedContext.get("fields")));
		assertThat(factory.getDefaultHLFields(),
				is(expectedContext.get("hlFields")));
		assertThat(factory.getDefaultFacetLimit(),
				is(expectedContext.get("facetLimit")));
		assertThat(factory.getAppendQuery(),
				is(expectedContext.get("appendQuery")));
		assertThat(factory.getAllowedFields(),
				is(expectedContext.get("allowedFields")));
		assertThat(factory.getAllowedFacetFields(),
				is(expectedContext.get("allowedFacets")));
	}

	@Test
	public void testParserDefault() throws Exception {

		factory = new SolrQueryFactoryXML("Anonymous", configFile, testPath);

		checkAsserts();
	}

	@Test
	public void testParserOverrideDefaults() throws Exception {

		expectedContext.put("allowedFields", new String[] { "filepath" });

		factory = new SolrQueryFactoryXML("FW", configFile, testPath);

		checkAsserts();
	}

	@Test
	public void testMultiLineInput() throws Exception {

		configFile = "multiLine.xml";
		expectedContext.put("allowedFields", new String[] { "test", "me",
				"foobar" });

		factory = new SolrQueryFactoryXML("multiLine", configFile, testPath);

		checkAsserts();
	}

	@Test(expected = FileNotFoundException.class)
	public void testFileNotFound() throws Exception {

		configFile = "imNotThere.xml";

		factory = new SolrQueryFactoryXML("", configFile, testPath);
	}

	@Test(expected = SAXParseException.class)
	public void testMissingEndTag() throws Exception {

		configFile = "missingEndTag.xml";

		factory = new SolrQueryFactoryXML("", configFile, testPath);

	}

	@Test(expected = SAXParseException.class)
	public void testMissingNameValuePair() throws Exception {

		configFile = "missingNameValue.xml";

		factory = new SolrQueryFactoryXML("", configFile, testPath);

	}

	@Test(expected = SAXParseException.class)
	public void testMissingName() throws Exception {

		configFile = "missingName.xml";

		factory = new SolrQueryFactoryXML("", configFile, testPath);

	}

	@Test(expected = SAXParseException.class)
	public void testMissingValue() throws Exception {

		configFile = "missingValue.xml";

		factory = new SolrQueryFactoryXML("", configFile, testPath);

	}

	@Test(expected = SAXParseException.class)
	public void testMissingFieldTag() throws Exception {

		configFile = "missingFieldTag.xml";

		factory = new SolrQueryFactoryXML("", configFile, testPath);

	}

	@Test(expected = SAXParseException.class)
	public void testMissingFieldEnviroment() throws Exception {

		configFile = "missingFieldEnviroment.xml";

		factory = new SolrQueryFactoryXML("", configFile, testPath);

	}
	
	@Test(expected = SAXParseException.class)
	public void testMissingIDAttribute() throws Exception {

		configFile = "missingIDAttribute.xml";

		factory = new SolrQueryFactoryXML("", configFile, testPath);

	}

	private static String createPath() {
		String filePath = null;
		try {
			filePath = SolrQueryFactoryXMLTest.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String newPath = System.getProperty("os.name").contains("indow") ? filePath
				.substring(1) : filePath;
		Path path = Paths.get(newPath, "test-config");
		return path.toString();
	}

}
