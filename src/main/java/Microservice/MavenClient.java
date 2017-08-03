package Microservice;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Arrays;
/**
 * Created by deshani on 8/2/17.
 */
public class MavenClient {

    public static void buildPom(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( pomFilePath ) );
        request.setGoals(  Arrays.asList( "clean", "install", "-DskipTests=true") );

        Invoker invoker = new DefaultInvoker();
        invoker.execute( request );
    }

    public static void buildDependencyCheck(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( pomFilePath ) );
        request.setGoals(  Arrays.asList( "org.owasp:dependency-check-maven:check") );

        Invoker invoker = new DefaultInvoker();
        invoker.execute( request );
    }

    public static void buildFindSecurityBugs(String pomFilePath) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( pomFilePath ) );
        request.setGoals(  Arrays.asList( "org.codehaus.mojo:findbugs-maven-plugin:com.h3xstream.findsecbugs:findsecbugs-plugin") );

        Invoker invoker = new DefaultInvoker();
        invoker.execute( request );
    }
}
