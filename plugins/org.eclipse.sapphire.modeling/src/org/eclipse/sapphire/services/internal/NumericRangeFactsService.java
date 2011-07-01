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
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.modeling.util.internal.SapphireCommonUtil;
import org.eclipse.sapphire.services.FactsService;

/**
 * Creates fact statements about numeric value property's range by using semantical information 
 * specified by @NumericRange annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class NumericRangeFactsService extends FactsService
{
    @Override
    protected void facts( final List<String> facts )
    {
        final ValueProperty property = (ValueProperty) property();
        final NumericRange range = property.getAnnotation( NumericRange.class );
        final String min = range.min();
        final String max = range.max();
            
        if( min.length() > 0 ) 
        {
            facts.add( NLS.bind( Resources.minValueStatement, SapphireCommonUtil.normalizeForDisplay( property, min ) ) );
        }
        
        if( max.length() > 0 ) 
        {
            facts.add( NLS.bind( Resources.maxValueStatement, SapphireCommonUtil.normalizeForDisplay( property, max ) ) );
        }
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            if( property instanceof ValueProperty )
            {
                final NumericRange range = property.getAnnotation( NumericRange.class );
                return ( range != null && ( range.min().length() > 0 || range.max().length() > 0 ) );
            }
            
            return false;
        }
    
        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new NumericRangeFactsService();
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String minValueStatement;
        public static String maxValueStatement;
        
        static
        {
            initializeMessages( NumericRangeFactsService.class.getName(), Resources.class );
        }
    }
    
}
