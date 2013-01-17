/******************************************************************************
 * Copyright (c) 2013 Oracle and Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Rob Cernich - [360369] Parameters not passed through to service
 ******************************************************************************/

package org.eclipse.sapphire.services.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactoryProxy;
import org.eclipse.sapphire.util.MapFactory;
import org.eclipse.sapphire.util.SetFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:rcernich@redhat.com">Rob Cernich</a>
 */

public abstract class AnnotationsAwareServiceContext extends ServiceContext
{
    public AnnotationsAwareServiceContext( final String type,
                                           final ServiceContext parent )
    {
        super( type, parent );
    }
    
    @Override
    protected final List<ServiceFactoryProxy> local()
    {
        final List<ServiceFactoryProxy> local = new ArrayList<ServiceFactoryProxy>();
        
        final List<org.eclipse.sapphire.modeling.annotations.Service> serviceAnnotations 
            = new ArrayList<org.eclipse.sapphire.modeling.annotations.Service>();
        
        serviceAnnotations.addAll( annotations( org.eclipse.sapphire.modeling.annotations.Service.class ) );

        for( Services servicesAnnotation : annotations( Services.class ) )
        {
            for( org.eclipse.sapphire.modeling.annotations.Service svc : servicesAnnotation.value() )
            {
                serviceAnnotations.add( svc );
            }
        }
        
        final boolean isInstanceContext = type().endsWith( ".Instance" );
        
        for( org.eclipse.sapphire.modeling.annotations.Service svc : serviceAnnotations )
        {
            if( isInstanceContext && svc.context() == org.eclipse.sapphire.modeling.annotations.Service.Context.INSTANCE )
            {
                final Class<? extends Service> cl = svc.impl();
                
                final SetFactory<String> overridesSetFactory = SetFactory.start();
                final MapFactory<String,String> paramsMapFactory = MapFactory.start();
                
                for( String override : svc.overrides() )
                {
                    overridesSetFactory.add( override );
                }
                
                for( org.eclipse.sapphire.modeling.annotations.Service.Param param : svc.params() )
                {
                    paramsMapFactory.add( param.name(), param.value() );
                }
                
                final Set<String> overrides = overridesSetFactory.result();
                final Map<String,String> params = paramsMapFactory.result();
                
                final ServiceFactoryProxy proxy = new ServiceFactoryProxy()
                {
                    @Override
                    public String id()
                    {
                        return cl.getName();
                    }
                    
                    @Override
                    public Class<? extends Service> type()
                    {
                        return cl;
                    }
    
                    @Override
                    public Set<String> overrides()
                    {
                        return overrides;
                    }
    
                    @Override
                    public Map<String,String> parameters()
                    {
                        return params;
                    }
    
                    @Override
                    protected Service createHandOff( final ServiceContext context,
                                                     final Class<? extends Service> service )
                    {
                        Service instance = null;
                        
                        try
                        {
                            instance = cl.newInstance();
                        }
                        catch( Exception e )
                        {
                            LoggingService.log( e );
                        }
                        
                        return instance;
                    }
                };
                
                local.add( proxy );
            }
        }
        
        return local;
    }
    
    protected abstract <A extends Annotation> List<A> annotations( Class<A> type );
    
}
