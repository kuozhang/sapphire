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

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Fact;
import org.eclipse.sapphire.modeling.annotations.Facts;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.services.FactsService;

/**
 * Creates fact statements about property by using static content specified in @Fact and @Facts annotations.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StaticFactsService extends FactsService
{
    @Override
    protected void facts( final List<String> facts )
    {
        final ModelProperty property = property();
        
        final Fact factAnnotation = property.getAnnotation( Fact.class );
        
        if( factAnnotation != null )
        {
            facts( facts, factAnnotation );
        }
        
        final Facts factsAnnotation = property.getAnnotation( Facts.class );
        
        if( factsAnnotation != null )
        {
            for( Fact a : factsAnnotation.value() )
            {
                facts( facts, a );
            }
        }
    }
    
    private void facts( final List<String> facts,
                        final Fact fact )
    {
        final LocalizationService localization = property().getLocalizationService();
        facts.add( localization.text( fact.statement(), CapitalizationType.NO_CAPS, true ) );
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            return ( property instanceof ValueProperty && ( property.hasAnnotation( Fact.class ) || property.hasAnnotation( Facts.class ) ) );
        }
    
        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new StaticFactsService();
        }
    }
    
}
