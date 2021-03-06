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
package org.codehaus.mojo.ounce.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.plexus.components.io.fileselectors.FileInfo;

/**
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 */
public class ProjectFileInfo
    implements FileInfo
{

    private File theFile;

    public ProjectFileInfo( File theFile )
    {
        super();
        this.theFile = theFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.components.io.fileselectors.FileInfo#getContents()
     */
    public InputStream getContents()
        throws IOException
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.components.io.fileselectors.FileInfo#getName()
     */
    public String getName()
    {
        // the selector utils has odd behavior. I must strip
        // off the leading separator char to match
        // the default includes etc. This causes problems on
        // unix os's.
        String name = theFile.getAbsolutePath().replace( '/', File.separatorChar ).replace( '\\', File.separatorChar );
        if ( name.startsWith( File.separator ) )
        {
            name = name.substring( 1 );
        }
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.components.io.fileselectors.FileInfo#isDirectory()
     */
    public boolean isDirectory()
    {
        return theFile.isDirectory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.components.io.fileselectors.FileInfo#isFile()
     */
    public boolean isFile()
    {
        return !isDirectory();
    }

}
