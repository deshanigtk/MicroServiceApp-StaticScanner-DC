package com.deshani;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.util.Collections;

/**
 * Created by deshani on 8/2/17.
 */

class GitClient {

    static Git gitClone(String gitURL, String branch, String filePath) throws GitAPIException {
        return Git.cloneRepository()
                .setURI(gitURL)
                .setDirectory(new File(filePath))
                .setBranchesToClone(Collections.singleton("refs/heads/" + branch))
                .setBranch("refs/heads/" + branch)
                .call();
    }

    static void gitCheckout(String branchName, Git git) throws GitAPIException {
        git.checkout().setName(branchName).call();
    }

    static Status gitStatus(Git git) throws GitAPIException {
        return git.status().call();
    }

}
