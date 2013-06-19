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

package org.eclipse.sapphire.services.internal;

import java.util.Set;

import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * Implementation of PossibleValuesService based on @PossibleValues annotation's values attribute..
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StaticPossibleValuesService extends PossibleValuesService
{
    private String[] values;
    private String invalidValueMessageTemplate;
    private Status.Severity invalidValueSeverity;
    private boolean caseSensitive;
    private boolean ordered;
    
    @Override
    protected void init()
    {
        super.init();
        
        final PossibleValues a = context( ValueProperty.class ).getAnnotation( PossibleValues.class );
        
        this.values = a.values();
        this.invalidValueMessageTemplate = a.invalidValueMessage();
        this.invalidValueSeverity = a.invalidValueSeverity();
        this.caseSensitive = a.caseSensitive();
        this.ordered = a.ordered();
    }

    @Override
    protected void fillPossibleValues( final Set<String> values )
    {
        for( String value : this.values )
        {
            values.add( value );
        }
    }
    
    @Override
    public String getInvalidValueMessage( final String invalidValue )
    {
        return NLS.bind( this.invalidValueMessageTemplate, invalidValue, context( PropertyDef.class ).getLabel( true, CapitalizationType.NO_CAPS, false ) );
    }
    
    @Override
    public Status.Severity getInvalidValueSeverity( final String invalidValue )
    {
        return this.invalidValueSeverity;
    }
    
    @Override
    public boolean isCaseSensitive()
    {
        return this.caseSensitive;
    }

    @Override
    public boolean ordered()
    {
        return this.ordered;
    }

    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            final ValueProperty property = context.find( ValueProperty.class );
            return ( property != null && property.hasAnnotation( PossibleValues.class ) && property.getAnnotation( PossibleValues.class ).values().length > 0 );
        }
    }
    
}
