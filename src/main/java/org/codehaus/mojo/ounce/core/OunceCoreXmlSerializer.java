/*
 * Copyright (c) 2007, Ounce Labs, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY OUNCE LABS, INC. ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OUNCE LABS, INC. BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.codehaus.mojo.ounce.core;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.plugin.logging.Log;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.codehaus.mojo.ounce.ProjectOnlyMojo;
import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:sam.headrick@ouncelabs.com">Sam Headrick</a>
 * @plexus.component role="org.codehaus.mojo.ounce.core.OunceCore" role-hint="ouncexml"
 */
public class OunceCoreXmlSerializer implements OunceCore {

	public final static String s_ounceComment = "";

	public void createApplication(String baseDir, String theName, String applicationRoot, List theProjects, Map options, Log log) throws OunceCoreException {
        // sort them to avoid implementation details messing
        // up the order for testing.
        Collections.sort( theProjects );
        
        log.info("OunceCoreXmlSerializer: Writing Application parameters to xml.");

        try {
	        // need to read the Application in first if it exists
	        // create the XML Document
	        Document xmlDoc;
	        Element root = null;
	        String filePath = theName + ".paf";
	        File pafFile = new File(filePath);
	        if (pafFile.exists()) {
	        	// load up the PAF
	        	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        	log.info("Reading paf: '" + filePath + "'...");
	        	xmlDoc = builder.parse(pafFile);
	        	
	        	NodeList nodes = xmlDoc.getChildNodes();
	        	for (int i = 0; i < nodes.getLength(); i++) {
	        		Node node = nodes.item(i);
	        		String name = node.getNodeName();
	        		
	        		if (name.equals("Application")) {
	        			root = (Element) node;
	        			NodeList applicationChildren = node.getChildNodes();
	        			for (int j = 0; j < applicationChildren.getLength(); j++) {
	        				Node child = applicationChildren.item(j);
	        				String childName = child.getNodeName();
	        				// don't preserve Projects, everything else should be left alone
	        				if (childName.equals("Project")) {
	        					node.removeChild(child);
	        				}
	        			}
	        		}
	        	}
	        } else {
	        	log.info("Creating new paf: '" + filePath + "'...");
	        	xmlDoc = new DocumentImpl();
		        Comment ounceComment = xmlDoc.createComment(s_ounceComment);
		        xmlDoc.appendChild(ounceComment);
		        root = xmlDoc.createElement("Application");
		        root.setAttribute("name", theName);
	        	xmlDoc.appendChild(root);
	        }
	        
	        for (int i = 0; i < theProjects.size(); i++) {
	        	OunceProjectBean projectBean = (OunceProjectBean) theProjects.get(i);
	        	String projectPath = projectBean.getPath() + File.separator + projectBean.name + ".ppf";
	        	Element project = xmlDoc.createElementNS(null, "Project");
	        	project.setAttributeNS(null, "path", projectPath);
	        	project.setAttributeNS(null, "language_type", "2");
	        	root.appendChild(project);
	        }
	        
	        // write out the XML
        	FileOutputStream fos = new FileOutputStream(filePath);
        	
        	OutputFormat of = new OutputFormat("XML", "UTF-8", true);
        	of.setIndent(1);
        	of.setIndenting(true);
        	
        	XMLSerializer serializer = new XMLSerializer(fos, of);
        	serializer.asDOMSerializer();
        	serializer.serialize(xmlDoc.getDocumentElement());
        	fos.close();        	
        } catch (Exception ex) {
        	log.error(ex);
        }
	}

