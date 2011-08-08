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

package org.eclipse.sapphire.samples.jee.web.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class WebContentRelativePathService extends RelativePathService
{
    @Override
    public List<Path> roots()
    {
        final IProject project = context( IModelElement.class ).adapt( IProject.class );
        
        if( project != null )
        {
            final IVirtualComponent vc = ComponentCore.createComponent( project );
            
            if( vc != null )
            {
                final List<Path> paths = new ArrayList<Path>();
                
                for( IContainer folder : vc.getRootFolder().getUnderlyingFolders() )
                {
                    paths.add( PathBridge.create( folder.getLocation() ) );
                }
                
                return paths;
            }
        }
        
        return Collections.emptyList();
    }

    @Override
    public Path convertToRelative( final Path path )
    {
        Path relative = super.convertToRelative( path );
        
        if( relative != null )
        {
            // Add a leading slash.
            
            relative = relative.makeAbsolute();
        }
        
        return relative;
    }
    
}
