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

import java.text.SimpleDateFormat;
import java.util.Date;
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
            isProductAvailable = uploadProductZipFileAndExtract(zipFile);
        } else {
            isProductAvailable = cloneProductFromGitHub(url, branch, tag);
        }
        if (isProductAvailable) {
            if (isFindSecBugs) {
                runFindSecBugs();
            }
            if (isDependencyCheck) {
                runDependencyCheck();
            }
        }
        LOGGER.error("Product upload/ clone failed");
    }

    private boolean cloneProductFromGitHub(String url, String branch, String tag) {
        if (GitHandler.startClone(url, branch, tag)) {
            LOGGER.info("Product cloned successfully");
            NotificationManager.notifyProductCloned(true, new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date()));
            return true;
        }
        LOGGER.error("Git clone failed");
        return false;
    }

    private boolean uploadProductZipFileAndExtract(MultipartFile zipFile) {
        if (FileHandler.uploadProductZipAndExtract(zipFile)) {
            NotificationManager.notifyFileExtracted(true, new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date()));
            LOGGER.info("Product is successfully uploaded and extracted");
            return true;
        }
        return false;
    }


    private void runFindSecBugs() {
        FindSecBugsScanner findSecBugsScanner = new FindSecBugsScanner();
        new Thread(findSecBugsScanner).start();
        LOGGER.info("FindSecBugs started");
        NotificationManager.notifyFindSecBugsStatus("running", new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date()));
    }

    private void runDependencyCheck() {
        DependencyCheckScanner dependencyCheckScanner = new DependencyCheckScanner();
        new Thread(dependencyCheckScanner).start();
        LOGGER.info("Dependency Check started");
        NotificationManager.notifyDependencyCheckStatus("running", new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date()));
    }

    public static void setProductPath(String productPath) {
        MainScanner.productPath = productPath;
    }

    public static String getProductPath() {
        return productPath;
    }
}
