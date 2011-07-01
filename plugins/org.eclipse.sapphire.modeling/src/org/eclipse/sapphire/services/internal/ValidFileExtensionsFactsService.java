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

package org.eclipse.sapphire.services.internal;

import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.ValidFileExtensions;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.FactsService;

/**
 * Creates fact statements about valid file extensions for property's value by using semantical 
 * information specified by @ValidFileExtensions annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ValidFileExtensionsFactsService extends FactsService
{
    @Override
    protected void facts( final List<String> facts )
    {
        final ValidFileExtensions a = property().getAnnotation( ValidFileExtensions.class );
        final String[] extensions = a.value();
        
        if( extensions.length > 0 )
        {
            if( extensions.length == 1 )
            {
                facts.add( NLS.bind( Resources.statementForOne, extensions[ 0 ] ) );
            }
            else if( extensions.length == 2 )
            {
                facts.add( NLS.bind( Resources.statementForTwo, extensions[ 0 ], extensions[ 1 ] ) );
            }
            else if( extensions.length == 3 )
            {
                facts.add( NLS.bind( Resources.statementForThree, extensions[ 0 ], extensions[ 1 ], extensions[ 2 ] ) );
            }
            else
            {
                final StringBuilder buf = new StringBuilder();
                
                for( String extension : extensions )
                {
                    if( buf.length() > 0 )
                    {
                        buf.append( ", " );
                    }
                    
                    buf.append( '"' );
                    buf.append( extension );
                    buf.append( '"' );
                }
                
                facts.add( NLS.bind( Resources.statementForMany, buf.toString() ) );
            }
        }
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            return ( property instanceof ValueProperty && property.hasAnnotation( ValidFileExtensions.class ) );
        }
    
        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new ValidFileExtensionsFactsService();
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String statementForOne;
        public static String statementForTwo;
        public static String statementForThree;
        public static String statementForMany;
        
        static
        {
            initializeMessages( ValidFileExtensionsFactsService.class.getName(), Resources.class );
        }
    }
    
}
