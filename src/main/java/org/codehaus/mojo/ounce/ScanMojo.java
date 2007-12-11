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

package org.codehaus.mojo.ounce;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.mojo.ounce.core.OunceCore;
import org.codehaus.mojo.ounce.core.OunceCoreException;
import org.codehaus.mojo.ounce.utils.Utils;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;

/**
 * This mojo allows an on demand scan of an application and the optional publishing of the results.
 * 
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @goal scan
 * @phase package
 */
public class ScanMojo
    extends AbstractOunceMojo
{

    /**
     * The name of the application to scan as known by the automation server. Either the applicationName or
     * applicationFile must be populated.
     * 
     * @parameter expression="${ounce.applicationName}"
     */
    String applicationName;

    /**
     * The location of the application file to scan. Either the applicationName or applicationFile must be populated.
     * 
     * @parameter expression="${ounce.applicationFile}"
     */
    String applicationFile;

    /**
     * A name for the assessment.
     * 
     * @parameter expression="${project.name}-${project.version}"
     */
    String assessmentName;

    /**
     * A file name may be specified where to save the file otherwise the automation server will place the assessment in
     * a temporary location pending other operations. The temporary location will delete files upon startup and after a
     * configurable period of time.
     * 
     * @parameter expression="${ounce.assessmentOutput}"
     */
    String assessmentOutput;

    /**
     * Assigns a caller to the assessment. Caller is an arbitrary string and may or may not correspond with any actual
     * user of any system. Caller is written to ounceautos log file. In the future this functionality will aid in
     * auditing.
     * 
     * @parameter expression="${ounce.caller}"
     */
    String caller;

    /**
     * Automatically generate a report for this assessment following the completion of the scan.
     * 
     * The following report types are included in a default Ounce installation:
     * 
     * Findings Report Types:
     * 		Findings By CWE, 
     * 		Findings By API,
     * 		Findings By Classification,
     * 		Findings By File,
     * 		Findings By Type,
     * 		Findings By Bundle,
     * 		Findings,
     * SmartAudit Report Types:
     * 		OWASP Top Ten,
     * 		PCI Data Security Standard,
     * 		Ounce Software Security Profile,
     * 		OWASP Top Ten 2007
     * 
     * @parameter expression="${ounce.reportType}"
     */
    String reportType;
    
    /**
     * The output type to use for the report.
     * 
     * Output type may be html, zip, pdf-summary, pdf-detailed, pdf-comprehensive, or pdf-annotated.
     * 
     * @parameter expression="${ounce.reportOutputType}"
     */
    String reportOutputType;
    
    /**
     * The path to the output location for the report.
     * 
     * @parameter expression="${ounce.reportOutputLocation}"
     */
    String reportOutputLocation;

    /**
     * Automatically publish the assessment following the completion of the scan.
     * 
     * @parameter expression="${ounce.publish}" default-value="false"
     */
    boolean publish;

    /**
     * The location of the Ounce client installation directory if the Ounce client is not on the path.
     * 
     * @parameter expression="${ounce.installDir}"
     */
    String installDir;

    /**
     * If the mojo should wait until the scan is complete. If set to true the build will block until
     * the scan has finished.  This is useful if the scan is being performed from the report mojo as part 
     * of integration with the site target and the site is getting deployed.
     * 
     * @parameter expression="${ounce.wait}" default-value="false"
     */
    boolean waitForScan;

    // private List projects;
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        try
        {
            if ( StringUtils.isEmpty( applicationName ) && StringUtils.isEmpty( applicationFile ) )
            {
                throw new MojoExecutionException( "One of \'applicationName\' or \'applicationFile\' must be defined." );
            }

            OunceCore core = getCore();
            core.scan( applicationName, Utils.convertToPropertyPath( applicationFile, pathVariableMap ), assessmentName,
                       assessmentOutput, caller, reportType, reportOutputType, reportOutputLocation, publish, this.options, this.installDir, waitForScan,
                       getLog() );
        }
        catch ( ComponentLookupException e )
        {
            throw new MojoExecutionException( "Unable to lookup the core interface for hint: " + coreHint, e );
        }
        catch ( OunceCoreException e )
        {
            throw new MojoExecutionException( "Nested Ouncecore exception: " + e.getLocalizedMessage(), e );
        }

    }
}
