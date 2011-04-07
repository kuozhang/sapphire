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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ReferenceService;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JdtJavaTypeReferenceService

    extends ReferenceService
    
{
    private final IJavaProject project;
    
    public JdtJavaTypeReferenceService( final IProject project )
    {
        this.project = JavaCore.create( project );
    }

    public JdtJavaTypeReferenceService( final IJavaProject project )
    {
        this.project = project;
    }

    @Override
    public JavaType resolve( final String name )
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
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            final Reference referenceAnnotation = property.getAnnotation( Reference.class );

            if( referenceAnnotation != null && referenceAnnotation.target() == JavaType.class )
            {
                final IProject project = element.adapt( IProject.class );
                
                if( project != null )
                {
                    try
                    {
                        for( String nature : project.getDescription().getNatureIds() )
                        {
                            if( nature.equals( JavaCore.NATURE_ID ) )
                            {
                                return true;
                            }
                        }
                    }
                    catch( CoreException e )
                    {
                        SapphireModelingFrameworkPlugin.log( e );
                    }
                }
            }
            
            return false;
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            final IProject project = element.adapt( IProject.class );
            return new JdtJavaTypeReferenceService( project );
        }
    }
    
}
