/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.services.ImageService;
import org.eclipse.sapphire.services.ImageServiceData;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;

/**
 * @author <a href="konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StaticImageService extends ImageService
{
    private ImageServiceData data;
    
    @Override
    protected void initImageService()
    {
        final ElementType type = context( Element.class ).type();
        final Image imageAnnotation = type.getAnnotation( Image.class );
        this.data = new ImageServiceData( ImageData.createFromClassLoader( type.findAnnotationHostClass( imageAnnotation ), imageAnnotation.path() ) );
    }

    @Override
    protected ImageServiceData compute()
    {
        return this.data;
    }
    
    public static final class Factory extends ServiceFactory
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service )
        {
            return ( context.find( Element.class ).type().getAnnotation( Image.class ) != null );
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service )
        {
            return new StaticImageService();
        }
    }
    
}
