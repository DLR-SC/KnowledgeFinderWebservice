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
package de.dlr.knowledgefinderII.webapp.webservice.solr.query;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.solr.client.solrj.SolrQuery;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SolrQueryFactoryXML extends AbstractSolrQueryFactory {

	private String configFile;
	private String type;
	private boolean IS_VALID = false;

	public SolrQueryFactoryXML(String factory) throws Exception {
		this(factory, "config.xml");
	}

	public SolrQueryFactoryXML(String factory, String configFile)
			throws Exception {
		this(factory, configFile, createPath());

	}

	public SolrQueryFactoryXML(String factory, String configFile,
			String basePath) throws Exception {
		this.type = factory;
		this.configFile = Paths.get(basePath, configFile).toString();

		parseXMLFile();
	}

	public String getConfigFile() {
		return configFile;
	}

	private void parseXMLFile() throws ParserConfigurationException,
			SAXException, SAXParseException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();

		factory.setNamespaceAware(true);
		factory.setXIncludeAware(true);
		factory.setValidating(true);
		SAXParser saxParser = factory.newSAXParser();
		DefaultHandler handler = new SolrQueryFactoryXMLContentHandler(type,
				this);
		XMLReader reader = saxParser.getXMLReader();
		reader.setErrorHandler(new ErrorHandler() {
			public void warning(SAXParseException e) throws SAXException {
				System.out.println("WARNING : " + e.getMessage()); // do nothing
			}

			public void error(SAXParseException e) throws SAXException {
				System.out.println("ERROR : " + e.getMessage());
				throw e;
			}

			public void fatalError(SAXParseException e) throws SAXException {
				System.out.println("FATAL : " + e.getMessage());
				throw e;
			}
		});
		reader.setContentHandler(handler);
		reader.parse(new InputSource(getConfigFile()));
	}

	protected void setParameter(String name, String value) {
		switch (name) {
		case "DEFAULT_QUERY":
			DEFAULT_QUERY = value;
			break;
		case "DEFAULT_FQ":
			DEFAULT_FQ = splitStringArrays(value);
			break;
		case "DEFAULT_FIELDS":
			DEFAULT_FIELDS = splitStringArrays(value);
			break;
		case "DEFAULT_HL_FIELDS":
			DEFAULT_HL_FIELDS = splitStringArrays(value);
			break;
		case "DEFAULT_SORT_FIELD":
			DEFAULT_SORT_FIELD = value;
			break;
		case "DEFAULT_SORT_ORDER":
			DEFAULT_SORT_ORDER = createSortOrder(value);
			break;
		case "DEFAULT_FACET_LIMIT":
			try {
				DEFAULT_FACET_LIMIT = new Integer(value).intValue();
			} catch (NumberFormatException e) {
				DEFAULT_FACET_LIMIT = -2;
			}
			break;
		case "APPEND_QUERY":
			APPEND_QUERY = value;
			break;
		case "ALLOWED_FIELDS":
			ALLOWED_FIELDS = splitStringArrays(value);
			break;
		case "ALLOWED_FACET_FIELDS":
			ALLOWED_FACET_FIELDS = splitStringArrays(value);
			break;
		case "IS_VALID":
			IS_VALID = true;
			break;
		default:
			System.out
					.println("The input does not match any of the accepted values.");
			System.out.println("\t input: " + name);
			System.out.println("\t value: " + value);
			throw new IllegalArgumentException("input: " + name + "; value: "
					+ value);
		}
	}

	private String[] splitStringArrays(String s) {
		if (s != null && !s.equals("")) {
			String replacedString = s.replace("\n", "").replace("\t", "");
			if (!replacedString.equals("")) {
				String[] sA = replacedString.split(",");
				for (int i = 0; i < sA.length; i++) {
					sA[i] = sA[i].trim();
				}
				return sA;
			}
		}
		return new String[] {};
	}

	private SolrQuery.ORDER createSortOrder(String s) {
		if (s.trim().equals("asc"))
			return SolrQuery.ORDER.asc;
		if (s.trim().equals("desc"))
			return SolrQuery.ORDER.desc;
		return null;
	}

	private static String createPath() {
		String filePath = SolrQueryFactoryXML.class.getClassLoader()
				.getResource("").getPath();
		String newPath = System.getProperty("os.name").contains("indow") ? filePath
				.substring(1) : filePath;
		Path path = Paths.get(newPath, "config");
		return path.toString();
	}

	public boolean isValid() {
		return IS_VALID;
	}

}
