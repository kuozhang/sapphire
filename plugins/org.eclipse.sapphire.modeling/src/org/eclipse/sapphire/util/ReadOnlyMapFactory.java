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

public final class ReadOnlyMapFactory<K,V>
{
    private K firstKey = null;
    private V firstValue = null;
    private Map<K,V> map = null;
    private boolean created = false;
    
    private ReadOnlyMapFactory() {}
    
    public static <K,V> ReadOnlyMapFactory<K,V> start()
    {
        return new ReadOnlyMapFactory<K,V>();
    }
    
    public ReadOnlyMapFactory<K,V> add( final K key,
                                        final V value )
    {
        if( this.created )
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
    
    public ReadOnlyMapFactory<K,V> add( final Map<K,V> map )
    {
        for( Map.Entry<K,V> entry : map.entrySet() )
        {
            add( entry.getKey(), entry.getValue() );
        }
        
        return this;
    }
    
    public Map<K,V> create()
    {
        if( this.created )
        {
            throw new IllegalStateException();
        }
        
        this.created = true;
        
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
    
    public static <K,V> Map<K,V> create( final K key,
                                         final V value )
    {
        return Collections.singletonMap( key, value );
    }
    
    public static <K,V> Map<K,V> create( final Map<K,V> map )
    {
        return ReadOnlyMapFactory.<K,V>start().add( map ).create();
    }
    
}
