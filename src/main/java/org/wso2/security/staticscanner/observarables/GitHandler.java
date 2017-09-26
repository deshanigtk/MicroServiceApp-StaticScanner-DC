package org.wso2.security.staticscanner.observarables;/*
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
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.wso2.security.staticscanner.Constants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Observable;

public class GitHandler extends Observable implements Runnable {


    private String url;
    private String branch;
    private String tag;
    private final String GIT_REFS_HEADS_PATH = "refs/heads/";


    public GitHandler(String url, String branch, String tag) {
        this.url = url;
        this.branch = branch;
        this.tag = tag;
    }

    @Override
    public void run() {
        try {
            startClone(url, branch, tag);
            setChanged();
            notifyObservers(true);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    private boolean startClone(String url, String branch, String tag) throws GitAPIException, IOException {
        Git git;
        if (new File(Constants.DEFAULT_PRODUCT_PATH).exists()) {
            git = gitClone(url, branch, Constants.DEFAULT_PRODUCT_PATH);
            gitCheckout(tag, git);

        } else {
            if (new File(Constants.DEFAULT_PRODUCT_PATH).mkdir()) {
                git = gitClone(url, branch, Constants.DEFAULT_PRODUCT_PATH);
                gitCheckout(tag, git);
            } else {
                return false;

            }
        }

        return hasAtLeastOneReference(git.getRepository());
    }

    private Git gitClone(String gitURL, String branch, String filePath) throws GitAPIException {
        return Git.cloneRepository()
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .setURI(gitURL)
                .setDirectory(new File(filePath))
                .setBranchesToClone(Collections.singleton(GIT_REFS_HEADS_PATH + branch))
                .setBranch(GIT_REFS_HEADS_PATH + branch)
                .call();
    }

    private boolean hasAtLeastOneReference(Repository repo) {
        for (Ref ref : repo.getAllRefs().values()) {
            if (ref.getObjectId() == null)
                continue;
            return true;
        }

        return false;
    }

    private Ref gitCheckout(String tag, Git git) throws GitAPIException {
        return git.checkout().setName(tag).call();
    }

    public static String gitDescribe(Git git) throws GitAPIException {
        return git.describe().call();
    }

    public static Git gitOpen(String productPath) throws IOException {
        return Git.open(new File(productPath));
    }

}
