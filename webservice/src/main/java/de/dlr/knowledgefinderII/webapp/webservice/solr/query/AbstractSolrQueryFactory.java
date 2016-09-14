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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.common.params.FacetParams;

/**
 * The Class AbstractSolrQueryFactory.
 */
public abstract class AbstractSolrQueryFactory {

	protected String DEFAULT_QUERY = "";
	protected String[] DEFAULT_FQ = new String[] {};
	protected String[] DEFAULT_FIELDS = new String[] {};
	protected String[] DEFAULT_HL_FIELDS = new String[] {};
	protected String DEFAULT_SORT_FIELD = "";
	protected SolrQuery.ORDER DEFAULT_SORT_ORDER = SolrQuery.ORDER.asc;
	protected int DEFAULT_FACET_LIMIT = 0;

	protected String APPEND_QUERY = "";
	protected String[] ALLOWED_FIELDS = new String[] {};
	protected String[] ALLOWED_FACET_FIELDS = new String[] {};

	protected String getAppendQuery() {
		return APPEND_QUERY;
	}

	protected String[] getAllowedFields() {
		return ALLOWED_FIELDS;
	}

	protected String[] getAllowedFacetFields() {
		return ALLOWED_FACET_FIELDS;
	}

	protected String getDefaultQuery() {
		return DEFAULT_QUERY;
	}

	protected String[] getDefaultFields() {
		return DEFAULT_FIELDS;
	}

	protected String[] getDefaultHLFields() {
		return DEFAULT_HL_FIELDS;
	}

	protected String[] getDefaultFilterQueries() {
		return DEFAULT_FQ;
	}

	protected int getDefaultFacetLimit() {
		return DEFAULT_FACET_LIMIT;
	}

	protected SortClause getDefaultSort() {
		return new SortClause(DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER);
	}

	/**
	 * Creates the query.
	 *
	 * @param query
	 *            the query, which should be executed
	 * @param filterQuery
	 *            the filter query
	 * @param fields
	 *            the fields
	 * @param facetFields
	 *            the facet fields
	 * @param start
	 *            the start
	 * @param rows
	 *            the rows
	 * @param sort
	 *            the sort
	 * @param highlightFields
	 *            the highlight fields
	 * @param limit
	 *            the limit
	 * @return the solr query
	 */
	public SolrQuery createQuery(String query, String[] filterQuery,
			String[] fields, String[] facetFields, String start, String rows,
			String[] sort, String[] highlightFields, String limit) {

		query = ((query != null) && !query.equals("")) ? query
				: getDefaultQuery();
		fields = fields != null ? fields : getDefaultFields();
		highlightFields = highlightFields != null ? highlightFields
				: getDefaultHLFields();
		filterQuery = filterQuery != null ? filterQuery
				: getDefaultFilterQueries();

		List<String> fieldList = splitStringArrays(fields);
		List<String> fieldHLList = splitStringArrays(highlightFields);
		for (int i = 0; i < fieldHLList.size(); i++) {
			fieldHLList.set(i, fieldHLList.get(i).trim());
		}

		SolrQuery solrQuery = createSolrQueryWithQueryString(query);

		setSolrQueryParameter(solrQuery);

		addFilterQueri(solrQuery, filterQuery);
		addFieldsToSolrQuery(solrQuery, fieldList);
		makeFieldsToHighlightFields(solrQuery, fieldHLList);
		addSortMethodToSolrQuery(solrQuery, sort);
		setStartForSolrQuery(solrQuery, start);
		setRowsForSolrQuery(solrQuery, rows);

		if (facetFields != null) {
			setFacetFieldsForSolrQuery(solrQuery, facetFields);
			setLimitForSolrQuery(solrQuery, limit);

		}
		return solrQuery;
	}

	private List<String> splitStringArrays(String[] strArr) {
		List<String> list = new ArrayList<String>();
		for (String f : strArr) {
			if (f != null) {
				list.addAll(splitString(f));
			}
		}
		return list;
	}

	private List<String> splitString(String toSplit) {
		return Arrays.asList(toSplit.split(","));
	}

	private SolrQuery createSolrQueryWithQueryString(String query) {
		SolrQuery solrQuery = new SolrQuery();

		String appendQuery = getAppendQuery().trim();
		String qValue = query;
		if (appendQuery != "" && !appendQuery.equals(qValue)) {
			qValue = appendQuery + " AND " + qValue;
		}
		solrQuery.setQuery(qValue);
		solrQuery.addField(null);

		return solrQuery;
	}

