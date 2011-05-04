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

package org.eclipse.sapphire.ui.def.internal;

import java.net.URL;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ResourceLocator;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.TypeCast;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StringToImageDataCast

    extends TypeCast
    
{
    @Override
    public boolean applicable( final FunctionContext context,
                               final Function requestor,
                               final Object value,
                               final Class<?> target )
    {
        final Object origin = requestor.origin();
        
        if( origin instanceof IModelElement )
        {
            return ( ( (IModelElement) origin ).adapt( ResourceLocator.class ) != null );
        }
        
        return false;
    }

    @Override
    public Object evaluate( final FunctionContext context,
                            final Function requestor,
                            final Object value,
                            final Class<?> target )
    {
        final IModelElement element = (IModelElement) requestor.origin();
        final ResourceLocator resourceLocator = element.adapt( ResourceLocator.class );
        final URL url = resourceLocator.find( (String) value );
        
        if( url != null )
        {
            return ImageData.readFromUrl( url );
        }
        
        return null;
    }

}
