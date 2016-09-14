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

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SolrQueryFactoryXMLContentHandler extends DefaultHandler {

	private boolean flag = false;
	private boolean valueFlag = false;
	private boolean nameFlag = false;
	private String role;
	private StringBuilder stringBuilder = new StringBuilder();
	private LinkedList<String[]> table = new LinkedList<String[]>();
	private SolrQueryFactoryXML caller;

	public SolrQueryFactoryXMLContentHandler(String role,
			SolrQueryFactoryXML caller) {
		super();
		this.role = role;
		this.caller = caller;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("default")) {
			flag = true;
		} else if (flag && qName.equals("name")) {
			nameFlag = true;
		} else if (flag && qName.equals("value")) {
			valueFlag = true;
		}
		if (qName.equals("role")
				&& new String(attributes.getValue("id")).equals(role)) {
			flag = true;
			table.add(new String[] { "IS_VALID", "true" });
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (nameFlag) {
			stringBuilder.append(new String(ch, start, length));

		} else if (valueFlag) {
			stringBuilder.append(new String(ch, start, length));

		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("role") || qName.equals("default")) {
			flag = false;
		}
		if (qName.equals("name")&&nameFlag) {
			table.addLast(new String[] { stringBuilder.toString(), null });
			stringBuilder = new StringBuilder();
			nameFlag = false;
		}
		if (qName.equals("value")&&valueFlag) {
			table.getLast()[1] = stringBuilder.toString();
			stringBuilder = new StringBuilder();
			valueFlag = false;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		for (String[] element : table) {
			caller.setParameter(element[0], element[1]);
		}
	}
}
