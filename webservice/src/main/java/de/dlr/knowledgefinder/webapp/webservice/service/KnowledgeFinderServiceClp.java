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
package de.dlr.knowledgefinder.webapp.webservice.service;

import com.liferay.portal.service.InvokableService;

/**
 * 
 * @generated
 */
public class KnowledgeFinderServiceClp implements KnowledgeFinderService {
	private InvokableService _invokableService;
	private String _methodName0;
	private String[] _methodParameterTypes0;
	private String _methodName1;
	private String[] _methodParameterTypes1;
	private String _methodName3;
	private String[] _methodParameterTypes3;
	private String _methodName4;
	private String[] _methodParameterTypes4;
	private String _methodName5;
	private String[] _methodParameterTypes5;
	private String _methodName6;
	private String[] _methodParameterTypes6;
	private String _methodName7;
	private String[] _methodParameterTypes7;
	private String _methodName8;
	private String[] _methodParameterTypes8;

	public KnowledgeFinderServiceClp(InvokableService invokableService) {
		_invokableService = invokableService;

		_methodName0 = "getBeanIdentifier";

		_methodParameterTypes0 = new String[] {};

		_methodName1 = "setBeanIdentifier";

		_methodParameterTypes1 = new String[] { "java.lang.String" };

		_methodName3 = "getDocuments";

		_methodParameterTypes3 = new String[] { "java.lang.String",
				"java.lang.String" };

		_methodName4 = "getDocuments";

		_methodParameterTypes4 = new String[] { "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String" };

		_methodName5 = "getNodes";

		_methodParameterTypes5 = new String[] { "java.lang.String",
				"java.lang.String", "java.lang.String" };

		_methodName6 = "getNodes";

		_methodParameterTypes6 = new String[] { "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String",
				"java.lang.String" };

		_methodName7 = "exportDocuments";

		_methodParameterTypes7 = new String[] { "java.lang.String",
				"java.lang.String", "java.lang.String" };

		_methodName8 = "exportDocuments";

		_methodParameterTypes8 = new String[] { "java.lang.String",
				"java.lang.String", "java.lang.String", "java.lang.String" };
	}

