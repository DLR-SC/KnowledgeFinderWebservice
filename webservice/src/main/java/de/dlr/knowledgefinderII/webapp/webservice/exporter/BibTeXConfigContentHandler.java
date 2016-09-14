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

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BibTeXConfigContentHandler extends DefaultHandler {
	
	private boolean flag = false;
	private boolean tagFlag = false;
	private boolean typeFlag = false;
	private boolean valueFlag = false;
	private boolean nameFlag = false;
	private StringBuilder stringBuilder = new StringBuilder();
	private LinkedList<String[]> tagsTable = new LinkedList<String[]>();
	private LinkedList<String[]> typesTable = new LinkedList<String[]>();
	private BibTeXExporter caller;
	
	
	public BibTeXConfigContentHandler(BibTeXExporter bibTeXExporter) {
		super();
		this.caller = bibTeXExporter;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("bibtex")) {
			flag = true;
		} else if (flag && qName.equals("type")){
			typeFlag = true;
		} else if (flag && qName.equals("tag")){
			tagFlag = true;
		} else if ((typeFlag ^ tagFlag) && qName.equals("name")) {
			nameFlag = true;
		} else if ((typeFlag ^ tagFlag) && qName.equals("value")) {
			valueFlag = true;
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
		if (qName.equals("bibtex")) {
			flag = false;
		} else if (qName.equals("type")) {
			typeFlag = false;
		} else if(qName.equals("tag")) {
			tagFlag = false;
		} else if (qName.equals("name")&&nameFlag) {
			if(tagFlag){
				tagsTable.addLast(new String[] { stringBuilder.toString(), null });
			} else if (typeFlag){
				typesTable.addLast(new String[] { stringBuilder.toString(), null });
			}
			stringBuilder = new StringBuilder();
			nameFlag = false;
		} else if (qName.equals("value")&&valueFlag) {
			if(tagFlag){
				tagsTable.getLast()[1] = stringBuilder.toString();
			} else if (typeFlag){
				typesTable.getLast()[1] = stringBuilder.toString();
			}
			stringBuilder = new StringBuilder();
			valueFlag = false;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		for (String[] element : tagsTable) {
			caller.addTag(element[0], element[1]);
		}
		for (String[] element : typesTable) {
			caller.addType(element[0], element[1]);
		}
	}
}


