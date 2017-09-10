package org.wso2.security.handlers;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * Created by deshani on 8/2/17.
 */

public class GitHandler {

    private static final String GIT_REFS_HEADS_PATH = "refs/heads/";

    public static Git gitClone(String gitURL, String branch, String filePath) throws GitAPIException {
        return Git.cloneRepository()
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setURI(gitURL)
                .setDirectory(new File(filePath))
                .setBranchesToClone(Collections.singleton(GIT_REFS_HEADS_PATH + branch))
                .setBranch(GIT_REFS_HEADS_PATH + branch)
                .call();

    }

    public static boolean hasAtLeastOneReference(Repository repo) {
        for (Ref ref : repo.getAllRefs().values()) {
            if (ref.getObjectId() == null)
                continue;
            return true;
        }

        return false;
    }

    public static void gitCheckout(String branchName, Git git) throws GitAPIException {
        git.checkout().setName(branchName).call();
    }

    public static Status gitStatus(Git git) throws GitAPIException {
        return git.status().call();
    }

    public static Git gitOpen(String productPath) throws IOException {
        return Git.open(new File(productPath));
    }

}