	@Override
	public java.lang.String getBeanIdentifier() {
		Object returnObj = null;

		try {
			returnObj = _invokableService.invokeMethod(_methodName0,
					_methodParameterTypes0, new Object[] {});
		} catch (Throwable t) {
			t = ClpSerializer.translateThrowable(t);

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t.getClass().getName()
						+ " is not a valid exception");
			}
		}

		return (java.lang.String) ClpSerializer.translateOutput(returnObj);
	}

	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		try {
			_invokableService
					.invokeMethod(_methodName1, _methodParameterTypes1,
							new Object[] { ClpSerializer
									.translateInput(beanIdentifier) });
		} catch (Throwable t) {
			t = ClpSerializer.translateThrowable(t);

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t.getClass().getName()
						+ " is not a valid exception");
			}
		}
	}

	@Override
	public java.lang.Object invokeMethod(java.lang.String name,
			java.lang.String[] parameterTypes, java.lang.Object[] arguments)
			throws java.lang.Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.util.Map<java.lang.String, java.lang.Object> getDocuments(
			java.lang.String query, java.lang.String filterQuery)
			throws com.liferay.portal.kernel.exception.PortalException {
		Object returnObj = null;

		try {
			returnObj = _invokableService.invokeMethod(_methodName3,
					_methodParameterTypes3,
					new Object[] { ClpSerializer.translateInput(query),

					ClpSerializer.translateInput(filterQuery) });
		} catch (Throwable t) {
			t = ClpSerializer.translateThrowable(t);

			if (t instanceof com.liferay.portal.kernel.exception.PortalException) {
				throw (com.liferay.portal.kernel.exception.PortalException) t;
			}

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t.getClass().getName()
						+ " is not a valid exception");
			}
		}

		return (java.util.Map<java.lang.String, java.lang.Object>) ClpSerializer
				.translateOutput(returnObj);
	}

	@Override
	public java.util.Map<java.lang.String, java.lang.Object> getDocuments(
			java.lang.String query, java.lang.String filterQuery,
			java.lang.String fields, java.lang.String start,
			java.lang.String rows, java.lang.String sort,
			java.lang.String highlightFields)
			throws com.liferay.portal.kernel.exception.PortalException {
		Object returnObj = null;

		try {
			returnObj = _invokableService.invokeMethod(_methodName4,
					_methodParameterTypes4,
					new Object[] { ClpSerializer.translateInput(query),

					ClpSerializer.translateInput(filterQuery),

					ClpSerializer.translateInput(fields),

					ClpSerializer.translateInput(start),

					ClpSerializer.translateInput(rows),

					ClpSerializer.translateInput(sort),

					ClpSerializer.translateInput(highlightFields) });
		} catch (Throwable t) {
			t = ClpSerializer.translateThrowable(t);

			if (t instanceof com.liferay.portal.kernel.exception.PortalException) {
				throw (com.liferay.portal.kernel.exception.PortalException) t;
			}

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t.getClass().getName()
						+ " is not a valid exception");
			}
		}

		return (java.util.Map<java.lang.String, java.lang.Object>) ClpSerializer
				.translateOutput(returnObj);
	}

	@Override
	public java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinder.webapp.webservice.solr.SimpleCount>> getNodes(
			java.lang.String query, java.lang.String filterQuery,
			java.lang.String groups)
			throws com.liferay.portal.kernel.exception.PortalException {
		Object returnObj = null;

		try {
			returnObj = _invokableService.invokeMethod(_methodName5,
					_methodParameterTypes5,
					new Object[] { ClpSerializer.translateInput(query),

					ClpSerializer.translateInput(filterQuery),

					ClpSerializer.translateInput(groups) });
		} catch (Throwable t) {
			t = ClpSerializer.translateThrowable(t);

			if (t instanceof com.liferay.portal.kernel.exception.PortalException) {
				throw (com.liferay.portal.kernel.exception.PortalException) t;
			}

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t.getClass().getName()
						+ " is not a valid exception");
			}
		}

		return (java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinder.webapp.webservice.solr.SimpleCount>>) ClpSerializer
				.translateOutput(returnObj);
	}

	@Override
	public java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinder.webapp.webservice.solr.SimpleCount>> getNodes(
			java.lang.String query, java.lang.String filterQuery,
			java.lang.String groups, java.lang.String groupsAnyValue,
			java.lang.String limit)
			throws com.liferay.portal.kernel.exception.PortalException {
		Object returnObj = null;

		try {
			returnObj = _invokableService.invokeMethod(_methodName6,
					_methodParameterTypes6,
					new Object[] { ClpSerializer.translateInput(query),

					ClpSerializer.translateInput(filterQuery),

					ClpSerializer.translateInput(groups),

					ClpSerializer.translateInput(groupsAnyValue),

					ClpSerializer.translateInput(limit) });
		} catch (Throwable t) {
			t = ClpSerializer.translateThrowable(t);

			if (t instanceof com.liferay.portal.kernel.exception.PortalException) {
				throw (com.liferay.portal.kernel.exception.PortalException) t;
			}

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t.getClass().getName()
						+ " is not a valid exception");
			}
		}

		return (java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinder.webapp.webservice.solr.SimpleCount>>) ClpSerializer
				.translateOutput(returnObj);
	}

	@Override
	public java.util.Map<java.lang.String, java.lang.Object> exportDocuments(
			java.lang.String query, java.lang.String filterQuery,
			java.lang.String exportType)
			throws com.liferay.portal.kernel.exception.PortalException {
		Object returnObj = null;

		try {
			returnObj = _invokableService.invokeMethod(_methodName7,
					_methodParameterTypes7,
					new Object[] { ClpSerializer.translateInput(query),

					ClpSerializer.translateInput(filterQuery),

					ClpSerializer.translateInput(exportType) });
		} catch (Throwable t) {
			t = ClpSerializer.translateThrowable(t);

			if (t instanceof com.liferay.portal.kernel.exception.PortalException) {
				throw (com.liferay.portal.kernel.exception.PortalException) t;
			}

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t.getClass().getName()
						+ " is not a valid exception");
			}
		}

		return (java.util.Map<java.lang.String, java.lang.Object>) ClpSerializer
				.translateOutput(returnObj);
	}

	@Override
	public java.util.Map<java.lang.String, java.lang.Object> exportDocuments(
			java.lang.String query, java.lang.String filterQuery,
			java.lang.String fields, java.lang.String exportType)
			throws com.liferay.portal.kernel.exception.PortalException {
		Object returnObj = null;

		try {
			returnObj = _invokableService.invokeMethod(_methodName8,
					_methodParameterTypes8,
					new Object[] { ClpSerializer.translateInput(query),

					ClpSerializer.translateInput(filterQuery),

					ClpSerializer.translateInput(fields),

					ClpSerializer.translateInput(exportType) });
		} catch (Throwable t) {
			t = ClpSerializer.translateThrowable(t);

			if (t instanceof com.liferay.portal.kernel.exception.PortalException) {
				throw (com.liferay.portal.kernel.exception.PortalException) t;
			}

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new RuntimeException(t.getClass().getName()
						+ " is not a valid exception");
			}
		}

		return (java.util.Map<java.lang.String, java.lang.Object>) ClpSerializer
				.translateOutput(returnObj);
	}
}
