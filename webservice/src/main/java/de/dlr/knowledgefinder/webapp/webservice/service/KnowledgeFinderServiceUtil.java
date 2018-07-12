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

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.InvokableService;

/**
 * Provides the remote service utility for KnowledgeFinder. This utility wraps
 * {@link de.dlr.knowledgefinder.webapp.webservice.service.impl.KnowledgeFinderServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on a remote server. Methods of this service are expected to have security
 * checks based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * 
 * @see KnowledgeFinderService
 * @see de.dlr.knowledgefinder.webapp.webservice.service.base.KnowledgeFinderServiceBaseImpl
 * @see de.dlr.knowledgefinder.webapp.webservice.service.impl.KnowledgeFinderServiceImpl
 * @generated
 */
public class KnowledgeFinderServiceUtil {
    private static KnowledgeFinderService _service;

    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify this class directly. Add custom service methods to {@link de.dlr.xps.server.knowledgefinder.webservice.service.impl.KnowledgeFinderServiceImpl} and rerun ServiceBuilder to regenerate this class.
     */

    /**
    * Returns the Spring bean ID for this bean.
    *
    * @return the Spring bean ID for this bean
    */
    public static java.lang.String getBeanIdentifier() {
        return getService().getBeanIdentifier();
    }

    /**
    * Sets the Spring bean ID for this bean.
    *
    * @param beanIdentifier the Spring bean ID for this bean
    */
    public static void setBeanIdentifier(java.lang.String beanIdentifier) {
        getService().setBeanIdentifier(beanIdentifier);
    }

    public static java.lang.Object invokeMethod(java.lang.String name,
        java.lang.String[] parameterTypes, java.lang.Object[] arguments)
        throws java.lang.Throwable {
        return getService().invokeMethod(name, parameterTypes, arguments);
    }

    public static java.util.Map<java.lang.String, java.lang.Object> getDocuments(
        java.lang.String query, java.lang.String filterQuery)
        throws com.liferay.portal.kernel.exception.PortalException {
        return getService().getDocuments(query, filterQuery);
    }

    public static java.util.Map<java.lang.String, java.lang.Object> getDocuments(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String fields, java.lang.String start, java.lang.String rows,
        java.lang.String sort, java.lang.String highlightFields)
        throws com.liferay.portal.kernel.exception.PortalException {
        return getService()
                   .getDocuments(query, filterQuery, fields, start, rows, sort,
            highlightFields);
    }

    public static java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinder.webapp.webservice.solr.SimpleCount>> getNodes(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String groups)
        throws com.liferay.portal.kernel.exception.PortalException {
        return getService().getNodes(query, filterQuery, groups);
    }

    public static java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinder.webapp.webservice.solr.SimpleCount>> getNodes(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String groups, java.lang.String groupsAnyValue,
        java.lang.String limit)
        throws com.liferay.portal.kernel.exception.PortalException {
        return getService()
                   .getNodes(query, filterQuery, groups, groupsAnyValue, limit);
    }
    
    public static java.util.Map<java.lang.String, java.lang.Object> exportDocuments(
            java.lang.String query, java.lang.String filterQuery, java.lang.String exportType)
            throws com.liferay.portal.kernel.exception.PortalException {
            return getService().exportDocuments(query, filterQuery, exportType);
        }

    public static java.util.Map<java.lang.String, java.lang.Object> exportDocuments(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String fields, java.lang.String exportType)
        throws com.liferay.portal.kernel.exception.PortalException {
        return getService()
                   .exportDocuments(query, filterQuery, fields, exportType);
    }

    public static void clearService() {
        _service = null;
    }

    public static KnowledgeFinderService getService() {
        if (_service == null) {
            InvokableService invokableService = (InvokableService) PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
                    KnowledgeFinderService.class.getName());

            if (invokableService instanceof KnowledgeFinderService) {
                _service = (KnowledgeFinderService) invokableService;
            } else {
                _service = new KnowledgeFinderServiceClp(invokableService);
            }

            ReferenceRegistry.registerReference(KnowledgeFinderServiceUtil.class,
                "_service");
        }

        return _service;
    }

    /**
     * @deprecated As of 6.2.0
     */
    public void setService(KnowledgeFinderService service) {
    }
}
