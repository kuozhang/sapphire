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

package org.eclipse.sapphire.java.jdt.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeService;
import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JdtJavaTypeService

    extends JavaTypeService
    
{
    private final IJavaProject project;
    
    public JdtJavaTypeService( final IProject project )
    {
        this.project = JavaCore.create( project );
    }

    public JdtJavaTypeService( final IJavaProject project )
    {
        this.project = project;
    }

    @Override
    public JavaType find( final String name )
    {
        if( name.trim().length() == 0 || name.startsWith( "." ) || name.endsWith( "." ) )
        {
            return null;
        }
        
        try
        {
            final IType type = this.project.findType( name );
            
            if( type != null && type.exists() && ! type.isAnonymous() )
            {
                return toJavaType( type );
            }
        }
        catch( JavaModelException e )
        {
            SapphireModelingFrameworkPlugin.log( e );
        }
        
        return null;
    }
    
    private JavaType toJavaType( final IType type )
    {
        try
        {
            final JavaType.Factory factory = new JavaType.Factory();
            
            factory.setName( type.getFullyQualifiedName() );
            
            if( type.isAnnotation() )
            {
                factory.setKind( JavaTypeKind.ANNOTATION );
            }
            else if( type.isEnum() )
            {
                factory.setKind( JavaTypeKind.ENUM );
            }
            else if( type.isInterface() )
            {
                factory.setKind( JavaTypeKind.INTERFACE );
            }
            else if( Flags.isAbstract( type.getFlags() ) )
            {
                factory.setKind( JavaTypeKind.ABSTRACT_CLASS );
            }
            else
            {
                factory.setKind( JavaTypeKind.CLASS );
            }
            
            final ITypeHierarchy typeHierarchy = type.newSupertypeHierarchy( null );
            
            final IType superClassType = typeHierarchy.getSuperclass( type );
            
            if( superClassType != null )
            {
                factory.setSuperClass( toJavaType( superClassType ) );
            }
            
            for( IType superInterface : typeHierarchy.getSuperInterfaces( type ) )
            {
                factory.addSuperInterface( toJavaType( superInterface ) );
            }
            
            return factory.create();
        }
        catch( JavaModelException e )
        {
            SapphireModelingFrameworkPlugin.log( e );
        }
        
        return null;
    }
    
}
