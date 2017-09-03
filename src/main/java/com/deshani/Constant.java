package com.deshani;


final class Constant {

    //Dependency check related
    static final String DEPENDENCY_CHECK_REPORTS_FOLDER = "Dependency-Check-Reports";
    static final String DEPENDENCY_CHECK_REPORT = "dependency-check-report.html";

    //FindSecBugs related
    static final String FIND_SEC_BUGS_REPORTS_FOLDER = "Find-Sec-Bugs-Reports";
    static final String FIND_BUGS_REPORT = "findbugsXml.xml";
    static final String FINDBUGS_SECURITY_INCLUDE = "findbugs-security-include.xml";
    static final String FINDBUGS_SECURITY_EXCLUDE = "findbugs-security-exclude.xml";

    //FindSecBugs plugin related
    static final String BUILD_ELEMENT = "build";
    static final String PLUGINS_ELEMENT = "plugins";
    static final String PLUGIN_ELEMENT = "plugin";
    static final String GROUP_ID_ELEMENT = "groupId";
    static final String GROUP_ID_TEXT = "org.codehaus.mojo";
    static final String ARTIFACT_ID_ELEMENT = "artifactId";
    static final String ARTIFACT_ID_TEXT = "findbugs-maven-plugin";
    static final String VERSION_ELEMENT = "version";
    static final String VERSION_TEXT = "3.0.1";
    static final String CONFIGURATION_ELEMENT = "configuration";
    static final String EFFORT_ELEMENT = "effort";
    static final String EFFORT_TEXT = "Max";
    static final String THRESHOLD_ELEMENT = "threshold";
    static final String THRESHOLD_TEXT = "Low";
    static final String FAIL_ON_ERROR_ELEMENT = "failOnError";
    static final String FAIL_ON_ERROR_TEXT = "true";
    static final String INCLUDE_FILTER_FILE_ELEMENT = "includeFilterFile";
    static final String INCLUDE_FILTER_FILE_TEXT = "${session.executionRootDirectory}/findbugs-security-include.xml";
    static final String EXCLUDE_FILTER_FILE_ELEMENT = "excludeFilterFile";
    static final String EXCLUDE_FILTER_FILE_TEXT = "${session.executionRootDirectory}/findbugs-security-exclude.xml";
    static final String GROUP_ID_TEXT_2 = "com.h3xstream.findsecbugs";
    static final String ARTIFACT_ID_TEXT_2 = "findsecbugs-plugin";
    static final String VERSION_TEXT_2 = "LATEST";

    //FindSecBugs include, exclude files related
    static final String FIND_BUGS_FILTER_ELEMENT = "FindBugsFilter";
    static final String MATCH_ELEMENT = "match";
    static final String BUG_ELEMENT = "Bug";
    static final String CATEGORY_ATTRIBUTE = "category";
    static final String CATEGORY_ATTRIBUTE_VALUE = "SECURITY";

    //Files
    static final String ZIP_FILE_EXTENSION = ".zip";
    static final String POM_FILE = "pom.xml";

    //Symbols
    static final String UNDERSCORE = "_";
    static final String NULL_STRING = "";

    //Maven Commands
    static final String MVN_COMMAND_DEPENDENCY_CHECK = "org.owasp:dependency-check-maven:check";
    static final String MVN_COMMAND_FIND_SEC_BUGS = "findbugs:findbugs";
    static final String MVN_COMMAND_COMPILE = "compile";
    static final String MVN_COMMAND_M2_HOME = "M2_HOME";

    //Git client
    static final String GIT_REFS_HEADS_PATH = "refs/heads/";

    static final String DEFAULT_PRODUCT_PATH = "/home/deshani/Documents/Product";

}
