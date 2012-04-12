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

package org.eclipse.sapphire.util;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CollectionsUtil
{
    private CollectionsUtil() {}
    
    public static <E> E findPrecedingItem( final List<E> list,
                                           final E item )
    {
        E lastSeenItem = null;
        
        for( E x : list )
        {
            if( x == item )
            {
                return lastSeenItem;
            }
            
            lastSeenItem = x;
        }
        
        return null;
    }
    
    public static <E> E findTrailingItem( final List<E> list,
                                          final E item )
    {
        boolean takeNextItem = false;
        
        for( E x : list )
        {
            if( takeNextItem )
            {
                return x;
            }
            else if( x == item )
            {
                takeNextItem = true;
            }
        }
        
        return null;
    }
    
}
