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
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Created by deshani on 8/1/17.
 */


@Controller
@EnableAutoConfiguration
@RequestMapping("staticScanner/")
public class UserAPI {

    private static String productPath = "/home/deshani/Documents/Product";

    @RequestMapping(value = "dependencyCheck", method = RequestMethod.GET)
    @ResponseBody
    public String runDependencyCheck() throws GitAPIException, MavenInvocationException, IOException {
        if (new File(productPath).exists()) {
            MainController.runDependencyCheck(productPath);
            if (new File(productPath + Constant.DEPENDENCY_CHECK_REPORTS_FOLDER + Constant.ZIP_FILE_EXTENSION).exists()) {
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
        if (new File(productPath).exists()) {
            MainController.runFindSecBugs(productPath);
            if (new File(productPath + Constant.FIND_SEC_BUGS_REPORTS_FOLDER + Constant.ZIP_FILE_EXTENSION).exists()) {
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
        return MainController.gitClone(url, branch, productPath, replaceExisting);

    }

    @RequestMapping(value = "uploadProductZipFile", method = RequestMethod.GET)
    @ResponseBody
    public boolean uploadProductZipFile(@RequestParam("zipFile") String zipFile, @RequestParam("replaceExisting") boolean replaceExisting) throws GitAPIException, IOException {
        MainController.uploadProductZipFile(zipFile, productPath, replaceExisting);
        return new File(productPath).exists() && !new File(zipFile).exists();
    }

}

