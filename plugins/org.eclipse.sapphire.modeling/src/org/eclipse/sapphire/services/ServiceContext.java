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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.internal.SapphireModelingExtensionSystem;
import org.eclipse.sapphire.util.ReadOnlyListFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class ServiceContext
{
    public static final String ID_ELEMENT_INSTANCE = "Sapphire.Element.Instance";
    public static final String ID_ELEMENT_METAMODEL = "Sapphire.Element.MetaModel";
    public static final String ID_PROPERTY_INSTANCE = "Sapphire.Property.Instance";
    public static final String ID_PROPERTY_METAMODEL = "Sapphire.Property.MetaModel";
    
    private final String type;
    private final ServiceContext parent;
    private final Map<Class<? extends Service>,List<Service>> services = new HashMap<Class<? extends Service>,List<Service>>();
    private boolean disposed = false;
    
    public ServiceContext( final String type,
                           final ServiceContext parent )
    {
        this.type = type;
        this.parent = parent;
    }

    public final String type()
    {
        return this.type;
    }
    
    public final ServiceContext parent()
    {
        return this.parent;
    }
    
    public <T> T find( final Class<T> type )
    {
        return null;
    }
    
    public final <S extends Service> S service( final Class<S> serviceType )
    {
        if( this.disposed )
        {
            throw new IllegalStateException();
        }
        
        final List<S> services = services( serviceType );
        return ( services.isEmpty() ? null : services.get( 0 ) );
    }

    @SuppressWarnings( "unchecked" )
    
    public final synchronized <S extends Service> List<S> services( final Class<S> serviceType )
    {
        if( this.disposed )
        {
            throw new IllegalStateException();
        }
        
        List<Service> services = this.services.get( serviceType );
        
        if( services == null )
        {
            // Find all applicable service factories declared via the extension system.
            
            final Map<String,ServiceFactoryProxy> applicable = new HashMap<String,ServiceFactoryProxy>();

            for( ServiceFactoryProxy factory : SapphireModelingExtensionSystem.getServiceFactories() )
            {
                if( factory.applicable( this, serviceType ) )
                {
                    applicable.put( factory.id(), factory );
                }
            }
            
            // Remove those that are overridden by another applicable service. Note that a cycle will 
            // cause all services in the cycle to be removed.
            
            for( ServiceFactoryProxy factory : new ArrayList<ServiceFactoryProxy>( applicable.values() ) )
            {
                for( String overriddenServiceId : factory.overrides() )
                {
                    applicable.remove( overriddenServiceId );
                }
            }
            
            // Process local service definitions.
            
            final ReadOnlyListFactory<Service> servicesListFactory = ReadOnlyListFactory.create();
            
            for( ServiceFactoryProxy factory : local() )
            {
                if( factory.applicable( this, serviceType ) )
                {
                    Service service = null;
                    
                    try
                    {
                        service = factory.create( this, serviceType );
                        service.init( this, factory.parameters() );
                    }
                    catch( Exception e )
                    {
                        LoggingService.log( e );
                    }
                    
                    if( service != null )
                    {
                        for( String overriddenServiceId : factory.overrides() )
                        {
                            applicable.remove( overriddenServiceId );
                        }
                        
                        servicesListFactory.add( service );
                    }
                }
            }
            
            // Instantiate global services that haven't been overridden.
            
            for( ServiceFactoryProxy factory : applicable.values() )
            {
                Service service = null;
                
                try
                {
                    service = factory.create( this, serviceType );
                    service.init( this, factory.parameters() );
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                }

                if( service != null )
                {
                    servicesListFactory.add( service );
                }
            }
            
            // Store the list of services for future use.
            
            services = servicesListFactory.export();
            this.services.put( serviceType, services );
            
            // Initialize services. This happens last because initialization can cause a reentrant call
            // to this method, so we need to ensure that instantiated services are stored prior to initialization.
            // Note the implication of this is that it is possible to access a service prior to it being
            // initialized. It is up to service implementation to handle this condition gracefully.
            
            for( Service service : services )
            {
                try
                {
                    service.init();
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                }
            }
        }
        
        if( services.isEmpty() && this.parent != null )
        {
            services = (List<Service>) this.parent.services( serviceType );
        }
        
        return (List<S>) services;
    }

    protected List<ServiceFactoryProxy> local()
    {
        return Collections.emptyList();
    }
    
    public final void dispose()
    {
        this.disposed = true;
        
        for( List<Service> services : this.services.values() )
        {
            for( Service service : services )
            {
                try
                {
                    service.dispose();
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                }
            }
        }
        
        this.services.clear();
    }
    
}
