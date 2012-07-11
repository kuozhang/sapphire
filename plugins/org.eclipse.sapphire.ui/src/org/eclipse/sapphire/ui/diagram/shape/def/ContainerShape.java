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
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public interface ContainerShape extends Shape
{
	ModelElementType TYPE = new ModelElementType( ContainerShape.class );
	
	// *** Layout ***
	    
    @Type
    ( 
        base = ShapeLayout.class, 
        possible = 
        { 
            SequenceLayout.class, 
            StackLayout.class
        }
    )    
    @Label( standard = "layout" )
    @XmlElementBinding
    ( 
        mappings = 
        {
            @XmlElementBinding.Mapping( element = "sequence-layout", type = SequenceLayout.class ),
            @XmlElementBinding.Mapping( element = "stack-layout", type = StackLayout.class )
        }
    )
    @Required
    
    ElementProperty PROP_LAYOUT = new ElementProperty( TYPE, "Layout" );
    
    ModelElementHandle<ShapeLayout> getLayout();
    
    // *** Content ***
    
	@Type( base = Shape.class, 
			possible = { Text.class, Image.class, ValidationMarker.class, 
		                Rectangle.class, ShapeFactory.class })
	@CountConstraint( min = 1 )
	@Label( standard = "content" )
	@XmlListBinding
	( 
	    path = "content",
	    mappings = 
	    {
	        @XmlListBinding.Mapping( element = "text", type = Text.class ),
	        @XmlListBinding.Mapping( element = "image", type = Image.class ),
	        @XmlListBinding.Mapping( element = "validation-marker", type = ValidationMarker.class ),
	        @XmlListBinding.Mapping( element = "rectangle", type = Rectangle.class ),
	        @XmlListBinding.Mapping( element = "shape-factory", type = ShapeFactory.class ),
	    }
	)	
	
	ListProperty PROP_CONTENT = new ListProperty( TYPE, "Content");
	
	ModelElementList<Shape> getContent();    
	
}
