package org.wso2.security.staticscanner;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wso2.security.staticscanner.observarables.GitHandler;
import org.wso2.security.staticscanner.observarables.ProductZipFileHandler;
import org.wso2.security.staticscanner.scanners.DependencyCheckScanner;
import org.wso2.security.staticscanner.scanners.FindSecBugsScanner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

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

@Controller
@EnableAutoConfiguration
@RequestMapping("staticScanner")
public class StaticScannerAPI {

    private String returnMessage;
    private static String productPath = Constants.DEFAULT_PRODUCT_PATH;
    private boolean gitStatus;
    private boolean uploadStatus;

    @RequestMapping(value = "runDependencyCheck", method = RequestMethod.GET)
    @ResponseBody
    public String runDependencyCheck() throws GitAPIException, MavenInvocationException, IOException {
        DependencyCheckScanner dependencyCheckScanner = new DependencyCheckScanner();
        if (new File(getProductPath()).isDirectory()) {
            if (new File(getProductPath()).list().length > 0) {
                Observer dependencyCheckObserver = new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        if (new File(getProductPath() + Constants.DEPENDENCY_CHECK_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION).exists()) {
                            returnMessage = "Success";
                        } else {
                            returnMessage = "Scan Failed";
                        }
                    }

                };

                dependencyCheckScanner.addObserver(dependencyCheckObserver);
                new Thread(dependencyCheckScanner).start();
                return returnMessage;
            }
        }
        return "Product Not Found";
    }

    @RequestMapping(value = "runFindSecBugs", method = RequestMethod.GET)
    @ResponseBody
    public String runFindSecBugs() throws MavenInvocationException, IOException, ParserConfigurationException, SAXException, TransformerException, GitAPIException, URISyntaxException {
        FindSecBugsScanner findSecBugsScanner = new FindSecBugsScanner();
        if (new File(getProductPath()).exists()) {
            Observer findSecBugsObserver = new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if (new File(getProductPath() + Constants.FIND_SEC_BUGS_REPORTS_FOLDER + Constants.ZIP_FILE_EXTENSION).exists()) {
                        returnMessage = "Success";
                    } else {
                        returnMessage = "Scan Failed";
                    }
                }
            };
            findSecBugsScanner.addObserver(findSecBugsObserver);
            new Thread(findSecBugsScanner).start();
            return returnMessage;
        }
        return "Product Not Found";
    }

    @RequestMapping(value = "cloneProductFromGitHub", method = RequestMethod.GET)
    @ResponseBody
    public boolean cloneProductFromGitHub(@RequestParam String url, @RequestParam String branch, @RequestParam String tag) throws GitAPIException, IOException {
        GitHandler gitHandler = new GitHandler(url, branch, tag);
        Observer gitHandlerObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (new File(getProductPath()).exists()) {
                    gitStatus = true;
                }
            }
        };
        gitHandler.addObserver(gitHandlerObserver);
        new Thread(gitHandler).start();
        return gitStatus;
    }

    @RequestMapping(value = "uploadProductZipFileANdExtract", method = RequestMethod.POST)
    @ResponseBody
    public boolean uploadProductZipFileAndExtract(@RequestParam MultipartFile file) throws IOException {
        ProductZipFileHandler productZipFileHandler = new ProductZipFileHandler(file);

        Observer productUploaderObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                boolean uploadedStatus;
                if (new File(getProductPath()).exists()) {
                    uploadStatus = true;
                } else {
                    uploadStatus = false;
                }
            }
        };
        productZipFileHandler.addObserver(productUploaderObserver);
        new Thread(productZipFileHandler).start();
        return uploadStatus;
    }

    public static void setProductPath(String productPath) {
        StaticScannerAPI.productPath = productPath;
    }

    public static String getProductPath() {
        return productPath;
    }
}

