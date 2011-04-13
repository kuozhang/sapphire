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
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@Label( standard = "diagram connection binding" )
@Image( small = "org.eclipse.sapphire.ui/images/objects/part.gif" )
@GenerateImpl

public interface IDiagramExplicitConnectionBindingDef 

	extends IDiagramConnectionBindingDef 
	
{
	ModelElementType TYPE = new ModelElementType( IDiagramExplicitConnectionBindingDef.class );
	
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
