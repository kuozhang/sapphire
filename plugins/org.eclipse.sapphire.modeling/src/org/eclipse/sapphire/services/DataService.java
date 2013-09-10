/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.services;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.Text;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class DataService<T> extends Service
{
    private static final Object INITIAL_DATA = new Object();
    
    @Text( "Reentrant call detected during refresh in {0}." )
    private static LocalizableText reentrantRefreshMessage;
    
    @Text( "{0} data accessed prior to service initialization." )
    private static LocalizableText dataAccessedPriorToInitMessage;
    
    static
    {
        LocalizableText.init( DataService.class );
    }

    @SuppressWarnings( "unchecked" )
    private T data = (T) INITIAL_DATA;
    
    private boolean initialized;
    private boolean refreshing;
    
    @Override
    protected final void init()
    {
        initDataService();
        
        this.initialized = true;
    }

    protected void initDataService()
    {
    }
    
    public final T data()
    {
        if( ! this.initialized )
        {
            throw new IllegalStateException( dataAccessedPriorToInitMessage.format( getClass().getSimpleName() ) );
        }
        
        if( this.data == INITIAL_DATA )
        {
            refresh();
        }
        
        return this.data;
    }
    
    protected abstract T compute();
    
    protected final void refresh()
    {
        if( this.refreshing )
        {
            throw new IllegalStateException( reentrantRefreshMessage.format( getClass().getSimpleName() ) );
        }
        
        this.refreshing = true;
        
        boolean broadcast = false;
        
        try
        {
            final T newData = compute();
            
            if( this.data == INITIAL_DATA )
            {
                this.data = newData;
            }
            else if( ! equal( this.data, newData ) )
            {
                this.data = newData;
                
                broadcast = true;
            }
        }
        finally
        {
            this.refreshing = false;
        }
        
        if( broadcast )
        {
            broadcast();
        }
    }

}
