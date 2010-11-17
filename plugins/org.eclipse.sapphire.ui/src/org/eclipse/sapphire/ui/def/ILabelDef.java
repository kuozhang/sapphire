/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.java.JavaTypeConstraints;
import org.eclipse.sapphire.modeling.java.JavaTypeKind;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface ILabelDef

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ILabelDef.class );
    
    // *** Text ***
    
    @Label( standard = "text" )
    @XmlBinding( path = "text" )
    
    ValueProperty PROP_TEXT = new ValueProperty( TYPE, "Text" );
    
    Value<String> getText();
    void setText( String value );
    
    // *** Property ***
    
    @Label( standard = "property" )
    @XmlBinding( path = "property" )
    
    ValueProperty PROP_PROPERTY = new ValueProperty( TYPE, "Property" );
    
    Value<String> getProperty();
    void setProperty( String value );
    
    // *** NullValueText ***
    
    @Label( standard = "null value text" )
    @XmlBinding( path = "null-value-text" )
    
    ValueProperty PROP_NULL_VALUE_TEXT = new ValueProperty( TYPE, "NullValueText" );
    
    Value<String> getNullValueText();
    void setNullValueText( String value );
    
    // *** ProviderClass ***
    
    @Reference( target = Class.class )
    @Label( standard = "provider class" )
    @JavaTypeConstraints( kind = JavaTypeKind.CLASS, type = "org.eclipse.sapphire.modeling.Expression" )
    @MustExist
    @XmlBinding( path = "class" )
    
    ValueProperty PROP_PROVIDER_CLASS = new ValueProperty( TYPE, "ProviderClass" );
    
    ReferenceValue<Class<?>> getProviderClass();
    void setProviderClass( String value );
    
}
