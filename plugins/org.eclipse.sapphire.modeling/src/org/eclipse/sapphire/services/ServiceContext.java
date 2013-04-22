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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.sapphire.ListenerContext;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.internal.SapphireModelingExtensionSystem;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class ServiceContext
{
    public static final String ID_ROOT = "Sapphire";
    public static final String ID_ELEMENT_INSTANCE = "Sapphire.Element.Instance";
    public static final String ID_ELEMENT_METAMODEL = "Sapphire.Element.MetaModel";
    public static final String ID_PROPERTY_INSTANCE = "Sapphire.Property.Instance";
    public static final String ID_PROPERTY_METAMODEL = "Sapphire.Property.MetaModel";
    
    private final String type;
    private final ServiceContext parent;
    private final List<Service> services = new ArrayList<Service>();
    private final Set<Class<?>> initializingServiceTypes = new HashSet<Class<?>>();
    private final Set<Class<?>> initializedServiceTypes = new HashSet<Class<?>>();
    private final Set<String> exhaustedServiceFactories = new HashSet<String>();
    private ListenerContext coordinatingListenerContext;
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
    
    public final <S extends Service> S service( final Class<S> type )
    {
        if( type == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( this.disposed )
        {
            throw new IllegalStateException();
        }
        
        final List<S> services = services( type );
        return ( services.isEmpty() ? null : services.get( 0 ) );
    }

    @SuppressWarnings( "unchecked" )
    
    public final synchronized <S extends Service> List<S> services( final Class<S> type )
    {
        if( type == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( this.disposed )
        {
            throw new IllegalStateException();
        }
        
        if( ! this.initializedServiceTypes.contains( type ) )
        {
            if( this.initializingServiceTypes.contains( type ) )
            {
                throw new IllegalStateException();
            }
            
            this.initializingServiceTypes.add( type );
            
            final Class<? super S> serviceTypeSuperClass = type.getSuperclass();
            
            if( serviceTypeSuperClass == Service.class || serviceTypeSuperClass == DataService.class )
            {
                // Find all applicable service factories declared via the extension system.
                
                final Map<String,ServiceFactoryProxy> applicable = new HashMap<String,ServiceFactoryProxy>();
    
                for( ServiceFactoryProxy factory : SapphireModelingExtensionSystem.getServiceFactories() )
                {
                    if( factory.applicable( this, type ) )
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
                
                for( ServiceFactoryProxy factory : local() )
                {
                    final String id = factory.id();
                    
                    if( ! this.exhaustedServiceFactories.contains( id ) && factory.applicable( this, type ) )
                    {
                        this.exhaustedServiceFactories.add( id );
                        
                        final Service service = factory.create( this, type );
                        
                        if( service != null )
                        {
                            service.init( this, id, factory.parameters(), factory.overrides() );
                            
                            if( this.coordinatingListenerContext != null )
                            {
                                service.coordinate( this.coordinatingListenerContext );
                            }
                            
                            for( String overriddenServiceId : factory.overrides() )
                            {
                                applicable.remove( overriddenServiceId );
                            }
                            
                            this.services.add( service );
                        }
                    }
                }
                
                // Instantiate global services that haven't been overridden.
                
                for( ServiceFactoryProxy factory : applicable.values() )
                {
                    final String id = factory.id();
                    
                    if( ! this.exhaustedServiceFactories.contains( id ) )
                    {
                        this.exhaustedServiceFactories.add( id );
                        
                        final Service service = factory.create( this, type );
                        
                        if( service != null )
                        {
                            service.init( this, id, factory.parameters(), factory.overrides() );
                            
                            if( this.coordinatingListenerContext != null )
                            {
                                service.coordinate( this.coordinatingListenerContext );
                            }
                            
                            this.services.add( service );
                        }
                    }
                }
            }
            else
            {
                services( (Class<Service>) serviceTypeSuperClass );
            }
            
            this.initializingServiceTypes.remove( type );
            this.initializedServiceTypes.add( type );
        }
        
        final Set<String> matchedServiceOverrides = new HashSet<String>();
        final ListFactory<S> matchedServicesListFactory = ListFactory.start();
        
        for( Service service : this.services )
        {
            if( type.isInstance( service ) )
            {
                matchedServiceOverrides.add( service.id() );
                matchedServiceOverrides.addAll( service.overrides() );
                matchedServicesListFactory.add( type.cast( service ) );
            }
        }
        
        for( int i = 0, n = matchedServicesListFactory.size(); i < n; i++ )
        {
            matchedServicesListFactory.get( i ).initIfNecessary();
        }
        
        if( this.parent != null )
        {
            for( Service service : this.parent.services( type ) )
            {
                if( ! matchedServiceOverrides.contains( service.id() ) )
                {
                    matchedServicesListFactory.add( type.cast( service ) );
                }
            }
        }
        
        return matchedServicesListFactory.result();
    }
    
    public final void coordinate( final ListenerContext context )
    {
        this.coordinatingListenerContext = context;
    }

    protected List<ServiceFactoryProxy> local()
    {
        return Collections.emptyList();
    }
    
    public final void dispose()
    {
        this.disposed = true;
        
        for( Service service : this.services )
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
        
        this.services.clear();
    }
    
}
