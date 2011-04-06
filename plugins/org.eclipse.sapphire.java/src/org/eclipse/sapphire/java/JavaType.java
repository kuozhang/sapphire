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

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaType
{
    private final String name;
    private final JavaTypeKind kind;
    private JavaType superClass;
    private final Set<JavaType> superInterfaces;
    
    private JavaType( final String name,
                      final JavaTypeKind kind,
                      final JavaType superClass,
                      final Set<JavaType> superInterfaces )
    {
        if( name == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.name = name;
        
        if( kind == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.kind = kind;
        
        if( superClass != null && superClass.getKind() != JavaTypeKind.CLASS && superClass.getKind() != JavaTypeKind.ABSTRACT_CLASS )
        {
            throw new IllegalArgumentException();
        }
        
        this.superClass = superClass;
        
        for( JavaType t : superInterfaces )
        {
            if( t != null )
            {
                if( t.getKind() != JavaTypeKind.INTERFACE )
                {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        this.superInterfaces = superInterfaces;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public JavaTypeKind getKind()
    {
        return this.kind;
    }
    
    public boolean isOfType( final String type )
    {
        if( type == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( this.name.equals( type ) )
        {
            return true;
        }
        
        for( JavaType t : this.superInterfaces )
        {
            if( t.isOfType( type ) )
            {
                return true;
            }
        }
        
        if( this.superClass != null && this.superClass.isOfType( type ) )
        {
            return true;
        }
        
        return false;
    }
    
    public static final class Factory
    {
        private String name;
        private JavaTypeKind kind;
        private JavaType superClass;
        private final Set<JavaType> superInterfaces = new HashSet<JavaType>();
        
        public void setName( final String name )
        {
            this.name = name;
        }
        
        public void setKind( final JavaTypeKind kind )
        {
            this.kind = kind;
        }
        
        public void setSuperClass( final JavaType t )
        {
            this.superClass = t;
        }
        
        public void addSuperInterface( final JavaType t )
        {
            if( t != null )
            {
                this.superInterfaces.add( t );
            }
        }
        
        public JavaType create()
        {
            return new JavaType( this.name, this.kind, this.superClass, this.superInterfaces );
        }
        
    }
    
}
