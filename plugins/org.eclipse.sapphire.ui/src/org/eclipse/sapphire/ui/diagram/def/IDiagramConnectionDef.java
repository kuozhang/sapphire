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
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
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
    
    // *** Property ***
    
    @Label( standard = "property" )
    @XmlBinding( path = "property" )
    
    ValueProperty PROP_PROPERTY = new ValueProperty( TYPE, "Property" );
    
    Value<String> getProperty();
    void setProperty( String property );
    
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
        
    // *** Label ***
    
    @Type( base = IDiagramLabelDef.class )
    @XmlBinding( path = "label" )
    
    ElementProperty PROP_LABEL = new ElementProperty( TYPE, "Label" );
    
    ModelElementHandle<IDiagramLabelDef> getLabel();

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
    
    // *** InstanceId ***
    
    @Type( base = Function.class )
    @Label( standard = "instance id" )
    @Localizable
    @XmlBinding( path = "instance-id" )
    
    ValueProperty PROP_INSTANCE_ID = new ValueProperty( TYPE, "InstanceId" );
    
    Value<Function> getInstanceId();
    void setInstanceId( String value );
    void setInstanceId( Function value );
            
}
