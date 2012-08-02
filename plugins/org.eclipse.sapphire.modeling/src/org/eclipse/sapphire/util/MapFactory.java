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
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MapFactory<K,V>
{
    private K firstKey = null;
    private V firstValue = null;
    private Map<K,V> map = null;
    private boolean exported = false;
    
    private MapFactory() {}
    
    public static <K,V> Map<K,V> empty()
    {
        return Collections.emptyMap();
    }
    
    public static <K,V> Map<K,V> singleton( final K key, final V value )
    {
        return Collections.singletonMap( key, value );
    }
    
    public static <K,V> Map<K,V> unmodifiable( final Map<K,V> map )
    {
        return MapFactory.<K,V>start().add( map ).result();
    }
    
    public static <K,V> MapFactory<K,V> start()
    {
        return new MapFactory<K,V>();
    }

    public MapFactory<K,V> add( final K key, final V value )
    {
        if( this.exported )
        {
            throw new IllegalStateException();
        }
        
        if( this.map != null )
        {
            this.map.put( key, value );
        }
        else if( this.firstKey != null )
        {
            this.map = new HashMap<K,V>();
            this.map.put( this.firstKey, this.firstValue );
            this.map.put( key, value );
            this.firstKey = null;
            this.firstValue = null;
        }
        else
        {
            this.firstKey = key;
            this.firstValue = value;
        }
        
        return this;
    }
    
    public MapFactory<K,V> add( final Map<K,V> map )
    {
        for( Map.Entry<K,V> entry : map.entrySet() )
        {
            add( entry.getKey(), entry.getValue() );
        }
        
        return this;
    }
    
    public V remove( final K key )
    {
        V removed = null;
        
        if( this.map != null )
        {
            removed = this.map.remove( key );
            
            if( this.map.size() == 1 )
            {
                final Map.Entry<K,V> entry = this.map.entrySet().iterator().next();
                this.firstKey = entry.getKey();
                this.firstValue = entry.getValue();
                this.map = null;
            }
        }
        else if( this.firstKey != null && this.firstKey.equals( key ) )
        {
            removed = this.firstValue;
            this.firstKey = null;
            this.firstValue = null;
        }
        
        return removed;
    }
    
    public V get( final K key )
    {
        V value = null;
        
        if( this.map != null )
        {
            value = this.map.get( key );
        }
        else if( this.firstKey != null && this.firstKey.equals( key ) )
        {
            value = this.firstValue;
        }
        
        return value;
    }
    
    public boolean contains( final K key )
    {
        return containsKey( key );
    }
    
    public boolean containsKey( final K key )
    {
        boolean contains = false;
        
        if( this.map != null )
        {
            contains = this.map.containsKey( key );
        }
        else if( this.firstKey != null && this.firstKey.equals( key ) )
        {
            contains = true;
        }
        
        return contains;
    }
    
    public boolean containsValue( final V value )
    {
        boolean contains = false;
        
        if( this.map != null )
        {
            contains = this.map.containsValue( value );
        }
        else if( this.firstValue != null && this.firstValue.equals( value ) )
        {
            contains = true;
        }
        
        return contains;
    }

    public int size()
    {
        final int size;
        
        if( this.map != null )
        {
            size = this.map.size();
        }
        else if( this.firstKey != null)
        {
            size = 1;
        }
        else
        {
            size = 0;
        }
        
        return size;
    }
    
    public Map<K,V> result()
    {
        if( this.exported )
        {
            throw new IllegalStateException();
        }
        
        this.exported = true;
        
        if( this.map != null )
        {
            return Collections.unmodifiableMap( this.map );
        }
        else if( this.firstKey != null )
        {
            return Collections.singletonMap( this.firstKey, this.firstValue );
        }
        else
        {
            return Collections.emptyMap();
        }
    }
    
}
