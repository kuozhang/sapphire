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
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public interface Image extends Shape 
{
	ModelElementType TYPE = new ModelElementType( Image.class );
	
    // *** Path ***
    
    @Type( base = Function.class )
    @Label( standard = "image path" )
    @XmlBinding( path = "path" )
    
    ValueProperty PROP_PATH = new ValueProperty( TYPE, "Path" );
    
    Value<Function> getPath();
    void setPath( String value );
    void setPath( Function value );	
	
	// *** Resizable23 ***
	
    @Type( base = Boolean.class )
    @XmlBinding( path = "resizable" )
    @DefaultValue( text = "false" )
    @Label( standard = "resizable")
    
    ValueProperty PROP_RESIZABLE = new ValueProperty(TYPE, "Resizable");
    
    Value<Boolean> isResizable();
    void setResizable( String value );
    void setResizable( Boolean value );
	
    // ** ResizeHorizontally ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "resize-horizontally" )
    @DefaultValue( text = "false" )
    @Label( standard = "resize horizontally")
    
    ValueProperty PROP_RESIZE_HORIZONTALLY = new ValueProperty(TYPE, "ResizeHorizontally");
    
    Value<Boolean> isResizeHorizontally();
    void setResizeHorizontally( String value );
    void setResizeHorizontally( Boolean value );

    // ** ResizeVertically ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "resize-vertically" )
    @DefaultValue( text = "false" )
    @Label( standard = "resize vertically")
    
    ValueProperty PROP_RESIZE_VERTICALLY = new ValueProperty(TYPE, "ResizeVertically");
    
    Value<Boolean> isResizeVertically();
    void setResizeVertically( String value );
    void setResizeVertically( Boolean value );
    
}
