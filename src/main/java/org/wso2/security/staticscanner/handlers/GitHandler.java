package org.wso2.security.staticscanner.handlers;/*
*  Copyright (c) ${date}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.security.staticscanner.Constants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

public class GitHandler {

    private final static String GIT_REFS_HEADS_PATH = "refs/heads/";

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHandler.class);

    public static boolean startClone(String url, String branch, String tag) {
        Git git;
        if ((new File(Constants.DEFAULT_PRODUCT_PATH).exists()) || (new File(Constants.DEFAULT_PRODUCT_PATH).mkdir())) {
            git = gitClone(url, branch, Constants.DEFAULT_PRODUCT_PATH);
            if (git != null && tag != null) {
                gitCheckout(tag, git);
                System.out.println(hasAtLeastOneReference(git.getRepository()));
            }return hasAtLeastOneReference(git.getRepository());

        }
        return false;
    }

    private static Git gitClone(String gitURL, String branch, String filePath) {
        try {
            return Git.cloneRepository()
                    .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                    .setURI(gitURL)
                    .setDirectory(new File(filePath))
                    .setBranchesToClone(Collections.singleton(GIT_REFS_HEADS_PATH + branch))
                    .setBranch(GIT_REFS_HEADS_PATH + branch)
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    private static boolean hasAtLeastOneReference(Repository repo) {
        for (Ref ref : repo.getAllRefs().values()) {
            if (ref.getObjectId() == null)
                continue;
            return true;
        }
        return false;
    }

    private static Ref gitCheckout(String tag, Git git) {
        try {
            return git.checkout().setName(tag).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static String gitDescribe(Git git) throws GitAPIException {
        return git.describe().call();
    }

    public static Git gitOpen(String productPath) throws IOException {
        return Git.open(new File(productPath));
    }

}
