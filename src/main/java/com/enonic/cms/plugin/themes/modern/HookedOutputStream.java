/**
 * Theme support plugin for Enonic CMS 4.7.4 and newer
 *
 * @author Henady Zakalusky ( henady.zakalusky@gmail.com )
 */
package com.enonic.cms.plugin.themes.modern;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

class HookedOutputStream
    extends ServletOutputStream
{
    private ByteArrayOutputStream outputStream;

    public HookedOutputStream( final ByteArrayOutputStream outputStream )
    {
        this.outputStream = outputStream;
    }

    @Override
    public void write( final int b )
        throws IOException
    {
        outputStream.write( b );
    }

    @Override
    public void write( final byte[] b )
        throws IOException
    {
        outputStream.write( b );
    }

    @Override
    public void write( final byte[] b, final int off, final int len )
        throws IOException
    {
        outputStream.write( b, off, len );
    }


}
