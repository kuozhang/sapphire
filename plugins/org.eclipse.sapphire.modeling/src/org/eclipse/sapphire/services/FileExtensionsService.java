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

package org.eclipse.sapphire.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.modeling.LoggingService;

/**
 * Produces the list of file extensions that are allowed for a path value property. Most frequently specified
 * via @FileExtensions annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class FileExtensionsService extends Service
{
    private List<String> extensions = Collections.emptyList();
    
    @Override
    protected final void init()
    {
        initFileExtensionsService();
        refresh( false );
    }

    protected void initFileExtensionsService()
    {
    }
    
    public final List<String> extensions()
    {
        return this.extensions;
    }
    
    protected abstract void compute( List<String> extensions );
    
    protected final void refresh()
    {
        refresh( true );
    }
    
    private final void refresh( final boolean notifyListeners )
    {
        List<String> extensions = new ArrayList<String>();
        
        try
        {
            compute( extensions );
        }
        catch( Exception e )
        {
            LoggingService.log( e );
        }
        
        extensions = scrub( extensions );
        
        if( ! this.extensions.equals( extensions ) )
        {
            this.extensions = extensions;
            
            if( notifyListeners )
            {
                broadcast();
            }
        }
    }
    
    private static List<String> scrub( final List<String> extensions )
    {
        final List<String> clean = new ArrayList<String>();
        
        for( String extension : extensions )
        {
            if( extension != null )
            {
                extension = extension.trim();
                
                if( extension.length() > 0 )
                {
                    clean.add( extension );
                }
            }
        }
        
        return Collections.unmodifiableList( clean );
    }
    
}
