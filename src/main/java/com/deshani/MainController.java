package com.deshani;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * Created by deshani on 8/8/17.
 */
public class MainController {

    private static String dependencyCheckReportHtml="dependency-check-report.html";

    public static void runDependencyCheck(String gitURL, String branch, String productPath) throws IOException, GitAPIException, MavenInvocationException {
        GitClient.gitClone(gitURL, branch, productPath);

        MavenClient.buildDependencyCheck(productPath+"/pom.xml");

        String reportsFolderPath = productPath+"/Dependency-Check-Reports";

        ReportHandler.findFilesAndMoveToFolder(productPath, reportsFolderPath, dependencyCheckReportHtml);
        FileOutputStream fos = new FileOutputStream(reportsFolderPath+".zip");
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(reportsFolderPath+".zip"));
        File fileToZip = new File(reportsFolderPath);

        ReportHandler.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }
}
