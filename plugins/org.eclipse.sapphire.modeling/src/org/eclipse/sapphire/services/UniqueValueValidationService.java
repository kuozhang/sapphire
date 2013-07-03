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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class UniqueValueValidationService extends ValidationService
{
    @Text( "Unique {0} required. Another occurrence of \"{1}\" was found." )
    private static LocalizableText message; 
    
    static
    {
        LocalizableText.init( UniqueValueValidationService.class );
    }

    @Override
    public Status validate()
    {
        final Value<?> value = context( Value.class );
        
        if( isUniqueValue( value ) == false )
        {
            final ValueProperty property = value.definition();
            final String label = property.getLabel( true, CapitalizationType.NO_CAPS, false );
            final String str = value.text();
            final String msg = message.format( label, str );
            return Status.createErrorStatus( msg );
        }
        
        return Status.createOkStatus();
    }
    
    protected boolean isUniqueValue( final Value<?> value )
    {
        final String str = value.text();
        
        if( str != null )
        {
            final Element element = value.element();
            final ValueProperty property = value.definition();
            final Property valueElementParent = element.parent();
            
            if( valueElementParent != null && valueElementParent.definition() instanceof ListProperty )
            {
                final ElementList<?> list = (ElementList<?>) valueElementParent;
                
                for( Element x : list )
                {
                    if( x != element )
                    {
                        final Value<?> xval = x.property( property );
                        
                        if( str.equals( xval.text() ) )
                        {
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
}
