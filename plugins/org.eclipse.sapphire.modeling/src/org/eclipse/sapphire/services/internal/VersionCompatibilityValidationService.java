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

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.services.VersionCompatibilityAggregationService;

/**
 * An abstract base class for implementations of ValidationService that produce a validation error when a property 
 * is not compatible with the version compatibility target yet it contains data. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class VersionCompatibilityValidationService extends ValidationService
{
    private VersionCompatibilityAggregationService versionCompatibilityService;
    private Listener versionCompatibilityServiceListener;
    
    @Override
    protected void init()
    {
        super.init();
        
        this.versionCompatibilityService = context( IModelElement.class ).service( context( ModelProperty.class ), VersionCompatibilityAggregationService.class );
        
        this.versionCompatibilityServiceListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                broadcast();
            }
        };
        
        this.versionCompatibilityService.attach( this.versionCompatibilityServiceListener );
    }

    @Override
    public Status validate()
    {
        if( ! this.versionCompatibilityService.compatible() && populated() )
        {
            final String message;
            
            final Version version = this.versionCompatibilityService.version();
            final String versioned = this.versionCompatibilityService.versioned();
            
            if( version == null )
            {
                message = Resources.versionConstraintTargetNotFoundMessage;
            }
            else
            {
                message = Resources.bind( Resources.notCompatibleWithVersionMessage, version.toString(), versioned );
            }
            
            return Status.createErrorStatus( message );
        }
        
        return Status.createOkStatus();
    }
    
    protected abstract boolean populated();
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.versionCompatibilityService != null )
        {
            this.versionCompatibilityService.detach( this.versionCompatibilityServiceListener );
        }
    }
    
    /**
     * Implementation of ValidationService that produces a validation error when a value property is not compatible 
     * with the version compatibility target yet it has a value.
     */
    
    public static final class ValuePropertyService extends VersionCompatibilityValidationService
    {
        @Override
        protected boolean populated()
        {
            return context( IModelElement.class ).read( context( ValueProperty.class ) ).getText( false ) != null;
        }
        
        public static final class Factory extends ServiceFactory
        {
            @Override
            public boolean applicable( final ServiceContext context,
                                       final Class<? extends Service> service )
            {
                final IModelElement element = context.find( IModelElement.class );
                final ValueProperty property = context.find( ValueProperty.class );
                
                return property != null && element.service( property, VersionCompatibilityAggregationService.class ) != null; 
            }

            @Override
            public Service create( final ServiceContext context,
                                   final Class<? extends Service> service )
            {
                return new ValuePropertyService();
            }
        }
    }
    
    /**
     * Implementation of ValidationService that produces a validation error when an element property is not compatible 
     * with the version compatibility target yet it contains an element.
     */

    public static final class ElementPropertyService extends VersionCompatibilityValidationService
    {
        @Override
        protected boolean populated()
        {
            return context( IModelElement.class ).read( context( ElementProperty.class ) ).element() != null;
        }
        
        public static final class Factory extends ServiceFactory
        {
            @Override
            public boolean applicable( final ServiceContext context,
                                       final Class<? extends Service> service )
            {
                final IModelElement element = context.find( IModelElement.class );
                final ElementProperty property = context.find( ElementProperty.class );
                
                return property != null && ! ( property instanceof ImpliedElementProperty ) && 
                       element.service( property, VersionCompatibilityAggregationService.class ) != null; 
            }

            @Override
            public Service create( final ServiceContext context,
                                   final Class<? extends Service> service )
            {
                return new ElementPropertyService();
            }
        }
    }
    
    /**
     * Implementation of ValidationService that produces a validation error when a list property is not compatible 
     * with the version compatibility target yet the list is not empty.
     */
    
    public static final class ListPropertyService extends VersionCompatibilityValidationService
    {
        @Override
        protected boolean populated()
        {
            return ! context( IModelElement.class ).read( context( ListProperty.class ) ).isEmpty();
        }
        
        public static final class Factory extends ServiceFactory
        {
            @Override
            public boolean applicable( final ServiceContext context,
                                       final Class<? extends Service> service )
            {
                final IModelElement element = context.find( IModelElement.class );
                final ListProperty property = context.find( ListProperty.class );
                
                return property != null && element.service( property, VersionCompatibilityAggregationService.class ) != null; 
            }

            @Override
            public Service create( final ServiceContext context,
                                   final Class<? extends Service> service )
            {
                return new ListPropertyService();
            }
        }
    }

    private static final class Resources extends NLS
    {
        public static String notCompatibleWithVersionMessage;
        public static String versionConstraintTargetNotFoundMessage;
        
        static
        {
            initializeMessages( VersionCompatibilityValidationService.class.getName(), Resources.class );
        }
    }

}
