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

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.Orientation;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface SequenceLayoutDef extends ShapeLayoutDef 
{
	ModelElementType TYPE = new ModelElementType( SequenceLayoutDef.class );
    
	// *** Orientation ***
    
    @Type( base = Orientation.class )
    @Label( standard = "orientation" )
    @Localizable
    @XmlBinding( path = "orientation" )
    @DefaultValue( text = "vertical" )
    
    ValueProperty PROP_ORIENTATION = new ValueProperty( TYPE, "Orientation" );
    
    Value<Orientation> getOrientation();
    void setOrientation( String value );
    void setOrientation( Orientation value );
    
    // *** Spacing ***
        
    @Type( base = Integer.class )
    @Label( standard = "spacing" )
    @XmlBinding( path = "spacing" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_SPACING = new ValueProperty( TYPE, "Spacing" );
    
    Value<Integer> getSpacing();
    void setSpacing( String value );
    void setSpacing( Integer value );
    
	
}
