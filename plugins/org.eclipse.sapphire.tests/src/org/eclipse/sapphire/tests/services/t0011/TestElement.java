/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.services.t0011;

import org.eclipse.sapphire.PreferDefaultValue;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface TestElement extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( TestElement.class );
    
    // *** Value ***
    
    @DefaultValue( text = "123" )
    @PreferDefaultValue
    
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );
    
    Value<String> getValue();
    void setValue( String value );
    
}
