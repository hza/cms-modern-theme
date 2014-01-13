/**
 * Theme support plugin for Enonic CMS 4.7.4 and newer
 *
 * @author Henady Zakalusky ( henady.zakalusky@gmail.com )
 */
package com.enonic.cms.plugin.themes.modern;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.client.ClientFactory;
import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;
import com.enonic.cms.core.client.InternalClient;

public final class ThemeInterceptor
    extends HttpInterceptor
    implements InitializingBean
{
    private static final Logger LOG = Logger.getLogger( "modern-theme-plugin" );

    private static final ScriptEngineManager manager = new ScriptEngineManager();

    private static final ScriptEngine engine = manager.getEngineByName( "js" );

    public void afterPropertiesSet()
        throws Exception
    {
        final Client client = ClientFactory.getLocalClient();

        final InternalClient internalClient = InternalClient.class.cast( client );
        final Object service = internalClient.getService( Class.forName( "com.enonic.vertical.adminweb.SplashServlet" ) );

        final Field applicationContextField = service.getClass().getSuperclass().getSuperclass().getDeclaredField( "applicationContext" );
        applicationContextField.setAccessible( true );
        final ApplicationContext applicationContext = ApplicationContext.class.cast( applicationContextField.get( service ) );

        final Map<String, SimpleUrlHandlerMapping> mappings = applicationContext.getBeansOfType( SimpleUrlHandlerMapping.class );

        for ( final SimpleUrlHandlerMapping mapping : mappings.values() )
        {
            if ( mapping.getUrlMap().keySet().contains( "/admin/**" ) )
            {
                final Field interceptors = mapping.getClass().getSuperclass().getSuperclass().getDeclaredField( "interceptors" );
                interceptors.setAccessible( true );

                final List list = List.class.cast( interceptors.get( mapping ) );

                if ( list.isEmpty() )
                {
                    final Object adminHttpInterceptorInterceptor = applicationContext.getBean( "adminHttpInterceptorInterceptor" );

                    mapping.setInterceptors( new Object[]{adminHttpInterceptorInterceptor} );
                    mapping.initApplicationContext();

                    LOG.info( "installed adminHttpInterceptorInterceptor to resourceController" );
                }
            }
        }
    }

    /**
     * Executes before the actual resource being called.
     */
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        // try to use own resource if it exists in jar
        final InputStream inputStream = getClass().getResourceAsStream( "/resources" + request.getRequestURI() );

        if ( inputStream != null )
        {
            IOUtils.copy( inputStream, response.getOutputStream() );
            inputStream.close();
            return false;
        }

        // check of resource can be processed using javascript
        final boolean wrapped = response instanceof HookedResponse;

        if ( !wrapped && shouldProcess( request ) )
        {
            final HookedResponse hookedResponse = new HookedResponse( response );
            request.getRequestDispatcher( request.getRequestURI() ).forward( request, hookedResponse );
            return false;
        }

        return true;
    }

    /**
     * checks if script with name =  "/scripts" + request.getRequestURI() + ".js" exists.
     */
    private boolean shouldProcess( final HttpServletRequest request )
        throws IOException
    {
        final InputStream inputStream = getClass().getResourceAsStream( "/scripts" + request.getRequestURI() + ".js" );

        final boolean scriptExists = inputStream != null;

        if ( scriptExists )
        {
            inputStream.close();
        }

        return scriptExists;
    }

    /**
     * Executes after the actual resource being called.
     */
    public void postHandle( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        final boolean wrapped = response instanceof HookedResponse;

        if ( wrapped )
        {
            // get hooked text
            final HookedResponse hookedResponse = HookedResponse.class.cast( response );
            final String text = hookedResponse.getText();

            final InputStream inputStream = getClass().getResourceAsStream( "/scripts" + request.getRequestURI() + ".js" );

            // compile script
            engine.eval( new InputStreamReader( inputStream ) );

            // process using js function
            final Invocable invocable = Invocable.class.cast( engine );
            final Object processedText = invocable.invokeFunction( "handleTextResource", text );
            hookedResponse.write( processedText.toString() );

            inputStream.close();
        }
    }
}
