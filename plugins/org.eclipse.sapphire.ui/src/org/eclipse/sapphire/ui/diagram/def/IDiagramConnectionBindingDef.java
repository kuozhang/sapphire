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
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@Label( standard = "diagram connection binding" )
@Image( small = "org.eclipse.sapphire.ui/images/objects/part.gif" )
@GenerateImpl

public interface IDiagramConnectionBindingDef 

	extends ISapphirePartDef 
	
{
	ModelElementType TYPE = new ModelElementType( IDiagramConnectionBindingDef.class );
	
    // *** Property ***
    
    @Label( standard = "property" )
    @NonNullValue
    @XmlBinding( path = "property" )
    
    ValueProperty PROP_PROPERTY = new ValueProperty( TYPE, "Property" );
    
    Value<String> getProperty();
    void setProperty( String property );
    
    // *** InstanceId ***
    
    @Type( base = Function.class )
    @Label( standard = "instance ID" )
    @NonNullValue
    @XmlBinding( path = "instance-id" )
    
    ValueProperty PROP_INSTANCE_ID = new ValueProperty( TYPE, "InstanceId" );
    
    Value<Function> getInstanceId();
    void setInstanceId( String value );
    void setInstanceId( Function value );
    
    // *** Label ***
    
    @Type( base = IDiagramLabelDef.class )
    @XmlBinding( path = "label" )
    
    ElementProperty PROP_LABEL = new ElementProperty( TYPE, "Label" );
    
    ModelElementHandle<IDiagramLabelDef> getLabel();

    // *** Endpoint1 ***
    
    @Type( base = IDiagramConnectionEndpointBindingDef.class )
    @XmlBinding( path = "endpoint1" )

    ElementProperty PROP_ENDPOINT_1 = new ElementProperty( TYPE, "Endpoint1" );
    
    ModelElementHandle<IDiagramConnectionEndpointBindingDef> getEndpoint1();

    // *** Endpoint2 ***
    
    @Type( base = IDiagramConnectionEndpointBindingDef.class )
    @XmlBinding( path = "endpoint2" )

    ElementProperty PROP_ENDPOINT_2 = new ElementProperty( TYPE, "Endpoint2" );
    
    ModelElementHandle<IDiagramConnectionEndpointBindingDef> getEndpoint2();

}
