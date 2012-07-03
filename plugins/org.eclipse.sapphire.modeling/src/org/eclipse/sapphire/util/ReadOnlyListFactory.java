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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ReadOnlyListFactory<E>
{
    private E firstElement = null;
    private ArrayList<E> list = null;
    private boolean exported = false;
    
    private ReadOnlyListFactory() {}
    
    public static <E> ReadOnlyListFactory<E> create()
    {
        return new ReadOnlyListFactory<E>();
    }
    
    public static <E> List<E> create( final E element )
    {
        return Collections.singletonList( element );
    }
    
    public static <E> List<E> create( final E... elements )
    {
        return ReadOnlyListFactory.<E>create().add( elements ).export();
    }
    
    public static <E> List<E> create( final Collection<E> elements )
    {
        return ReadOnlyListFactory.<E>create().add( elements ).export();
    }

    public ReadOnlyListFactory<E> add( final E element )
    {
        if( this.exported )
        {
            throw new IllegalStateException();
        }
        
        if( this.list != null )
        {
            this.list.add( element );
        }
        else if( this.firstElement != null )
        {
            this.list = new ArrayList<E>();
            this.list.add( this.firstElement );
            this.list.add( element );
            this.firstElement = null;
        }
        else
        {
            this.firstElement = element;
        }
        
        return this;
    }
    
    public ReadOnlyListFactory<E> add( final E... elements )
    {
        for( E element : elements )
        {
            add( element );
        }
        
        return this;
    }

    public ReadOnlyListFactory<E> add( final Collection<E> elements )
    {
        for( E element : elements )
        {
            add( element );
        }
        
        return this;
    }
    
    public E remove( final int index )
    {
        final int size = size();
        
        if( index < 0 || index >= size )
        {
            throw new IllegalArgumentException();
        }
        
        E removed;
        
        if( this.list != null )
        {
            if( size == 2 )
            {
                removed = this.list.get( index );
                this.firstElement = this.list.get( index == 0 ? 1 : 0 );
                this.list = null;
            }
            else
            {
                removed = this.list.remove( index );
            }
        }
        else if( this.firstElement != null )
        {
            removed = this.firstElement;
            this.firstElement = null;
        }
        else
        {
            throw new IllegalStateException();
        }
        
        return removed;
    }
    
    public E get( final int index )
    {
        if( index < 0 || index >= size() )
        {
            throw new IllegalArgumentException();
        }
        
        E element;
        
        if( this.list != null )
        {
            element = this.list.get( index );
        }
        else if( this.firstElement != null )
        {
            element = this.firstElement;
        }
        else
        {
            throw new IllegalStateException();
        }
        
        return element;
    }

    public int size()
    {
        final int size;
        
        if( this.list != null )
        {
            size = this.list.size();
        }
        else if( this.firstElement != null)
        {
            size = 1;
        }
        else
        {
            size = 0;
        }
        
        return size;
    }
    
    public List<E> export()
    {
        if( this.exported )
        {
            throw new IllegalStateException();
        }
        
        this.exported = true;
        
        if( this.list != null )
        {
            this.list.trimToSize();
            return Collections.unmodifiableList( this.list );
        }
        else if( this.firstElement != null )
        {
            return Collections.singletonList( this.firstElement );
        }
        else
        {
            return Collections.emptyList();
        }
    }
    
}
