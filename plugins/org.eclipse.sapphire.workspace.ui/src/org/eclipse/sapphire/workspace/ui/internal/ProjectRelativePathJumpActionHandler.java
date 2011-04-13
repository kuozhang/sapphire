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

package org.eclipse.sapphire.workspace.ui.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.SapphireJumpActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectRelativePathJumpActionHandler 

    extends SapphireJumpActionHandler
    
{
    @Override
    protected void refreshEnablementState()
    {
        boolean enabled = false;
        
        final IModelElement element = getModelElement();
        final IProject project = element.adapt( IProject.class );
        
        if( project != null )
        {
            final IPath path = element.<IPath>read( getProperty() ).getContent( true );
            
            if( path != null )
            {
                final IResource resource = project.findMember( path );
                
                if( resource != null && resource.exists() && resource.getType() == IResource.FILE )
                {
                    enabled = true;
                }
            }
        }

        setEnabled( enabled );
    }

    @Override
    protected Object run( final SapphireRenderingContext context )
    {
        final IModelElement element = getModelElement();
        final IProject project = element.adapt( IProject.class );
        
        if( project != null )
        {
            final IPath path = element.<IPath>read( getProperty() ).getContent( true );
            
            if( path != null )
            {
                final IResource resource = project.findMember( path );
                
                if( resource != null && resource.exists() && resource.getType() == IResource.FILE )
                {
                    final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    
                    if( window != null )
                    {
                        final IWorkbenchPage page = window.getActivePage();
    
                        try
                        {
                            IDE.openEditor( page, (IFile) resource );
                        } 
                        catch( PartInitException e ) 
                        {
                            SapphireUiFrameworkPlugin.log( e );
                        }
                    }
                }
            }
        }
            
        return null;
    }

}