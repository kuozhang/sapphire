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

package org.eclipse.sapphire.modeling.internal;

import static org.eclipse.sapphire.modeling.ImageData.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.EnumValueType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.PossibleValuesService;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EnumPossibleValuesService

    extends PossibleValuesService
    
{
    private EnumValueType enumType;
    private ValueSerializationService valueSerializationService;
    private final List<String> values = new ArrayList<String>();
    private final Map<Enum<?>,ImageData> images = new HashMap<Enum<?>,ImageData>();
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element );
        
        this.enumType = new EnumValueType( property.getTypeClass() );
        this.valueSerializationService = element.service( property, ValueSerializationService.class );
        
        for( Enum<?> item : this.enumType.getItems() )
        {
            this.values.add( this.valueSerializationService.encode( item ) );
        }
    }
    
    
    @Override
    protected void fillPossibleValues( final SortedSet<String> values )
    {
        values.addAll( this.values );
    }

    @Override
    public Status.Severity getInvalidValueSeverity( final String invalidValue )
    {
        return Status.Severity.OK;
    }
    
    @Override
    public String label( final String value )
    {
        final Enum<?> item = (Enum<?>) this.valueSerializationService.decode( value );
        
        if( item == null )
        {
            return value;
        }
        else
        {
            return this.enumType.getLabel( item, false, CapitalizationType.NO_CAPS, false );
        }
    }
    
    @Override
    public ImageData image( final String value )
    {
        final Enum<?> item = (Enum<?>) this.valueSerializationService.decode( value );
        
        if( item == null )
        {
            return null;
        }
        else
        {
            ImageData image = this.images.get( item );
            
            if( image == null )
            {
                final Image imageAnnotation = this.enumType.getAnnotation( item, Image.class );
                
                if( imageAnnotation != null )
                {
                    image = readFromClassLoader( this.enumType.getEnumTypeClass(), imageAnnotation.path() );
                    
                    if( image != null )
                    {
                        this.images.put( item, image );
                    }
                }
            }
            
            return image;
        }
    }

    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            return ( property instanceof ValueProperty && Enum.class.isAssignableFrom( property.getTypeClass() ) );
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new EnumPossibleValuesService();
        }
    }
    
}
