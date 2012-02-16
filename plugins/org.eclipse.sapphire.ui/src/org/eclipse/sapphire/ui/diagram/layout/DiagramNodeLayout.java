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

package org.eclipse.sapphire.ui.diagram.layout;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface DiagramNodeLayout extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( DiagramNodeLayout.class );
    
    // *** NodeId ***
    
    @XmlBinding( path = "id")
    @Required

    ValueProperty PROP_NODE_ID = new ValueProperty( TYPE, "NodeId" );

    Value<String> getNodeId();
    void setNodeId( String name );

    // *** X ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "bounds/@x" )
    
    ValueProperty PROP_X = new ValueProperty( TYPE, "X");
    
    Value<Integer> getX();
    void setX(Integer value);
    void setX(String value);

    // *** Y ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "bounds/@y" )

    ValueProperty PROP_Y = new ValueProperty( TYPE, "Y");
    
    Value<Integer> getY();
    void setY(Integer value);
    void setY(String value);
    
    // *** Width ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "bounds/@width" )
    
    ValueProperty PROP_WIDTH = new ValueProperty( TYPE, "Width");
    
    Value<Integer> getWidth();
    void setWidth(Integer value);
    void setWidth(String value);
    
    // *** Height ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "bounds/@height" )
    
    ValueProperty PROP_HEIGHT = new ValueProperty( TYPE, "Height");
    
    Value<Integer> getHeight();
    void setHeight(Integer value);
    void setHeight(String value);
    
    // *** EmbeddedConnectionGeometries ***

    @Type( base = DiagramConnectionLayout.class )
    
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "connection", type = DiagramConnectionLayout.class ) )
    
    ListProperty PROP_EMBEDDED_CONNECTION_GEOMETRIES = new ListProperty( TYPE, "EmbeddedConnectionGeometries" );
    
    ModelElementList<DiagramConnectionLayout> getEmbeddedConnectionGeometries();    
    
}