	public void createProject(String baseDir, String theName, String projectRoot, List theSourceRoots, String theWebRoot, String theClassPath, String theJdkName, String compilerOptions, String packaging, Map options, Log log) throws OunceCoreException {
       

        log.info("OunceCoreXmlSerializer: Writing Project parameters to xml.");
        
        // place all of the Project properties into a property bundle
        Properties projectProperties = new Properties();

        // set the dynamic values
        projectProperties.setProperty("name", theName);
        
        // set the constant values
        projectProperties.setProperty("language_type", "2");
        projectProperties.setProperty("default_configuration_name", "Configuration 1");

        if (options != null) {
	        Set keys = options.keySet();
	        Iterator it = keys.iterator();
	        while (it.hasNext()) {
	        	String key = (String) it.next();
	        	String value = (String) options.get(key);
	        	projectProperties.setProperty(key, value);
	        }
        }

        if (theWebRoot != null && theWebRoot.trim().length() > 0) {
        	projectProperties.setProperty("web_context_root_path", theWebRoot.trim());
        }
        
    	try {
    		Document xmlDoc;
    		Element root = null;
        	String filePath = theName + ".ppf";
    		File ppfFile = new File(filePath);
    		if (ppfFile.exists()) {
    			// need to preserve information that could be in the ppf (Project validation routines, etc.)
        		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        		log.info("Reading ppf: '" + filePath + "'...");
    			xmlDoc = builder.parse(ppfFile);
    			NodeList nodes = xmlDoc.getChildNodes();
	    		for (int i = 0; i < nodes.getLength(); i++) {
	    			Node node = nodes.item(i);
	    			String name = node.getNodeName();
	    			// Project should be the only top-level node -- others can be ignored
	    			if (name.equals("Project")) {
	    				root = (Element) node;
	    				NodeList projectChildren = node.getChildNodes();
	    				for (int j = 0; j < projectChildren.getLength(); j++) {
	    					Node child = projectChildren.item(j);
	    					String childName = child.getNodeName();
	    					// don't preserve Configuration and Source, these should come fresh from Maven
	    					// everything else should be left alone
	    					if (childName.equals("Configuration") || childName.equals("Source")) {
	    						node.removeChild(child);
	    					}
	    				}
	    			}
	    		}
    		} else {
    			log.info("Creating new Document...");
    			// create a new XML Document
    			xmlDoc = new DocumentImpl();
    	        Comment ounceComment = xmlDoc.createComment(s_ounceComment);
    	        xmlDoc.appendChild(ounceComment);
    			root = xmlDoc.createElement("Project");
	        	xmlDoc.appendChild(root);
    		}
    		
	        // enumerate over the Project properties and set them as attributes on the Project node
	        Enumeration propertyNames = projectProperties.propertyNames();
	        while (propertyNames.hasMoreElements()) {
	        	Object propertyNameObject = propertyNames.nextElement();
	        	String name = (String) propertyNameObject;
	        	String value = projectProperties.getProperty(name);
	        	root.setAttribute(name, value);
	        }
	        
	        // place all of the Configuration properties into a property bundle
	        Properties configProperties = new Properties();
	        configProperties.setProperty("name", "Configuration 1");
	        configProperties.setProperty("class_path", theClassPath);
	        if (theJdkName != null && theJdkName.trim().length() > 0) {
	        	configProperties.setProperty("jdk_name", theJdkName.trim());
	        }
	        
	        // add the Configuration element to Project.  Java Projects always have exactly one Configuration
	        Element configuration = xmlDoc.createElementNS(null, "Configuration");
	
	        // give the Configuration all its attributes
	        propertyNames = configProperties.propertyNames();
	        while (propertyNames.hasMoreElements()) {
	        	Object propertyNameObject = propertyNames.nextElement();
	        	String name = (String) propertyNameObject;
	        	String value = configProperties.getProperty(name);
	        	configuration.setAttributeNS(null, name, value);
	        }
	        
	        // add Configuration to under the Project node
	        root.appendChild(configuration);
	        
	        for (int i = 0; i < theSourceRoots.size(); i++) {
	        	String sourceRoot = (String) theSourceRoots.get(i);
	        	
	        	Element source = xmlDoc.createElementNS(null, "Source");
	        	source.setAttributeNS(null, "path", "./" + sourceRoot);
	        	source.setAttributeNS(null, "exclude", "false");
	        	source.setAttributeNS(null, "web", "false");
	
	        	// add this Source under the Project node
	        	root.appendChild(source);
	        }

        	FileOutputStream fos = new FileOutputStream(filePath);
        	
        	OutputFormat of = new OutputFormat("XML", "UTF-8", true);
        	of.setIndent(1);
        	of.setIndenting(true);
        	
        	XMLSerializer serializer = new XMLSerializer(fos, of);
        	
        	serializer.asDOMSerializer();
        	serializer.serialize(xmlDoc.getDocumentElement());
        	fos.close();
        } catch (Exception ex) {
        	log.error(ex);
        }
	}

	public OunceCoreApplication readApplication(String path, Log log) throws OunceCoreException {
        try {
	        Document xmlDoc;
	        File pafFile = new File(path);
	        if (pafFile.exists()) {
	        	String parentDir = pafFile.getParent();
	        	String applicationName = null;
	        	String applicationRoot= null;
	        	List projects = new ArrayList();
	        	Map options = new HashMap();
	        	
	        	// load up the PAF
	        	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        	if (log != null) {
	        		log.info("Reading paf: '" + path + "'...");
	        	}
	        	xmlDoc = builder.parse(pafFile);
	        	
	        	NodeList nodes = xmlDoc.getChildNodes();
	        	for (int i = 0; i < nodes.getLength(); i++) {
	        		Node node = nodes.item(i);
	        		String name = node.getNodeName();
	        		
	        		if (name.equals("Application")) {
	        			NamedNodeMap applicationAttribs = node.getAttributes();
	        			applicationName = applicationAttribs.getNamedItem("name").getNodeValue();
	        			
	        			NodeList applicationChildren = node.getChildNodes();
	        			for (int j = 0; j < applicationChildren.getLength(); j++) {
	        				Node child = applicationChildren.item(j);
	        				String childName = child.getNodeName();
	        				if (childName.equals("Project")) {
	        					String projectPath = parentDir + File.separator + child.getAttributes().getNamedItem("path").getNodeValue();
	        					OunceCoreProject project = readProject(projectPath, log);
	        					projects.add(project);
	        				}
	        			}
	        		}
	        	}

	        	OunceCoreApplication application = new OunceCoreApplication(applicationName, applicationRoot, projects, options);
	        	return application;
	        }
        } catch (Exception ex) {
        	if (log != null) {
        		log.error(ex);
        	}
        }
		
		return null;
	}

