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
package de.dlr.knowledgefinderII.webapp.webservice.exporter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.Key;
import org.jbibtex.StringValue;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class BibTeXExporter {

	private Map<String, String> tags = new HashMap<String, String>();
	private Map<String, String> types = new HashMap<String, String>();

	public BibTeXExporter() throws Exception {
		this("bibtex.xml");
	}

	public BibTeXExporter(String configFile) throws Exception {
		this(configFile, createPath());
	}

	public BibTeXExporter(String configFile, String basePath) throws Exception {
		String configFilePath = Paths.get(basePath, configFile).toString();

		parseXMLFile(configFilePath);
	}

	public void addTag(String name, String value) {
		tags.put(value, name);
	}

	public void addType(String name, String value) {
		types.put(value, name);
	}

	public String export(SolrDocumentList docs) throws IOException {
		StringWriter writer = new StringWriter();
		BibTeXDatabase db = new BibTeXDatabase();
		Iterator<SolrDocument> itr = docs.iterator();
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			db.addObject(convertData(doc));
		}
		BibTeXFormatter formatter = new BibTeXFormatter();
		formatter.setIndent("");
		formatter.format(db, writer);
		return writer.toString().replaceAll("(\\n)|(\\t)", "");
	}

	private BibTeXEntry convertData(SolrDocument doc) {
		String type = (String) doc.getFieldValue("resourceType");
		type = types.containsKey(type) ? types.get(type) : "misc";
		String id = (String) doc.getFieldValue("id");
		BibTeXEntry entry = new BibTeXEntry(new Key(type), new Key(id));
		Set<String> fields = doc.keySet();
		Iterator<String> itr = fields.iterator();
		while (itr.hasNext()) {
			String field = itr.next();
			Object value = doc.get(field);
			String stringValue = "";
			if (value.getClass().equals(ArrayList.class)) {

				for (Object o : (ArrayList<Object>) value) {
					if (tags.get(field).equals("Author")) {
						stringValue = formatAuthorText(String.valueOf(o));
					} else {
						stringValue += String.valueOf(o);
					}
				}
			} else if (value.getClass().equals(Date.class)) {
				DateFormat df;
				if (tags.get(field).equals("Year"))
					df = new SimpleDateFormat("yyyy");
				else
					df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				stringValue = df.format((Date) value);
			} else {
				stringValue = (String) value;
			}
			if (tags.containsKey(field)) {
				entry.addField(new Key(tags.get(field)), new StringValue(stringValue, StringValue.Style.BRACED));
			}
		}

		return entry;
	}

	private String formatAuthorText(String input) {
		StringBuilder stringBuilder = new StringBuilder();
		String[] split = input.split(",");
		for (String s : split) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" and ");
			}
			stringBuilder.append(s);
		}
		return stringBuilder.toString();
	}

	private void parseXMLFile(String configFile)
			throws ParserConfigurationException, SAXException, SAXParseException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();

		factory.setNamespaceAware(true);
		factory.setXIncludeAware(true);
		factory.setValidating(true);
		SAXParser saxParser = factory.newSAXParser();
		DefaultHandler handler = new BibTeXConfigContentHandler(this);
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
		reader.parse(new InputSource(configFile));
	}

	private static String createPath() {
		String filePath = BibTeXExporter.class.getClassLoader().getResource("").getPath();
		String newPath = System.getProperty("os.name").contains("indow") ? filePath.substring(1) : filePath;
		Path path = Paths.get(newPath, "config");
		return path.toString();
	}
}