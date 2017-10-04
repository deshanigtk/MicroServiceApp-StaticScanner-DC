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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.wso2.security.staticscanner.handlers.FileHandler;
import org.wso2.security.staticscanner.observarables.GitHandler;
import org.wso2.security.staticscanner.observarables.ProductZipFileHandler;
import org.wso2.security.staticscanner.scanners.DependencyCheckScanner;
import org.wso2.security.staticscanner.scanners.FindSecBugsScanner;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

@Service
public class StaticScannerService {

    private static String productPath = Constants.DEFAULT_PRODUCT_PATH;
    private static boolean isProductAvailable;

    private final static Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);


    public static boolean configureNotificationManager(String automationManagerHost, int automationManagerPort, String containerId) {
        NotificationManager.setAutomationManagerHost(automationManagerHost);
        NotificationManager.setAutomationManagerPort(automationManagerPort);
        NotificationManager.setMyContainerId(containerId);
        return NotificationManager.isConfigured();
    }

    public static String cloneProductFromGitHub(@RequestParam String url, @RequestParam String branch, @RequestParam String tag) {
        if (NotificationManager.isConfigured()) {
            Observer gitHandlerObserver = new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if (new File(getProductPath()).isDirectory()) {
                        if (new File(getProductPath()).list().length > 0) {
                            isProductAvailable = true;
                            LOGGER.info("Product cloned successfully");
                            NotificationManager.notifyProductCloned(true, new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date()));
                        } else {
                            LOGGER.error("Unable to clone product");
                        }
                    }
                }
            };
            if (!isProductAvailable) {
                GitHandler gitHandler = new GitHandler(url, branch, tag);
                gitHandler.addObserver(gitHandlerObserver);
                new Thread(gitHandler).start();
                return "Ok";
            } else {
                String message = "Product is already available";
                LOGGER.error(message);
                return message;
            }
        } else {
            String message = "Please configure notification manager";
            LOGGER.error(message);
            return message;
        }
    }

    public static String uploadProductZipFileAndExtract(@RequestParam MultipartFile file) {
        if (NotificationManager.isConfigured()) {
            Observer productUploaderObserver = new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if (new File(getProductPath()).isDirectory()) {
                        if (new File(getProductPath()).list().length > 0) {
                            isProductAvailable = true;
                            NotificationManager.notifyFileExtracted(true, new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date()));
                            LOGGER.info("Product is successfully uploaded");
                        }
                    }
                }
            };
            if (!isProductAvailable) {
                ProductZipFileHandler productZipFileHandler = new ProductZipFileHandler(file);
                productZipFileHandler.addObserver(productUploaderObserver);
                new Thread(productZipFileHandler).start();
                return "Ok";
            } else {
                String message = "Product is already available";
                LOGGER.error(message);
                return message;
            }
        } else {
            String message = "Please configure notification manager";
            LOGGER.error(message);
            return message;
        }
    }


    public static String runFindSecBugs() {
        if (NotificationManager.isConfigured()) {
            if (new File(getProductPath()).isDirectory()) {
                if (new File(getProductPath()).list().length > 0) {
                    Observer findSecBugsObserver = new Observer() {
                        @Override
                        public void update(Observable o, Object arg) {
                            if (new File(getProductPath() + File.separator + Constants.FIND_SEC_BUGS_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION).exists()) {
                                LOGGER.info("FindSecBugs scanning completed");
                                String time = new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date());
                                NotificationManager.notifyFindSecBugsReportReady(true, time);
                            } else {
                                LOGGER.error("Unable to complete FindSecBugs scanning");
                            }
                        }
                    };
                    FindSecBugsScanner findSecBugsScanner = new FindSecBugsScanner();
                    findSecBugsScanner.addObserver(findSecBugsObserver);
                    new Thread(findSecBugsScanner).start();
                    return "Ok";
                }
            }
            String message = "Product not available";
            LOGGER.error(message);
            return message;
        } else {
            String message = "Please configure notification manager";
            LOGGER.error(message);
            return message;
        }
    }

    public static String runDependencyCheck() {
        if (NotificationManager.isConfigured()) {
            if (new File(getProductPath()).isDirectory()) {
                if (new File(getProductPath()).list().length > 0) {
                    DependencyCheckScanner dependencyCheckScanner = new DependencyCheckScanner();

                    Observer dependencyCheckObserver = new Observer() {
                        @Override
                        public void update(Observable o, Object arg) {
                            if (new File(getProductPath() + File.separator + Constants.DEPENDENCY_CHECK_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION).exists()) {
                                LOGGER.info("Successfully completed Dependency Check Scan");
                                String time = new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date());
                                NotificationManager.notifyDependencyCheckReportReady(true, time);
                            } else {
                                LOGGER.error("Dependency Check scan failed");
                            }
                        }

                    };

                    dependencyCheckScanner.addObserver(dependencyCheckObserver);
                    new Thread(dependencyCheckScanner).start();
                    return "Ok";
                }
            }
            String message = "Product not available";
            LOGGER.error(message);
            return message;
        } else {
            String message = "Please configure notification manager";
            LOGGER.error(message);
            return message;
        }
    }


    public static HttpResponse getReport(HttpServletResponse response, boolean dependencyCheckReport) {
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

    public static void setProductPath(String productPath) {
        StaticScannerService.productPath = productPath;
    }

    public static String getProductPath() {
        return productPath;
    }
}
