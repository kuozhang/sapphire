/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.services.ImageService;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;

/**
 * @author <a href="konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StaticImageService extends ImageService
{
    private ImageData image;
    
    @Override
    protected void init()
    {
        super.init();
        
        final ModelElementType type = context( IModelElement.class ).getModelElementType();
        final Image imageAnnotation = type.getAnnotation( Image.class );
        this.image = ImageData.createFromClassLoader( type.getAnnotationHostClass( imageAnnotation ), imageAnnotation.path() );
    }

    @Override
    public ImageData provide()
    {
        return this.image;
    }
    
    public static final class Factory extends ServiceFactory
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service )
        {
            return ( context.find( IModelElement.class ).getModelElementType().getAnnotation( Image.class ) != null );
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service )
        {
            return new StaticImageService();
        }
    }
    
}
