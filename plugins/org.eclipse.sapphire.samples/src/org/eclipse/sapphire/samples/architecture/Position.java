/******************************************************************************
 * Copyright (c) 2013 Oracle
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
public interface Position extends IModelElement
{
	ModelElementType TYPE = new ModelElementType( Position.class );
	
    // *** X ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "@x" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_X = new ValueProperty( TYPE, "X");
    
    Value<Integer> getX();
    void setX(Integer value);
    void setX(String value);

    // *** Y ***
    
    @Type( base = Integer.class )
    @XmlBinding( path = "@y" )
    @DefaultValue( text = "0" )

    ValueProperty PROP_Y = new ValueProperty( TYPE, "Y");
    
    Value<Integer> getY();
    void setY(Integer value);
    void setY(String value);
    	
}
