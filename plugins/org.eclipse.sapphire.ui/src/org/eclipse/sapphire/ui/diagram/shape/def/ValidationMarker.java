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
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface ValidationMarker extends Shape 
{
	ModelElementType TYPE = new ModelElementType( ValidationMarker.class );
	
    // *** Size ***
    	
    @Type( base = ValidationMarkerSize.class )
    @Label( standard = "size")
    @DefaultValue( text = "large" )
    @XmlBinding( path = "size" )
    
    ValueProperty PROP_SIZE = new ValueProperty( TYPE, "Size" );
    
    Value<ValidationMarkerSize> getSize();
    void setSize( String value );
    void setSize( ValidationMarkerSize value ) ;
    
}
