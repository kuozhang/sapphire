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

package org.eclipse.sapphire.internal;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.RequiredConstraintService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class RequiredConstraintValidationService extends ValidationService
{
    private Property property;
    private RequiredConstraintService requiredConstraintService;
    private Listener listener;
    
    @Override
    protected void init()
    {
        super.init();
        
        this.property = context( Property.class );
        this.requiredConstraintService = this.property.service( RequiredConstraintService.class );
        
        this.listener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                broadcast();
            }
        };
        
        this.requiredConstraintService.attach( this.listener );
    }

    @Override
    public final Status validate()
    {
        if( ! this.requiredConstraintService.required() || check() )
        {
            return Status.createOkStatus();
        }
        else
        {
            final String label = this.property.definition().getLabel( true, CapitalizationType.FIRST_WORD_ONLY, false );
            final String message = NLS.bind( Resources.message, label );
            return Status.createErrorStatus( message );
        }
    }
    
    protected abstract boolean check();

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.listener != null )
        {
            this.requiredConstraintService.detach( this.listener );
        }
    }

    public static final class Factory extends ServiceFactory
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service )
        {
            final Property property = context.find( Property.class );
            return ( property != null && property.service( RequiredConstraintService.class ) != null );
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service )
        {
            final PropertyDef property = context.find( PropertyDef.class );
            
            if( property instanceof ValueProperty )
            {
                return new RequiredConstraintValidationService()
                {
                    @Override
                    protected boolean check()
                    {
                        return ( context( Value.class ).text() != null );
                    }
                };
            }
            else
            {
                return new RequiredConstraintValidationService()
                {
                    @Override
                    protected boolean check()
                    {
                        return ( context( ElementHandle.class ).content() != null );
                    }
                };
            }
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String message;
        
        static
        {
            initializeMessages( RequiredConstraintValidationService.class.getName(), Resources.class );
        }
    }

}
