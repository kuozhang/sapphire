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

package org.eclipse.sapphire.modeling.java2.internal;

import java.lang.reflect.Modifier;

import org.eclipse.sapphire.modeling.java2.JavaType;
import org.eclipse.sapphire.modeling.java2.JavaTypeKind;
import org.eclipse.sapphire.modeling.java2.JavaTypeService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ClassLoaderJavaTypeService

    extends JavaTypeService
    
{
    private final ClassLoader classLoader;
    
    public ClassLoaderJavaTypeService( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    @Override
    public JavaType find( final String name )
    {
        try
        {
            final Class<?> cl = this.classLoader.loadClass( name );
            return toJavaType( cl );
        }
        catch( ClassNotFoundException e )
        {
            // Intentionally converting exception to null result.
        }

        return null;
    }
    
    private JavaType toJavaType( final Class<?> cl )
    {
        final JavaType.Factory factory = new JavaType.Factory();
        
        factory.setName( cl.getName() );
        
        if( cl.isAnnotation() )
        {
            factory.setKind( JavaTypeKind.ANNOTATION );
        }
        else if( cl.isEnum() )
        {
            factory.setKind( JavaTypeKind.ENUM );
        }
        else if( cl.isInterface() )
        {
            factory.setKind( JavaTypeKind.INTERFACE );
        }
        else if( Modifier.isAbstract( cl.getModifiers() ) )
        {
            factory.setKind( JavaTypeKind.ABSTRACT_CLASS );
        }
        else
        {
            factory.setKind( JavaTypeKind.CLASS );
        }
        
        final Class<?> superClass = cl.getSuperclass();
        
        if( superClass != null )
        {
            factory.setSuperClass( find( superClass.getName() ) );
        }
        
        for( Class<?> superInterface : cl.getInterfaces() )
        {
            factory.addSuperInterface( find( superInterface.getName() ) );
        }
        
        return factory.create();
    }
    
}
