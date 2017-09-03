package com.deshani;


import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * Created by deshani on 8/4/17.
 */
class ReportHandler {

    static void findFilesAndMoveToFolder(String sourcePath, String destinationPath, String fileName) throws IOException {
        File dir = new File(destinationPath);
        dir.mkdir();

        Files.find(Paths.get(sourcePath),
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> filePath.getFileName().toString().equals(fileName)).forEach((f) -> {
            try {
                File file = f.toFile();

                String newFileName = file.getAbsolutePath().replace(sourcePath, Constant.NULL_STRING).replace(File.separator, Constant.UNDERSCORE);
                File newFile = new File(destinationPath + File.separator + newFileName);

                file.renameTo(newFile);
                FileUtils.copyFileToDirectory(newFile, dir);

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + File.separator + childFile.getName(), zipOut);
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

    static String extractFolder(String zipFile) throws ZipException, IOException {
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = file.getParent();

        String fileName = file.getName();

        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);

            File destinationParent = destFile.getParentFile();
            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip
                        .getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }

            if (currentEntry.endsWith(Constant.ZIP_FILE_EXTENSION)) {
                // found a zip file, try to open
                extractFolder(destFile.getAbsolutePath());
            }
        }
        //FileUtils.deleteDirectory(new File(zipFile));
        return fileName.substring(0, fileName.length() - 4);
    }

}
