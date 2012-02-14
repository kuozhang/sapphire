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
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.ui.def.IPropertiesViewContributorDef;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;
import org.eclipse.sapphire.ui.diagram.def.internal.ToolPaletteCompartmentPossibleValuesService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@Label( standard = "diagram node" )
@GenerateImpl

public interface IDiagramNodeDef 

    extends ISapphirePartDef, IDiagramDimension, IPropertiesViewContributorDef
    
{
    ModelElementType TYPE = new ModelElementType( IDiagramNodeDef.class );
    
    // *** Id ***
    
    @Required
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, ISapphirePartDef.PROP_ID );
    
    // *** InstanceId ***
    
    @Type( base = Function.class )
    @Label( standard = "instance ID" )
    @XmlBinding( path = "instance-id" )
    
    ValueProperty PROP_INSTANCE_ID = new ValueProperty( TYPE, "InstanceId" );
    
    Value<Function> getInstanceId();
    void setInstanceId( String value );
    void setInstanceId( Function value );
        
    // *** ToolPaletteLabel ***
    
    @Label( standard = "tool palette item label" )
    @Required
    @Localizable
    @XmlBinding( path = "tool-palette-label" )
    
    ValueProperty PROP_TOOL_PALETTE_LABEL = new ValueProperty( TYPE, "ToolPaletteLabel" );
    
    Value<String> getToolPaletteLabel();
    void setToolPaletteLabel( String paletteLabel );
    
    // *** ToolPaletteDescription ***
    
    @Label( standard = "tool palette item description" )
    @LongString
    @Localizable
    @XmlBinding( path = "tool-palette-desc" )
    
    ValueProperty PROP_TOOL_PALETTE_DESCRIPTION = new ValueProperty( TYPE, "ToolPaletteDescription" );
    
    Value<String> getToolPaletteDescription();
    void setToolPaletteDescription( String paletteDesc );
    
    // *** ToolPaletteImage ***

    @Type( base = IDiagramImageChoice.class )
    @Label( standard = "tool palette item image" )
    @XmlBinding( path = "tool-palette-image" )

    ElementProperty PROP_TOOL_PALETTE_IMAGE = new ElementProperty( TYPE, "ToolPaletteImage" );
    
    ModelElementHandle<IDiagramImageChoice> getToolPaletteImage();
        
    // *** ToolPaletteCompartment ***

    @Label( standard = "tool palette compartment" )
    @XmlBinding( path = "tool-palette-compartment" )    
    @DefaultValue( text = "Sapphire.Diagram.Palette.Nodes" )
    @Service( impl = ToolPaletteCompartmentPossibleValuesService.class )
    @DependsOn("../PaletteCompartments/Id")
    
    ValueProperty PROP_TOOL_PALETTE_COMPARTMENT = new ValueProperty( TYPE, "ToolPaletteCompartment" );
    
    Value<String> getToolPaletteCompartment();
    void setToolPaletteCompartment( String value );
    
    // *** Property ***
    
    @Label( standard = "property" )
    @XmlBinding( path = "property" )
    @Required
    
    ValueProperty PROP_PROPERTY = new ValueProperty( TYPE, "Property" );
    
    Value<String> getProperty();
    void setProperty( String property );
        
    // *** ElementType ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "model element type" )
    @JavaTypeConstraint( kind = JavaTypeKind.INTERFACE, type = "org.eclipse.sapphire.modeling.IModelElement" )
    @MustExist
    @XmlBinding( path = "model-element-type" )
    
    ValueProperty PROP_ELEMENT_TYPE = new ValueProperty( TYPE, "ElementType" );
    
    ReferenceValue<JavaTypeName,JavaType> getElementType();
    void setElementType( String value );
    void setElementType( JavaTypeName value );
    
    // *** Resizable ***
    
    @Type( base = Boolean.class )
    @Label( standard = "resizable" )
    @XmlBinding( path = "resizable" )
    @DefaultValue( text = "false" )
    
    ValueProperty PROP_RESIZABLE = new ValueProperty( TYPE, "Resizable" );
    
    Value<Boolean> isResizable();
    void setResizable( String value );
    void setResizable( Boolean value );
    
    // *** HorizontalSpacing ***
    
    @Type( base = Integer.class )
    @Label( standard = "horizontal spacing" )
    @XmlBinding( path = "horizontal-spacing" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_HORIZONTAL_SPACING = new ValueProperty( TYPE, "HorizontalSpacing" );
    
    Value<Integer> getHorizontalSpacing();
    void setHorizontalSpacing( String value );
    void setHorizontalSpacing( Integer value );
    
    // *** VerticalSpacing ***
    
    @Type( base = Integer.class )
    @Label( standard = "vertical spacing" )
    @XmlBinding( path = "vertical-spacing" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_VERTICAL_SPACING = new ValueProperty( TYPE, "VerticalSpacing" );
    
    Value<Integer> getVerticalSpacing();
    void setVerticalSpacing( String value );
    void setVerticalSpacing( Integer value );

    // *** Image ***
    
    @Type( base = IDiagramNodeImageDef.class )
    @XmlBinding( path = "image" )

    ElementProperty PROP_IMAGE = new ElementProperty( TYPE, "Image" );
    
    ModelElementHandle<IDiagramNodeImageDef> getImage();
    
    // *** Label ***
    
    @Type( base = IDiagramLabelDef.class )
    @XmlBinding( path = "label" )
    
    ElementProperty PROP_LABEL = new ElementProperty( TYPE, "Label" );
    
    ModelElementHandle<IDiagramLabelDef> getLabel();
            
    // *** ProblemDecorator ***
    
    @Type( base = IDiagramNodeProblemDecoratorDef.class )
    @XmlBinding( path = "problem-decorator" )

    ImpliedElementProperty PROP_PROBLEM_DECORATOR = new ImpliedElementProperty( TYPE, "ProblemDecorator" );
    
    IDiagramNodeProblemDecoratorDef getProblemDecorator();
    
    // *** ImageDecorators ***
    
    @Type( base = IDiagramImageDecoratorDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "image-decorator", type = IDiagramImageDecoratorDef.class ) )
                             
    ListProperty PROP_IMAGE_DECORATORS = new ListProperty( TYPE, "ImageDecorators" );
    
    ModelElementList<IDiagramImageDecoratorDef> getImageDecorators();
    
    // *** EmbeddedConnections ***
    
    @Type( base = IDiagramExplicitConnectionBindingDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "connection-binding", type = IDiagramExplicitConnectionBindingDef.class ) )

    ListProperty PROP_EMBEDDED_CONNECTIONS = new ListProperty( TYPE, "EmbeddedConnections" );
    
    ModelElementList<IDiagramExplicitConnectionBindingDef> getEmbeddedConnections();
            
    // *** VisibleWhen ***
    
    @Type( base = Function.class )
    @XmlBinding( path = "visible-when" )
    @Label( standard = "visible when" )
    
    ValueProperty PROP_VISIBLE_WHEN = new ValueProperty(TYPE, "VisibleWhen");
    
    Value<Function> getVisibleWhen();
    void setVisibleWhen( String value );
    void setVisibleWhen( Function value );        

}
