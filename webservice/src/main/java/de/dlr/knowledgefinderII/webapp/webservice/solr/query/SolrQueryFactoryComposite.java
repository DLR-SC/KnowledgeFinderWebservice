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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery.SortClause;


/**
 * The Class SolrQueryFactoryComposite. Following the Composite-Pattern,
 * combine multiple solrqueryfactories ({@link SolrQueryFactoryMacker}) 
 */
public class SolrQueryFactoryComposite extends AbstractSolrQueryFactory {

	private List<AbstractSolrQueryFactory> factories = new ArrayList<AbstractSolrQueryFactory>();

	/**
	 * Add a query factory.
	 *
	 * @param factory the factory
	 */
	public void add(AbstractSolrQueryFactory factory) {
		factories.add(factory);
	}

	/**
	 * Remove a query factory.
	 *
	 * @param factory the factory
	 */
	public void remove(AbstractSolrQueryFactory factory) {
		factories.remove(factory);
	}
	
	public int getNumberOfRelatedFactories(){
		return factories.size();
	}

	@Override
	protected String getAppendQuery() {
		String query = "";
		for(AbstractSolrQueryFactory fac : factories){
			String facQuery = fac.getAppendQuery();
			if(facQuery != null && facQuery != ""){
				if(query != "" && !query.equals(facQuery))
					query = query + " OR " + facQuery;
				else 
					query = facQuery;
			}
		}
		if(query.contains(" OR "))
			query = "(" + query + ")";
		
		return query;
	}

	@Override
	protected String[] getAllowedFields() {
		return mergeStringArrays("ALLOWED_FIELDS");
	}
	
	@Override
	protected String[] getAllowedFacetFields() {
		return mergeStringArrays("ALLOWED_FACET_FIELDS");
	}
	
	@Override
	protected String getDefaultQuery(){
		String query = DEFAULT_QUERY; 
		for(AbstractSolrQueryFactory fac : factories){
			String facQuery = fac.getDefaultQuery();
			// if not empty use option that was later added --> the order of adding SolrQueryFactories is important
			if(facQuery != null && !facQuery.isEmpty())
				query = facQuery;
		}
		return query;
	}

	@Override
	protected String[] getDefaultFields(){
		return mergeStringArrays("DEFAULT_FIELDS");
	}

	@Override
	protected String[] getDefaultHLFields(){
		return mergeStringArrays("DEFAULT_HL_FIELDS");
	}

	@Override
	protected String[] getDefaultFilterQueries(){
		return mergeStringArrays("DEFAULT_FQ");
	}

	@Override
	protected int getDefaultFacetLimit(){
		int facetLimit = DEFAULT_FACET_LIMIT; 
		for(AbstractSolrQueryFactory fac : factories){
			int facFacetLimit = fac.getDefaultFacetLimit();
			// if not empty use option that was later added --> the order of adding SolrQueryFactories is important
			if(facFacetLimit != 0)
				facetLimit = facFacetLimit;
		}
		return facetLimit;
	}

	@Override
	protected SortClause getDefaultSort(){
		SortClause sort = new SortClause(DEFAULT_SORT_FIELD, DEFAULT_SORT_ORDER);
		for(AbstractSolrQueryFactory fac : factories){
			SortClause facSort = fac.getDefaultSort();
			// if not empty use option that was later added --> the order of adding SolrQueryFactories is important
			if(facSort != null && !facSort.getItem().isEmpty())
				sort = facSort;
		}
		return sort;
	}
	
	protected String[] mergeStringArrays(String field){
		Set<String> fields = new HashSet<String>();
		for(AbstractSolrQueryFactory fac : factories)
			Collections.addAll(fields, getParameter(field, fac));
		return fields.toArray(new String[0]);
	}
	
	protected String[] getParameter(String field, AbstractSolrQueryFactory factory){
		if(field.equals("ALLOWED_FIELDS"))
			return factory.getAllowedFields();
		if(field.equals("ALLOWED_FACET_FIELDS"))
			return factory.getAllowedFacetFields();
		if(field.equals("DEFAULT_FIELDS"))
			return factory.getDefaultFields();
		if(field.equals("DEFAULT_HL_FIELDS"))
			return factory.getDefaultHLFields();
		if(field.equals("DEFAULT_FQ"))
			return factory.getDefaultFilterQueries();
		
		System.out.println("The input does not match any of the accepted values.");
		System.out.println("\t input: "+ field);
		throw new IllegalArgumentException("input: "+ field);
	}
}
