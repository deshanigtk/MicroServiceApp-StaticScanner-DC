package com.deshani;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Created by deshani on 8/1/17.
 */


@Controller
@EnableAutoConfiguration
@RequestMapping("staticScanner/runScan")
public class UserAPI {

    @RequestMapping(value = "dependencyCheck/byGitURL", method = RequestMethod.GET)
    @ResponseBody
    public String runDependencyCheckByGitURL(@RequestParam("gitURL") String gitURL, @RequestParam("branch") String branch) throws GitAPIException, MavenInvocationException, IOException {
        MainController.runDependencyCheck(gitURL,branch,"/home/deshani/Documents/Product");
        return "success";
    }

    @RequestMapping(value = "findSecBugs/byGitURL", method = RequestMethod.GET)
    @ResponseBody
    public String runFindSecBugsByGitURL(@RequestParam("gitURL") String gitURL, @RequestParam("branch") String branch) throws GitAPIException, MavenInvocationException, IOException, ParserConfigurationException, SAXException, TransformerException {
        MainController.runFindSecBugs(gitURL,branch,"/home/deshani/Documents/Product");
        return "success";
    }

}

