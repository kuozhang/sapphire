/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.internal;

import static org.eclipse.sapphire.modeling.util.internal.SapphireCommonUtil.getDefaultValueLabel;

import java.util.SortedSet;

import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.PreferDefaultValue;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.FactsService;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * Creates fact statements about property's recommended value by using semantical information specified 
 * by @PreferDefaultValue annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PreferDefaultValueFactsService extends FactsService
{
    @Text( "Recommended value is {0}" )
    private static LocalizableText fact;
    
    static
    {
        LocalizableText.init( PreferDefaultValueFactsService.class );
    }

    @Override
    protected void facts( final SortedSet<String> facts )
    {
        final Value<?> property = context( Value.class );
        final String defaultValue = getDefaultValueLabel( property );
        
        if( defaultValue != null )
        {
            facts.add( fact.format( defaultValue ) );
        }
    }
    
    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            final ValueProperty property = context.find( ValueProperty.class );
            
            if( property != null )
            {
                return property.hasAnnotation( PreferDefaultValue.class );
            }
            
            return false;
        }
    }
    
}
