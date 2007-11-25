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

import org.codehaus.mojo.ounce.utils.Utils;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * 
 * This is a bean for passing the compiler options as
 * supported by the maven-compiler-plugin
 * 
 */
public class OunceCoreCompilerOptions
{

    /**
     * Set to true to include debugging information in the
     * compiled class files.
     * 
     * @parameter expression="${maven.compiler.debug}"
     *            default-value="true"
     */
    private boolean debug = true;

    /**
     * Set to true to show messages about what the compiler
     * is doing.
     * 
     * @parameter expression="${maven.compiler.verbose}"
     *            default-value="false"
     */
    private boolean verbose;

    /**
     * Sets whether to show source locations where
     * deprecated APIs are used.
     * 
     * @parameter expression="${maven.compiler.showDeprecation}"
     *            default-value="false"
     */
    private boolean showDeprecation;

    /**
     * Set to true to optimize the compiled code using the
     * compiler's optimization methods.
     * 
     * @parameter expression="${maven.compiler.optimize}"
     *            default-value="false"
     */
    private boolean optimize;

    /**
     * Set to true to show compilation warnings.
     * 
     * @parameter expression="${maven.compiler.showWarnings}"
     *            default-value="false"
     */
    private boolean showWarnings;

    /**
     * The -source argument for the Java compiler.
     * 
     * @parameter expression="${maven.compiler.source}"
     */
    private String source;

    /**
     * The -target argument for the Java compiler.
     * 
     * @parameter expression="${maven.compiler.target}"
     */
    private String target;

    /**
     * The -encoding argument for the Java compiler.
     * 
     * @parameter expression="${maven.compiler.encoding}"
     */
    private String encoding;

    /**
     * Initial size, in megabytes, of the memory allocation
     * pool, ex. "64", "64m" if fork is set to true.
     * 
     * @parameter expression="${maven.compiler.meminitial}"
     */
    private String meminitial;

    /**
     * Sets the maximum size, in megabytes, of the memory
     * allocation pool, ex. "128", "128m" if fork is set to
     * true.
     * 
     * @parameter expression="${maven.compiler.maxmem}"
     */
    private String maxmem;

    /**
     * @return the debug
     */
    public boolean isDebug ()
    {
        return this.debug;
    }

    /**
     * @param theDebug the debug to set
     */
    public void setDebug ( boolean theDebug )
    {
        this.debug = theDebug;
    }

    /**
     * @return the verbose
     */
    public boolean isVerbose ()
    {
        return this.verbose;
    }

    /**
     * @param theVerbose the verbose to set
     */
    public void setVerbose ( boolean theVerbose )
    {
        this.verbose = theVerbose;
    }

    /**
     * @return the showDeprecation
     */
    public boolean isShowDeprecation ()
    {
        return this.showDeprecation;
    }

    /**
     * @param theShowDeprecation the showDeprecation to set
     */
    public void setShowDeprecation ( boolean theShowDeprecation )
    {
        this.showDeprecation = theShowDeprecation;
    }

    /**
     * @return the optimize
     */
    public boolean isOptimize ()
    {
        return this.optimize;
    }

    /**
     * @param theOptimize the optimize to set
     */
    public void setOptimize ( boolean theOptimize )
    {
        this.optimize = theOptimize;
    }

    /**
     * @return the showWarnings
     */
    public boolean isShowWarnings ()
    {
        return this.showWarnings;
    }

    /**
     * @param theShowWarnings the showWarnings to set
     */
    public void setShowWarnings ( boolean theShowWarnings )
    {
        this.showWarnings = theShowWarnings;
    }

    /**
     * @return the source
     */
    public String getSource ()
    {
        return this.source;
    }

    /**
     * @param theSource the source to set
     */
    public void setSource ( String theSource )
    {
        this.source = theSource;
    }

    /**
     * @return the target
     */
    public String getTarget ()
    {
        return this.target;
    }

    /**
     * @param theTarget the target to set
     */
    public void setTarget ( String theTarget )
    {
        this.target = theTarget;
    }

    /**
     * @return the encoding
     */
    public String getEncoding ()
    {
        return this.encoding;
    }

    /**
     * @param theEncoding the encoding to set
     */
    public void setEncoding ( String theEncoding )
    {
        this.encoding = theEncoding;
    }

    /**
     * @return the meminitial
     */
    public String getMeminitial ()
    {
        return this.meminitial;
    }

    /**
     * @param theMeminitial the meminitial to set
     */
    public void setMeminitial ( String theMeminitial )
    {
        this.meminitial = theMeminitial;
    }

    /**
     * @return the maxmem
     */
    public String getMaxmem ()
    {
        return this.maxmem;
    }

    /**
     * @param theMaxmem the maxmem to set
     */
    public void setMaxmem ( String theMaxmem )
    {
        this.maxmem = theMaxmem;
    }

    public String toString ()
    {
        return Utils.getDynamicToString( this );
    }
}
