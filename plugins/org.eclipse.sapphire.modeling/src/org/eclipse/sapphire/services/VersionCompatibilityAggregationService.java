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

package org.eclipse.sapphire.services;

import java.util.List;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.services.VersionCompatibilityService.Data;

/**
 * Aggregates the data from all applicable version compatibility services in order to produce a single statement
 * about version compatibility.
 * 
 * <p>An implementation of this service is provided with Sapphire. This service is not intended to
 * be implemented by adopters.</p>
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class VersionCompatibilityAggregationService extends DataService<Data>
{
    private List<VersionCompatibilityService> services;
    private Listener listener;
    
    @Override
    protected final void initDataService()
    {
        this.services = context( IModelElement.class ).services( context( ModelProperty.class ), VersionCompatibilityService.class );
        
        this.listener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                refresh();
            }
        };
        
        for( VersionCompatibilityService service : this.services )
        {
            service.attach( this.listener );
        }
    }

    public final boolean compatible()
    {
        final Data data = data();
        return ( data == null ? false : data.compatible() );
    }
    
    public final Version version()
    {
        final Data data = data();
        return ( data == null ? null : data.version() );
    }
    
    public final String versioned()
    {
        final Data data = data();
        return ( data == null ? null : data.versioned() );
    }
    
    @Override
    protected Data compute()
    {
        VersionCompatibilityService.Data data = null;
        
        for( VersionCompatibilityService service : this.services )
        {
            data = service.data();
            
            if( service.compatible() == false )
            {
                break;
            }
        }
        
        return data;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.listener != null )
        {
            for( VersionCompatibilityService service : this.services )
            {
                service.detach( this.listener );
            }
        }
    }

}
