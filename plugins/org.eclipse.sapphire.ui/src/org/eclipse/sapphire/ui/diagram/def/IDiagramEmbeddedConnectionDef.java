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
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramEmbeddedConnectionDef 

	extends IDiagramConnectionDef
	
{
	ModelElementType TYPE = new ModelElementType( IDiagramEmbeddedConnectionDef.class );
	            	    
    // *** Endpoint ***
    
    @Type( base = IDiagramConnectionEndpointDef.class )
    @XmlBinding( path = "endpoint" )

    ElementProperty PROP_ENDPOINT = new ElementProperty( TYPE, "Endpoint" );
    
    ModelElementHandle<IDiagramConnectionEndpointDef> getEndpoint();

}
