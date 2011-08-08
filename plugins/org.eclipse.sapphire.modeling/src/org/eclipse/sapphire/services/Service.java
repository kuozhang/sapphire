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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.sapphire.modeling.LoggingService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class Service
{
    private ServiceContext context;
    private Map<String,String> params;
    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    
    public final void init( final ServiceContext context,
                            final Map<String,String> params )
    {
        this.context = context;
        this.params = params;
        
        init();
    }
    
    protected void init()
    {
        
    }
    
    protected final ServiceContext context()
    {
        return this.context;
    }
    
    protected final <T> T context( final Class<T> type )
    {
        return this.context.find( type );
    }
    
    protected final String param( final String name )
    {
        return this.params.get( name );
    }
    
    public final void attach( final Listener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.listeners.add( listener );
    }
    
    public final void detach( final Listener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.listeners.remove( listener );
    }
    
    protected final void notify( final Event event )
    {
        for( Listener listener : this.listeners )
        {
            try
            {
                listener.handle( event );
            }
            catch( Exception e )
            {
                LoggingService.log( e );
            }
        }
    }
    
    public void dispose()
    {
    }
    
    public static class Event
    {
        private final Service service;
        
        public Event( final Service service )
        {
            this.service = service;
        }
        
        public Service service()
        {
            return this.service;
        }
    }
    
    public static abstract class Listener
    {
        public abstract void handle( Event event );
    }
    
}
