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
import org.wso2.security.staticscanner.handlers.FileHandler;
import org.wso2.security.staticscanner.scanners.MainScanner;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Observer;
import java.util.zip.ZipOutputStream;

@Service
public class StaticScannerService {

    private boolean isFindSecBugsSuccess;
    private boolean isDependencyCheckSuccess;

    private final Logger LOGGER = LoggerFactory.getLogger(StaticScannerService.class);


    private boolean configureNotificationManager(String automationManagerHost, int automationManagerPort, String containerId) {
        NotificationManager.setAutomationManagerHost(automationManagerHost);
        NotificationManager.setAutomationManagerPort(automationManagerPort);
        NotificationManager.setMyContainerId(containerId);
        return NotificationManager.isConfigured();
    }

    public String startScan(String automationManagerHost, int automationManagerPort, String containerId, boolean isFileUpload,
                            MultipartFile zipFile, String url, String branch, String tag, boolean isFindSecBugs, boolean isDependencyCheck) {

        String zipFileName = null;
        if (!configureNotificationManager(automationManagerHost, automationManagerPort, containerId)) {
            return "Notification manager is not configured";
        }
        if (isFileUpload) {
            if (zipFile == null || !zipFile.getOriginalFilename().endsWith(".zip")) {
                return "Please upload a zip file";
            } else {
                zipFileName = zipFile.getOriginalFilename();
                if (new File(Constants.DEFAULT_PRODUCT_PATH).exists() || new File(Constants.DEFAULT_PRODUCT_PATH).mkdir()) {
                    String fileUploadPath = Constants.DEFAULT_PRODUCT_PATH + File.separator + zipFileName;
                    if (FileHandler.uploadFile(zipFile, fileUploadPath)) {
                        LOGGER.info("File successfully uploaded");
                    } else {
                        return "Error occurred while uploading zip file";
                    }
                }
            }
        } else {
            if (url == null || branch == null) {
                return "Please enter a URL and branch to perform clone operation";
            }
        }
        if (!isFindSecBugs && !isDependencyCheck) {
            return "Please enter a scan to process";
        }
        if (zipFile != null) {

        }
        Observer mainScannerObserver = (o, arg) -> {
            if (isFindSecBugs) {
                if (new File(Constants.REPORTS_FOLDER_PATH + File.separator + Constants.FIND_SEC_BUGS_REPORTS_FOLDER).exists()) {
                    LOGGER.info("FindSecBugs scanning completed");
                    isFindSecBugsSuccess = true;
                    NotificationManager.notifyFindSecBugsStatus("completed");
                    NotificationManager.notifyFindSecBugsReportReady(true);
                } else {
                    LOGGER.error("FindSecBugs scan failed");
                }
            }
            if (isDependencyCheck) {
                if (new File(Constants.REPORTS_FOLDER_PATH + File.separator + Constants.DEPENDENCY_CHECK_REPORTS_FOLDER).exists()) {
                    LOGGER.info("Successfully completed Dependency Check Scan");
                    isDependencyCheckSuccess = true;
                    NotificationManager.notifyDependencyCheckStatus("completed");
                    NotificationManager.notifyDependencyCheckReportReady(true);
                } else {
                    LOGGER.error("Dependency Check scan failed");
                }
            }
            try {
                if (isFindSecBugsSuccess || isDependencyCheckSuccess) {
                    FileOutputStream fos = new FileOutputStream(Constants.REPORTS_FOLDER_PATH + Constants.ZIP_FILE_EXTENSION);
                    ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(Constants.REPORTS_FOLDER_PATH + Constants.ZIP_FILE_EXTENSION));
                    File fileToZip = new File(Constants.REPORTS_FOLDER_PATH);

                    FileHandler.zipFolder(fileToZip, fileToZip.getName(), zipOut);
                    zipOut.close();
                    fos.close();

                    LOGGER.info("Report zip file ready");
                    NotificationManager.notifyReportReady(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        };

        MainScanner mainScanner = new MainScanner(isFileUpload, zipFileName, url, branch, tag, isFindSecBugs, isDependencyCheck);
        mainScanner.addObserver(mainScannerObserver);
        new Thread(mainScanner).start();
        return "Ok";
    }

    public HttpResponse getReport(HttpServletResponse response) {
        String reportsPath = Constants.REPORTS_FOLDER_PATH + Constants.ZIP_FILE_EXTENSION;

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
