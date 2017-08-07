package com.deshani;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by deshani on 8/7/17.
 */
public class XMLHandler {

    private static void iterateNode(Node node, Document document) {
        System.out.println();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                if (currentNode.getNodeName().equals("plugins")) {
                    appendFindSecBugsPlugin(document, currentNode.getNodeName());
                    break;
                }
                iterateNode(currentNode, document);
            }
        }
    }

    private static void appendFindSecBugsPlugin(Document document, String rootElementName) {
        NodeList nodeList = document.getElementsByTagName(rootElementName);
        Node node = nodeList.item(0);
        System.out.println(node.getNodeName());
//        Element buildElement = document.createElement("build");
//        node.appendChild(buildElement);
//
//        Element pluginsElement = document.createElement("plugins");
//        buildElement.appendChild(pluginsElement);

        Element pluginElement = document.createElement("plugin");
        node.appendChild(pluginElement);

        Element groupIdElement = document.createElement("groupId");
        groupIdElement.appendChild(document.createTextNode("org.codehaus.mojo"));
        pluginElement.appendChild(groupIdElement);

        Element artifactIdElement = document.createElement("artifactId");
        artifactIdElement.appendChild(document.createTextNode("findbugs-maven-plugin"));
        pluginElement.appendChild(artifactIdElement);

        Element versionElement = document.createElement("version");
        versionElement.appendChild(document.createTextNode("3.0.1"));

        Element configurationElement = document.createElement("configuration");
        pluginElement.appendChild(configurationElement);

        Element effortElement = document.createElement("effort");
        effortElement.appendChild(document.createTextNode("Max"));
        configurationElement.appendChild(effortElement);

        Element thresholdElement = document.createElement("threshold");
        thresholdElement.appendChild(document.createTextNode("Low"));
        configurationElement.appendChild(thresholdElement);

        Element failOnErrorElement = document.createElement("failOnError");
        failOnErrorElement.appendChild(document.createTextNode("true"));
        configurationElement.appendChild(failOnErrorElement);

        Element includeFilterFileElement = document.createElement("includeFilterFile");
        includeFilterFileElement.appendChild(document.createTextNode("${session.executionRootDirectory}/findbugs-security-include.xml"));
        configurationElement.appendChild(includeFilterFileElement);

        Element excludeFilterFileElement = document.createElement("excludeFilterFile");
        excludeFilterFileElement.appendChild(document.createTextNode("${session.executionRootDirectory}/findbugs-security-exclude.xml"));
        configurationElement.appendChild(excludeFilterFileElement);

        Element pluginsElement2 = document.createElement("plugins");
        configurationElement.appendChild(pluginsElement2);

        Element pluginElement2 = document.createElement("plugin");
        pluginsElement2.appendChild(pluginElement2);

        Element groupIdElement2 = document.createElement("groupId");
        groupIdElement2.appendChild(document.createTextNode("com.h3xstream.findsecbugs"));
        pluginElement2.appendChild(groupIdElement2);

        Element artifactIdElement2 = document.createElement("artifactId");
        artifactIdElement2.appendChild(document.createTextNode("findsecbugs-plugin"));
        pluginElement2.appendChild(artifactIdElement2);

        Element versionElement2 = document.createElement("version");
        versionElement2.appendChild(document.createTextNode("LATEST"));
        pluginElement2.appendChild(versionElement2);

    }

    public static void main(String[] args) {

        try {

            File file = new File("/home/deshani/Documents/IS/product-is/pom.xml");

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = dBuilder.parse(file);
            System.out.println(doc.getDocumentElement().getNodeName());
            iterateNode(doc.getDocumentElement(),doc);


        } catch (Exception e)

        {
            System.out.println(e.getMessage());
        }

    }
}