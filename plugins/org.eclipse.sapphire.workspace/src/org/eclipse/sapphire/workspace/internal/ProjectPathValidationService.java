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

package org.eclipse.sapphire.workspace.internal;

import static org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin.PLUGIN_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.validation.PathValidationService;
import org.eclipse.sapphire.workspace.ProjectRelativePath;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectPathValidationService

    extends PathValidationService
    
{
    @Override
    public IStatus validate()
    {
        final Value<IPath> value = target();
        final IPath path = value.getContent( false );
        
        if( path != null )
        {
            final IProject project = adapt( IProject.class );
            
            if( project == null )
            {
                return new Status( Status.ERROR, PLUGIN_ID, LocalResources.noProject );
            }
            else if( ! project.isAccessible() )
            {
                return new Status( Status.ERROR, PLUGIN_ID, LocalResources.projectNotAccessible );
            }
            else
            {
                final IResource resource = project.findMember( path );
                
                if( resource != null && resource.exists() )
                {
                    if( this.validResourceType == FileSystemResourceType.FILE )
                    {
                        if( resource.getType() == IResource.FILE )
                        {
                            return validateExtensions( path );
                        }
                        else
                        {
                            final String message = Resources.bind( Resources.pathIsNotFile, path.toString() );
                            return new Status( Status.ERROR, PLUGIN_ID, message );
                        }
                    }
                    else if( this.validResourceType == FileSystemResourceType.FOLDER )
                    {
                        if( resource.getType() != IResource.FOLDER && resource.getType() != IResource.PROJECT )
                        {
                            final String message = Resources.bind( Resources.pathIsNotFolder, path.toString() );
                            return new Status( Status.ERROR, PLUGIN_ID, message );
                        }
                    }
                }
                else
                {
                    if( this.resourceMustExist )
                    {
                        if( this.validResourceType == FileSystemResourceType.FILE )
                        {
                            final String message = Resources.bind( Resources.fileMustExist, path.toString() );
                            return new Status( Status.ERROR, PLUGIN_ID, message );
                        }
                        else if( this.validResourceType == FileSystemResourceType.FOLDER )
                        {
                            final String message = Resources.bind( Resources.folderMustExist, path.toString() );
                            return new Status( Status.ERROR, PLUGIN_ID, message );
                        }
                        else
                        {
                            final String message = Resources.bind( Resources.resourceMustExist, path.toString() );
                            return new Status( Status.ERROR, PLUGIN_ID, message );
                        }
                    }
                }
            }
        }
        
        return Status.OK_STATUS;
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            return ( property instanceof ValueProperty && property.hasAnnotation( ProjectRelativePath.class ) && IPath.class.isAssignableFrom( property.getTypeClass() ) );
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new ProjectPathValidationService();
        }
    }
    
    private static final class LocalResources extends NLS
    {
        public static String noProject;
        public static String projectNotAccessible;
        
        static
        {
            initializeMessages( ProjectPathValidationService.class.getName(), LocalResources.class );
        }
    }
    
}
