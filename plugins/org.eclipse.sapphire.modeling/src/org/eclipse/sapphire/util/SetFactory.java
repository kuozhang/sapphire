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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SetFactory<E>
{
    private E firstElement = null;
    private Set<E> set = null;
    private boolean created = false;
    
    public void add( E element )
    {
        if( this.created )
        {
            throw new IllegalStateException();
        }
        
        if( this.set != null )
        {
            this.set.add( element );
        }
        else if( this.firstElement != null )
        {
            this.set = new HashSet<E>();
            this.set.add( this.firstElement );
            this.set.add( element );
            this.firstElement = null;
        }
        else
        {
            this.firstElement = element;
        }
    }
    
    public Set<E> create()
    {
        if( this.created )
        {
            throw new IllegalStateException();
        }
        
        this.created = true;
        
        if( this.set != null )
        {
            return Collections.unmodifiableSet( this.set );
        }
        else if( this.firstElement != null )
        {
            return Collections.singleton( this.firstElement );
        }
        else
        {
            return Collections.emptySet();
        }
    }
    
}
