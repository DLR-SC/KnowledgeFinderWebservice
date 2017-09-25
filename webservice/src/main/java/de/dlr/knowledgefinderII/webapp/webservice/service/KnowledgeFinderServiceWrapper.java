package de.dlr.knowledgefinderII.webapp.webservice.service;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link KnowledgeFinderService}.
 *
 * @author Efrain Lima Miranda, efrain.limamiranda@dlr.de
 * @see KnowledgeFinderService
 * @generated
 */
public class KnowledgeFinderServiceWrapper implements KnowledgeFinderService,
    ServiceWrapper<KnowledgeFinderService> {
    private KnowledgeFinderService _knowledgeFinderService;

    public KnowledgeFinderServiceWrapper(
        KnowledgeFinderService knowledgeFinderService) {
        _knowledgeFinderService = knowledgeFinderService;
    }

    /**
    * Returns the Spring bean ID for this bean.
    *
    * @return the Spring bean ID for this bean
    */
    @Override
    public java.lang.String getBeanIdentifier() {
        return _knowledgeFinderService.getBeanIdentifier();
    }

    /**
    * Sets the Spring bean ID for this bean.
    *
    * @param beanIdentifier the Spring bean ID for this bean
    */
    @Override
    public void setBeanIdentifier(java.lang.String beanIdentifier) {
        _knowledgeFinderService.setBeanIdentifier(beanIdentifier);
    }

    @Override
    public java.lang.Object invokeMethod(java.lang.String name,
        java.lang.String[] parameterTypes, java.lang.Object[] arguments)
        throws java.lang.Throwable {
        return _knowledgeFinderService.invokeMethod(name, parameterTypes,
            arguments);
    }

    @Override
    public java.util.Map<java.lang.String, java.lang.Object> getDocuments(
        java.lang.String query, java.lang.String filterQuery)
        throws com.liferay.portal.kernel.exception.PortalException {
        return _knowledgeFinderService.getDocuments(query, filterQuery);
    }

    @Override
    public java.util.Map<java.lang.String, java.lang.Object> getDocuments(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String fields, java.lang.String start, java.lang.String rows,
        java.lang.String sort, java.lang.String highlightFields)
        throws com.liferay.portal.kernel.exception.PortalException {
        return _knowledgeFinderService.getDocuments(query, filterQuery, fields,
            start, rows, sort, highlightFields);
    }

    @Override
    public java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinderII.webapp.webservice.solr.SimpleCount>> getNodes(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String groups)
        throws com.liferay.portal.kernel.exception.PortalException {
        return _knowledgeFinderService.getNodes(query, filterQuery, groups);
    }

    @Override
    public java.util.Map<java.lang.String, java.util.List<de.dlr.knowledgefinderII.webapp.webservice.solr.SimpleCount>> getNodes(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String groups, java.lang.String groupsAnyValue,
        java.lang.String limit)
        throws com.liferay.portal.kernel.exception.PortalException {
        return _knowledgeFinderService.getNodes(query, filterQuery, groups,
            groupsAnyValue, limit);
    }

    @Override
    public java.util.Map<java.lang.String, java.lang.Object> exportDocuments(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String exportType)
        throws com.liferay.portal.kernel.exception.PortalException {
        return _knowledgeFinderService.exportDocuments(query, filterQuery,
            exportType);
    }

    @Override
    public java.util.Map<java.lang.String, java.lang.Object> exportDocuments(
        java.lang.String query, java.lang.String filterQuery,
        java.lang.String fields, java.lang.String exportType)
        throws com.liferay.portal.kernel.exception.PortalException {
        return _knowledgeFinderService.exportDocuments(query, filterQuery,
            fields, exportType);
    }

    /**
     * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
     */
    public KnowledgeFinderService getWrappedKnowledgeFinderService() {
        return _knowledgeFinderService;
    }

    /**
     * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
     */
    public void setWrappedKnowledgeFinderService(
        KnowledgeFinderService knowledgeFinderService) {
        _knowledgeFinderService = knowledgeFinderService;
    }

    @Override
    public KnowledgeFinderService getWrappedService() {
        return _knowledgeFinderService;
    }

    @Override
    public void setWrappedService(KnowledgeFinderService knowledgeFinderService) {
        _knowledgeFinderService = knowledgeFinderService;
    }
}
