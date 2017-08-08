package com.deshani;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.FileUtils;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * Created by deshani on 8/8/17.
 */
public class MainController {

    static void runDependencyCheck( String productPath) throws IOException, GitAPIException, MavenInvocationException {
       MavenClient.buildDependencyCheck(productPath + "/pom.xml");

        String reportsFolderPath = productPath + "/Dependency-Check-Reports";

        ReportHandler.findFilesAndMoveToFolder(productPath, reportsFolderPath, "dependency-check-report.html");
        FileOutputStream fos = new FileOutputStream(reportsFolderPath + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(reportsFolderPath + ".zip"));
        File fileToZip = new File(reportsFolderPath);

        ReportHandler.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    static void runFindSecBugs(String productPath) throws TransformerException, IOException, SAXException, ParserConfigurationException, GitAPIException, MavenInvocationException {

        FileUtils.copyFileToDirectory("findbugs-security-exclude.xml", productPath);
        FileUtils.copyFileToDirectory("findbugs-security-include.xml", productPath);

        File file = new File(productPath + "/pom.xml");

        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document document = dBuilder.parse(file);
        document = XMLHandler.iterateNode(document.getDocumentElement(), document);

        DOMSource source = new DOMSource(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(productPath + "/pom.xml");
        transformer.transform(source, result);

        MavenClient.compile(productPath + "/pom.xml");
        MavenClient.buildFindSecBugs(productPath + "/pom.xml");

        String reportsFolderPath = productPath + "/Findbugs-Reports";

        ReportHandler.findFilesAndMoveToFolder(productPath, reportsFolderPath, "findbugsXml.xml");
        FileOutputStream fos = new FileOutputStream(reportsFolderPath + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(reportsFolderPath + ".zip"));
        File fileToZip = new File(reportsFolderPath);

        ReportHandler.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    static void gitClone(String url, String branch, String filePath) throws GitAPIException {
        GitClient.gitClone(url,branch, filePath);
    }
}
