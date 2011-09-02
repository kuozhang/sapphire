/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramPaletteDef 

	extends IModelElement 
	
{
	ModelElementType TYPE = new ModelElementType( IDiagramPaletteDef.class );
	
    // *** ConnectionsGroupLabel ***

    @Label( standard = "connections group label" )
    @XmlBinding( path = "connections-group-label" )
    @DefaultValue( text = "connections" )
    
    ValueProperty PROP_CONNECTIONS_GROUP_LABEL = new ValueProperty( TYPE, "ConnectionsGroupLabel" );
    
    Value<String> getConnectionsGroupLabel();
    void setConnectionsGroupLabel( String label );

    // *** NodesGroupLabel ***

    @Label( standard = "nodes group label" )
    @XmlBinding( path = "nodes-group-label" )
    @DefaultValue( text = "objects" )
    
    ValueProperty PROP_NODES_GROUP_LABEL = new ValueProperty( TYPE, "NodesGroupLabel" );
    
    Value<String> getNodesGroupLabel();
    void setNodesGroupLabel( String label );
	
}
