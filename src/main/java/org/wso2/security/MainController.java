package org.wso2.security;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.wso2.security.handlers.FileHandler;
import org.wso2.security.handlers.GitHandler;
import java.io.*;

/**
 * Created by deshani on 8/8/17.
 */
class MainController {

    private static String productPath = Constants.DEFAULT_PRODUCT_PATH;


    static boolean gitClone(String url, String branch, boolean replaceExisting) throws GitAPIException, IOException {
        Git git;
        if (new File(productPath).exists() && replaceExisting) {
            FileUtils.deleteDirectory(new File(productPath));
            git = GitHandler.gitClone(url, branch, productPath);

        } else if (!new File(productPath).exists()) {
            System.out.println(productPath);
            git = GitHandler.gitClone(url, branch, productPath);

        } else {
            git = GitHandler.gitOpen(productPath);
        }
        return GitHandler.hasAtLeastOneReference(git.getRepository());
    }

    static void uploadProductZipFile(String fileName, boolean replaceExisting) throws IOException {
        if (new File(productPath).exists() && replaceExisting) {
            FileUtils.deleteDirectory(new File(productPath));
        }
        String extractedFolder = FileHandler.extractFolder(productPath + File.separator + fileName);
        MainController.setProductPath(MainController.getProductPath() + File.separator + extractedFolder);
        System.out.println(extractedFolder);
        System.out.println(MainController.getProductPath());

    }

    static void setProductPath(String productPath) {
        MainController.productPath = productPath;
    }

    static String getProductPath() {
        return productPath;
    }

}
