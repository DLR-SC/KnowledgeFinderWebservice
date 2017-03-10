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
package de.dlr.knowledgefinderII.webapp.portlet.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;


/**
 * The Class TemplateController. A simple controller, renders menu in JSP
 * templates and reads JS/CSS files
 */
@Controller(value = "templateController")
public class TemplateController {

	private ObjectMapper jsonMapper;

	// Webservice Properties
	private static final String HOST_FIELD = "host";
	private static final String URL_FIELD = "url";
	private static final String URL_DOCUMENTS = "urlDocuments";
	private static final String URL_NODES = "urlNodes";

	// Menu properties
	private static final String MENU_ITEMS = "facets";

	private static final String NAME_FIELD = "name";
	private static final String ID_FIELD = "id";
	private static final String COUNT_FIELD = "count";
	private static final String NUM_FOUND_FIELD = "numFound";
	private static final String CSS_CLASS_FIELD = "cssClass";
	private static final String QUERY_FIELD = "query";
	private static final String SUBITEMS_FIELD = "subItems";
	private static final String SUBITEMSFACET_FIELD = "subItemsFacet";
	private static final String COLLAPSED_FIELD = "collapsed";
	private static final String SCROLL_FIELD = "scrollable";
	private static final String IP_SPLIT = "_";

	public TemplateController() {
		jsonMapper = new ObjectMapper(new JsonFactory());
	}

	/**
	 * Handle view render request.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	@RenderMapping
	@RequestMapping("VIEW")
	// this will tell the front controller or dispatcher for view mode
	public ModelAndView handleViewRenderRequest(RenderRequest request,
			RenderResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();

		String url = "";
		String filterConfig = "";
		String resultListConfig = "";
		String detailViewConfig = "";
		String exportConfig = "";
		String facetList = "";
		String fieldList = "";
		try {
			Properties wserver = new Properties();
			wserver.load(TemplateController.class.getClassLoader().getResourceAsStream("server.properties"));
			String urlNodes = wserver.getProperty(HOST_FIELD) + wserver.getProperty(URL_FIELD) + wserver.getProperty(URL_NODES);

			url = wserver.getProperty("url");

			InputStream is = TemplateController.class.getClassLoader().getResourceAsStream("filterConfig.json");
			Map<String, Object> configuration = jsonMapper.readValue(is, new TypeReference<Map<String, Object>>() {});

			Map<String, Map<String, Object>> facetMap = new HashMap<String, Map<String, Object>>();
			addfacetsListEntry(facetMap, (List<Map<String, Object>>) configuration.get(MENU_ITEMS), null);

			Map<String, Object> fieldMap = new HashMap<String, Object>();
			for (String key : facetMap.keySet()) {
				String field = (String) facetMap.get(key).get("field");
				if (field != null && !field.equals(""))
					fieldMap.put(field, (String) facetMap.get(key).get("id"));
			}

			for (String field : fieldMap.keySet()) {
				if (field != null && !field.equals("")) {
					String urlQuery = urlNodes + "-query/-filterQuery/-limit/groups/" + field.replace(" ", "%20");
					Map<String, Object> valuesService = jsonMapper.readValue(new URL(urlQuery), 
							new TypeReference<Map<String, Object>>() {});
					Map<String, Object> parentFacet = facetMap.get(fieldMap.get(field));
					addfacetsListEntry(facetMap, (List<Map<String, Object>>) valuesService.get(field), parentFacet);

				}
			}

			fieldList = jsonMapper.writeValueAsString(fieldMap).replace("\\", "\\\\").replace("\"", "\\\"");
			facetList = jsonMapper.writeValueAsString(facetMap).replace("\\", "\\\\").replace("\"", "\\\"");

			filterConfig = jsonMapper.writeValueAsString(configuration);

			is = TemplateController.class.getClassLoader().getResourceAsStream("detailViewConfig.json");
			configuration = jsonMapper.readValue(is, new TypeReference<Map<String, Object>>() {});
			detailViewConfig = jsonMapper.writeValueAsString(configuration);

			is = TemplateController.class.getClassLoader().getResourceAsStream("resultListConfig.json");
			configuration = jsonMapper.readValue(is, new TypeReference<Map<String, Object>>() {});
			resultListConfig = jsonMapper.writeValueAsString(configuration);
			
			is = TemplateController.class.getClassLoader().getResourceAsStream("exportConfig.json");
			configuration = jsonMapper.readValue(is, new TypeReference<Map<String, Object>>() {});
			exportConfig = jsonMapper.writeValueAsString(configuration);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		model.put("allFacets", facetList);
		model.put("allFields", fieldList);
		model.put("filterConfig", filterConfig);
		model.put("detailViewConfig", detailViewConfig);
		model.put("resultListConfig", resultListConfig);
		model.put("exportConfig", exportConfig);
		model.put("baseUrl", url);

		return new ModelAndView("index", model);
	}

	private void addfacetsListEntry(Map<String, Map<String, Object>> list,
			List<Map<String, Object>> facets, Map<String, Object> parentFacet) {
		if (facets != null) {
			for (Map<String, Object> facet : facets) {
				String id = (String) facet.get(ID_FIELD);
				String field = (String) facet.get(SUBITEMSFACET_FIELD);
				String query = (String) facet.get(QUERY_FIELD);
				String css = (String) facet.get(CSS_CLASS_FIELD);
				String parent = "";
				String group = id;

				if (parentFacet != null) {
					id = (String) parentFacet.get("id") + "_" + id;
					if (css == null) {
						css = (String) parentFacet.get("cssClass");
					}
					parent = (String) parentFacet.get("id");
					group = (String) parentFacet.get("group");
				}

				if (query == null && field != null) {
					query = field + ":[\"\" TO *]";
				}

				Map<String, Object> entry = new HashMap<String, Object>();
				entry.put("id", id);
				entry.put("name", (String) facet.get(NAME_FIELD));
				entry.put("query", query);
				entry.put("field", field);
				entry.put("cssClass", css);
				entry.put("count", (Integer) facet.get(COUNT_FIELD));
				entry.put("group", group);
				entry.put("parent", parent);

				facet.put("id", id);

				list.put(id, entry);

				List<Map<String, Object>> subItems = (List<Map<String, Object>>) facet.get(SUBITEMS_FIELD);
				if (subItems != null)
					addfacetsListEntry(list, subItems, entry);
			}
		}
	}
}
