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

package org.eclipse.sapphire.tests.modeling.el.operators;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface TestElement extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( TestElement.class );
    
    // *** Integer3 ***
    
    @Type( base = Integer.class )
    @Derived( text = "3" )
    
    ValueProperty PROP_INTEGER_3 = new ValueProperty( TYPE, "Integer3" );
    
    Value<Integer> getInteger3();
    
    // *** Integer5 ***
    
    @Type( base = Integer.class )
    @Derived( text = "5" )
    
    ValueProperty PROP_INTEGER_5 = new ValueProperty( TYPE, "Integer5" );
    
    Value<Integer> getInteger5();
    
    // *** BooleanTrue ***
    
    @Type( base = Boolean.class )
    @Derived( text = "true" )
    
    ValueProperty PROP_BOOLEAN_TRUE = new ValueProperty( TYPE, "BooleanTrue" );
    
    Value<Boolean> getBooleanTrue();
    
    // *** BooleanFalse ***
    
    @Type( base = Boolean.class )
    @Derived( text = "false" )
    
    ValueProperty PROP_BOOLEAN_FALSE = new ValueProperty( TYPE, "BooleanFalse" );
    
    Value<Boolean> getBooleanFalse();
    
    // *** EmptyList ***
    
    interface Entry extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( Entry.class );
    }
    
    @Type( base = Entry.class )

    ListProperty PROP_EMPTY_LIST = new ListProperty( TYPE, "EmptyList" );
    
    ModelElementList<Entry> getEmptyList();

}
