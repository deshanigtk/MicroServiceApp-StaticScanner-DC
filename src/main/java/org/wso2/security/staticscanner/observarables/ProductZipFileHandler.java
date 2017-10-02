package org.wso2.security.staticscanner.observarables;/*
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
import org.wso2.security.staticscanner.StaticScannerService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

public class ProductZipFileHandler extends Observable implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    private MultipartFile file;

    public ProductZipFileHandler(MultipartFile file) {
        this.file = file;
    }

    @Override
    public void run() {
        try {
            uploadProductZipAndExtract();
            setChanged();
            notifyObservers(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean uploadProductZipAndExtract() throws IOException {
        boolean isProductPathCreated = new File(Constants.DEFAULT_PRODUCT_PATH).exists() || new File(Constants.DEFAULT_PRODUCT_PATH).mkdir();
        String fileName = null;
        String folderName = null;

        if (isProductPathCreated) {
            fileName = FileHandler.uploadFile(file);
        }
        if (fileName != null) {
            String time = new SimpleDateFormat("yyyy-MM-dd:HH.mm.ss").format(new Date());
            NotificationManager.notifyFileUploaded(true, time);
            folderName = FileHandler.extractFolder(Constants.DEFAULT_PRODUCT_PATH + File.separator + fileName);
        }
        if (folderName != null) {
            StaticScannerService.setProductPath(StaticScannerService.getProductPath() + File.separator + folderName);
            return true;
        }
        return false;
    }
}
