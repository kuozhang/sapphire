/******************************************************************************
 * Copyright (c) 2015 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.services.internal;

import java.util.Set;

import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.services.PossibleTypesService;
import org.eclipse.sapphire.util.SetFactory;

/**
 * Implementation of PossibleTypesService using information specified by @Type annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StandardPossibleTypesService extends PossibleTypesService
{
    @Override
    protected Set<ElementType> compute()
    {
        final PropertyDef property = context( PropertyDef.class );
        final Type typeAnnotation = property.getAnnotation( Type.class );
        final SetFactory<ElementType> possibleTypesSetFactory = SetFactory.start();
        
        if( ( property instanceof ElementProperty || property instanceof ListProperty ) && typeAnnotation != null )
        {
            if( typeAnnotation.possible().length == 0 )
            {
                possibleTypesSetFactory.add( ElementType.read( typeAnnotation.base() ) );
            }
            else
            {
                for( final Class<?> cl : typeAnnotation.possible() )
                {
                    possibleTypesSetFactory.add( ElementType.read( cl ) );
                }
            }
        }
        
        return possibleTypesSetFactory.result();
    }

}
