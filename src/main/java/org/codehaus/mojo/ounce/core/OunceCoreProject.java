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

public class OunceCoreProject
{
    String name;

    String projectRoot;

    List sourceRoots;

    String webRoot;

    String classPath;

    String jdkName;

    String packaging;

    String compilerOptions;
    
    Map ounceOptions;

    public OunceCoreProject( String theName, String theProjectRoot, List theSourceRoots, String theWebRoot,
                             String theClassPath, String theJdkName, String packaging,
                             String theCompilerOptions,Map theOunceOptions )
    {
        super();
        this.name = theName;
        this.projectRoot = theProjectRoot;
        this.sourceRoots = theSourceRoots;
        this.webRoot = theWebRoot;
        this.classPath = theClassPath;
        this.jdkName = theJdkName;
        this.packaging = packaging;
        this.compilerOptions = theCompilerOptions;
        this.ounceOptions = theOunceOptions;
    }

    /**
     * @return the name
     */
    public String getName ()
    {
        return this.name;
    }

    /**
     * @return the projectRoot
     */
    public String getProjectRoot ()
    {
        return this.projectRoot;
    }

    /**
     * @return the sourceRoots
     */
    public List getSourceRoots ()
    {
        return this.sourceRoots;
    }

    /**
     * @return the webRoot
     */
    public String getWebRoot ()
    {
        return this.webRoot;
    }

    /**
     * @return the classPath
     */
    public String getClassPath ()
    {
        return this.classPath;
    }

    /**
     * @return the jdkName
     */
    public String getJdkName ()
    {
        return this.jdkName;
    }

    /**
     * @return the packaging
     */
    public String getPackaging ()
    {
        return this.packaging;
    }

    /**
     * @return the compilerOptions
     */
    public String getCompilerOptions ()
    {
        return this.compilerOptions;
    }

    /**
     * @return the ounceOptions
     */
    public Map getOunceOptions ()
    {
        return this.ounceOptions;
    }
}
