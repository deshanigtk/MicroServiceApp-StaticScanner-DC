package com.deshani;

import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Created by deshani on 8/7/17.
 */
public class XMLHandler {

    public static Document iterateNode(Node node, Document document) throws TransformerException {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                if (currentNode.getNodeName().equals(Constant.BUILD_ELEMENT)) {
                    appendFindSecBugsPlugin(document, (Element) currentNode);
                    break;
                }
                iterateNode(currentNode, document);
            }
        }
        return document;
    }

    private static Document appendFindSecBugsPlugin(Document document, Element rootElement) throws TransformerException {

        //Get the <plugins> element that available under <build> element
        Element pluginsElement = (Element) rootElement.getElementsByTagName(Constant.PLUGINS_ELEMENT).item(0);

        //Create new element <plugin>
        Element pluginElement = document.createElement(Constant.PLUGIN_ELEMENT);

        //Create <groupId>org.codehaus.mojo</groupId> under <plugin>
        Element groupIdElement = document.createElement(Constant.GROUP_ID_ELEMENT);
        groupIdElement.appendChild(document.createTextNode(Constant.GROUP_ID_TEXT));

        //Create <artifactId>findbugs-maven-plugin</artifactId> under <plugin>
        Element artifactIdElement = document.createElement(Constant.ARTIFACT_ID_ELEMENT);
        artifactIdElement.appendChild(document.createTextNode(Constant.ARTIFACT_ID_TEXT));

        //Create <version>3.0.1</version> under <plugin>
        Element versionElement = document.createElement(Constant.VERSION_ELEMENT);
        versionElement.appendChild(document.createTextNode(Constant.VERSION_TEXT));

        //Create <configuration> under <plugin>
        Element configurationElement = document.createElement(Constant.CONFIGURATION_ELEMENT);
        pluginElement.appendChild(configurationElement);

        //Create <effort>Max</effort> under <configuration>
        Element effortElement = document.createElement(Constant.EFFORT_ELEMENT);
        effortElement.appendChild(document.createTextNode(Constant.EFFORT_TEXT));

        //Create <threshold>Low</threshold> under <configuration>
        Element thresholdElement = document.createElement(Constant.THRESHOLD_ELEMENT);
        thresholdElement.appendChild(document.createTextNode(Constant.THRESHOLD_TEXT));

        //Create <failOnError>true</failOnError> under <configuration>
        Element failOnErrorElement = document.createElement(Constant.FAIL_ON_ERROR_ELEMENT);
        failOnErrorElement.appendChild(document.createTextNode(Constant.FAIL_ON_ERROR_TEXT));

        //Create <includeFilterFile>${session.executionRootDirectory}/findbugs-security-include.xml</includeFilterFile> under <configuration>
        Element includeFilterFileElement = document.createElement(Constant.INCLUDE_FILTER_FILE_ELEMENT);
        includeFilterFileElement.appendChild(document.createTextNode(Constant.INCLUDE_FILTER_FILE_TEXT));

        //Create <excludeFilterFile>${session.executionRootDirectory}/findbugs-security-exclude.xml</excludeFilterFile> under <configuration>
        Element excludeFilterFileElement = document.createElement(Constant.EXCLUDE_FILTER_FILE_ELEMENT);
        excludeFilterFileElement.appendChild(document.createTextNode(Constant.EXCLUDE_FILTER_FILE_TEXT));

        //Create <plugins> under <configuration>
        Element pluginsElement2 = document.createElement(Constant.PLUGINS_ELEMENT);

        //Create <plugin> under <plugins>
        Element pluginElement2 = document.createElement(Constant.PLUGIN_ELEMENT);

        //Create <groupId>com.h3xstream.findsecbugs<groupId> under <plugin>
        Element groupIdElement2 = document.createElement(Constant.GROUP_ID_ELEMENT);
        groupIdElement2.appendChild(document.createTextNode(Constant.GROUP_ID_TEXT_2));

        //Create <artifactId>findsecbugs-plugin</artifactId> under <plugin>
        Element artifactIdElement2 = document.createElement(Constant.ARTIFACT_ID_ELEMENT);
        artifactIdElement2.appendChild(document.createTextNode(Constant.ARTIFACT_ID_TEXT_2));

        //Create <version>LATEST</version> under <plugin>
        Element versionElement2 = document.createElement(Constant.VERSION_ELEMENT);
        versionElement2.appendChild(document.createTextNode(Constant.VERSION_TEXT_2));


        pluginElement2.appendChild(groupIdElement2);
        pluginElement2.appendChild(artifactIdElement2);
        pluginElement2.appendChild(versionElement2);

        pluginsElement2.appendChild(pluginElement2);

        configurationElement.appendChild(effortElement);
        configurationElement.appendChild(thresholdElement);
        configurationElement.appendChild(failOnErrorElement);
        configurationElement.appendChild(includeFilterFileElement);
        configurationElement.appendChild(excludeFilterFileElement);
        configurationElement.appendChild(pluginsElement2);

        pluginElement.appendChild(groupIdElement);
        pluginElement.appendChild(artifactIdElement);
        pluginElement.appendChild(versionElement);
        pluginElement.appendChild(configurationElement);

        pluginsElement.appendChild(pluginElement);

        return document;

    }

    public static void writeFindSecBugsExcludeFile(Document document) throws ParserConfigurationException {

        // create the root element
        Element rootEle = document.createElement(Constant.FIND_BUGS_FILTER_ELEMENT);
        document.appendChild(rootEle);

    }

    public static void writeFindSecBugsIncludeFile(Document document) throws ParserConfigurationException {

        Element rootEle = document.createElement(Constant.FIND_BUGS_FILTER_ELEMENT);

        Element matchElement = document.createElement(Constant.MATCH_ELEMENT);

        Element bugElement = document.createElement(Constant.BUG_ELEMENT);
        bugElement.setAttribute(Constant.CATEGORY_ATTRIBUTE, Constant.CATEGORY_ATTRIBUTE_VALUE);

        matchElement.appendChild(bugElement);
        rootEle.appendChild(matchElement);
        document.appendChild(rootEle);
    }

}