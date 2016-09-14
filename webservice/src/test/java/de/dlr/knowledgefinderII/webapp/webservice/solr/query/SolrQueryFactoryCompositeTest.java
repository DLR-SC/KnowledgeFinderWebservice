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

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolrQueryFactoryCompositeTest {

	SolrQueryFactoryComposite compFac;
	AbstractSolrQueryFactory factory1;
	AbstractSolrQueryFactory factory2;

	@Before
	public void setup() {
		compFac = new SolrQueryFactoryComposite();
		factory1 = createMock("Factory1", SolrQueryFactoryXML.class);
		factory2 = createMock("Factory2", SolrQueryFactoryXML.class);

	}

	@After
	public void checkMocks() {
		verify(factory1);
		verify(factory2);
	}

	private void setReplayAndAddBothMocks() {
		replayMocks();
		addMocksToCompFactory();
	}

	private void replayMocks() {
		replay(factory1);
		replay(factory2);
	}

	private void addMocksToCompFactory() {
		compFac.add(factory1);
		compFac.add(factory2);
	}

	@Test
	public void testGetAppendQuery() {

		expect(factory1.getAppendQuery()).andReturn("this is query1");
		expect(factory2.getAppendQuery()).andReturn("this is query2");
		setReplayAndAddBothMocks();

		String output = compFac.getAppendQuery();

		assertThat("The querys of all classes should be connected by"
				+ " ' OR ' and surrounded by parentheses", output,
				is("(this is query1 OR this is query2)"));
	}

	@Test
	public void testGetAppendQueryWithEmptyString() {
		expect(factory1.getAppendQuery()).andReturn("");
		expect(factory2.getAppendQuery()).andReturn("this is query2");
		setReplayAndAddBothMocks();

		String output = compFac.getAppendQuery();

		assertThat("If just one query is available the single query"
				+ " surrounded by parentheses should be returned.", output,
				is("this is query2"));
	}

	@Test
	public void testGetAppendQueryWithoutFactorys() {
		replayMocks();
		
		String output = compFac.getAppendQuery();

		assertThat("If no factories are available just a pair of"
				+ " parantheses should be returned", output, is(""));
	}

	@Test
	public void testGetAllowedFields() {
		expect(factory1.getAllowedFields()).andReturn(
				new String[] { "id", "title" });
		expect(factory2.getAllowedFields()).andReturn(
				new String[] { "id", "year", "author" });
		setReplayAndAddBothMocks();

		String[] output = compFac.getAllowedFields();

		List<String> l1 = Arrays.asList(output);
		assertThat(
				"The returned array should be a jointed array of the single factories.",
				l1, hasItems("id", "title", "year", "author"));
	}

	@Test
	public void testGetAllowedFieldsWithEmptyArray() {
		expect(factory1.getAllowedFields()).andReturn(new String[0]);
		expect(factory2.getAllowedFields()).andReturn(
				new String[] { "id", "year", "author" });
		setReplayAndAddBothMocks();

		String[] output = compFac.getAllowedFields();

		List<String> l1 = Arrays.asList(output);
		assertThat(
				"The returned array should be a jointed array of the single factories.",
				l1, hasItems("id", "author", "year"));
	}

	@Test
	public void testGetAllowedFacetFields() {
		expect(factory1.getAllowedFacetFields()).andReturn(
				new String[] { "id", "title" });
		expect(factory2.getAllowedFacetFields()).andReturn(
				new String[] { "id", "year", "author" });
		setReplayAndAddBothMocks();

		String[] output = compFac.getAllowedFacetFields();

		List<String> l1 = Arrays.asList(output);
		assertThat(
				"The returned array should be a jointed array of the single factories.",
				l1, hasItems("id", "title", "year", "author"));
	}

	@Test
	public void testGetAllowedFacetFieldsWithEmptyArray() {
		expect(factory1.getAllowedFacetFields()).andReturn(new String[0]);
		expect(factory2.getAllowedFacetFields()).andReturn(
				new String[] { "id", "year", "author" });
		setReplayAndAddBothMocks();

		String[] output = compFac.getAllowedFacetFields();

		List<String> l1 = Arrays.asList(output);
		assertThat(
				"The returned array should be a jointed array of the single factories.",
				l1, hasItems("id", "author", "year"));
	}

	@Test
	public void testGetDefaultQuery() {
		expect(factory1.getDefaultQuery()).andReturn("id:title");
		expect(factory2.getDefaultQuery()).andReturn("title:id");
		setReplayAndAddBothMocks();
		
		String output = compFac.getDefaultQuery();
		
		assertThat(output, is("title:id"));
	}
	
	@Test
	public void testGetDefaultQueryWithEmptylQuery() {
		expect(factory1.getDefaultQuery()).andReturn("");
		expect(factory2.getDefaultQuery()).andReturn("title:id");
		setReplayAndAddBothMocks();
		
		String output = compFac.getDefaultQuery();
		
		assertThat(output, is("title:id"));
	}
	
	@Test
	public void testGetDefaultFields() {
		expect(factory1.getDefaultFields()).andReturn(new String[]{"id","title"});
		expect(factory2.getDefaultFields()).andReturn(new String[]{"id","author"});
		setReplayAndAddBothMocks();
		
		String[] output = compFac.getDefaultFields();
		
		List<String> outputList = Arrays.asList(output);
		
		assertThat(outputList, hasItems("id", "title", "author"));
	}
	
	@Test
	public void testGetDefaultFieldsWithEmptyFields() {
		expect(factory1.getDefaultFields()).andReturn(new String[0]);
		expect(factory2.getDefaultFields()).andReturn(new String[]{"id","author"});
		setReplayAndAddBothMocks();
		
		String[] output = compFac.getDefaultFields();
		
		List<String> outputList = Arrays.asList(output);
		
		assertThat(outputList, hasItems("id", "author"));
	}
	
	@Test
	public void testGetDefaultHLFields() {
		expect(factory1.getDefaultHLFields()).andReturn(new String[]{"id","title"});
		expect(factory2.getDefaultHLFields()).andReturn(new String[]{"id","author"});
		setReplayAndAddBothMocks();
		
		String[] output = compFac.getDefaultHLFields();
		
		List<String> outputList = Arrays.asList(output);
		
		assertThat(outputList, hasItems("id", "title", "author"));
	}
	
	@Test
	public void testGetDefaultHLFieldsWithEmptyHLFields() {
		expect(factory1.getDefaultHLFields()).andReturn(new String[0]);
		expect(factory2.getDefaultHLFields()).andReturn(new String[]{"id","author"});
		setReplayAndAddBothMocks();
		
		String[] output = compFac.getDefaultHLFields();
		
		List<String> outputList = Arrays.asList(output);
		
		assertThat(outputList, hasItems("id",  "author"));
	}
	
	@Test
	public void testGetDefaultFilterQueries() {
		expect(factory1.getDefaultFilterQueries()).andReturn(new String[]{"id","title"});
		expect(factory2.getDefaultFilterQueries()).andReturn(new String[]{"id","author"});
		setReplayAndAddBothMocks();
		
		String[] output = compFac.getDefaultFilterQueries();
		
		List<String> outputList = Arrays.asList(output);
		
		assertThat(outputList, hasItems("id", "title", "author"));
	}
	
	@Test
	public void testGetDefaultFilterQueriesWithEmptyFilterQueries() {
		expect(factory1.getDefaultFilterQueries()).andReturn(new String[0]);
		expect(factory2.getDefaultFilterQueries()).andReturn(new String[]{"id","author"});
		setReplayAndAddBothMocks();
		
		String[] output = compFac.getDefaultFilterQueries();
		
		List<String> outputList = Arrays.asList(output);
		
		assertThat(outputList, hasItems("id",  "author"));
	}
	
	@Test
	public void testGetDefaultFacetLimit() {
		expect(factory1.getDefaultFacetLimit()).andReturn(1);
		expect(factory2.getDefaultFacetLimit()).andReturn(3);
		setReplayAndAddBothMocks();
		
		int output = compFac.getDefaultFacetLimit();
		
		assertThat(output, is(3));
	}
	
	@Test
	public void testGetDefaultFacetLimitWith0Value() {
		expect(factory1.getDefaultFacetLimit()).andReturn(1);
		expect(factory2.getDefaultFacetLimit()).andReturn(0);
		setReplayAndAddBothMocks();
		
		int output = compFac.getDefaultFacetLimit();
		
		assertThat(output, is(1));
	}
	
	@Test
	public void testGetDefaultSort(){
		expect(factory1.getDefaultSort()).andReturn(new SortClause("id", ORDER.asc));
		expect(factory2.getDefaultSort()).andReturn(new SortClause("title", ORDER.desc));
		setReplayAndAddBothMocks();
		
		SortClause output = compFac.getDefaultSort();
		
		assertThat(output, is(new SortClause("title", ORDER.desc)));
	}
	
	@Test
	public void testGetDefaultSortWithFirstFactoryNull(){
		expect(factory1.getDefaultSort()).andReturn(null);
		expect(factory2.getDefaultSort()).andReturn(new SortClause("title", ORDER.desc));
		setReplayAndAddBothMocks();
		
		SortClause output = compFac.getDefaultSort();
		
		assertThat(output, is(new SortClause("title", ORDER.desc)));
	}
	
	@Test
	public void testGetDefaultSortWithSecondFactoryNull(){
		expect(factory1.getDefaultSort()).andReturn(new SortClause("title", ORDER.desc));
		expect(factory2.getDefaultSort()).andReturn(null);
		setReplayAndAddBothMocks();
		
		SortClause output = compFac.getDefaultSort();
		
		assertThat(output, is(new SortClause("title", ORDER.desc)));
	}
	
	@Test
	public void testGetDefaultSortWithEmptyField(){
		expect(factory1.getDefaultSort()).andReturn(new SortClause("id", ORDER.asc));
		expect(factory2.getDefaultSort()).andReturn(new SortClause("", ORDER.desc));
		setReplayAndAddBothMocks();
		
		SortClause output = compFac.getDefaultSort();
		
		assertThat(output, is(new SortClause("id", ORDER.asc)));
	}

}
