/**
 * Theme support plugin for Enonic CMS 4.7.4 and newer
 *
 * @author Henady Zakalusky ( henady.zakalusky@gmail.com )
 */
package com.enonic.cms.plugin.themes.modern;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

class HookedResponse
    extends HttpServletResponseWrapper
{
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    private ServletOutputStream outputStream = new HookedOutputStream(byteArrayOutputStream);

    private HttpServletResponse response;

    public HookedResponse( final HttpServletResponse response )
        throws IOException
    {
        super( response );
        this.response = response;
    }

    @Override
    public ServletOutputStream getOutputStream()
        throws IOException
    {
        return outputStream;
    }

    public String getText()
    {
        return byteArrayOutputStream.toString();
    }

    public void write( final String text )
        throws IOException
    {
        response.getWriter().write( text.toCharArray() );
    }
}
