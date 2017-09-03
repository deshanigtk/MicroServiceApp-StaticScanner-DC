package com.deshani;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;
import java.util.OptionalInt;

/**
 * Created by deshani on 8/2/17.
 */

class MavenClient {

    static void buildDependencyCheck(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomFilePath));
        request.setGoals(Collections.singletonList(Constant.MVN_COMMAND_DEPENDENCY_CHECK));

        Invoker invoker = new DefaultInvoker();
        // invoker.setMavenHome(new File(System.getenv(Constant.MVN_COMMAND_M2_HOME)));
        InvocationResult result = invoker.execute(request);
        OptionalInt.of(result.getExitCode());
    }

    static void compile(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomFilePath));
        request.setGoals(Collections.singletonList(Constant.MVN_COMMAND_COMPILE));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv(Constant.MVN_COMMAND_M2_HOME)));
        invoker.execute(request);
    }

    static void buildFindSecBugs(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pomFilePath));
        request.setGoals(Collections.singletonList(Constant.MVN_COMMAND_FIND_SEC_BUGS));

        Invoker invoker = new DefaultInvoker();

        invoker.setMavenHome(new File(System.getenv(Constant.MVN_COMMAND_M2_HOME)));
        invoker.execute(request);
    }
}
