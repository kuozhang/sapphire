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

package org.eclipse.sapphire.ui.diagram.geometry;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramConnectionGeometry extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( IDiagramConnectionGeometry.class );
	
	// *** ConnectionId ***
	
	@XmlBinding( path = "id")
	@NonNullValue

	ValueProperty PROP_CONNECTION_ID = new ValueProperty( TYPE, "ConnectionId" );

    Value<String> getConnectionId();
    void setConnectionId( String name );

    // *** ConnectionBendpoints***

    @Type( base = IBendPoint.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "bendpoint", type = IBendPoint.class ) )
    
    ListProperty PROP_CONNECTION_BENDPOINTS = new ListProperty( TYPE, "ConnectionBendPoints" );
    
    ModelElementList<IBendPoint> getConnectionBendpoints();
    
    
}
