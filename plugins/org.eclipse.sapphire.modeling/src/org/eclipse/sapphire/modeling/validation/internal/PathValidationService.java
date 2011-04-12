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

package org.eclipse.sapphire.modeling.validation.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.ValidFileExtensions;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.validation.PathValidation;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class PathValidationService

    extends ModelPropertyValidationService<Value<IPath>>
    
{
    protected boolean resourceMustExist;
    protected FileSystemResourceType validResourceType;
    private String[] validFileExtensions;
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element, property, params );
        
        this.resourceMustExist = property.hasAnnotation( MustExist.class );
        
        final ValidFileSystemResourceType validResourceTypeAnnotation = property.getAnnotation( ValidFileSystemResourceType.class );
        this.validResourceType = ( validResourceTypeAnnotation != null ? validResourceTypeAnnotation.value() : null );
        
        final ValidFileExtensions validFileExtensionsAnnotation = property.getAnnotation( ValidFileExtensions.class );
        
        if( validFileExtensionsAnnotation != null )
        {
            this.validFileExtensions = validFileExtensionsAnnotation.value();
        }
        else
        {
            this.validFileExtensions = null;
        }
    }
    
    protected final IStatus validateExtensions( final IPath path )
    {
        final String fileName = path.lastSegment();
        
        if( fileName != null )
        {
            return PathValidation.validateExtensions( fileName, this.validFileExtensions );
        }
        
        return Status.OK_STATUS;
    }
    
    protected static final class Resources extends NLS
    {
        public static String folderMustExist;
        public static String fileMustExist;
        public static String resourceMustExist;
        public static String pathIsNotFile;
        public static String pathIsNotFolder;
        
        static
        {
            initializeMessages( PathValidationService.class.getName(), Resources.class );
        }
    }
    
}
