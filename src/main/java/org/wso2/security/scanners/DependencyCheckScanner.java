package org.wso2.security.scanners;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.wso2.security.Constants;
import org.wso2.security.handlers.FileHandler;
import org.wso2.security.handlers.MavenHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.zip.ZipOutputStream;

public class DependencyCheckScanner extends Observable implements Runnable {

    //Maven Commands
    private static final String MVN_COMMAND_DEPENDENCY_CHECK = "org.owasp:dependency-check-maven:check";


    @Override
    public void run() {
        try {
            startScan();
            setChanged();
            notifyObservers(true);
        } catch (IOException | MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    private void startScan() throws IOException, MavenInvocationException {
        MavenHandler.runMavenCommand(Constants.DEFAULT_PRODUCT_PATH + File.separator + Constants.POM_FILE, MVN_COMMAND_DEPENDENCY_CHECK);

        String reportsFolderPath = Constants.DEFAULT_PRODUCT_PATH + File.separator + Constants.DEPENDENCY_CHECK_REPORTS_FOLDER;
        FileHandler.findFilesAndMoveToFolder(Constants.DEFAULT_PRODUCT_PATH, reportsFolderPath, Constants.DEPENDENCY_CHECK_REPORT);

        FileOutputStream fos = new FileOutputStream(reportsFolderPath + Constants.ZIP_FILE_EXTENSION);
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(reportsFolderPath + Constants.ZIP_FILE_EXTENSION));
        File fileToZip = new File(reportsFolderPath);

        FileHandler.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }
}
