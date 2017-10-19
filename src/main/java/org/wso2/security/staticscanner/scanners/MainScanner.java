package org.wso2.security.staticscanner.scanners;/*
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.wso2.security.staticscanner.Constants;
import org.wso2.security.staticscanner.NotificationManager;
import org.wso2.security.staticscanner.handlers.FileHandler;
import org.wso2.security.staticscanner.handlers.GitHandler;

import java.util.Observable;

public class MainScanner extends Observable implements Runnable {

    private boolean isFileUpload;
    private MultipartFile zipFile;
    private String url;
    private String branch;
    private String tag;
    private boolean isFindSecBugs;
    private boolean isDependencyCheck;

    private static String productPath = Constants.DEFAULT_PRODUCT_PATH;

    private final Logger LOGGER = LoggerFactory.getLogger(MainScanner.class);

    @Override
    public void run() {
        startScan();
        setChanged();
        notifyObservers(true);
    }

    public MainScanner(boolean isFileUpload, MultipartFile zipFile, String url, String branch, String tag, boolean isFindSecBugs, boolean isDependencyCheck) {
        this.isFileUpload = isFileUpload;
        this.zipFile = zipFile;
        this.url = url;
        this.branch = branch;
        this.tag = tag;
        this.isFindSecBugs = isFindSecBugs;
        this.isDependencyCheck = isDependencyCheck;
    }

    private void startScan() {
        boolean isProductAvailable;
        if (isFileUpload) {
            isProductAvailable = FileHandler.uploadProductZipAndExtract(zipFile);
            if (isProductAvailable) {
                NotificationManager.notifyFileExtracted(true);
                LOGGER.info("Product is successfully uploaded and extracted");
            }
        } else {
            isProductAvailable = GitHandler.startClone(url, branch, tag);
            if (isProductAvailable) {
                LOGGER.info("Product cloned successfully");
                NotificationManager.notifyProductCloned(true);
            }
        }
        if (isProductAvailable) {
            if (isDependencyCheck) {
                DependencyCheckScanner.startScan();
            }
            if (isFindSecBugs) {
                FindSecBugsScanner.startScan();
            }
        }
    }

    public static void setProductPath(String productPath) {
        MainScanner.productPath = productPath;
    }

    public static String getProductPath() {
        return productPath;
    }
}
