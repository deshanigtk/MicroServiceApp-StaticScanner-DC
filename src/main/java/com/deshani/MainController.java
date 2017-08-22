package com.deshani;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.util.zip.ZipOutputStream;

/**
 * Created by deshani on 8/8/17.
 */
class MainController {

    static void runDependencyCheck(String productPath) throws IOException, GitAPIException, MavenInvocationException {
        MavenClient.buildDependencyCheck(productPath + File.separator + Constant.POM_FILE);

        String reportsFolderPath = productPath + File.separator + Constant.DEPENDENCY_CHECK_REPORTS_FOLDER;

        ReportHandler.findFilesAndMoveToFolder(productPath, reportsFolderPath, Constant.DEPENDENCY_CHECK_REPORT);

        FileOutputStream fos = new FileOutputStream(reportsFolderPath + Constant.ZIP_FILE_EXTENSION);
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(reportsFolderPath + Constant.ZIP_FILE_EXTENSION));
        File fileToZip = new File(reportsFolderPath);

        ReportHandler.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();

    }

    static void runFindSecBugs(String productPath) throws TransformerException, IOException, SAXException, ParserConfigurationException, GitAPIException, MavenInvocationException, URISyntaxException {

        //Create new files as "findbugs-security-include.xml" and "findbugs-security-exclude.xml"
        File findBugsSecIncludeFile = new File(productPath + File.separator + Constant.FINDBUGS_SECURITY_INCLUDE);
        File findBugsSecExcludeFile = new File(productPath + File.separator + Constant.FINDBUGS_SECURITY_EXCLUDE);

        File productPomFile = new File(productPath + File.separator + Constant.POM_FILE);

        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document findBugsSecIncludeDocument = dBuilder.newDocument();
        Document findBugsSecExcludeDocument = dBuilder.newDocument();

        XMLHandler.writeFindSecBugsIncludeFile(findBugsSecIncludeDocument);
        XMLHandler.writeFindSecBugsExcludeFile(findBugsSecExcludeDocument);

        Document findBugsPluginDocument = dBuilder.parse(productPomFile);
        findBugsPluginDocument = XMLHandler.iterateNode(findBugsPluginDocument.getDocumentElement(), findBugsPluginDocument);

        DOMSource findBugsSecIncludeSource = new DOMSource(findBugsSecIncludeDocument);
        DOMSource findBugsSecExcludeSource = new DOMSource(findBugsSecExcludeDocument);
        DOMSource findBugsPluginSource = new DOMSource(findBugsPluginDocument);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        StreamResult findBugsSecurityIncludeResult = new StreamResult(findBugsSecIncludeFile);
        StreamResult findBugsSecurityExcludeResult = new StreamResult(findBugsSecExcludeFile);

        StreamResult result = new StreamResult(productPath + File.separator + Constant.POM_FILE);

        transformer.transform(findBugsSecIncludeSource, findBugsSecurityIncludeResult);
        transformer.transform(findBugsSecExcludeSource, findBugsSecurityExcludeResult);
        transformer.transform(findBugsPluginSource, result);

        MavenClient.compile(productPath + File.separator + Constant.POM_FILE);
        MavenClient.buildFindSecBugs(productPath + File.separator + Constant.POM_FILE);

        String reportsFolderPath = productPath + File.separator + Constant.FIND_SEC_BUGS_REPORTS_FOLDER;

        ReportHandler.findFilesAndMoveToFolder(productPath, reportsFolderPath, Constant.FIND_BUGS_REPORT);
        FileOutputStream fos = new FileOutputStream(reportsFolderPath + Constant.ZIP_FILE_EXTENSION);
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(reportsFolderPath + Constant.ZIP_FILE_EXTENSION));
        File fileToZip = new File(reportsFolderPath);

        ReportHandler.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();

    }

    static boolean gitClone(String url, String branch, String productPath, boolean replaceExisting) throws GitAPIException, IOException {
        Git git;
        if (new File(productPath).exists() && replaceExisting) {
            FileUtils.deleteDirectory(new File(productPath));
            git = GitClient.gitClone(url, branch, productPath);

        } else if (!new File(productPath).exists()) {
            git = GitClient.gitClone(url, branch, productPath);

        } else {
            git = GitClient.gitOpen(productPath);
        }
        return GitClient.hasAtLeastOneReference(git.getRepository());
    }

    static void uploadProductZipFile(String fileName, String productPath, boolean replaceExisting) throws IOException {
        if(new File(productPath).exists() && replaceExisting){
            FileUtils.deleteDirectory(new File(productPath));
            ReportHandler.unzip(productPath + fileName, productPath);
        }
        ReportHandler.unzip(productPath + fileName, productPath);

    }

}
