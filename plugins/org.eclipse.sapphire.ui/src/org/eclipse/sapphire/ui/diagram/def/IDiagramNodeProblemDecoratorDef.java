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
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;
import org.eclipse.sapphire.ui.def.VerticalAlignment;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramNodeProblemDecoratorDef 
	
	extends ISapphirePartDef 

{
	ModelElementType TYPE = new ModelElementType( IDiagramNodeProblemDecoratorDef.class );

	// *** ShowDecorator ***
	
    @Type( base = Boolean.class )
    @XmlBinding( path = "show-decorator" )
    @DefaultValue( text = "true" )
    
    ValueProperty PROP_SHOW_DECORATOR = new ValueProperty(TYPE, "ShowDecorator");
    
    Value<Boolean> isShowDecorator();
    void setShowDecorator( String value );
    void setShowDecorator( Boolean value );
    
    // *** DecoratorPlacement ***
    
    @Type( base = DecoratorPlacement.class )
    @Label( standard = "placement")
    @DefaultValue( text = "image" )
    @Enablement( expr = "${ ShowDecorator }" )
    @XmlBinding( path = "decorator-placement" )
    
    ValueProperty PROP_DECORATOR_PLACEMENT = new ValueProperty( TYPE, "DecoratorPlacement" );
    
    Value<DecoratorPlacement> getDecoratorPlacement();
    void setDecoratorPlacement( String value );
    void setDecoratorPlacement( DecoratorPlacement value ) ;
    
    // *** Size ***
    
    @Type( base = ProblemDecoratorSize.class )
    @Label( standard = "size")
    @DefaultValue( text = "large" )
    @Enablement( expr = "${ ShowDecorator }" )
    @XmlBinding( path = "size" )
    
    ValueProperty PROP_SIZE = new ValueProperty( TYPE, "Size" );
    
    Value<ProblemDecoratorSize> getSize();
    void setSize( String value );
    void setSize( ProblemDecoratorSize value ) ;

    // *** HorizontalAlignment ***
    
    @Type( base = HorizontalAlignment.class )
    @Label( standard = "horizontal alignment")
    @DefaultValue( text = "left" )
    @Enablement( expr = "${ ShowDecorator }" )
    @XmlBinding( path = "horizontal-align" )
    
    ValueProperty PROP_HORIZONTAL_ALIGNMENT = new ValueProperty( TYPE, "HorizontalAlignment" );
    
    Value<HorizontalAlignment> getHorizontalAlignment();
    void setHorizontalAlignment( String value );
    void setHorizontalAlignment( HorizontalAlignment value ) ;
	
    // *** VerticalAlignment ***
    
    @Type( base = VerticalAlignment.class )
    @Label( standard = "vertical alignment")
    @DefaultValue( text = "bottom" )
    @Enablement( expr = "${ ShowDecorator }" )
    @XmlBinding( path = "vertical-align" )
    
    ValueProperty PROP_VERTICAL_ALIGNMENT = new ValueProperty( TYPE, "VerticalAlignment" );
    
    Value<VerticalAlignment> getVerticalAlignment();
    void setVerticalAlignment( String value );
    void setVerticalAlignment( VerticalAlignment value ) ;
    
    // *** VerticalMargin ***
	
    @Type( base = Integer.class )
    @Label( standard = "vertical margin" )
    @DefaultValue( text = "0" )
    @Enablement( expr = "${ ShowDecorator }" )
    @XmlBinding( path = "vertical-margin" )
    
    ValueProperty PROP_VERTICAL_MARGIN = new ValueProperty( TYPE, "VerticalMargin" );
    
    Value<Integer> getVerticalMargin();
    void setVerticalMargin( String value );
    void setVerticalMargin( Integer value );
    
    // *** HorizontalMargin ***
	
    @Type( base = Integer.class )
    @Label( standard = "horizontal margin" )
    @DefaultValue( text = "0" )
    @Enablement( expr = "${ ShowDecorator }" )
    @XmlBinding( path = "horizontal-margin" )
    
    ValueProperty PROP_HORIZONTAL_MARGIN = new ValueProperty( TYPE, "HorizontalMargin" );
    
    Value<Integer> getHorizontalMargin();
    void setHorizontalMargin( String value );
    void setHorizontalMargin( Integer value );
    
}
