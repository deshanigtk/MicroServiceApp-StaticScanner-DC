package com.deshani;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;

/**
 * Created by deshani on 8/2/17.
 */

class MavenClient {

    static void buildPom(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomFilePath));
        request.setGoals(Arrays.asList("clean", "install", "-DskipTests=true"));

        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
    }

    static void buildDependencyCheck(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomFilePath));
        request.setGoals(Arrays.asList("org.owasp:dependency-check-maven:check"));

        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
    }

    static void compile(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomFilePath));
        request.setGoals(Arrays.asList("compile"));

        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
    }
    static void buildFindSecBugs(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomFilePath));
        request.setGoals(Arrays.asList("findbugs:findbugs"));

        Invoker invoker = new DefaultInvoker();
        invoker.execute(request);
    }
}
