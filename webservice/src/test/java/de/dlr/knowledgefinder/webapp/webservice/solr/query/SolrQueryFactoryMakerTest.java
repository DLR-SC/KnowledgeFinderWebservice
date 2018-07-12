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
package de.dlr.knowledgefinder.webapp.webservice.solr.query;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

@RunWith(PowerMockRunner.class)
public class SolrQueryFactoryMakerTest {

	User testUser;
	Role someUnknownRole;
	Role powerUser;

	SolrQueryFactoryXML powerUserFac;
	SolrQueryFactoryXML anonymousFac;
	SolrQueryFactoryXML invalidFac;

	@Before
	public void setup() {
		testUser = createMock("testUser", User.class);
		someUnknownRole = createMock("anonymous", Role.class);
		powerUser = createMock("powerUser", Role.class);

		powerUserFac = createMock("validFac", SolrQueryFactoryXML.class);
		anonymousFac = createMock("validAnonymousFac", SolrQueryFactoryXML.class);
		invalidFac = createMock("invalidFac", SolrQueryFactoryXML.class);

		expect(someUnknownRole.getName()).andStubReturn("some string");
		expect(powerUser.getName()).andStubReturn("Power user");
		expect(powerUserFac.isValid()).andStubReturn(true);
		expect(anonymousFac.isValid()).andStubReturn(true);
		expect(invalidFac.isValid()).andStubReturn(false);

		PowerMock.mockStaticPartial(SolrQueryFactoryMaker.class, "getOrCreateSolrQueryFactory");

		replay(someUnknownRole);
		replay(powerUser);
		replay(powerUserFac);
		replay(invalidFac);

		try {
			PowerMock.expectPrivate(SolrQueryFactoryMaker.class, "getOrCreateSolrQueryFactory", "Power user")
					.andStubReturn(powerUserFac);
			PowerMock.expectPrivate(SolrQueryFactoryMaker.class, "getOrCreateSolrQueryFactory", "Anonymous")
					.andStubReturn(anonymousFac);
			PowerMock.expectPrivate(SolrQueryFactoryMaker.class, "getOrCreateSolrQueryFactory", "some string")
					.andStubReturn(invalidFac);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PowerMock.replay(SolrQueryFactoryMaker.class);
	}

	@After
	public void ending() {
		verify(testUser);
	}

	@Test
	@PrepareForTest(SolrQueryFactoryMaker.class)
	public void createSolrQueryFactory_WithPowerUser() throws SystemException {
		expect(testUser.getRoles()).andReturn(Arrays.asList(new Role[] { powerUser, someUnknownRole }));
		replay(testUser);

		AbstractSolrQueryFactory factory = SolrQueryFactoryMaker.createSolrQueryFactory(testUser);

		assertTrue("If a role with valid configuration is given, the returned Object should be an instance of"
				+ " 'SolrQueryFactoryComposite'", factory instanceof SolrQueryFactoryComposite);
		verify(powerUser);
	}

	@Test
	@PrepareForTest(SolrQueryFactoryMaker.class)
	public void createSolrQueryFactory_WithoutPowerUser() throws SystemException {
		expect(testUser.getRoles()).andReturn(Arrays.asList(new Role[] { someUnknownRole }));

		replay(testUser);

		AbstractSolrQueryFactory factory = SolrQueryFactoryMaker.createSolrQueryFactory(testUser);

		assertTrue(
				"If no valid role is given, the returned Object should be the the initialy created anonymouse factory",
				factory == anonymousFac);
		verify(someUnknownRole);
	}

	@Test
	@PrepareForTest(SolrQueryFactoryMaker.class)
	public void createSolrQueryFactory_EmptyList() throws SystemException {
		replay(testUser);

		AbstractSolrQueryFactory factory = SolrQueryFactoryMaker.createSolrQueryFactory(null);

		assertTrue("If no roles are given, the returned Object should be the the initialy created anonymouse factory",
				factory == anonymousFac);
	}
}
