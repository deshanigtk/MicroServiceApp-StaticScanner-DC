package org.wso2.security.staticscanner;/*
*  Copyright (c) ${date}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.security.staticscanner.handlers.HttpRequestHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class NotificationManager {

    private final static String NOTIFY = "automationManager/staticScanner/notify";
    private final static String FILE_EXTRACTED = NOTIFY + "/fileExtracted";
    private final static String PRODUCT_CLONED = NOTIFY + "/productCloned";
    private final static String DEPENDENCY_CHECK_STATUS = NOTIFY + "/dependencyCheckStatus";
    private final static String FIND_SEC_BUGS_STATUS = NOTIFY + "/findSecBugsStatus";
    private final static String FIND_SEC_BUGS_REPORT_READY = NOTIFY + "/findSecBugsReportReady";
    private final static String DEPENDENCY_CHECK_REPORT_READY = NOTIFY + "/dependencyCheckReportReady";
    private final static String REPORT_READY = NOTIFY + "/reportReady";

    private static String myContainerId;
    private static String automationManagerHost;
    private static int automationManagerPort;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManager.class);


    public static void setMyContainerId(String myContainerId) {
        NotificationManager.myContainerId = myContainerId;
    }

    public static void setAutomationManagerHost(String automationManagerHost) {
        NotificationManager.automationManagerHost = automationManagerHost;
    }

    public static void setAutomationManagerPort(int automationManagerPort) {
        NotificationManager.automationManagerPort = automationManagerPort;
    }

    private static void notifyStatus(String path, boolean status) {
        try {
            URI uri = (new URIBuilder()).setHost(automationManagerHost).setPort(automationManagerPort).setScheme("http").setPath(path)
                    .addParameter("containerId", myContainerId)
                    .addParameter("status", String.valueOf(status))
                    .build();
            HttpRequestHandler.sendGetRequest(uri);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
        }
    }

    private static void notifyScanStatus(String path, String status) {
        try {
            URI uri = (new URIBuilder()).setHost(automationManagerHost).setPort(automationManagerPort).setScheme("http").setPath(path)
                    .addParameter("containerId", myContainerId)
                    .addParameter("status", status)
                    .build();
            HttpRequestHandler.sendGetRequest(uri);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
        }
    }

    public static void notifyFileExtracted(boolean status) {
        notifyStatus(FILE_EXTRACTED, status);
    }

    public static void notifyProductCloned(boolean status) {
        notifyStatus(PRODUCT_CLONED, status);
    }

    public static void notifyFindSecBugsStatus(String status) {
        notifyScanStatus(FIND_SEC_BUGS_STATUS, status);
    }

    public static void notifyDependencyCheckStatus(String status) {
        notifyScanStatus(DEPENDENCY_CHECK_STATUS, status);
    }

    public static void notifyFindSecBugsReportReady(boolean status) {
        notifyStatus(FIND_SEC_BUGS_REPORT_READY, status);
    }

    public static void notifyDependencyCheckReportReady(boolean status) {
        notifyStatus(DEPENDENCY_CHECK_REPORT_READY, status);
    }

    public static void notifyReportReady(boolean status) {
        notifyStatus(REPORT_READY, status);
    }

    public static boolean isConfigured() {
        return automationManagerHost != null && automationManagerPort != 0 && myContainerId != null;
    }
}
