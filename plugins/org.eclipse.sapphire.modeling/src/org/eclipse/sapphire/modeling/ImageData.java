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

package org.eclipse.sapphire.modeling;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ImageData extends BinaryData
{
    public ImageData( final InputStream stream ) throws IOException
    {
        super( stream );
    }
    
    public ImageData( final byte[] data )
    {
        super( data );
    }
    
    public static ImageData readFromUrl( final URL url )
    {
        InputStream stream = null;
        
        try
        {
            stream = url.openStream();
            return new ImageData( stream );
        }
        catch( IOException e )
        {
            throw new IllegalArgumentException( e );
        }
        finally
        {
            if( stream != null )
            {
                try
                {
                    stream.close();
                }
                catch( Exception e ) {}
            }
        }
    }
    
    public static ImageData readFromBundle( final String path )
    {
        final int slash = path.indexOf( '/' );
        final String bundleId = path.substring( 0, slash );
        final String relPath = path.substring( slash + 1 );
        
        return readFromBundle( bundleId, relPath );
    }
    
    public static ImageData readFromBundle( final String bundleId,
                                            final String path )
    {
        
        final Bundle bundle = Platform.getBundle( bundleId );
        
        if( bundle == null )
        {
            throw new IllegalArgumentException( NLS.bind( Resources.couldNotLoadImageBadBundle, bundleId ) );
        }
        else
        {
            return readFromBundle( bundle, path );
        }
    }
    
    public static ImageData readFromBundle( final Bundle bundle,
                                            final String path )
    {
        final URL url = FileLocator.find( bundle, new Path( path ), null );
        
        if( url == null )
        {
            throw new IllegalArgumentException( NLS.bind( Resources.couldNotLoadImageBadPath, path ) );
        }
        else
        {
            try
            {
                final InputStream stream = url.openStream();
                
                try
                {
                    return new ImageData( stream );
                }
                finally
                {
                    try
                    {
                        stream.close();
                    }
                    catch( IOException e ) {}
                }
            }
            catch( IOException e )
            {
                throw new IllegalArgumentException( e );
            }
        }
    }
    
    public static ImageData createFromUrl( final URL url )
    {
        InputStream stream = null;
        
        try
        {
            stream = url.openStream();
            return new ImageData( stream );
        }
        catch( IOException e )
        {
            return null;
        }
        finally
        {
            if( stream != null )
            {
                try
                {
                    stream.close();
                }
                catch( Exception e ) {}
            }
        }
    }
    
    public static ImageData createFromBundle( final String path )
    {
        final int slash = path.indexOf( '/' );
        final String bundleId = path.substring( 0, slash );
        final String relPath = path.substring( slash + 1 );
        
        return readFromBundle( bundleId, relPath );
    }
    
    public static ImageData createFromBundle( final String bundleId,
                                              final String path )
    {
        
        final Bundle bundle = Platform.getBundle( bundleId );
        
        if( bundle == null )
        {
            return null;
        }
        else
        {
            return createFromBundle( bundle, path );
        }
    }
    
    public static ImageData createFromBundle( final Bundle bundle,
                                              final String path )
    {
        final URL url = FileLocator.find( bundle, new Path( path ), null );
        
        if( url == null )
        {
            return null;
        }
        else
        {
            try
            {
                final InputStream stream = url.openStream();
                
                try
                {
                    return new ImageData( stream );
                }
                finally
                {
                    try
                    {
                        stream.close();
                    }
                    catch( IOException e ) {}
                }
            }
            catch( IOException e )
            {
                return null;
            }
        }
    }

    private static final class Resources extends NLS
    {
        public static String couldNotLoadImageBadBundle;
        public static String couldNotLoadImageBadPath;
        
        static
        {
            initializeMessages( ImageData.class.getName(), Resources.class );
        }
    }

}