	public OunceCoreProject readProject(String path, Log log) throws OunceCoreException {
		
    	try {
    		Document xmlDoc;
    		File ppfFile = new File(path);
    		if (ppfFile.exists()) {
    			String projectRoot = ppfFile.getParent();
    			String projectName = null;
    			String jdkName = null;
    			String classPath = null;
    			String webRoot = null;
	    		String optionsStr = null;
    			List sourceRoots = new ArrayList();
    			Map options = new HashMap();
    			
        		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        		if (log != null) {
        			log.info("Reading ppf: '" + path + "'...");
        		}
    			xmlDoc = builder.parse(ppfFile);
    			NodeList nodes = xmlDoc.getChildNodes();
	    		for (int i = 0; i < nodes.getLength(); i++) {
	    			Node node = nodes.item(i);
	    			String name = node.getNodeName();
	    			
	    			// Project should be the only top-level node -- others can be ignored
	    			if (name.equals("Project")) {
	    				NamedNodeMap projectAttribs = node.getAttributes();
	        			for (int j = 0; j <projectAttribs.getLength(); j++) {
	        				Node attribNode = projectAttribs.item(j);
	        				String nodeName = attribNode.getNodeName();
	        				String nodeValue = attribNode.getNodeValue();
	        				if (!nodeName.equals("name") && !nodeName.equals("web_context_root_path")) {
	        					options.put(nodeName, nodeValue);
	        				}
	        			}
	        			
	        			if (projectAttribs.getNamedItem("web_context_root_path") != null) {
	        				webRoot = projectAttribs.getNamedItem("web_context_root_path").getNodeValue();
	        			}
	        			
	        			projectName = projectAttribs.getNamedItem("name").getNodeValue();
	    				NodeList projectChildren = node.getChildNodes();
	    				for (int j = 0; j < projectChildren.getLength(); j++) {
	    					Node child = projectChildren.item(j);
	    					String childName = child.getNodeName();
	    					
    						NamedNodeMap attribs = child.getAttributes();
	    					
	    					if (childName.equals("Configuration")) {
	    						// attribs have name, jdk_name
	    						if (attribs.getNamedItem("jdk_name") != null) {
	    							jdkName = attribs.getNamedItem("jdk_name").getNodeValue();
	    						}
	    						if (attribs.getNamedItem("class_path") != null) {
	    							classPath = attribs.getNamedItem("class_path").getNodeValue();
	    						}
	    						if (attribs.getNamedItem("compiler_options") != null) {
	    							optionsStr = attribs.getNamedItem("compiler_options").getNodeValue();
	    						}
	    					} else if (childName.equals("Source")) {
	    						String sourcePath = attribs.getNamedItem("path").getNodeValue();
	    						String webStr = attribs.getNamedItem("web").getNodeValue();
	    					    if (webStr != null && webStr.equals("true")) {
	    					    	// TODO: need a way to maintain that this is a web source root   
	    						} 
	    					    sourceRoots.add(sourcePath);
	    					}
	    				}
	    			}
	    		}
	    		String packaging = null; // TODO -- what are the various packaging options?
	    		if (webRoot != null) {
	    			packaging = "";
	    		}
	    		
	    		OunceCoreProject project = new OunceCoreProject(projectName, projectRoot, sourceRoots, webRoot, classPath, jdkName, packaging, optionsStr, options);
	    		return project;
    		} else {
    			throw new OunceCoreException("The file '" + ppfFile.getPath() + "' does not exist.");
    		}
        } catch (Exception ex) {
        	if (log != null) {
        		log.error(ex);
        	}
        }
		
		return null;
	}

	public void scan(String applicationName, String applicationFile, String assessmentName, String assessmentOutput, String caller, String reportType, boolean publish, Map ounceOptions, String installDir, boolean wait, Log log) throws OunceCoreException {
		
	}
}
