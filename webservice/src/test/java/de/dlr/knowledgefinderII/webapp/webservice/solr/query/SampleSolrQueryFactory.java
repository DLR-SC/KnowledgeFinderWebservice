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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;

public class SampleSolrQueryFactory extends AbstractSolrQueryFactory {

	@Override
	protected String getAppendQuery() {
		return "*:*";
	}
	
	@Override
	protected String[] getAllowedFields() {
		return new String[] { "id", "title" };
	}

	@Override
	protected String[] getAllowedFacetFields() {
		return new String[] { "id", "author" };
	}
	
	protected String getDefaultQuery() {
		return "*:*";
	}
	
	@Override
	protected String[] getDefaultFields() {
		return new String[]{"id"};
	}
	
	@Override
	protected String[] getDefaultHLFields() {
		return new String[]{"*:*"};
	}

	protected String[] getDefaultFilterQueries() {
		return new String[]{};
	}
	

	@Override
	protected int getDefaultFacetLimit() {
		return 2;
	}
	
	@Override
	protected SortClause getDefaultSort() {
		return new SortClause("id", SolrQuery.ORDER.asc);
	}
}
