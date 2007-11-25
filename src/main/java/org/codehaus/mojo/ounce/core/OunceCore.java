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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.PlexusContainer;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * 
 * This is the interface to define interaction between the
 * ounce-maven-plugin and the ounce labs core code.
 * 
 * Multiple implementations will be created for unit
 * testing, integration testing and runtime.
 */
public interface OunceCore
{
    String ROLE = OunceCore.class.getName();

    public PlexusContainer container = null;

    /**
     * Create an Ounce Application
     * 
     * @param baseDir path of current project (where the
     *            file should be created)
     * @param name Name of the application.
     * @param applicationRoot Base folder used to calculate
     *            the relative location of the projects.
     * @param projects List of paths to included projects.
     * @param preserveApp If set, then the application files
     *            are not overwritten.
     * @param storeFullPaths If true, all paths are
     *            absolute.
     * @param ounceOptions Extra options
     * @param log Logger
     * @throws OunceCoreException
     */
    void createApplication ( String baseDir, String name, String applicationRoot, List projects, Map ounceOptions,
                             Log log )
        throws OunceCoreException;

    OunceCoreApplication readApplication ( String path, Log log )
        throws OunceCoreException;

    /**
     * Create an Ounce Project
     * 
     * @param baseDir path of current project (where the
     *            file should be created)
     * @param name Name of the project.
     * @param projectRoot Base folder used to calculate the
     *            relative location of the source roots.
     * @param sourceRoots List of source roots with includes
     *            and excludes (bean TBD)
     * @param webRoot Location of the exploded war (null if
     *            not a war)
     * @param classPath Classpath needed for compilation
     * @param jdkName Name of JDK configuration known to
     *            Ounce Core
     * @param storeFullPaths If true, all paths are
     *            absolute.
     * @param compilerOptions Bean containing compiler
     *            options (based on maven-compiler-plugin
     *            options)
     * @param packaging The packaging type of the current
     *            project.
     * @param ounceOptions Extra Ounce options
     * @param log Logger
     * @throws OunceCoreException
     */
    void createProject ( String baseDir, String name, String projectRoot, List sourceRoots, String webRoot,
                         String classPath, String jdkName, OunceCoreCompilerOptions compilerOptions, String packaging,
                         Set includes, Set excludes, Map ounceOptions, Log log )
        throws OunceCoreException;

    OunceCoreProject readProject ( String path, Log log )
        throws OunceCoreException;

    /**
     * 
     * Initiate a scan on the assessment server.
     * 
     * @param applicationName the name of the application to
     *            scan. Must be known to the Automation
     *            Server
     * @param applicationFile the application file to scan
     *            (if name is not used)
     * @param assessmentName A name for the assessment.
     * @param assessmentOutput The location to store the
     *            assessment results.
     * @param caller A name to use for auditing purposes.
     * @param reportType store the reports in this format.
     * @param publish automatically publish the results.
     * @param ounceOptions Extra Ounce options
     * @param installDir location of ounce client
     * @param wait if the client should wait for the scan to complete before returning.
     * @param log
     * @throws OunceCoreException
     */
    void scan ( String applicationName, String applicationFile, String assessmentName, String assessmentOutput,
                String caller, String reportType, boolean publish, Map ounceOptions, String installDir, boolean wait, Log log )
        throws OunceCoreException;

}
