package com.deshani;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by deshani on 8/1/17.
 */


@Controller
@EnableAutoConfiguration
@RequestMapping("staticScanner/runScan")
public class UserAPI {

    @RequestMapping(value = "dependencyCheck/byGitURL", method = RequestMethod.GET)
    @ResponseBody
    public String runDependencyCheckByGitURL(@RequestParam("gitURL") String gitURL, @RequestParam("branch") String branch) throws GitAPIException, MavenInvocationException {
        Git git= GitClient.gitClone(gitURL,branch, "/opt/Product");

        MavenClient.buildDependencyCheck("/opt/Product/pom.xml");
        return String.valueOf(GitClient.gitStatus(git));
    }

    @RequestMapping(value = "findSecBugs/byGitURL", method = RequestMethod.GET)
    @ResponseBody
    public String runFindSecBugsByGitURL(@RequestParam("gitURL") String gitURL, @RequestParam("branch") String branch) throws GitAPIException, MavenInvocationException {
        Git git= GitClient.gitClone(gitURL,branch, "/opt/Product");

       // MavenClient.buildDependencyCheck("/opt/Product/pom.xml");
        return String.valueOf(GitClient.gitStatus(git));
    }

}

