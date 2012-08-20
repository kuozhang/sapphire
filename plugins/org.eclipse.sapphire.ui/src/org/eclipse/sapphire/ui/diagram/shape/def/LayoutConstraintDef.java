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

package org.eclipse.sapphire.ui.diagram.shape.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.def.VerticalAlignment;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface LayoutConstraintDef extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( LayoutConstraintDef.class );
	
    // *** Width ***
    
    @Type( base = Integer.class )
    @Label( standard = "width" )
    @XmlBinding( path = "width" )
    @DefaultValue( text = "-1" )
    
    ValueProperty PROP_WIDTH = new ValueProperty( TYPE, "Width" );
    
    Value<Integer> getWidth();
    void setWidth( String value );
    void setWidth( Integer value );
	
    // *** Height ***
    
    @Type( base = Integer.class )
    @Label( standard = "height" )
    @XmlBinding( path = "height" )
    @DefaultValue( text = "-1" )
    
    ValueProperty PROP_HEIGHT = new ValueProperty( TYPE, "Height" );
    
    Value<Integer> getHeight();
    void setHeight( String value );
    void setHeight( Integer value );
	
	// *** HorizontalAlignment ***
    
    @Type( base = HorizontalAlignment.class )
    @Label( standard = "horizontal alignment")
    @Localizable
    @XmlBinding( path = "horizontal-alignment" )
    @DefaultValue( text = "center" )
    
    ValueProperty PROP_HORIZONTAL_ALIGNMENT = new ValueProperty( TYPE, "HorizontalAlignment" );
    
    Value<HorizontalAlignment> getHorizontalAlignment();
    void setHorizontalAlignment( String value );
    void setHorizontalAlignment( HorizontalAlignment value );
    
	// *** VerticalAlignment ***
    
    @Type( base = VerticalAlignment.class )
    @Label( standard = "vertical alignment")
    @Localizable
    @XmlBinding( path = "vertical-alignment" )
    @DefaultValue( text = "center" )
    
    ValueProperty PROP_VERTICAL_ALIGNMENT = new ValueProperty( TYPE, "VerticalAlignment" );
    
    Value<VerticalAlignment> getVerticalAlignment();
    void setVerticalAlignment( String value );
    void setVerticalAlignment( VerticalAlignment value );
    
    // *** Margin ***
    
    @Type( base = Integer.class )
    @Label( standard = "margin" )
    @XmlBinding( path = "margin" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_MARGIN = new ValueProperty( TYPE, "Margin" );
    
    Value<Integer> getMargin();
    void setMargin( String value );
    void setMargin( Integer value );
    
    // *** HorizontalMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "horizontal margin" )
    @XmlBinding( path = "horizontal-margin" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_HORIZONTAL_MARGIN = new ValueProperty( TYPE, "HorizontalMargin" );
    
    Value<Integer> getHorizontalMargin();
    void setHorizontalMargin( String value );
    void setHorizontalMargin( Integer value );
    
    // *** VerticalMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "vertical margin" )
    @XmlBinding( path = "vertical-margin" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_VERTICAL_MARGIN = new ValueProperty( TYPE, "VerticalMargin" );
    
    Value<Integer> getVerticalMargin();
    void setVerticalMargin( String value );
    void setVerticalMargin( Integer value );
    
    // *** LeftMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "left margin" )
    @XmlBinding( path = "left-margin" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_LEFT_MARGIN = new ValueProperty( TYPE, "LeftMargin" );
    
    Value<Integer> getLeftMargin();
    void setLeftMargin( String value );
    void setLeftMargin( Integer value );
    
    // *** RightMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "right margin" )
    @XmlBinding( path = "right-margin" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_RIGHT_MARGIN = new ValueProperty( TYPE, "RightMargin" );
    
    Value<Integer> getRightMargin();
    void setRightMargin( String value );
    void setRightMargin( Integer value );
    
    // *** TopMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "top margin" )
    @XmlBinding( path = "top-margin" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_TOP_MARGIN = new ValueProperty( TYPE, "TopMargin" );
    
    Value<Integer> getTopMargin();
    void setTopMargin( String value );
    void setTopMargin( Integer value );
    
    // *** BottomMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "bottom margin" )
    @XmlBinding( path = "bottom-margin" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_BOTTOM_MARGIN = new ValueProperty( TYPE, "BottomMargin" );
    
    Value<Integer> getBottomMargin();
    void setBottomMargin( String value );
    void setBottomMargin( Integer value );
}
