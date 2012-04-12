/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.sapphire.modeling.LoggingService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ListenerContext
{
    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    private final Map<Class<? extends Event>,Event> suspended = new HashMap<Class<? extends Event>,Event>();
    
    public void attach( final Listener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.listeners.add( listener );
    }
    
    public void detach( final Listener listener )
    {
        if( listener == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.listeners.remove( listener );
    }
    
    public void broadcast( final Event event )
    {
        final Class<? extends Event> eventType = event.getClass();
        final boolean broadcast;
        
        synchronized( this )
        {
            if( this.suspended.containsKey( eventType ) )
            {
                this.suspended.put( eventType, event );
                broadcast = false;
            }
            else
            {
                broadcast = true;
            }
        }
        
        if( broadcast )
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
    }
    
    public void broadcast()
    {
        broadcast( new Event() );
    }
    
    public void suspend( final Class<? extends Event> eventType )
    {
        synchronized( this )
        {
            if( ! this.suspended.containsKey( eventType ) )
            {
                this.suspended.put( eventType, null );
            }
        }
    }
    
    public void resume( final Class<? extends Event> eventType )
    {
        Event event = null;
        
        synchronized( this )
        {
            event = this.suspended.remove( eventType );
        }
        
        if( event != null )
        {
            broadcast( event );
        }
    }

}
