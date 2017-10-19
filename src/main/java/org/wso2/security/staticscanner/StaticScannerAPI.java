package org.wso2.security.staticscanner;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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
@RequestMapping("staticScanner")
public class StaticScannerAPI {

    private final StaticScannerService staticScannerService;

    //TODO:send error messages to automation manager

    @Autowired
    public StaticScannerAPI(StaticScannerService staticScannerService) {
        this.staticScannerService = staticScannerService;
    }

    @GetMapping("isReady")
    @ResponseBody
    public boolean isReady() {
        return true;
    }

    @PostMapping("startScan")
    @ResponseBody
    public String startScan(@RequestParam String automationManagerHost,
                            @RequestParam int automationManagerPort,
                            @RequestParam String myContainerId,
                            @RequestParam boolean isFileUpload,
                            @RequestParam(required = false) MultipartFile zipFile,
                            @RequestParam(required = false) String url,
                            @RequestParam(required = false, defaultValue = "master") String branch,
                            @RequestParam(required = false) String tag,
                            @RequestParam boolean isFindSecBugs,
                            @RequestParam boolean isDependencyCheck) {
        return staticScannerService.startScan(automationManagerHost, automationManagerPort, myContainerId, isFileUpload, zipFile, url, branch, tag,
                isFindSecBugs, isDependencyCheck);
    }

    @RequestMapping(value = "getReport", method = RequestMethod.GET, produces = "application/octet-stream")
    @ResponseBody
    public HttpResponse getReport(HttpServletResponse response, @RequestParam boolean dependencyCheckReport) {
        return staticScannerService.getReport(response, dependencyCheckReport);
    }
}

