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

package org.eclipse.sapphire.modeling.validation.internal;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.PossibleValuesService;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PossibleValuesValidationService

    extends ModelPropertyValidationService<Value<?>>
    
{
    @Override
    public IStatus validate()
    {
        final Value<?> value = target();
        final IModelElement modelElement = value.parent();
        final String valueString = value.getText( true );
        
        if( valueString != null )
        {
            final PossibleValuesService valuesProvider = modelElement.service( value.getProperty(), PossibleValuesService.class );
            
            if( valuesProvider != null )
            {
                Collection<String> values = valuesProvider.getPossibleValues();
                
                for( String v : values )
                {
                    if( v == null )
                    {
                        final String msg = NLS.bind( Resources.valuesProviderReturnedNull, valuesProvider.getClass().getName() );
                        SapphireModelingFrameworkPlugin.logError( msg, null );
                        
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
                    final int severity = valuesProvider.getInvalidValueSeverity( valueString );
                    final String message = valuesProvider.getInvalidValueMessage( valueString );
                    return new Status( severity, SapphireModelingFrameworkPlugin.PLUGIN_ID, message );
                }
            }
        }
        
        return Status.OK_STATUS;
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            if( property instanceof ValueProperty )
            {
                final PossibleValues annotation = property.getAnnotation( PossibleValues.class );
                
                if( annotation != null && ( annotation.service() != PossibleValuesService.class || annotation.invalidValueSeverity() != IStatus.OK ) )
                {
                    return true;
                }
            }
            
            return false;
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
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
