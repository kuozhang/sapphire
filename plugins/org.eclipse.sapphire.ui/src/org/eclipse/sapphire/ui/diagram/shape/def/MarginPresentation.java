/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.shape.def;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface MarginPresentation extends Element 
{
	ElementType TYPE = new ElementType( MarginPresentation.class );
	
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
    @DefaultValue( text = "${ Margin }" )
    
    ValueProperty PROP_HORIZONTAL_MARGIN = new ValueProperty( TYPE, "HorizontalMargin" );
    
    Value<Integer> getHorizontalMargin();
    void setHorizontalMargin( String value );
    void setHorizontalMargin( Integer value );
    
    // *** VerticalMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "vertical margin" )
    @XmlBinding( path = "vertical-margin" )
    @DefaultValue( text = "${ Margin }" )
    
    ValueProperty PROP_VERTICAL_MARGIN = new ValueProperty( TYPE, "VerticalMargin" );
    
    Value<Integer> getVerticalMargin();
    void setVerticalMargin( String value );
    void setVerticalMargin( Integer value );
    
    // *** TopMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "top margin" )
    @XmlBinding( path = "top-margin" )
    @DefaultValue( text = "${ VerticalMargin }" )
    
    ValueProperty PROP_TOP_MARGIN = new ValueProperty( TYPE, "TopMargin" );
    
    Value<Integer> getTopMargin();
    void setTopMargin( String value );
    void setTopMargin( Integer value );
    
    // *** BottomMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "bottom margin" )
    @XmlBinding( path = "bottom-margin" )
    @DefaultValue( text = "${ VerticalMargin }" )
    
    ValueProperty PROP_BOTTOM_MARGIN = new ValueProperty( TYPE, "BottomMargin" );
    
    Value<Integer> getBottomMargin();
    void setBottomMargin( String value );
    void setBottomMargin( Integer value );

    // *** LeftMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "left margin" )
    @XmlBinding( path = "left-margin" )
    @DefaultValue( text = "${ HorizontalMargin }" )
    
    ValueProperty PROP_LEFT_MARGIN = new ValueProperty( TYPE, "LeftMargin" );
    
    Value<Integer> getLeftMargin();
    void setLeftMargin( String value );
    void setLeftMargin( Integer value );
    
    // *** RightMargin ***
    
    @Type( base = Integer.class )
    @Label( standard = "right margin" )
    @XmlBinding( path = "right-margin" )
    @DefaultValue( text = "${ HorizontalMargin }" )
    
    ValueProperty PROP_RIGHT_MARGIN = new ValueProperty( TYPE, "RightMargin" );
    
    Value<Integer> getRightMargin();
    void setRightMargin( String value );
    void setRightMargin( Integer value );
    
}
