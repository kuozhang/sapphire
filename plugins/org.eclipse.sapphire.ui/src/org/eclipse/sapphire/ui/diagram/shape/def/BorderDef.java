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
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.LineStyle;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface BorderDef extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( BorderDef.class );
	
	// *** Width ***
    
    @Type( base = Integer.class )
    @Label( standard = "width" )
    @XmlBinding( path = "width" )
    @DefaultValue( text = "1" )
    
    ValueProperty PROP_WIDTH = new ValueProperty(TYPE, "Width");
    
    Value<Integer> getWidth();
    void setWidth( String value );
    void setWidth( Integer value );        
		
	// *** Color ***
	    
    @Type( base = Color.class )
    @Label( standard = "color")
    @XmlBinding( path = "color")
    @DefaultValue( text = "#000000" )
    
    ValueProperty PROP_COLOR = new ValueProperty( TYPE, "Color" );
    
    Value<Color> getColor();
    void setColor( String value );
    void setColor( Color value );    

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
