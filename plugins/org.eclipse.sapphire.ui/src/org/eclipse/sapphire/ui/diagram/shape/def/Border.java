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
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.LineStyle;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface Border extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( Border.class );
	
	// *** Width ***
    
    @Type( base = Function.class )
    @XmlBinding( path = "width" )
    @Label( standard = "width" )
    
    ValueProperty PROP_WIDTH = new ValueProperty(TYPE, "Width");
    
    Value<Function> getWidth();
    void setWidth( String value );
    void setWidth( Function value );        
		
	// *** Color ***
	    
    @Type( base = Function.class )
    @Label( standard = "color")
    @XmlBinding( path = "color")
    
    ValueProperty PROP_COLOR = new ValueProperty( TYPE, "Color" );
    
    Value<Function> getColor();
    void setColor( String value );
    void setColor( Function value );
    
    // *** Style ***
    
    @Type( base = LineStyle.class )
    @Label( standard = "style")
    @XmlBinding( path = "style" )
    @DefaultValue( text = "solid" )
    
    ValueProperty PROP_STYLE = new ValueProperty( TYPE, "Style" );
    
    Value<LineStyle> getStyle();
    void setStyle( String value );
    void setStyle( LineStyle value ) ;
    	
}
