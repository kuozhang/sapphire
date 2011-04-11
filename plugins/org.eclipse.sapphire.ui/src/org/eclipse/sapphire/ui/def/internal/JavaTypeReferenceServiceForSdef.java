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

package org.eclipse.sapphire.ui.def.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.java.ClassBasedJavaType;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.java.JavaTypeReferenceService;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.ui.def.IImportDirective;
import org.eclipse.sapphire.ui.def.IPackageReference;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaTypeReferenceServiceForSdef

    extends JavaTypeReferenceService
    
{
    @Override
    public JavaType resolve( final String name )
    {
        for( IImportDirective directive : element().nearest( ISapphireUiDef.class ).getImportDirectives() )
        {
            final String bundleId = directive.getBundle().getText();
            
            if( bundleId != null )
            {
                final Bundle bundle = Platform.getBundle( bundleId );
                
                if( bundle != null )
                {
                    for( IPackageReference packageRef : directive.getPackages() )
                    {
                        final String packageName = packageRef.getName().getText();
                        
                        if( packageName != null )
                        {
                            final String fullClassName = packageName + "." + name;
                            
                            try
                            {
                                return new ClassBasedJavaType( bundle.loadClass( fullClassName ) );
                            }
                            catch( ClassNotFoundException e ) {}
                        }
                    }
                }
            }
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
            if( property instanceof ValueProperty && property.getTypeClass() == JavaTypeName.class && element.nearest( ISapphireUiDef.class ) != null )
            {
                final Reference referenceAnnotation = property.getAnnotation( Reference.class );
                
                if( referenceAnnotation != null && referenceAnnotation.target() == JavaType.class )
                {
                    return true;
                }
            }
            
            return false;
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new JavaTypeReferenceServiceForSdef();
        }
    }

}
