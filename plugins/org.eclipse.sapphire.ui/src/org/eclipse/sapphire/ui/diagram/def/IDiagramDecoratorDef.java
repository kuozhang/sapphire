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

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramDecoratorDef 
	
	extends ISapphirePartDef 

{
	ModelElementType TYPE = new ModelElementType( IDiagramDecoratorDef.class );

	// *** ShowDecorator ***
	
    @Type( base = Boolean.class )
    @XmlBinding( path = "show-decorator" )
    
    ValueProperty PROP_SHOW_DECORATOR = new ValueProperty(TYPE, "ShowDecorator");
    
    Value<Boolean> isShowDecorator();
    void setShowDecorator( String value );
    void setShowDecorator( Boolean value );
    
    // *** DecoratorPlacement ***
    
    @Type( base = DecoratorPlacement.class )
    @Label( standard = "decorator placement")
    @XmlBinding( path = "decorator-placement" )
    
    ValueProperty PROP_DECORATOR_PLACEMENT = new ValueProperty( TYPE, "DecoratorPlacement" );
    
    Value<DecoratorPlacement> getDecoratorPlacement();
    void setDecoratorPlacement( String value );
    void setDecoratorPlacement( DecoratorPlacement value ) ;
    
    // *** HorizontalAlign ***
    
    @Type( base = Alignment.class )
    @Label( standard = "horizontal alignment")
    @XmlBinding( path = "horizontal-align" )
    
    ValueProperty PROP_HORIZONTAL_ALIGN = new ValueProperty( TYPE, "HorizontalAlign" );
    
    Value<Alignment> getHorizontalAlign();
    void setHorizontalAlign( String value );
    void setHorizontalAlign( Alignment value ) ;
	
    // *** VerticalAlign ***
    
    @Type( base = Alignment.class )
    @Label( standard = "vertical alignment")
    @XmlBinding( path = "vertical-align" )
    
    ValueProperty PROP_VERTICAL_ALIGN = new ValueProperty( TYPE, "VerticalAlign" );
    
    Value<Alignment> getVerticalAlign();
    void setVerticalAlign( String value );
    void setVerticalAlign( Alignment value ) ;
    
    // *** TopMargin ***
	
    @Type( base = Integer.class )
    @Label( standard = "top margin" )
    @XmlBinding( path = "top-margin" )
    
    ValueProperty PROP_TOP_MARGIN = new ValueProperty( TYPE, "TopMargin" );
    
    Value<Integer> getTopMargin();
    void setTopMargin( String width );
    void setTopMargin( Integer width );
    
    // *** BottomMargin ***
	
    @Type( base = Integer.class )
    @Label( standard = "bottom margin" )
    @XmlBinding( path = "bottom-margin" )
    
    ValueProperty PROP_BOTTOM_MARGIN = new ValueProperty( TYPE, "BottomMargin" );
    
    Value<Integer> getBottomMargin();
    void setBottomMargin( String width );
    void setBottomMargin( Integer width );
    
    // *** LeftMargin ***

    @Type( base = Integer.class )
    @Label( standard = "left margin" )
    @XmlBinding( path = "left-margin" )
    
    ValueProperty PROP_LEFT_MARGIN = new ValueProperty( TYPE, "LeftMargin" );
    
    Value<Integer> getLeftMargin();
    void setLeftMargin( String width );
    void setLeftMargin( Integer width );
    
    // *** BottomMargin ***
	
    @Type( base = Integer.class )
    @Label( standard = "right margin" )
    @XmlBinding( path = "right-margin" )
    
    ValueProperty PROP_RIGHT_MARGIN = new ValueProperty( TYPE, "RightMargin" );
    
    Value<Integer> getRightMargin();
    void setRightMargin( String width );
    void setRightMargin( Integer width );
        
    
}
