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

package org.eclipse.sapphire.tests.modeling.el.t0018;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface TestElement extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( TestElement.class );
    
    enum DataType
    {
        INTEGER,
        FLOAT,
        STRING
    }
    
    // *** Type ***
    
    @Type( base = DataType.class )
    
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    Value<DataType> getType();
    void setType( String value );
    void setType( DataType value );
    
    // *** TypeWithDefault ***
    
    @Type( base = DataType.class )
    @DefaultValue( text = "INTEGER" )

    ValueProperty PROP_TYPE_WITH_DEFAULT = new ValueProperty( TYPE, "TypeWithDefault" );
    
    Value<DataType> getTypeWithDefault();
    void setTypeWithDefault( String value );
    void setTypeWithDefault( DataType value );
    
}
