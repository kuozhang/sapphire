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

package org.eclipse.sapphire.tests.modeling.events;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface TestElement extends Element
{
    ElementType TYPE = new ElementType( TestElement.class );
    
    // *** Enablement ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )

    ValueProperty PROP_ENABLEMENT = new ValueProperty( TYPE, "Enablement" );

    Value<Boolean> getEnablement();
    void setEnablement( String value );
    void setEnablement( Boolean value );
    
    // *** ValuePlain ***
    
    ValueProperty PROP_VALUE_PLAIN = new ValueProperty( TYPE, "ValuePlain" );
    
    Value<String> getValuePlain();
    void setValuePlain( String value );
    
    // *** ValueConstrained ***
    
    @Required
    @Enablement( expr = "${ Enablement }" )
    
    ValueProperty PROP_VALUE_CONSTRAINED = new ValueProperty( TYPE, "ValueConstrained" );
    
    Value<String> getValueConstrained();
    void setValueConstrained( String value );

}