/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.shape.def;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "shape factory case" )
@Image( path = "ShapeFactoryCaseDef.png" )

public interface ShapeFactoryCaseDef extends Element 
{
	ElementType TYPE = new ElementType( ShapeFactoryCaseDef.class );
	
    // *** Type ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "type" )
    @JavaTypeConstraint( kind = JavaTypeKind.INTERFACE, type = "org.eclipse.sapphire.Element" )
    @MustExist
    @XmlBinding( path = "type" )
    
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    ReferenceValue<JavaTypeName,JavaType> getType();
    void setType( String value );
    void setType( JavaTypeName value );

    // *** Shape ***
    
	@Type
	(
		base = ShapeDef.class, 
		possible =
		{
			TextDef.class, 
			ImageDef.class, 
			ValidationMarkerDef.class, 
		    RectangleDef.class,
		    LineShapeDef.class,
		    ShapeFactoryDef.class
		}
	)
	
	@Label( standard = "shape" )
	
	@XmlElementBinding
	( 
	    mappings = 
	    {
	    	@XmlElementBinding.Mapping( element = "text", type = TextDef.class ),
	        @XmlElementBinding.Mapping( element = "image", type = ImageDef.class ),
	        @XmlElementBinding.Mapping( element = "validation-marker", type = ValidationMarkerDef.class ),
	        @XmlElementBinding.Mapping( element = "rectangle", type = RectangleDef.class ),
	        @XmlElementBinding.Mapping( element = "line", type = LineShapeDef.class ),
	        @XmlElementBinding.Mapping( element = "shape-factory", type = ShapeFactoryDef.class )
	    }
	)	
	
    ElementProperty PROP_SHAPE = new ElementProperty( TYPE, "Shape" );
    
    ElementHandle<ShapeDef> getShape();
    
    // *** SelectionPresentation ***
    
    @Type( base = SelectionPresentation.class )
    @Label( standard = "selection presentation" )
    @XmlBinding( path = "selection-presentation" )

    ImpliedElementProperty PROP_SELECTION_PRESENTATION = new ImpliedElementProperty( TYPE, "SelectionPresentation" );
    
    SelectionPresentation getSelectionPresentation();

}
