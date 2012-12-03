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

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl
@Label( standard = "gradient background" )

public interface GradientBackgroundDef extends BackgroundDef 
{
	ModelElementType TYPE = new ModelElementType( GradientBackgroundDef.class );
	
	// *** GradientSegments ***
	
    @Type( base = GradientSegmentDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "gradient-segment", type = GradientSegmentDef.class ) )
                             
    ListProperty PROP_GRADIENT_SEGMENTS = new ListProperty( TYPE, "GradientSegments" );
    
    ModelElementList<GradientSegmentDef> getGradientSegments();
    
    // ** Vertical ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "vertical" )
    @DefaultValue( text = "true" )
    @Label( standard = "vertical")
    
    ValueProperty PROP_VERTICAL = new ValueProperty(TYPE, "Vertical");
    
    Value<Boolean> isVertical();
    void setVertical( String value );
    void setVertical( Boolean value );    
    
}
