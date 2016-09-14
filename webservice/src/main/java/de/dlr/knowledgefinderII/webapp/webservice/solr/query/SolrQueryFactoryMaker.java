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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.xml.sax.SAXParseException;

import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

/**
 * The Class SolrQueryFactoryMaker.
 */
public class SolrQueryFactoryMaker {

	private static ConcurrentMap<String, SolrQueryFactoryXML> factoryCache = new ConcurrentHashMap<>();

	/**
	 * Creates the solr query factory.
	 *
	 * @param user
	 *            the user
	 * @return the solr query factory
	 */
	public static AbstractSolrQueryFactory createSolrQueryFactory(User user) {
		try {
			SolrQueryFactoryXML anonymous = getOrCreateSolrQueryFactory("Anonymous");
			SolrQueryFactoryComposite factoryUser = new SolrQueryFactoryComposite();
			if (user != null) {

				factoryUser.add(anonymous);
				List<Role> userRoles = user.getRoles();
				for (Role r : userRoles) {
					SolrQueryFactoryXML factory = getOrCreateSolrQueryFactory(r
							.getName());
					if (factory.isValid()) {
						factoryUser.add(factory);
						anonymous = factory;
					}
				}
			}
			if (factoryUser.getNumberOfRelatedFactories() > 1) {
				return factoryUser;
			} else {
				return anonymous;
			}

		} catch (SAXParseException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getPublicId());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return new AbstractSolrQueryFactory() {
		};
	}

	private static synchronized SolrQueryFactoryXML getOrCreateSolrQueryFactory(
			String factoryType) throws Exception {
		if (factoryCache.containsKey(factoryType)) {
			return factoryCache.get(factoryType);
		} else {
			SolrQueryFactoryXML factory = new SolrQueryFactoryXML(factoryType,
					"config.xml");

			factoryCache.put(factoryType, factory);
			return factory;
		}
	}
}
