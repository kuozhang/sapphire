/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation
 ******************************************************************************/

package org.eclipse.sapphire.samples.architecture;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl
public interface Bounds extends IModelElement
{
	ModelElementType TYPE = new ModelElementType( Bounds.class );
	
    // *** X ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "@x" )
    @DefaultValue( text = "-1" )
    
    ValueProperty PROP_X = new ValueProperty( TYPE, "X");
    
    Value<Integer> getX();
    void setX(Integer value);
    void setX(String value);

    // *** Y ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "@y" )
    @DefaultValue( text = "-1" )

    ValueProperty PROP_Y = new ValueProperty( TYPE, "Y");
    
    Value<Integer> getY();
    void setY(Integer value);
    void setY(String value);
    
    // *** Width ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "@width" )
    @DefaultValue( text = "-1" )
    
    ValueProperty PROP_WIDTH = new ValueProperty( TYPE, "Width");
    
    Value<Integer> getWidth();
    void setWidth(Integer value);
    void setWidth(String value);
    
    // *** Height ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "@height" )
    @DefaultValue( text = "-1" )
    
    ValueProperty PROP_HEIGHT = new ValueProperty( TYPE, "Height");
    
    Value<Integer> getHeight();
    void setHeight(Integer value);
    void setHeight(String value);
    
	
}
