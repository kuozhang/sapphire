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

package org.eclipse.sapphire.java.internal;

import java.lang.reflect.Modifier;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ReferenceService;
import org.eclipse.sapphire.modeling.annotations.Reference;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ClassLoaderJavaTypeReferenceService

    extends ReferenceService
    
{
    private final ClassLoader classLoader;
    
    public ClassLoaderJavaTypeReferenceService( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    @Override
    public JavaType resolve( final String name )
    {
        try
        {
            final Class<?> cl = this.classLoader.loadClass( name );
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
                factory.setSuperClass( resolve( superClass.getName() ) );
            }
            
            for( Class<?> superInterface : cl.getInterfaces() )
            {
                factory.addSuperInterface( resolve( superInterface.getName() ) );
            }
            
            return factory.create();
        }
        catch( ClassNotFoundException e )
        {
            // Intentionally converting exception to null result.
        }

        return null;
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            final Reference referenceAnnotation = property.getAnnotation( Reference.class );
            return ( referenceAnnotation != null && referenceAnnotation.target() == JavaType.class );
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            final ClassLoader classLoader = element.getModelElementType().getModelElementClass().getClassLoader();
            return new ClassLoaderJavaTypeReferenceService( classLoader );
        }
    }
    
}
