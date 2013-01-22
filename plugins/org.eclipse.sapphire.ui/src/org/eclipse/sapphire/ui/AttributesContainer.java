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

package org.eclipse.sapphire.ui;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.ui.internal.AttributesContainerMethods;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface AttributesContainer extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( AttributesContainer.class );
    
    // *** Attributes ***
    
    interface Attribute extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( AttributesContainer.Attribute.class );
        
        // *** Name ***
        
        @Required
        
        ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
        
        Value<String> getName();
        void setName( String value );
        
        // *** Value ***
        
        @Required
        
        ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );
        
        Value<String> getValue();
        void setValue( String value );
    }

    @Type( base = Attribute.class )

    ListProperty PROP_ATTRIBUTES = new ListProperty( TYPE, "Attributes" );
    
    ModelElementList<Attribute> getAttributes();
    
    // *** Method: getAttribute ***
    
    @DelegateImplementation( AttributesContainerMethods.class )
    
    String getAttribute( String name );
    
    @DelegateImplementation( AttributesContainerMethods.class )
    
    <T> T getAttribute( String name, T def );
    
    // *** Method: setAttribute ***
    
    @DelegateImplementation( AttributesContainerMethods.class )
    
    void setAttribute( String name, Object value );
    
}
