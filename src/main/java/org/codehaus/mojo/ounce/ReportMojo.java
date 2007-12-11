package org.codehaus.mojo.ounce;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;

/**
 * Generate the scan results as part of the site.
 * 
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @goal report
 * @phase site
 */
public class ReportMojo
    extends ScanMojo
    implements MavenReport
{
    /**
     * Directory where reports will go.
     * 
     * @parameter expression="${project.reporting.outputDirectory}/ounce"
     * @required
     * @readonly
     */
    private File reportOutputDirectory;

    /**
     * Existing assessment file to generate a report for.  
     * If specified the report will be run on an existing assessment rather than a new scan.
     * 
     * @parameter expression="${ounce.existingAssessmentFile}"
     */
    String existingAssessmentFile;
    
    /**
     * Number of lines of source code to include in the report before each finding.
     * 
     * @parameter expression="${ounce.includeSrcBefore}"
     */
    int includeSrcBefore = -1;
    
    /**
     * Number of lines of source code to include in the report after each finding.
     * 
     * @parameter expression="${ounce.includeSrcAfter}"
     */
    int includeSrcAfter = -1;
    
    /**
     * The current Project.
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * For internal use only.
     * 
     * @component
     * @required
     * @readonly
     */
    private Renderer siteRenderer;

    protected Renderer getSiteRenderer()
    {
        return siteRenderer;
    }

    public String getDescription( Locale locale )
    {
        return getBundle( locale ).getString( "report.description" ); 
    }

    public String getName( Locale locale )
    {
        return getBundle( locale ).getString( "report.name" ); 
    }

    public String getOutputName()
    {
       return "Ounce-Analysis/index";
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#isExternalReport()
     */
    public boolean isExternalReport()
    {
        return true;
    }
    
    private ResourceBundle getBundle( Locale locale )
    {
        return ResourceBundle.getBundle( "ounce-report", locale, this.getClass().getClassLoader() );
    }

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
    	System.out.println("execute");
    	this.getLog().warn( "Generating..." );
        
        if (existingAssessmentFile != null || includeSrcAfter != -1 || includeSrcBefore != -1) {
        	if (options == null) {
        		options = new HashMap();
        	}
        	options.put("existingAssessmentFile", existingAssessmentFile);
        	options.put("includeSrcAfter", new Integer(includeSrcAfter));
        	options.put("includeSrcBefore", new Integer(includeSrcBefore));
        }

        super.execute();
    }

    public boolean canGenerateReport()
    {
        return true;
    }

    public void generate( Sink sink, Locale locale )
        throws MavenReportException
    {
        this.getLog().warn( "Generating..." );
        this.waitForScan = true;
        if (this.reportType == null) {
        	this.reportType = "Findings";
        }
        if (this.reportOutputType == null) {
            this.reportOutputType = "html";
        }
        this.reportOutputLocation = reportOutputDirectory+File.separator+getOutputName()+".html";

        try
        {
            super.execute();
        }
        catch ( MojoExecutionException e )
        {
            throw new MavenReportException("Execption generating report:",e);
        }
        catch ( MojoFailureException e )
        {
            throw new MavenReportException("Execption generating report:",e);
        }
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getCategoryName()
     */
    public String getCategoryName()
    {
        return CATEGORY_PROJECT_REPORTS;
    }


    /**
     * @see org.apache.maven.reporting.MavenReport#getReportOutputDirectory()
     */
    public File getReportOutputDirectory()
    {
        return this.reportOutputDirectory;
    }
    
    /**
     * @see org.apache.maven.reporting.MavenReport#setReportOutputDirectory()
     */
    public void setReportOutputDirectory( File outputDirectory )
    {
        reportOutputDirectory = outputDirectory;    
    }
}
