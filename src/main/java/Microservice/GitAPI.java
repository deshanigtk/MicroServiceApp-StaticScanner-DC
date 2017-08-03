package Microservice;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.util.Arrays;

/**
 * Created by deshani on 8/2/17.
 */

public class GitAPI {

    public static Git gitClone(String gitURL, String filePath, String branch) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(gitURL)
                .setDirectory(new File(filePath))
                .setBranchesToClone(Arrays.asList("refs/heads/"+branch))
                .setBranch("refs/heads/"+branch)
                .call();

        return git;
    }

    public static void gitCheckout(String branchName, Git git) throws GitAPIException {
        git.checkout().setName(branchName).call();
    }

    public static Status gitStatus(Git git) throws GitAPIException {
        return  git.status().call();
    }

}
