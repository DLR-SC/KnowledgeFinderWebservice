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
package de.dlr.knowledgefinder.webapp.webservice.solr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField.Count;

import com.github.slugify.Slugify;

public class SimpleCount implements Serializable {

	private static final long serialVersionUID = 488892642787249685L;

	private long _count = 0;
	private String _name = null;
	private String _query = null;

	
	public static List<SimpleCount> transform(List<Count> fromList) {
		if (fromList == null)
			return null;
		
		List<SimpleCount> restList = new ArrayList<SimpleCount>();
		for(Count count : fromList)
			restList.add(new SimpleCount(count));
		
		return restList;
	}
	
	public SimpleCount(String n, String query, long c) {
		_name = n;
		_count = c;
		_query = query;
	}

	public SimpleCount(Count c) {
		_name = c.getName();
		_query = c.getAsFilterQuery();
		_count = c.getCount();
	}

	public String getId(){
		// case sensitivity:
		// https://github.com/slugify/slugify
		Slugify slg = new Slugify(false);
		return slg.slugify(this.getName());
	}
	
	public String getName() {
		return _name;
	}

	public void setName(String n) {
		_name = n;
	}

	public long getCount() {
		return _count;
	}

	public void setCount(long c) {
		_count = c;
	}

	public String getQuery() {
		return _query;
	}

	@Override
	public String toString() {
		return _name + " (" + _count + ")";
	}

}