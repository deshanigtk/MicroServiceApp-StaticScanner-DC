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

import org.apache.http.HttpResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wso2.security.staticscanner.scanners.MainScanner;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import static org.wso2.security.staticscanner.scanners.MainScanner.getProductPath;

@Service
public class StaticScannerService {

    private final Logger LOGGER = LoggerFactory.getLogger(StaticScannerService.class);


    private boolean configureNotificationManager(String automationManagerHost, int automationManagerPort, String containerId) {
        NotificationManager.setAutomationManagerHost(automationManagerHost);
        NotificationManager.setAutomationManagerPort(automationManagerPort);
        NotificationManager.setMyContainerId(containerId);
        return NotificationManager.isConfigured();
    }


    public String startScan(String automationManagerHost, int automationManagerPort, String containerId, boolean isFileUpload,
                            MultipartFile zipFile, String url, String branch, String tag, boolean isFindSecBugs, boolean isDependencyCheck) {

        if (!configureNotificationManager(automationManagerHost, automationManagerPort, containerId)) {
            return "Notification manager is not configured";
        }
        if (isFileUpload) {
            if (zipFile == null || !zipFile.getOriginalFilename().endsWith(".zip")) {
                return "Please upload a zip File";
            }
        } else {
            if (url == null || branch == null) {
                return "Please enter a URL and branch to perform clone operation";
            }
        }
        if (!isFindSecBugs && !isDependencyCheck) {
            return "Please enter a scan to process";
        }

        Observer mainScannerObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (isFindSecBugs) {
                    if (new File(getProductPath() + File.separator + Constants.FIND_SEC_BUGS_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION).exists()) {
                        LOGGER.info("FindSecBugs scanning completed");
                        NotificationManager.notifyFindSecBugsReportReady(true);
                    } else {
                        LOGGER.error("FindSecBugs scan failed");
                    }
                }
                if (isDependencyCheck) {
                    if (new File(getProductPath() + File.separator + Constants.DEPENDENCY_CHECK_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION).exists()) {
                        LOGGER.info("Successfully completed Dependency Check Scan");
                        NotificationManager.notifyDependencyCheckReportReady(true);
                    } else {
                        LOGGER.error("Dependency Check scan failed");
                    }
                }
            }
        };

        MainScanner mainScanner = new MainScanner(isFileUpload, zipFile, url, branch, tag, isFindSecBugs, isDependencyCheck);
        mainScanner.addObserver(mainScannerObserver);
        new Thread(mainScanner).start();
        return "Ok";
    }

    public HttpResponse getReport(HttpServletResponse response, boolean dependencyCheckReport) {
        String reportsPath;
        if (dependencyCheckReport) {
            reportsPath = getProductPath() + File.separator + Constants.DEPENDENCY_CHECK_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION;
        } else {
            reportsPath = getProductPath() + File.separator + Constants.FIND_SEC_BUGS_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION;
        }
        if (new File(reportsPath).exists()) {
            try {
                InputStream inputStream = new FileInputStream(reportsPath);
                IOUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
                LOGGER.info("Successfully written to output stream");

                return (HttpResponse) response;

            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error(e.toString());
            }
        } else {
            LOGGER.error("Report is not found");
        }
        return null;
    }
}
