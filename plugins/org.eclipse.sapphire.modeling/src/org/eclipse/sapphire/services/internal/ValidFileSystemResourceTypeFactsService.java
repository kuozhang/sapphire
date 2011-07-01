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

package org.eclipse.sapphire.services.internal;

import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.FactsService;

/**
 * Creates fact statements about valid file system resource type (file or folder) for property's value 
 * by using semantical information specified by @ValidFileSystemResourceType annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ValidFileSystemResourceTypeFactsService extends FactsService
{
    @Override
    protected void facts( final List<String> facts )
    {
        final ValidFileSystemResourceType a = property().getAnnotation( ValidFileSystemResourceType.class );
        final FileSystemResourceType type = a.value();
        
        if( type == FileSystemResourceType.FILE )
        {
            facts.add( Resources.statementForFile );
        }
        else if( type == FileSystemResourceType.FOLDER )
        {
            facts.add( Resources.statementForFolder );
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            return ( property instanceof ValueProperty && property.hasAnnotation( ValidFileSystemResourceType.class ) );
        }
    
        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new ValidFileSystemResourceTypeFactsService();
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String statementForFile;
        public static String statementForFolder;
        
        static
        {
            initializeMessages( ValidFileSystemResourceTypeFactsService.class.getName(), Resources.class );
        }
    }
    
}
