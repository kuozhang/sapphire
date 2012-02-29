/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 *    Ling Hao - [344319] Image specification for diagram parts inconsistent with the rest of sdef 
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

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
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.ui.def.IEditorPageDef;
import org.eclipse.sapphire.ui.def.IPropertiesViewContributorDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@Label( standard = "diagram editor page" )
@GenerateImpl

public interface IDiagramEditorPageDef 
    
    extends IEditorPageDef, IPropertiesViewContributorDef
    
{
    ModelElementType TYPE = new ModelElementType( IDiagramEditorPageDef.class);
    
    // *** GridDefinition ***
    
    @Type( base = IDiagramGridDef.class )
    @XmlBinding( path = "grid")
    
    ImpliedElementProperty PROP_GRID_DEFINITION = new ImpliedElementProperty( TYPE, "GridDefinition" );

    IDiagramGridDef getGridDefinition();    
    
    // *** GuidesDefinition ***
    
    @Type( base = IDiagramGuidesDef.class )
    @XmlBinding( path = "guides")
    
    ImpliedElementProperty PROP_GUIDES_DEFINITION = new ImpliedElementProperty( TYPE, "GuidesDefinition" );

    IDiagramGuidesDef getGuidesDefinition(); 
    
    // *** LayoutStorage ***
    
    @Type( base = LayoutStorage.class )
    @Label( standard = "layout-storage")
    @Localizable
    @XmlBinding( path = "layout-storage" )
    @DefaultValue( text = "workspace" )
    
    ValueProperty PROP_LAYOUT_STORAGE = new ValueProperty( TYPE, "LayoutStorage" );
    
    Value<LayoutStorage> getLayoutStorage();
    void setLayoutStorage( String value );
    void setLayoutStorage( LayoutStorage value );
    
    // *** PaletteCompartments ***
    
    @Type( base = IDiagramPaletteCompartmentDef.class )
    @Label( standard = "palette compartments")
    @XmlListBinding( path = "palette", mappings = @XmlListBinding.Mapping( element = "compartment", type = IDiagramPaletteCompartmentDef.class ) )
                             
    ListProperty PROP_PALETTE_COMPARTMENTS = new ListProperty( TYPE, "PaletteCompartments" );
    
    ModelElementList<IDiagramPaletteCompartmentDef> getPaletteCompartments();
    
    // *** Property ***
    
    @Label( standard = "property" )
    @XmlBinding( path = "property" )
    
    ValueProperty PROP_PROPERTY = new ValueProperty( TYPE, "Property" );
    
    Value<String> getProperty();
    void setProperty( String property );
    
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
    
    // *** DiagramConnectionBindingDefs ***
    
    @Type( base = IDiagramExplicitConnectionBindingDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "connection-binding", type = IDiagramExplicitConnectionBindingDef.class ) )
                             
    ListProperty PROP_DIAGRAM_CONNECTION_BINDING_DEFS = new ListProperty( TYPE, "DiagramConnectionBindingDefs" );
    
    ModelElementList<IDiagramExplicitConnectionBindingDef> getDiagramConnectionBindingDefs();
    
    // *** ImplicitConnectionBindingDefs ***
    
    @Type( base = IDiagramImplicitConnectionBindingDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "implicit-connection-binding", type = IDiagramImplicitConnectionBindingDef.class ) )
                             
    ListProperty PROP_IMPLICIT_CONNECTION_BINDING_DEFS = new ListProperty( TYPE, "ImplicitConnectionBindingDefs" );
    
    ModelElementList<IDiagramImplicitConnectionBindingDef> getImplicitConnectionBindingDefs();
    
}
