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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class UniqueValueValidationService

    extends ModelPropertyValidationService<Value<?>>
    
{
    @Override
    public IStatus validate()
    {
        final Value<?> value = target();
        
        if( isUniqueValue( value ) == false )
        {
            final ValueProperty property = value.getProperty();
            final String label = property.getLabel( true, CapitalizationType.NO_CAPS, false );
            final String str = value.getText();
            final String msg = NLS.bind( Resources.message, label, str );
            return createErrorStatus( msg );
        }
        
        return Status.OK_STATUS;
    }
    
    private boolean isUniqueValue( final Value<?> value )
    {
        final String str = value.getText();
        
        if( str != null )
        {
            final IModelElement modelElement = value.parent();
            final ValueProperty property = value.getProperty();
            final IModelParticle parent = modelElement.parent();
            
            if( parent instanceof ModelElementList<?> )
            {
                final ModelElementList<?> list = (ModelElementList<?>) parent;
                
                for( IModelElement x : list )
                {
                    if( x != modelElement )
                    {
                        final Value<?> xval = x.read( property );
                        
                        if( str.equals( xval.getText() ) )
                        {
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            return ( property instanceof ValueProperty && property.hasAnnotation( NoDuplicates.class ) );
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new UniqueValueValidationService();
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String message; 
        
        static
        {
            initializeMessages( UniqueValueValidationService.class.getName(), Resources.class );
        }
    }
    
}
