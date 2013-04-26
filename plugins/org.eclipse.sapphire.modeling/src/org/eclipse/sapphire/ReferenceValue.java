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

package org.eclipse.sapphire;

import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ReferenceValue<R,T> extends Value<R>
{
    private final ReferenceService service;
    
    public ReferenceValue( final Element element,
                           final ValueProperty property )
    {
        super( element, property );
        
        this.service = service( ReferenceService.class );
    }
    
    @SuppressWarnings( "unchecked" )
    
    public <RX,TX> ReferenceValue<RX,TX> typed( final Class<RX> referenceType, final Class<TX> targetType )
    {
        if( definition().getTypeClass() != referenceType )
        {
            throw new IllegalArgumentException();
        }
        
        if( definition().getAnnotation( Reference.class ).target() != targetType )
        {
            throw new IllegalArgumentException();
        }
        
        return (ReferenceValue<RX,TX>) this;
    }
    
    @SuppressWarnings( "unchecked" )
    
    public T resolve()
    {
        assertNotDisposed();
        
        T result = null;
        
        if( this.service != null )
        {
            final String ref = text();
            
            if( ref != null )
            {
                try
                {
                    result = (T) this.service.resolve( ref );
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                }
            }
        }
        
        return result;
    }
    
}
