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
import org.eclipse.sapphire.ui.def.ISapphirePartDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramPageDef 
	
	extends ISapphirePartDef
	
{
	ModelElementType TYPE = new ModelElementType( IDiagramPageDef.class);
	
    // *** Id ***
    
    @Label( standard = "ID" )
    @XmlBinding( path = "id" )
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );
    
    Value<String> getId();
    void setId( String id );
    
    // *** PageName ***
    
    @Label( standard = "page name" )
    @DefaultValue( text = "Diagram" )
    @XmlBinding( path = "page-name" )
    
    ValueProperty PROP_PAGE_NAME = new ValueProperty( TYPE, "PageName" );
    
    Value<String> getPageName();
    void setPageName( String pageName );
    
    // *** PageHeaderText ***
    
    @Label( standard = "page header text" )
    @DefaultValue( text = "Diagram View" )
    @XmlBinding( path = "page-header-text" )
    
    ValueProperty PROP_PAGE_HEADER_TEXT = new ValueProperty( TYPE, "PageHeaderText" );
    
    Value<String> getPageHeaderText();
    void setPageHeaderText( String pageHeaderText );
	
	// *** DiagramNodeDefs ***
    
    @Type( base = IDiagramNodeDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "node", type = IDiagramNodeDef.class ) )
                             
    ListProperty PROP_DIAGRAM_NODE_DEFS = new ListProperty( TYPE, "DiagramNodeDefs" );
    
    ModelElementList<IDiagramNodeDef> getDiagramNodeDefs();
    
	// *** DiagramConnectionDefs ***
    
    @Type( base = IDiagramConnectionDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "connection", type = IDiagramConnectionDef.class ) )
                             
    ListProperty PROP_DIAGRAM_CONNECTION_DEFS = new ListProperty( TYPE, "DiagramConnectionDefs" );
    
    ModelElementList<IDiagramConnectionDef> getDiagramConnectionDefs();
	
}
