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

package org.eclipse.sapphire.ui.internal;

import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphirePart;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PartServiceContext extends ServiceContext
{
    private final ISapphirePart part;
    
    public PartServiceContext( final ISapphirePart part )
    {
        super( "Sapphire.Part", null );
        
        this.part = part;
    }
    
    @Override
    
    public <T> T find( final Class<T> type )
    {
        T obj = super.find( type );
        
        if( obj == null )
        {
            if( type == ISapphirePart.class || type == SapphirePart.class )
            {
                obj = type.cast( this.part );
            }
        }
        
        return obj;
    }
    
}
