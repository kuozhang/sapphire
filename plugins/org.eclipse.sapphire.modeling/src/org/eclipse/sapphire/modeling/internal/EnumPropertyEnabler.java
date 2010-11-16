/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.EnablementService;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EnumPropertyEnabler

    extends EnablementService
    
{
    private ValueProperty property;
    private ValueSerializationService serializer;    
    private Enum<?>[] values;
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element, property, params );
        
        if( params.length != 2 )
        {
            throw new IllegalArgumentException();
        }
        
        final ModelProperty prop = element.getModelElementType().getProperty( params[ 0 ] );
        
        if( prop == null )
        {
            throw new IllegalArgumentException();
        }

        if( ! Enum.class.isAssignableFrom( prop.getTypeClass() ) )
        {
            throw new IllegalArgumentException();
        }
        
        this.property = (ValueProperty) prop;
        this.serializer = element.service( this.property, ValueSerializationService.class );
        
        final List<Enum<?>> valuesList = new ArrayList<Enum<?>>();
        
        for( String segment : params[ 1 ].split( "," ) )
        {
            final Enum<?> value = (Enum<?>) this.serializer.decode( segment );
            
            if( value != null )
            {
                valuesList.add( value );
            }
            else
            {
                final String message
                    = NLS.bind( Resources.couldNotDecode, 
                                new Object[] { element.getModelElementType().getModelElementClass().getName(),
                                property.getName(), segment } );
                
                SapphireModelingFrameworkPlugin.logError( message, null );
            }
        }
        
        this.values = valuesList.toArray( new Enum<?>[ valuesList.size() ] );
    }

    @Override
    public boolean isEnabled()
    {
        final IModelElement element = element();
        
        if( element.isPropertyEnabled( this.property ) )
        {
            final Value<Enum<?>> result;
            
            try
            {
                result = element.read( this.property );
            }
            catch( Exception e )
            {
                throw new RuntimeException( e );
            }
            
            final String res = result.getText( true );
            
            if( res != null )
            {
                for( Enum<?> value : this.values )
                {
                    if( value == this.serializer.decode( res ) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    private static final class Resources
    
        extends NLS
    
    {
        public static String couldNotDecode;
        
        static
        {
            initializeMessages( EnumPropertyEnabler.class.getName(), Resources.class );
        }
    }
    
}
