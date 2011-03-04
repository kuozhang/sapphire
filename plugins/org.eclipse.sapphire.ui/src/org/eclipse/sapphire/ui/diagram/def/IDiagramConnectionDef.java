/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.LineStyle;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramConnectionDef 
	
	extends ISapphirePartDef 
	
{
	ModelElementType TYPE = new ModelElementType( IDiagramConnectionDef.class );
	
    // *** Id ***
    
    @Label( standard = "ID" )
    @XmlBinding( path = "id" )
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );
    
    Value<String> getId();
    void setId( String id );
    
    // *** ToolPaletteLabel ***
    
    @Label( standard = "tool palette label" )
    @XmlBinding( path = "tool-palette-label" )
    
    ValueProperty PROP_TOOL_PALETTE_LABEL = new ValueProperty( TYPE, "ToolPaletteLabel" );
    
    Value<String> getToolPaletteLabel();
    void setToolPaletteLabel( String paletteLabel );
    
    // *** ToolPaletteDesc ***
    
    @Label( standard = "tool palette description" )
    @XmlBinding( path = "tool-palette-desc" )
    
    ValueProperty PROP_TOOL_PALETTE_DESC = new ValueProperty( TYPE, "ToolPaletteDesc" );
    
    Value<String> getToolPaletteDesc();
    void setToolPaletteDesc( String paletteDesc );    
        
    // *** Endpoint1 ***
    
    @Type( base = IDiagramConnectionEndpointDef.class )
    @XmlBinding( path = "endpoint1" )

    ElementProperty PROP_ENDPOINT_1 = new ElementProperty( TYPE, "Endpoint1" );
    
    ModelElementHandle<IDiagramConnectionEndpointDef> getEndpoint1();

    // *** Endpoint2 ***
    
    @Type( base = IDiagramConnectionEndpointDef.class )
    @XmlBinding( path = "endpoint2" )

    ElementProperty PROP_ENDPOINT_2 = new ElementProperty( TYPE, "Endpoint2" );
    
    ModelElementHandle<IDiagramConnectionEndpointDef> getEndpoint2();
        
    // *** LineStyle ***
    
    @Type( base = LineStyle.class )
    @Label( standard = "line style")
    @Localizable
    @XmlBinding( path = "line-style" )
    @DefaultValue( text = "solid" )
    
    ValueProperty PROP_LINE_STYLE = new ValueProperty( TYPE, "LineStyle" );
    
    Value<LineStyle> getLineStyle();
    void setLineStyle( String value );
    void setLineStyle( LineStyle value ) ;
    
    // *** LineColor ***
    
    @Type( base = Color.class )
    @Label( standard = "line color")
    @Localizable
    @XmlBinding( path = "line-color")
    @DefaultValue( text = "#333399" )
    
    ValueProperty PROP_LINE_COLOR = new ValueProperty( TYPE, "LineColor" );
    
    Value<Color> getLineColor();
    void setLineColor( String value );
    void setLineColor( Color value );
}
