/******************************************************************************
 * Copyright (c) 2012 Oracle
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
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.FileExtensionsService;
import org.eclipse.sapphire.services.PathValidationService;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.workspace.CreateWorkspaceFileOp;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CreateWorkspaceFileOpFileNameValidator extends ValidationService
{
    private FileExtensionsService fileExtensionsService;
    
    @Override
    protected void init()
    {
        super.init();
        
        final IModelElement element = context( IModelElement.class );
        final ModelProperty property = context( ModelProperty.class );
        
        this.fileExtensionsService = element.service( property, FileExtensionsService.class );
        
        if( this.fileExtensionsService != null )
        {
            this.fileExtensionsService.attach
            (
                new Listener()
                {
                    @Override
                    public void handle( final Event event )
                    {
                        element.refresh( property );
                    }
                }
            );
        }
    }
    
    @Override
    public Status validate()
    {
        final Value<String> value = context( IModelElement.class ).read( context( ValueProperty.class ) );
        final String fileName = value.getText();
        
        if( fileName != null )
        {
            final int lastDot = fileName.indexOf( '.' );
            
            if( lastDot >= 0 && lastDot < fileName.length() )
            {
                if( this.fileExtensionsService != null )
                {
                    final List<String> extensions = this.fileExtensionsService.extensions();
                    final Status st = PathValidationService.validateExtensions( fileName, extensions );
                    
                    if( ! st.ok() )
                    {
                        return st;
                    }
                }
            }
            
            final CreateWorkspaceFileOp operation = value.nearest( CreateWorkspaceFileOp.class );
            final IFile fileHandle = operation.getFileHandle();
            
            if( fileHandle != null && fileHandle.exists() && 
                operation.getOverwriteExistingFile().getContent() == false )
            {
                final String msg = NLS.bind( Resources.fileExists, fileName );
                return Status.factoryForLeaf().severity( Status.Severity.ERROR ).type( PROBLEM_FILE_EXISTS ).message( msg ).create();
            }
        }
        
        return Status.createOkStatus();
    }
    
    private static final class Resources extends NLS
    {
        public static String fileExists;
        
        static
        {
            initializeMessages( CreateWorkspaceFileOpFileNameValidator.class.getName(), Resources.class );
        }
    }
    
}
