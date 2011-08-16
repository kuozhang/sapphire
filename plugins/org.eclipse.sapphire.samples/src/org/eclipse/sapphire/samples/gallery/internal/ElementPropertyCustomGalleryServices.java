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

package org.eclipse.sapphire.samples.gallery.internal;

import java.util.SortedSet;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.samples.gallery.ElementPropertyCustomGallery;
import org.eclipse.sapphire.samples.gallery.IChildElement;
import org.eclipse.sapphire.samples.gallery.IChildElementWithEnum;
import org.eclipse.sapphire.samples.gallery.IChildElementWithInteger;
import org.eclipse.sapphire.services.PossibleTypesService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ElementPropertyCustomGalleryServices
{
    private ElementPropertyCustomGalleryServices() {}
    
    public static final class ElementPossibleTypesService extends PossibleTypesService
    {
        @Override
        protected void initPossibleTypesService()
        {
            final ModelPropertyListener listener = new ModelPropertyListener()
            {
                @Override
                public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
                {
                    broadcast();
                }
            };
            
            final ElementPropertyCustomGallery gallery = context( ElementPropertyCustomGallery.class );
            gallery.addListener( listener, ElementPropertyCustomGallery.PROP_ALLOW_CHILD_ELEMENT_WITH_INTEGER.getName() );
            gallery.addListener( listener, ElementPropertyCustomGallery.PROP_ALLOW_CHILD_ELEMENT_WITH_ENUM.getName() );
        }

        @Override
        protected void types( final SortedSet<ModelElementType> types )
        {
            final ElementPropertyCustomGallery gallery = context( ElementPropertyCustomGallery.class );
            
            types.add( IChildElement.TYPE );
            
            if( gallery.getAllowChildElementWithInteger().getContent() )
            {
                types.add( IChildElementWithInteger.TYPE );
            }
            
            if( gallery.getAllowChildElementWithEnum().getContent() )
            {
                types.add( IChildElementWithEnum.TYPE );
            }
        }
    }
    
}
