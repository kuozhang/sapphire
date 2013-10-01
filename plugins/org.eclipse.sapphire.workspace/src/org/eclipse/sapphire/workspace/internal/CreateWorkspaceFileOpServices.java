/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 ******************************************************************************/

package org.eclipse.sapphire.workspace.internal;

import static org.eclipse.sapphire.workspace.CreateWorkspaceFileOp.PROBLEM_FILE_EXISTS;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FileName;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.FileExtensionsService;
import org.eclipse.sapphire.services.InitialValueService;
import org.eclipse.sapphire.services.InitialValueServiceData;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.workspace.CreateWorkspaceFileOp;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CreateWorkspaceFileOpServices
{
    @Text( "Project \"{0}\" does not exist or is not accessible" )
    private static LocalizableText projectDoesNotExist;
    
    @Text( "File \"{0}\" already exists" )
    private static LocalizableText fileExists;
    
    @Text( "File extension should be \"{0}\"" )
    private static LocalizableText invalidFileExtensionOne;
    
    @Text( "File extension should be \"{0}\" or \"{1}\"" )
    private static LocalizableText invalidFileExtensionTwo;
    
    @Text( "File extension should be one of \"{0}\"" )
    private static LocalizableText invalidFileExtensionMultiple;

    static
    {
        LocalizableText.init( CreateWorkspaceFileOpServices.class );
    }

    public CreateWorkspaceFileOpServices() {}
    
    public static final class FolderValidationService extends ValidationService
    {
        @Override
        protected Status compute()
        {
            final Value<Path> target = context( Element.class ).property( context( ValueProperty.class ) );
            final Path path = target.content();
            
            if( path != null && path.segmentCount() > 0 )
            {
                final String projectName = path.segment( 0 );
                final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember( projectName );
                
                if( resource == null || ! ( resource instanceof IProject && resource.isAccessible() ) )
                {
                    final String msg = projectDoesNotExist.format( projectName );
                    return Status.createErrorStatus( msg );
                }
            }
            
            return Status.createOkStatus();
        }
    }
    
    public static final class FolderInitialValueService extends InitialValueService 
    {
        @Override
        protected void initInitialValueService()
        {
            final Listener listener = new FilteredListener<PropertyContentEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyContentEvent event )
                {
                    refresh();
                }
            };
            
            context( CreateWorkspaceFileOp.class ).getContext().attach( listener );
        }
    
        @Override
        protected InitialValueServiceData compute()
        {
            final CreateWorkspaceFileOp op = context( CreateWorkspaceFileOp.class );
            
            IResource resource = op.getContext().content();
            
            if( resource instanceof IFile )
            {
                resource = resource.getParent();
            }
            
            return new InitialValueServiceData( resource == null ? null : resource.getFullPath().makeRelative().toPortableString() );
        }
    }
    
    public static final class FileNameValidationService extends ValidationService
    {
        @Override
        protected void initValidationService()
        {
            final Value<?> value = context( Value.class );
            final CreateWorkspaceFileOp op = value.nearest( CreateWorkspaceFileOp.class );

            final Listener listener = new FilteredListener<PropertyContentEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyContentEvent event )
                {
                    refresh();
                }
            };
            
            op.getFolder().attach( listener );
            op.getOverwriteExistingFile().attach( listener );
            
            final FileExtensionsService fileExtensionsService = value.service( FileExtensionsService.class );
            
            if( fileExtensionsService != null )
            {
                fileExtensionsService.attach
                (
                    new Listener()
                    {
                        @Override
                        public void handle( final Event event )
                        {
                            refresh();
                        }
                    }
                );
            }
        }
        
        @Override
        protected Status compute()
        {
            final Value<?> value = context( Value.class );
            final CreateWorkspaceFileOp op = value.nearest( CreateWorkspaceFileOp.class );
            final FileName fileName = (FileName) value.content();
            
            if( fileName != null )
            {
                final String extension = fileName.extension();
                
                if( extension != null )
                {
                    final FileExtensionsService fileExtensionsService = value.service( FileExtensionsService.class );
                    
                    if( fileExtensionsService != null )
                    {
                        final List<String> extensions = fileExtensionsService.extensions();
                        final int count = extensions.size();
                        
                        if( count > 0 )
                        {
                            boolean match = false;
                            
                            for( String ext : extensions )
                            {
                                if( extension.equalsIgnoreCase( ext ) )
                                {
                                    match = true;
                                    break;
                                }
                            }
                            
                            if( ! match )
                            {
                                final String message;
                                
                                if( count == 1 )
                                {
                                    message = invalidFileExtensionOne.format( extensions.get( 0 ) );
                                }
                                else if( count == 2 )
                                {
                                    message = invalidFileExtensionTwo.format( extensions.get( 0 ), extensions.get( 1 ) );
                                }
                                else
                                {
                                    final StringBuilder buf = new StringBuilder();
                                    
                                    for( String ext : extensions )
                                    {
                                        if( buf.length() != 0 )
                                        {
                                            buf.append( ", " );
                                        }
                                        
                                        buf.append( ext );
                                    }
                                    
                                    message = invalidFileExtensionMultiple.format( buf.toString() ); 
                                }
                                
                                return Status.createWarningStatus( message );
                            }
                        }
                    }
                }
                
                final IFile fileHandle = op.getFileHandle();
                
                if( fileHandle != null && fileHandle.exists() && op.getOverwriteExistingFile().content() == false )
                {
                    final String msg = fileExists.format( fileName );
                    return Status.factoryForLeaf().severity( Status.Severity.ERROR ).type( PROBLEM_FILE_EXISTS ).message( msg ).create();
                }
            }
            
            return Status.createOkStatus();
        }
    }
    
}
