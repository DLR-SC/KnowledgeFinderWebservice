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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.security.ac.AccessControlled;
import com.liferay.portal.service.BaseService;
import com.liferay.portal.service.InvokableService;

/**
 * Provides the remote service interface for KnowledgeFinder. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * 
 * @see KnowledgeFinderServiceUtil
 * @see de.dlr.knowledgefinder.webapp.webservice.service.base.KnowledgeFinderServiceBaseImpl
 * @see de.dlr.knowledgefinder.webapp.webservice.service.impl.KnowledgeFinderServiceImpl
 * @generated
 */
@AccessControlled
@JSONWebService
@Transactional(isolation = Isolation.PORTAL, rollbackFor =  {
    PortalException.class, SystemException.class}
)
public interface KnowledgeFinderService extends BaseService, InvokableService {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify or reference this interface directly. Always use {@link KnowledgeFinderServiceUtil} to access the KnowledgeFinder Webservice remote service. Add custom service methods to {@link de.dlr.xps.server.knowledgefinder.webservice.service.impl.KnowledgeFinderServiceImpl} and rerun ServiceBuilder to automatically copy the method declarations to this interface.
     */

    /**
    * Returns the Spring bean ID for this bean.
    *
    * @return the Spring bean ID for this bean
    */
    public java.lang.String getBeanIdentifier();

    /**
    * Sets the Spring bean ID for this bean.
    *
    * @param beanIdentifier the Spring bean ID for this bean
    */
    public void setBeanIdentifier(java.lang.String beanIdentifier);

    @Override
    public java.lang.Object invokeMethod(java.lang.String name,
        java.lang.String[] parameterTypes, java.lang.Object[] arguments)
        throws java.lang.Throwable;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public java.util.Map<java.lang.String, java.lang.Object> getDocuments(
        java.lang.String query, java.lang.String filterQuery)
        throws com.liferay.portal.kernel.exception.PortalException;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public java.util.Map<java.lang.String, java.lang.Object> getDocuments(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String fields, java.lang.String start, java.lang.String rows,
        java.lang.String sort, java.lang.String highlightFields)
        throws com.liferay.portal.kernel.exception.PortalException;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinder.webapp.webservice.solr.SimpleCount>> getNodes(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String groups)
        throws com.liferay.portal.kernel.exception.PortalException;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinder.webapp.webservice.solr.SimpleCount>> getNodes(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String groups, java.lang.String groupsAnyValue,
        java.lang.String limit)
        throws com.liferay.portal.kernel.exception.PortalException;
    
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public java.util.Map<java.lang.String, java.lang.Object> exportDocuments(
        java.lang.String query, java.lang.String filterQuery, java.lang.String exportType)
        throws com.liferay.portal.kernel.exception.PortalException;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public java.util.Map<java.lang.String, java.lang.Object> exportDocuments(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String fields, java.lang.String exportType)
        throws com.liferay.portal.kernel.exception.PortalException;
}
