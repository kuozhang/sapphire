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
    private boolean created = false;
    
    public void put( K key,
                     V value )
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
    
}
