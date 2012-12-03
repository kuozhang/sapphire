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

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl
@Label( standard = "container shape" )

public interface ContainerShapeDef extends ShapeDef
{
	ModelElementType TYPE = new ModelElementType( ContainerShapeDef.class );
	
	// *** Layout ***
	    
    @Type
    ( 
        base = ShapeLayoutDef.class, 
        possible = 
        { 
            SequenceLayoutDef.class, 
            StackLayoutDef.class
        }
    )    
    @Label( standard = "layout" )
    @XmlElementBinding
    ( 
        mappings = 
        {
            @XmlElementBinding.Mapping( element = "sequence-layout", type = SequenceLayoutDef.class ),
            @XmlElementBinding.Mapping( element = "stack-layout", type = StackLayoutDef.class )
        }
    )
    @Required
    
    ElementProperty PROP_LAYOUT = new ElementProperty( TYPE, "Layout" );
    
    ModelElementHandle<ShapeLayoutDef> getLayout();
    
    // *** Content ***
    
	@Type( base = ShapeDef.class, 
			possible = { TextDef.class, ImageDef.class, ValidationMarkerDef.class, 
		                RectangleDef.class, ShapeFactoryDef.class })
	@CountConstraint( min = 1 )
	@Label( standard = "content" )
	@XmlListBinding
	( 
	    path = "content",
	    mappings = 
	    {
	        @XmlListBinding.Mapping( element = "text", type = TextDef.class ),
	        @XmlListBinding.Mapping( element = "image", type = ImageDef.class ),
	        @XmlListBinding.Mapping( element = "validation-marker", type = ValidationMarkerDef.class ),
	        @XmlListBinding.Mapping( element = "rectangle", type = RectangleDef.class ),
	        @XmlListBinding.Mapping( element = "shape-factory", type = ShapeFactoryDef.class )
	    }
	)	
	
	ListProperty PROP_CONTENT = new ListProperty( TYPE, "Content");
	
	ModelElementList<ShapeDef> getContent();    
	
}
