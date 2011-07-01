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
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.FactsService;

/**
 * Creates fact statements about list property's count constraint by using semantical information 
 * specified by @CountConstraint annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CountConstraintFactsService extends FactsService
{
    @Override
    protected void facts( final List<String> facts )
    {
        final CountConstraint constraint = property().getAnnotation( CountConstraint.class );
        final int min = constraint.min();
        final int max = constraint.max();
        
        if( min == 1 )
        {
            facts.add( Resources.atLeastOneStatement );
        }
        else if( min > 1 ) 
        {
            facts.add( NLS.bind( Resources.minCountStatement, min ) );
        }
        
        if( max < Integer.MAX_VALUE ) 
        {
            facts.add( NLS.bind( Resources.maxCountStatement, max ) );
        }
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            if( property instanceof ListProperty )
            {
                final CountConstraint constraint = property.getAnnotation( CountConstraint.class );
                return ( constraint != null && ( constraint.min() > 0 || constraint.max() < Integer.MAX_VALUE ) );
            }
            
            return false;
        }
    
        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new CountConstraintFactsService();
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String atLeastOneStatement;
        public static String minCountStatement;
        public static String maxCountStatement;
        
        static
        {
            initializeMessages( CountConstraintFactsService.class.getName(), Resources.class );
        }
    }
    
}