	private void addFilterQueri(SolrQuery solrQuery, String[] filterQuery) {

		for (String fq : filterQuery) {
			// cached queries, better performance and not in
			// highlight hl.q = q but fq != q and fq != hl.q
			if (fq != null && fq != "") {
				solrQuery.addFilterQuery(fq.trim());
			}
		}
	}

	private void addFieldsToSolrQuery(SolrQuery solrQuery,
			List<String> fieldList) {

		tryToAddGivenFieldsToSolrQuery(solrQuery, fieldList);
		if (solrQuery.getFields().equals("")) {
			addDefaultFieldsToSolrQuery(solrQuery);
		}
	}

	private void tryToAddGivenFieldsToSolrQuery(SolrQuery solrQuery,
			List<String> fieldList) {
		List<String> allowedFields = Arrays.asList(getAllowedFields());
		for (String f : fieldList) {
			f = f.trim();
			if (allowedFields.contains(f)) {
				solrQuery.addField(f);
			}
		}
	}

	private void addDefaultFieldsToSolrQuery(SolrQuery solrQuery) {
		for (String f : getDefaultFields()) {
			solrQuery.addField(f.trim());
		}
	}

	private void setSolrQueryParameter(SolrQuery solrQuery) {
		solrQuery.setHighlight(true);
		solrQuery.setParam("hl.fragsize", "0");
		solrQuery.setParam("hl.preserveMulti", "true");
	}

	private void makeFieldsToHighlightFields(SolrQuery solrQuery,
			List<String> fieldHLList) {
		List<String> fieldList = splitString(solrQuery.getFields());

		for (String field : fieldList) {
			field = field.trim();
			if (fieldHLList.contains(field)) {
				solrQuery.addHighlightField(field);

			}
		}
	}

	private void addSortMethodToSolrQuery(SolrQuery solrQuery, String[] sort) {
		if (sort != null) {
			for (String s : sort) {
				tryToAddSortOrder(solrQuery, s);
			}
		}

		if (solrQuery.getSorts().size() == 0) {
			solrQuery.addSort(getDefaultSort());
		}
	}

	private void tryToAddSortOrder(SolrQuery solrQuery, String s) {
		if (s != null && !s.equals("")) {
			String[] sortSplit = s.trim().split(" ");
			if (sortSplit.length == 2) {
				ORDER queryOrderSort = null;
				if (sortSplit[1].equals("asc")) {
					queryOrderSort = SolrQuery.ORDER.asc;
				} else if (sortSplit[1].equals("desc")) {
					queryOrderSort = SolrQuery.ORDER.desc;
				}
				String fieldSort = sortSplit[0];
				if (queryOrderSort != null) {
					solrQuery
							.addSort(new SortClause(fieldSort, queryOrderSort));
				}
			}
		}
	}

	private void setStartForSolrQuery(SolrQuery solrQuery, String start) {
		try {
			int startInt = Integer.parseInt(start);
			if (startInt >= 0) {
				solrQuery.setStart(startInt);
			}
		} catch (NumberFormatException e) {
		}

	}

	private void setRowsForSolrQuery(SolrQuery solrQuery, String rows) {
		try {
			int rowsInt = Integer.parseInt(rows);
			if (rowsInt >= 0) {
				solrQuery.setRows(rowsInt);
			}
		} catch (NumberFormatException e) {
		}
	}

	private void setFacetFieldsForSolrQuery(SolrQuery solrQuery,
			String[] facetFields) {

		List<String> allowedFacetFields = Arrays
				.asList(getAllowedFacetFields());
		List<String> facetFieldList = splitStringArrays(facetFields);

		solrQuery.setFacetSort(FacetParams.FACET_SORT_COUNT);

		for (String fField : facetFieldList) {
			fField = fField.trim();
			if (allowedFacetFields.contains(fField)) {
				solrQuery.addFacetField(fField);
			}
		}

	}

	private void setLimitForSolrQuery(SolrQuery solrQuery, String limit) {
		try {
			int limitInt = Integer.parseInt(limit);
			if (limitInt >= 0) {
				solrQuery.setFacetLimit(limitInt);
			} else {
				solrQuery.setFacetLimit(getDefaultFacetLimit());
			}
		} catch (NumberFormatException e) {
			solrQuery.setFacetLimit(getDefaultFacetLimit());
		}
	}

}
