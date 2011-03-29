/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class MiscUtil
{
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$
    
    public static final boolean equal( final Object obj1, 
                                       final Object obj2 )
    {
        if( obj1 == obj2 )
        {
            return true;
        }
        else if( obj1 != null && obj2 != null )
        {
            return obj1.equals( obj2 );
        }

        return false;
    }
    

    public static boolean contains( final Object[] array,
                                    final Object object )
    {
        for( int i = 0; i < array.length; i++ )
        {
            if( array[ i ].equals( object ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public static int indexOf( final Object[] array,
                               final Object object )
    {
        for( int i = 0; i < array.length; i++ )
        {
            if( array[ i ].equals( object ) )
            {
                return i;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    public static String readTextContent( final Reader reader ) 
    
        throws IOException
        
    {
        final StringBuffer buf = new StringBuffer();
        final char[] chars = new char[ 8 * 1024 ];
        int count;

        while( ( count = reader.read( chars, 0, chars.length ) ) > 0 ) 
        {
            buf.append( chars, 0, count );
        }
        
        return buf.toString();
    }
    
    public static String readTextContent( final InputStream in ) 
    
        throws IOException
        
    {
        return readTextContent( new InputStreamReader( in ) );
    }
    
    public static String readTextResource( final ClassLoader cl,
                                           final String resourceFullPath )
    {
        final InputStream in = cl.getResourceAsStream( resourceFullPath );
        
        try
        {
            return readTextContent( in );
        }
        catch( IOException e )
        {
            SapphireModelingFrameworkPlugin.log( e );
            return "";
        }
        finally
        {
            try
            {
                in.close();
            }
            catch( IOException e ) {}
        }
    }
    
    public static String readTextResource( final Class<?> c,
                                           final String resourceLocalName )
    {
        final ClassLoader cl = c.getClassLoader();
        final String resourcePath = c.getName().replace( '.', '/' ) + "." + resourceLocalName;
        
        return readTextResource( cl, resourcePath );
    }
    
    public final static String collapseWhitespace( final String str ) 
    {
        String text = str.trim();
        
        final StringBuilder buf = new StringBuilder();
        boolean skipNextWhitespace = true;
        
        for( int i = 0, n = text.length(); i < n; i++ )
        {
            final char ch = text.charAt( i );
            
            if( Character.isWhitespace( ch ) )
            {
                if( ! skipNextWhitespace )
                {
                    buf.append( ' ' );
                    skipNextWhitespace = true;
                }
            }
            else
            {
                buf.append( ch );
                skipNextWhitespace = false;
            }
        }
        
        final int length = buf.length();
        
        if( length > 0 && buf.charAt( length - 1 ) == ' ' )
        {
            buf.deleteCharAt( length - 1 );
        }
        
        return buf.toString();
    }
    
}
