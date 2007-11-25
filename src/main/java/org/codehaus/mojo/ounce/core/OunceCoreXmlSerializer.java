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
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author <a href="mailto:sam.headrick@ouncelabs.com">Sam Headrick</a>
 * @plexus.component role="org.codehaus.mojo.ounce.core.OunceCore"
 *                   role-hint="ouncexml"
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
        	
        	// TODO-NOW: is this the right encoding?
        	OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
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

	public void createProject(String baseDir, String theName, String projectRoot, List theSourceRoots, String theWebRoot, String theClassPath, String theJdkName, OunceCoreCompilerOptions compilerOptions, String packaging, Set includes, Set excludes, Map options, Log log) throws OunceCoreException {
        if ( StringUtils.isNotEmpty( theClassPath ) )
        {
            // the classpath order can change subtly from
            // maven. Lets sort it alphabetically since we
            // really
            // only care about the contents for testing the
            // plugin
            String[] classp = theClassPath.split( ";" );
            Arrays.sort( classp );

            StringBuffer sb = new StringBuffer();
            if ( classp.length > 0 )
            {
                // first one, no separator needed
                sb.append( classp[0] );

                // separate the rest of them with
                // pathSeparator
                for ( int i = 1; i < classp.length; i++ )
                {
                    sb.append( ProjectOnlyMojo.PATH_SEPARATOR );
                    sb.append( classp[i] );
                }
                theClassPath = sb.toString();
            }
        }

        log.info("OunceCoreXmlSerializer: Writing Project parameters to xml.");
        
        // place all of the Project properties into a property bundle
        Properties projectProperties = new Properties();

        // set the dynamic values
        projectProperties.setProperty("name", theName);
        
        // set the constant values
        // TODO-NOW: determine the proper default for all these attributes.  Also, do any need to be configurable from within Maven?
        projectProperties.setProperty("language_type", "2");
        projectProperties.setProperty("default_configuration_name", "Configuration 1");
        projectProperties.setProperty("cma_memory_limit", "100");
        projectProperties.setProperty("cma_compute_limit", "50");
        projectProperties.setProperty("perform_cma", "true");
        projectProperties.setProperty("cma_behavior", "default");
        projectProperties.setProperty("jsp_compiler_type", "0");
        projectProperties.setProperty("analyze_struts_framework", "false");
        projectProperties.setProperty("import_struts_validation", "false");
        
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

        	// TODO-NOW: this is not going to the correct directory
        	FileOutputStream fos = new FileOutputStream(filePath);
        	
        	// TODO-NOW: is this the right encoding?
        	OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
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
		// TODO Auto-generated method stub
		return null;
	}

	public OunceCoreProject readProject(String path, Log log) throws OunceCoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void report(Log log) throws OunceCoreException {
		log.info("Report...");
	}

	public void scan(Log log) throws OunceCoreException {
		log.info("Scan...");
	}

    /* (non-Javadoc)
     * @see org.codehaus.mojo.ounce.core.OunceCore#scan(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.util.Map, org.apache.maven.plugin.logging.Log)
     */
    public void scan ( String theApplicationName, String theApplicationFile, String theAssessmentName,
                       String theAssessmentOutput, String theCaller, String theReportType, boolean thePublish,
                       Map theOunceOptions, String installDir, Log theLog )
        throws OunceCoreException
    {
        // TODO Auto-generated method stub
        
    }
}
