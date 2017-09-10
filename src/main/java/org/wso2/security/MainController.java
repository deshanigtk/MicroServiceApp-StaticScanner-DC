package org.wso2.security;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.wso2.security.handlers.FileHandler;
import org.wso2.security.handlers.GitHandler;
import java.io.*;

/*
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

class MainController {

    private static String productPath = Constants.DEFAULT_PRODUCT_PATH;


    static boolean gitClone(String url, String branch, boolean replaceExisting) throws GitAPIException, IOException {
        Git git;
        if (new File(productPath).exists() && replaceExisting) {
            FileUtils.deleteDirectory(new File(productPath));
            git = GitHandler.gitClone(url, branch, productPath);

        } else if (!new File(productPath).exists()) {
            System.out.println(productPath);
            git = GitHandler.gitClone(url, branch, productPath);

        } else {
            git = GitHandler.gitOpen(productPath);
        }
        return GitHandler.hasAtLeastOneReference(git.getRepository());
    }

    static void uploadProductZipFile(String fileName, boolean replaceExisting) throws IOException {
        if (new File(productPath).exists() && replaceExisting) {
            FileUtils.deleteDirectory(new File(productPath));
        }
        String extractedFolder = FileHandler.extractFolder(productPath + File.separator + fileName);
        MainController.setProductPath(MainController.getProductPath() + File.separator + extractedFolder);
        System.out.println(extractedFolder);
        System.out.println(MainController.getProductPath());

    }

    static void setProductPath(String productPath) {
        MainController.productPath = productPath;
    }

    static String getProductPath() {
        return productPath;
    }

}
