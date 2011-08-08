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

package org.eclipse.sapphire.services.internal;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.services.ReferenceService;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PossibleValuesValidationService extends ValidationService
{
    @Override
    public Status validate()
    {
        final Value<?> value = context( IModelElement.class ).read( context( ValueProperty.class ) );
        final IModelElement modelElement = value.parent();
        final String valueString = value.getText( true );
        
        if( valueString != null )
        {
            final PossibleValuesService valuesProvider = modelElement.service( value.getProperty(), PossibleValuesService.class );
            
            if( valuesProvider != null )
            {
                Collection<String> values = valuesProvider.values();
                
                for( String v : values )
                {
                    if( v == null )
                    {
                        final String msg = NLS.bind( Resources.valuesProviderReturnedNull, valuesProvider.getClass().getName() );
                        LoggingService.log( Status.createErrorStatus( msg ) );
                        
                        values = Collections.emptyList();
                    }
                }
                
                boolean found = false;
                
                if( valuesProvider.isCaseSensitive() )
                {
                    found = values.contains( valueString );
                }
                else
                {
                    for( String v : values )
                    {
                        if( v.equalsIgnoreCase( valueString ) )
                        {
                            found = true;
                            break;
                        }
                    }
                }

                if( ! found )
                {
                    final Status.Severity severity = valuesProvider.getInvalidValueSeverity( valueString );
                    
                    if( severity != Status.Severity.OK )
                    {
                        final String message = valuesProvider.getInvalidValueMessage( valueString );
                        return Status.createStatus( severity, message );
                    }
                }
            }
        }
        
        return Status.createOkStatus();
    }
    
    public static final class Factory extends ServiceFactory
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service )
        {
            final ValueProperty property = context.find( ValueProperty.class );
            
            if( property != null )
            {
                final IModelElement element = context.find( IModelElement.class );
                
                if( element.service( property, PossibleValuesService.class ) != null && element.service( property, ReferenceService.class ) == null )
                {
                    return true;
                }
            }
            
            return false;
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service )
        {
            return new PossibleValuesValidationService();
        }
    }

    private static final class Resources extends NLS
    {
        public static String valuesProviderReturnedNull;
        
        static
        {
            initializeMessages( PossibleValuesValidationService.class.getName(), Resources.class );
        }
    }
    
}
