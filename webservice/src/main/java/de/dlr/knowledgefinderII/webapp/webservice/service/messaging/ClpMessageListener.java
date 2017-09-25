package de.dlr.knowledgefinderII.webapp.webservice.service.messaging;

import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;

import de.dlr.knowledgefinderII.webapp.webservice.service.ClpSerializer;
import de.dlr.knowledgefinderII.webapp.webservice.service.KnowledgeFinderServiceUtil;


public class ClpMessageListener extends BaseMessageListener {
    public static String getServletContextName() {
        return ClpSerializer.getServletContextName();
    }

    @Override
    protected void doReceive(Message message) throws Exception {
        String command = message.getString("command");
        String servletContextName = message.getString("servletContextName");

        if (command.equals("undeploy") &&
                servletContextName.equals(getServletContextName())) {
            KnowledgeFinderServiceUtil.clearService();
        }
    }
}
