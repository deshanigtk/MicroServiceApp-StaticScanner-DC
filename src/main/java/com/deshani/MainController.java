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
import java.net.URL;
import java.util.zip.ZipOutputStream;

/**
 * Created by deshani on 8/8/17.
 */
public class MainController {

    static void runDependencyCheck(String productPath) throws IOException, GitAPIException, MavenInvocationException {
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

    static boolean runFindSecBugs(String productPath) throws TransformerException, IOException, SAXException, ParserConfigurationException, GitAPIException, MavenInvocationException, URISyntaxException {

        Boolean success = false;

        URL findBugsSecExcludeFileInputStream = MainController.class.getClassLoader().getResource("findbugs-security-exclude.xml");
        System.out.println(findBugsSecExcludeFileInputStream);
        File findBugsSecExcludeFile = null;

        if (findBugsSecExcludeFileInputStream != null) {
            findBugsSecExcludeFile = new File(findBugsSecExcludeFileInputStream.toURI());
        }

        URL findBugsSecIncludeFileUrl = MainController.class.getClassLoader().getResource("findbugs-security-include.xml");
        File findBugsSecIncludeFile = null;

        if (findBugsSecIncludeFileUrl != null) {
            findBugsSecIncludeFile = new File(findBugsSecIncludeFileUrl.toURI());
        }

        if (findBugsSecExcludeFile != null && findBugsSecIncludeFile != null) {
            FileUtils.copyFileToDirectory(findBugsSecExcludeFile, new File(productPath));
            FileUtils.copyFileToDirectory(findBugsSecIncludeFile, new File(productPath));

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

            success = true;
        }
        return success;
    }

    static boolean gitClone(String url, String branch, String productPath) throws GitAPIException, IOException {
        Git git;
        if (new File(productPath).exists()) {
            git = GitClient.gitOpen(productPath);
            if (!GitClient.hasAtLeastOneReference(git.getRepository())) {
                FileUtils.deleteDirectory(new File(productPath));
                git = GitClient.gitClone(url, branch, productPath);
            }

        } else {
            git = GitClient.gitClone(url, branch, productPath);
        }
        return GitClient.hasAtLeastOneReference(git.getRepository());
    }

    public static void main1(String[] args) throws GitAPIException, IOException {
        String url = "https://github.com/gabrielf/maven-samples";
        String branch = "master";
        String productPath = "/home/deshani/Documents/Product-old";
        System.out.println(gitClone(url, "6.0", productPath));
    }
}

