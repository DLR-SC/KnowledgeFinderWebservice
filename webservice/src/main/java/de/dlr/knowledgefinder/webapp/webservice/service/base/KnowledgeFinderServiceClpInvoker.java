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
package de.dlr.knowledgefinder.webapp.webservice.service.base;

import de.dlr.knowledgefinder.webapp.webservice.service.KnowledgeFinderServiceUtil;

import java.util.Arrays;

/**
 * 
 * @generated
 */
public class KnowledgeFinderServiceClpInvoker {
    private String _methodName14;
    private String[] _methodParameterTypes14;
    private String _methodName15;
    private String[] _methodParameterTypes15;
    private String _methodName18;
    private String[] _methodParameterTypes18;
    private String _methodName19;
    private String[] _methodParameterTypes19;
    private String _methodName21;
    private String[] _methodParameterTypes21;
    private String _methodName22;
    private String[] _methodParameterTypes22;
    private String _methodName23;
    private String[] _methodParameterTypes23;
    private String _methodName24;
    private String[] _methodParameterTypes24;

    public KnowledgeFinderServiceClpInvoker() {
        _methodName14 = "getBeanIdentifier";

        _methodParameterTypes14 = new String[] {  };

        _methodName15 = "setBeanIdentifier";

        _methodParameterTypes15 = new String[] { "java.lang.String" };

        _methodName18 = "getDocuments";

        _methodParameterTypes18 = new String[] {
                "java.lang.String", "java.lang.String"
            };

        _methodName19 = "getDocuments";

        _methodParameterTypes19 = new String[] {
                "java.lang.String", "java.lang.String", "java.lang.String",
                "java.lang.String", "java.lang.String", "java.lang.String",
                "java.lang.String"
            };

        _methodName21 = "getNodes";

        _methodParameterTypes21 = new String[] {
                "java.lang.String", "java.lang.String", "java.lang.String"
            };

        _methodName22 = "getNodes";

        _methodParameterTypes22 = new String[] {
                "java.lang.String", "java.lang.String", "java.lang.String",
                "java.lang.String", "java.lang.String"
            };
        
        _methodName23 = "exportDocuments";

        _methodParameterTypes23 = new String[] {
                "java.lang.String", "java.lang.String", "java.lang.String"
            };

        _methodName24 = "exportDocuments";

        _methodParameterTypes24 = new String[] {
                "java.lang.String", "java.lang.String", "java.lang.String",
                "java.lang.String"
            };
    }

    public Object invokeMethod(String name, String[] parameterTypes,
        Object[] arguments) throws Throwable {
        if (_methodName14.equals(name) &&
                Arrays.deepEquals(_methodParameterTypes14, parameterTypes)) {
            return KnowledgeFinderServiceUtil.getBeanIdentifier();
        }

        if (_methodName15.equals(name) &&
                Arrays.deepEquals(_methodParameterTypes15, parameterTypes)) {
            KnowledgeFinderServiceUtil.setBeanIdentifier((java.lang.String) arguments[0]);

            return null;
        }

        if (_methodName18.equals(name) &&
                Arrays.deepEquals(_methodParameterTypes18, parameterTypes)) {
            return KnowledgeFinderServiceUtil.getDocuments((java.lang.String) arguments[0],
                (java.lang.String) arguments[1]);
        }

        if (_methodName19.equals(name) &&
                Arrays.deepEquals(_methodParameterTypes19, parameterTypes)) {
            return KnowledgeFinderServiceUtil.getDocuments((java.lang.String) arguments[0],
                (java.lang.String) arguments[1],
                (java.lang.String) arguments[2],
                (java.lang.String) arguments[3],
                (java.lang.String) arguments[4],
                (java.lang.String) arguments[5], (java.lang.String) arguments[6]);
        }

        if (_methodName21.equals(name) &&
                Arrays.deepEquals(_methodParameterTypes21, parameterTypes)) {
            return KnowledgeFinderServiceUtil.getNodes((java.lang.String) arguments[0],
                (java.lang.String) arguments[1], (java.lang.String) arguments[2]);
        }

        if (_methodName22.equals(name) &&
                Arrays.deepEquals(_methodParameterTypes22, parameterTypes)) {
            return KnowledgeFinderServiceUtil.getNodes((java.lang.String) arguments[0],
                (java.lang.String) arguments[1],
                (java.lang.String) arguments[2],
                (java.lang.String) arguments[3], (java.lang.String) arguments[4]);
        }
        
        if (_methodName23.equals(name) &&
                Arrays.deepEquals(_methodParameterTypes23, parameterTypes)) {
            return KnowledgeFinderServiceUtil.exportDocuments((java.lang.String) arguments[0],
                (java.lang.String) arguments[1], (java.lang.String) arguments[1]);
        }

        if (_methodName24.equals(name) &&
                Arrays.deepEquals(_methodParameterTypes24, parameterTypes)) {
            return KnowledgeFinderServiceUtil.exportDocuments((java.lang.String) arguments[0],
                (java.lang.String) arguments[1],
                (java.lang.String) arguments[2],
                (java.lang.String) arguments[3]);
        }

        throw new UnsupportedOperationException();
    }
}
