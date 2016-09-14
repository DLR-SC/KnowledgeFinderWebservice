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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractSolrQueryFactoryTest {

	private AbstractSolrQueryFactory factory;
	private SolrQuery solrQuery;

	private String query;
	private String[] filterQuery;
	private String[] fields;
	private String[] facetFields;
	private String start;
	private String rows;
	private String[] sort;
	private String[] highlightFields;
	private String limit;

	private Map<String, Object> expectedContext = new HashMap<>();

	@Before
	public void setup() {
		factory = new SampleSolrQueryFactory();
		solrQuery = new SolrQuery();

		query = "id";
		filterQuery = new String[] { "popularity:[10 TO *]" };
		fields = new String[] { "title" };
		facetFields = new String[] { "id" };
		start = "1";
		rows = "10";
		sort = new String[] { "title desc" };
		highlightFields = new String[] { "title" };
		limit = "9";

		expectedContext.put("query", "*:* AND id");
		expectedContext.put("filterQuery",
				new String[] { "popularity:[10 TO *]" });
		expectedContext.put("fields", "title");
		expectedContext.put("facetFields", new String[] { "id" });
		expectedContext.put("start", 1);
		expectedContext.put("rows", 10);
		expectedContext.put("sort",
				Arrays.asList(new SortClause[] { new SortClause("title",
						ORDER.desc) }));
		expectedContext.put("highlightFields", new String[] { "title" });
		expectedContext.put("limit", 9);
	}

	@After
	public void matchSolrQueryWithExpectetContext() {

		assertThat(solrQuery.getQuery(),
				is((String) expectedContext.get("query")));
		assertThat(solrQuery.getFilterQueries(),
				is((String[]) expectedContext.get("filterQuery")));
		assertThat(solrQuery.getFields(),
				is(expectedContext.get("fields")));
		assertThat(solrQuery.getFacetFields(),
				is((String[]) expectedContext.get("facetFields")));
		assertThat(solrQuery.getStart(), is( expectedContext.get("start")));
		assertThat(solrQuery.getRows(), is( expectedContext.get("rows")));
		assertThat(solrQuery.getSorts(), is(expectedContext.get("sort")));
		assertThat(solrQuery.getHighlightFields(),
				is((String[]) expectedContext.get("highlightFields")));
		assertThat(solrQuery.getFacetLimit(),
				is( expectedContext.get("limit")));
	}

	@Test
	public void testCreateSolrQueryWithSimpleStrings() {
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}

	@Test
	public void testCreateSolrQueryWithNullQuery() {
		query = null;
		expectedContext.put("query", "*:*");

		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}

	@Test
	public void testCreateSolrQueryWithEmptyQuery() {
		query = "";
		expectedContext.put("query", "*:*");

		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}

	@Test
	public void testCreateSolrQueryWithNullFilterQuery() {
		filterQuery = null;
		expectedContext.put("filterQuery", null);

		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}

	@Test
	public void testCreateSolrQueryWithEmptyFilterQuery() {
		filterQuery = new String[0];
		expectedContext.put("filterQuery", null);

		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNullFields(){
		fields = null;
		expectedContext.put("fields", "id");
		expectedContext.put("highlightFields", solrQuery.getHighlightFields());
		
		solrQuery =  factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithEmptyFields(){
		fields = new String[0];
		expectedContext.put("fields", "id");
		expectedContext.put("highlightFields", solrQuery.getHighlightFields());
		
		solrQuery =  factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNullFacetFields() {
		facetFields = null;
		expectedContext.put("facetFields", null);
		expectedContext.put("limit", solrQuery.getFacetLimit());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithEmptyFacetFields() {
		facetFields = new String[0];
		expectedContext.put("facetFields", null);
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNullStart() {
		start = null;
		expectedContext.put("start", solrQuery.getStart());

		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}

	@Test
	public void testCreateSolrQueryWithEmptyStart() {
		start = "";
		expectedContext.put("start", solrQuery.getStart());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNonIntegerStart() {
		start = "NAN";
		expectedContext.put("start", solrQuery.getRows());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNullRows() {
		rows = null;
		expectedContext.put("rows", solrQuery.getRows());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithEmptyRows() {
		rows = "";
		expectedContext.put("rows", solrQuery.getRows());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNonIntegerRows() {
		rows = "NAN";
		expectedContext.put("rows", solrQuery.getRows());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNullSort() {
		sort = null;
		expectedContext.put("sort", Arrays.asList(new SortClause[]{new SortClause("id",ORDER.asc)}));
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithEmptySort() {
		sort = new String[0];
		expectedContext.put("sort", Arrays.asList(new SortClause[]{new SortClause("id",ORDER.asc)}));
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNullHighlightFields() {
		highlightFields = null;
		expectedContext.put("highlightFields", null);
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithEmptyHighlightFields() {
		highlightFields = new String[0];
		expectedContext.put("highlightFields", null);
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNullLimit() {
		limit = null;
		expectedContext.put("limit", factory.getDefaultFacetLimit());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithEmptyLimit() {
		limit="";
		expectedContext.put("limit", factory.getDefaultFacetLimit());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithNonIntegerLimit() {
		limit = "NAN";
		expectedContext.put("limit", factory.getDefaultFacetLimit());
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementFilterQuery(){
		filterQuery = new String[]{" id ","author "};
		expectedContext.put("filterQuery", new String[]{"id","author"});
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementFilterQueryWithNullAndEmpty(){
		filterQuery = new String[]{" id ",null, "","author "};
		expectedContext.put("filterQuery", new String[]{"id","author"});
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementFields(){
		fields = new String[]{" id ","title "};
		expectedContext.put("fields","id,title");
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementFieldsWithNullAndEmpty(){
		fields = new String[]{" id ",null, "", "title "};
		expectedContext.put("fields", "id,title");
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithThreeElementFieldsWithNonAllowed(){
		fields = new String[]{" id ","date", "title "};
		expectedContext.put("fields", "id,title");
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementFacetFields(){
		facetFields = new String[]{" id ","author "};
		expectedContext.put("facetFields", new String[]{"id","author"});
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementFacetFieldsWithNullAndEmpty(){
		facetFields = new String[]{" id ",null, "","author "};
		expectedContext.put("facetFields", new String[]{"id","author"});
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithThreeElementFacetFieldsWithNonAllowed(){
		facetFields = new String[]{" id ","date","author "};
		expectedContext.put("facetFields", new String[]{"id","author"});
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementSort(){
		sort = new String[]{"id desc", "title asc"};
		expectedContext.put("sort", Arrays.asList(new SortClause[]{new SortClause("id", ORDER.desc), new SortClause("title", ORDER.asc)}));
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementSortWithNullAndEmpty(){
		sort = new String[]{"id desc", null, "", "title asc"};
		expectedContext.put("sort", Arrays.asList(new SortClause[]{new SortClause("id", ORDER.desc), new SortClause("title", ORDER.asc)}));
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementHighLightFields(){
		highlightFields = new String[]{" id ", "title "};
		expectedContext.put("highlightFields", new String[]{"title"});
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	@Test
	public void testCreateSolrQueryWithTwoElementHighLightFieldsWithNullAndEmpty(){
		highlightFields = new String[]{" id ", null, "", "title "};
		expectedContext.put("highlightFields", new String[]{"title"});
		
		solrQuery = factory.createQuery(query, filterQuery, fields,
				facetFields, start, rows, sort, highlightFields, limit);
	}
	
	
}
