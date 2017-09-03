package com.deshani;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by deshani on 8/1/17.
 */


@Controller
@EnableAutoConfiguration
@RequestMapping("staticScanner/")
public class UserAPI {

    @RequestMapping(value = "dependencyCheck", method = RequestMethod.GET)
    @ResponseBody
    public String runDependencyCheck() throws GitAPIException, MavenInvocationException, IOException {
        if (new File(MainController.getProductPath()).exists()) {
            MainController.runDependencyCheck();
            if (new File(MainController.getProductPath() + Constant.DEPENDENCY_CHECK_REPORTS_FOLDER + Constant.ZIP_FILE_EXTENSION).exists()) {
                return "Success";
            } else {
                return "Scan Failed";
            }
        }
        return "Product Not Found";
    }

    @RequestMapping(value = "findSecBugs", method = RequestMethod.GET)
    @ResponseBody
    public String runFindSecBugs() throws MavenInvocationException, IOException, ParserConfigurationException, SAXException, TransformerException, GitAPIException, URISyntaxException {
        if (new File(MainController.getProductPath()).exists()) {
            MainController.runFindSecBugs();
            if (new File(MainController.getProductPath() + Constant.FIND_SEC_BUGS_REPORTS_FOLDER + Constant.ZIP_FILE_EXTENSION).exists()) {
                return "Success";
            } else {
                return "Scan Failed";
            }
        }
        return "Product Not Found";
    }

    @RequestMapping(value = "cloneProduct", method = RequestMethod.GET)
    @ResponseBody
    public boolean cloneProduct(@RequestParam("gitUrl") String url, @RequestParam("branch") String branch, @RequestParam("replaceExisting") boolean replaceExisting) throws GitAPIException, IOException {
        return MainController.gitClone(url, branch, replaceExisting);

    }

    @RequestMapping(value = "uploadProductZipFile", method = RequestMethod.GET)
    @ResponseBody
    public boolean uploadProductZipFile(@RequestParam("zipFile") String zipFile, @RequestParam("replaceExisting") boolean replaceExisting) throws GitAPIException, IOException {
        MainController.uploadProductZipFile(zipFile, replaceExisting);
        return new File(MainController.getProductPath()).exists() && !new File(zipFile).exists();
    }

    @RequestMapping(value = "productPath", method = RequestMethod.GET)
    @ResponseBody
    public boolean config(@RequestParam("productPath") String productPath) {
        MainController.setProductPath(productPath);
        return MainController.getProductPath() != null;

    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return MainController.getProductPath();

    }

}

