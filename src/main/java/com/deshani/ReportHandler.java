package com.deshani;


import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by deshani on 8/4/17.
 */
public class ReportHandler {

    public static void findFilesAndMoveToFolder(String sourcePath, String destinationPath, String fileName) throws IOException {
        File dir = new File(destinationPath);
        dir.mkdir();

        Files.find(Paths.get(sourcePath),
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> filePath.getFileName().toString().equals(fileName)).forEach((f) -> {
            try {
                File file = f.toFile();

                String newFileName = file.getAbsolutePath().replace(sourcePath, "").replace("/", "_");
                File newFile = new File(destinationPath + "/" + newFileName);

                file.renameTo(newFile);
                FileUtils.copyFileToDirectory(newFile, dir);

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
