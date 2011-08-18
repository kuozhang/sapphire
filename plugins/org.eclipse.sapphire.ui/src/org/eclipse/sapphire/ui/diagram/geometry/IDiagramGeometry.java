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
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;
import org.eclipse.sapphire.ui.diagram.def.IDiagramGridDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl
@XmlRootBinding( elementName = "diagram-geometry" )

public interface IDiagramGeometry extends IModelElement 
{
    ModelElementType TYPE = new ModelElementType( IDiagramGeometry.class );
    
    // *** GridDefinition ***
    
    @Type( base = IDiagramGridDef.class )
    @XmlBinding( path = "grid")
    
    ImpliedElementProperty PROP_GRID_DEFINITION = new ImpliedElementProperty( TYPE, "GridDefinition" );

    IDiagramGridDef getGridDefinition();    
    
    // *** ShowGuides ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "show-guides" )
    @DefaultValue( text = "false" )
    @Label( standard = "show guides")
    
    ValueProperty PROP_SHOW_GUIDES = new ValueProperty(TYPE, "ShowGuides");
    
    Value<Boolean> isShowGuides();
    void setShowGuides( String value );
    void setShowGuides( Boolean value );
    
    // *** DiagramNodeGeometries ***

    @Type( base = IDiagramNodeGeometry.class )
    
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "node", type = IDiagramNodeGeometry.class ) )
    
    ListProperty PROP_DIAGRAM_NODE_GEOMETRIES = new ListProperty( TYPE, "DiagramNodeGeometries" );
    
    ModelElementList<IDiagramNodeGeometry> getDiagramNodeGeometries();
    
    // *** DiagramConnectionGeometries ***

    @Type( base = IDiagramConnectionGeometry.class )
    
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "connection", type = IDiagramConnectionGeometry.class ) )
    
    ListProperty PROP_DIAGRAM_CONNECTION_GEOMETRIES = new ListProperty( TYPE, "DiagramConnectionGeometries" );
    
    ModelElementList<IDiagramConnectionGeometry> getDiagramConnectionGeometries();

}
