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

package org.eclipse.sapphire.java;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaTypeName extends JavaQualifiedName
{
    private final String pkg;
    private final String simple;
    
    public JavaTypeName( final String name )
    {
        super( name );
        
        final int lastDot = name.lastIndexOf( '.' );
        
        if( lastDot == -1 )
        {
            this.pkg = null;
            this.simple = name;
        }
        else
        {
            this.pkg = name.substring( 0, lastDot );
            this.simple = name.substring( lastDot + 1 );
        }
    }
    
    public String pkg()
    {
        return this.pkg;
    }
    
    public String local()
    {
        return this.simple;
    }
    
    public String qualified()
    {
        return toString();
    }
    
}
