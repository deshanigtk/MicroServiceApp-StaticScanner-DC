package org.wso2.security.staticscanner;

import org.apache.http.HttpResponse;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;

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

    @RequestMapping(value = "configureNotificationManager", method = RequestMethod.GET)
    @ResponseBody
    public boolean configureNotificationManager(@RequestParam String automationManagerHost, @RequestParam int automationManagerPort, @RequestParam String myContainerId) throws IOException {
        return StaticScannerService.configureNotificationManager(automationManagerHost, automationManagerPort, myContainerId);
    }

    @RequestMapping(value = "runDependencyCheck", method = RequestMethod.GET)
    @ResponseBody
    public String runDependencyCheck() throws GitAPIException, MavenInvocationException, IOException {
        return StaticScannerService.runDependencyCheck();
    }

    @RequestMapping(value = "runFindSecBugs", method = RequestMethod.GET)
    @ResponseBody
    public String runFindSecBugs() throws MavenInvocationException, IOException, ParserConfigurationException, SAXException, TransformerException, GitAPIException, URISyntaxException {
        return StaticScannerService.runFindSecBugs();
    }

    @RequestMapping(value = "cloneProductFromGitHub", method = RequestMethod.GET)
    @ResponseBody
    public String cloneProductFromGitHub(@RequestParam String url, @RequestParam String branch, @RequestParam String tag) throws GitAPIException, IOException {
        return StaticScannerService.cloneProductFromGitHub(url, branch, tag);
    }

    @RequestMapping(value = "uploadProductZipFileAndExtract", method = RequestMethod.POST)
    @ResponseBody
    public String uploadProductZipFileAndExtract(@RequestParam MultipartFile file) throws IOException {
        return StaticScannerService.uploadProductZipFileAndExtract(file);
    }

    @RequestMapping(value = "getReport", method = RequestMethod.GET, produces = "application/octet-stream")
    @ResponseBody
    public HttpResponse getReport(HttpServletResponse response, @RequestParam boolean dependencyCheckreports) {
        return StaticScannerService.getReport(response, dependencyCheckreports);
    }
}

