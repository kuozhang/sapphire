/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.services.internal;

import java.io.File;

import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.services.PathValidationService;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RelativePathValidationService extends PathValidationService
{
    @Text( "Relative path \"{0}\" could not be resolved" )
    private static LocalizableText couldNotResolveRelative;
    
    static
    {
        LocalizableText.init( RelativePathValidationService.class );
    }

    @Override
    protected Status compute()
    {
        final Value<?> value = context( Value.class );
        final Path path = (Path) value.content();
        
        if( path != null )
        {
            final Path absolutePath = value.service( RelativePathService.class ).convertToAbsolute( path );
            
            if( absolutePath == null )
            {
                final String message = couldNotResolveRelative.format( path.toString() );
                return Status.createErrorStatus( message );
            }
            else
            {
                final File absolutePathFile = absolutePath.toFile();
                
                if( absolutePathFile.exists() )
                {
                    if( this.validResourceType == FileSystemResourceType.FILE )
                    {
                        if( absolutePathFile.isFile() )
                        {
                            return validateExtensions( path );
                        }
                        else
                        {
                            final String message = pathIsNotFile.format( absolutePath.toPortableString() );
                            return Status.createErrorStatus( message );
                        }
                    }
                    else if( this.validResourceType == FileSystemResourceType.FOLDER )
                    {
                        if( ! absolutePathFile.isDirectory() )
                        {
                            final String message = pathIsNotFolder.format( absolutePath.toPortableString() );
                            return Status.createErrorStatus( message );
                        }
                    }
                    
                    return Status.createOkStatus();
                }
            }
            
            if( this.resourceMustExist )
            {
                if( this.validResourceType == FileSystemResourceType.FILE )
                {
                    final String message = fileMustExist.format( path.toString() );
                    return Status.createErrorStatus( message );
                }
                else if( this.validResourceType == FileSystemResourceType.FOLDER )
                {
                    final String message = folderMustExist.format( path.toString() );
                    return Status.createErrorStatus( message );
                }
                else
                {
                    final String message = resourceMustExistMessage.format( path.toString() );
                    return Status.createErrorStatus( message );
                }
            }
        }
        
        return Status.createOkStatus();
    }
    
    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            final Property property = context.find( Property.class );
            return ( property != null && Path.class.isAssignableFrom( property.definition().getTypeClass() ) && property.service( RelativePathService.class ) != null );
        }
    }
    
}
