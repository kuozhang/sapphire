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

package org.eclipse.sapphire.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.ModelElementType;

/**
 * Enumerates the possible child element types for a list or an element property. Each 
 * returned type is required to derive from the property's base type.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class PossibleTypesService extends Service
{
    private static final Comparator<ModelElementType> COMPARATOR = new Comparator<ModelElementType>()
    {
        public int compare( final ModelElementType x,
                            final ModelElementType y )
        {
            return x.getSimpleName().compareTo( y.getSimpleName() );
        }
    };
    
    private static SortedSet<ModelElementType> EMPTY_TYPES = Collections.unmodifiableSortedSet( new TreeSet<ModelElementType>() );
    
    private SortedSet<ModelElementType> types = EMPTY_TYPES;
    
    @Override
    protected final void init()
    {
        initPossibleTypesService();
        refresh( false );
    }

    protected void initPossibleTypesService()
    {
    }
    
    public final SortedSet<ModelElementType> types()
    {
        return this.types;
    }
    
    protected abstract void types( SortedSet<ModelElementType> types );
    
    protected final void refresh()
    {
        refresh( true );
    }
    
    private final void refresh( final boolean notifyListeners )
    {
        final SortedSet<ModelElementType> types = new TreeSet<ModelElementType>( COMPARATOR );
        
        try
        {
            types( types );
        }
        catch( Exception e )
        {
            LoggingService.log( e );
        }
        
        if( ! this.types.equals( types ) )
        {
            this.types = Collections.unmodifiableSortedSet( types );
            
            if( notifyListeners )
            {
                broadcast();
            }
        }
    }

}
