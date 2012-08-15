/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.shape.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface FontDef extends IModelElement
{
	ModelElementType TYPE = new ModelElementType( FontDef.class );
	
	// *** Name ***

	@Label( standard = "name" )
    @XmlBinding( path = "name" )
    @Required
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String name );
	
	// *** Size ***
	
    @Type( base = Integer.class )
    @Label( standard = "size" )
    @XmlBinding( path = "size" )
    
    ValueProperty PROP_SIZE = new ValueProperty( TYPE, "Size" );
    
    Value<Integer> getSize();
    void setSize( String value );
    void setSize( Integer value );
		
	// *** Bold ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "bold" )
    @DefaultValue( text = "false" )
    @Label( standard = "bold")
    
    ValueProperty PROP_BOLD = new ValueProperty(TYPE, "Bold");
    
    Value<Boolean> isBold();
    void setBold( String value );
    void setBold( Boolean value );    
    	
	// *** Italic ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "italic" )
    @DefaultValue( text = "false" )
    @Label( standard = "italic")
    
    ValueProperty PROP_ITALIC = new ValueProperty(TYPE, "Italic");
    
    Value<Boolean> isItalic();
    void setItalic( String value );
    void setItalic( Boolean value );    
    
}
