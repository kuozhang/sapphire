/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.expr;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class Expression<T>
{
    private Object context;
    private T value;
    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    
    public final void init( final Object context,
                            final String[] params )
    {
        this.context = context;
        initExpression( context, params );
        refresh();
    }

    protected void initExpression( final Object context,
                                   final String[] params )
    {
    }
    
    protected final Object context()
    {
        return this.context;
    }
    
    protected abstract T evaluate();
    
    public final T value()
    {
        return this.value;
    }

    protected final void refresh()
    {
        final T newValue;
        
        try
        {
            newValue = evaluate();
        }
        catch( Exception e )
        {
            SapphireModelingFrameworkPlugin.log( e );
            return;
        }
        
        if( ! equal( this.value, newValue ) )
        {
            this.value = newValue;
            notifyListeners();
        }
    }
    
    protected boolean equal( final T a,
                             final T b )
    {
        if( a == b )
        {
            return true;
        }
        else if( a == null || b == null )
        {
            return false;
        }
        else
        {
            return a.equals( b );
        }
    }
    
    public final void addListener( final Listener listener )
    {
        this.listeners.add( listener );
    }
    
    public final void removeListener( final Listener listener )
    {
        this.listeners.remove( listener );
    }
    
    private final void notifyListeners()
    {
        for( Listener listener : this.listeners )
        {
            try
            {
                listener.handleValueChanged();
            }
            catch( Exception e )
            {
                SapphireModelingFrameworkPlugin.log( e );
            }
        }
    }
    
    public void dispose()
    {
    }

    public static abstract class Listener
    {
        public abstract void handleValueChanged();
    }
    
}
