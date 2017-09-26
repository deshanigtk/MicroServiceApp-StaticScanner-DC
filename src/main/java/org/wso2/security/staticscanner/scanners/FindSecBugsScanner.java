package org.wso2.security.staticscanner.scanners;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.w3c.dom.Document;
import org.wso2.security.staticscanner.Constants;
import org.wso2.security.staticscanner.StaticScannerAPI;
import org.wso2.security.staticscanner.handlers.FileHandler;
import org.wso2.security.staticscanner.handlers.MavenHandler;
import org.wso2.security.staticscanner.handlers.XMLHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.zip.ZipOutputStream;

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

public class FindSecBugsScanner extends Observable implements Runnable {
    //FindSecBugs related
    private static final String FIND_BUGS_REPORT = "findbugsXml.xml";
    private static final String FINDBUGS_SECURITY_INCLUDE = "findbugs-security-include.xml";
    private static final String FINDBUGS_SECURITY_EXCLUDE = "findbugs-security-exclude.xml";

    //Maven Commands
    private static final String MVN_COMMAND_FIND_SEC_BUGS = "findbugs:findbugs";
    private static final String MVN_COMMAND_COMPILE = "compile";

    @Override
    public void run() {
        try {
            startScan();
            setChanged();
            notifyObservers(true);
        } catch (ParserConfigurationException | TransformerException | IOException | SAXException | MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    private void startScan() throws ParserConfigurationException, TransformerException, IOException, SAXException, MavenInvocationException {

        //Create new files as "findbugs-security-include.xml" and "findbugs-security-exclude.xml"
        File findBugsSecIncludeFile = new File(StaticScannerAPI.getProductPath() + File.separator + FINDBUGS_SECURITY_INCLUDE);
        File findBugsSecExcludeFile = new File(StaticScannerAPI.getProductPath() + File.separator + FINDBUGS_SECURITY_EXCLUDE);

        File productPomFile = new File(StaticScannerAPI.getProductPath() + File.separator + Constants.POM_FILE);

        DocumentBuilder dBuilder;
        dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        assert dBuilder != null;
        Document findBugsSecIncludeDocument = dBuilder.newDocument();
        Document findBugsSecExcludeDocument = dBuilder.newDocument();
        XMLHandler.writeFindSecBugsIncludeFile(findBugsSecIncludeDocument);
        XMLHandler.writeFindSecBugsExcludeFile(findBugsSecExcludeDocument);

        Document findBugsPluginDocument;
        findBugsPluginDocument = dBuilder.parse(productPomFile);
        findBugsPluginDocument = XMLHandler.iterateNode(findBugsPluginDocument.getDocumentElement(), findBugsPluginDocument);

        DOMSource findBugsSecIncludeSource = new DOMSource(findBugsSecIncludeDocument);
        DOMSource findBugsSecExcludeSource = new DOMSource(findBugsSecExcludeDocument);
        DOMSource findBugsPluginSource = new DOMSource(findBugsPluginDocument);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = transformerFactory.newTransformer();

        StreamResult findBugsSecurityIncludeResult = new StreamResult(findBugsSecIncludeFile);
        StreamResult findBugsSecurityExcludeResult = new StreamResult(findBugsSecExcludeFile);
        StreamResult result = new StreamResult(StaticScannerAPI.getProductPath() + File.separator + Constants.POM_FILE);

        transformer.transform(findBugsSecIncludeSource, findBugsSecurityIncludeResult);
        transformer.transform(findBugsSecExcludeSource, findBugsSecurityExcludeResult);
        transformer.transform(findBugsPluginSource, result);
        MavenHandler.runMavenCommand(StaticScannerAPI.getProductPath() + File.separator + Constants.POM_FILE, MVN_COMMAND_COMPILE);
        MavenHandler.runMavenCommand(StaticScannerAPI.getProductPath() + File.separator + Constants.POM_FILE, MVN_COMMAND_FIND_SEC_BUGS);

        String reportsFolderPath = StaticScannerAPI.getProductPath() + File.separator + Constants.FIND_SEC_BUGS_REPORTS_FOLDER;
        FileHandler.findFilesAndMoveToFolder(StaticScannerAPI.getProductPath(), reportsFolderPath, FIND_BUGS_REPORT);

        FileOutputStream fos = new FileOutputStream(reportsFolderPath + Constants.ZIP_FILE_EXTENSION);
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(reportsFolderPath + Constants.ZIP_FILE_EXTENSION));
        File fileToZip = new File(reportsFolderPath);

        FileHandler.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();

    }
}
