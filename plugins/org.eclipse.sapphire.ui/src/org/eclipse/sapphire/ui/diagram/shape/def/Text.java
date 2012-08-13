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

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface Text extends Shape 
{
	ModelElementType TYPE = new ModelElementType( Text.class );
	
	// *** Content ***
	
    @Type( base = Function.class )
    @Label( standard = "content" )
    @Localizable
    @Required
    @XmlBinding( path = "content" )
    
    ValueProperty PROP_CONTENT = new ValueProperty( TYPE, "Content" );
    
    Value<Function> getContent();
    void setContent( String value );
    void setContent( Function value );
    
	// *** Color ***
    
    @Type( base = Function.class )
    @Label( standard = "color")
    @XmlBinding( path = "color")
    
    ValueProperty PROP_COLOR = new ValueProperty( TYPE, "Color" );
    
    Value<Function> getColor();
    void setColor( String value );
    void setColor( Function value );
    
    // *** Font ***
    
    @Type( base = Font.class )
    @XmlBinding( path = "font" )
    
    ElementProperty PROP_FONT = new ElementProperty( TYPE, "Font" );
    
    ModelElementHandle<Font> getFont();
    
	
}
