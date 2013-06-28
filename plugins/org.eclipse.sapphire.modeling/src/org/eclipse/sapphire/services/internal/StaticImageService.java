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
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.services.ImageService;
import org.eclipse.sapphire.services.ImageServiceData;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;

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
        this.data = new ImageServiceData( ImageData.readFromClassLoader( type.findAnnotationHostClass( imageAnnotation ), imageAnnotation.path() ).required() );
    }

    @Override
    protected ImageServiceData compute()
    {
        return this.data;
    }
    
    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            return ( context.find( Element.class ).type().getAnnotation( Image.class ) != null );
        }
    }
    
}
