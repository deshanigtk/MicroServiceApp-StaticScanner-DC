package com.deshani;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by deshani on 8/7/17.
 */
public class XMLHandler {

    private static void iterateNode(Node node, Document document) throws TransformerException {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                if (currentNode.getNodeName().equals("build")) {
                    appendFindSecBugsPlugin(document, (Element) currentNode);
                    break;
                }
                iterateNode(currentNode, document);
            }
        }
    }

    private static void appendFindSecBugsPlugin(Document document, Element rootElement) throws TransformerException {

        //Get the <plugins> element that available under <build> element
        Element pluginsElement = (Element) rootElement.getElementsByTagName("plugins").item(0);


        //Create new element <plugin>
        Element pluginElement = document.createElement("plugin");

        //Create <groupId>org.codehaus.mojo</groupId> under <plugin>
        Element groupIdElement = document.createElement("groupId");
        groupIdElement.appendChild(document.createTextNode("org.codehaus.mojo"));

        //Create <artifactId>findbugs-maven-plugin</artifactId> under <plugin>
        Element artifactIdElement = document.createElement("artifactId");
        artifactIdElement.appendChild(document.createTextNode("findbugs-maven-plugin"));

        //Create <version>3.0.1</version> under <plugin>
        Element versionElement = document.createElement("version");
        versionElement.appendChild(document.createTextNode("3.0.1"));

        //Create <configuration> under <plugin>
        Element configurationElement = document.createElement("configuration");
        pluginElement.appendChild(configurationElement);

        //Create <effort>Max</effort> under <configuration>
        Element effortElement = document.createElement("effort");
        effortElement.appendChild(document.createTextNode("Max"));

        //Create <threshold>Low</threshold> under <configuration>
        Element thresholdElement = document.createElement("threshold");
        thresholdElement.appendChild(document.createTextNode("Low"));

        //Create <failOnError>true</failOnError> under <configuration>
        Element failOnErrorElement = document.createElement("failOnError");
        failOnErrorElement.appendChild(document.createTextNode("true"));

        //Create <includeFilterFile>${session.executionRootDirectory}/findbugs-security-include.xml</includeFilterFile> under <configuration>
        Element includeFilterFileElement = document.createElement("includeFilterFile");
        includeFilterFileElement.appendChild(document.createTextNode("${session.executionRootDirectory}/findbugs-security-include.xml"));

        //Create <excludeFilterFile>${session.executionRootDirectory}/findbugs-security-exclude.xml</excludeFilterFile> under <configuration>
        Element excludeFilterFileElement = document.createElement("excludeFilterFile");
        excludeFilterFileElement.appendChild(document.createTextNode("${session.executionRootDirectory}/findbugs-security-exclude.xml"));

        //Create <plugins> under <configuration>
        Element pluginsElement2 = document.createElement("plugins");

        //Create <plugin> under <plugins>
        Element pluginElement2 = document.createElement("plugin");

        //Create <groupId>com.h3xstream.findsecbugs<groupId> under <plugin>
        Element groupIdElement2 = document.createElement("groupId");
        groupIdElement2.appendChild(document.createTextNode("com.h3xstream.findsecbugs"));

        //Create <artifactId>findsecbugs-plugin</artifactId> under <plugin>
        Element artifactIdElement2 = document.createElement("artifactId");
        artifactIdElement2.appendChild(document.createTextNode("findsecbugs-plugin"));

        //Create <version>LATEST</version> under <plugin>
        Element versionElement2 = document.createElement("version");
        versionElement2.appendChild(document.createTextNode("LATEST"));


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

        DOMSource source = new DOMSource(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult("/home/deshani/Documents/IS/product-is/pom.xml");
        transformer.transform(source, result);

    }

    public static void main(String[] args) {

        try {

            File file = new File("/home/deshani/Documents/IS/product-is/pom.xml");

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = dBuilder.parse(file);
            iterateNode(doc.getDocumentElement(), doc);

        } catch (Exception e)

        {
            System.out.println(e.getMessage());
        }

    }
}