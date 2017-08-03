package Microservice;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.SpringApplication;
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

    @RequestMapping(value = "/byGitHubURL", method = RequestMethod.GET)
    @ResponseBody
    public String runScanByGitHubURL(@RequestParam("gitURL") String gitURL, @RequestParam("branch") String branch) throws GitAPIException, MavenInvocationException {
        //Git git=GitAPI.gitClone(gitURL,"/home/deshani/Documents/newFolder",branch);

        MavenClient.buildDependencyCheck("/home/deshani/Documents/newFolder/pom.xml");
        return "success";
    }

}

