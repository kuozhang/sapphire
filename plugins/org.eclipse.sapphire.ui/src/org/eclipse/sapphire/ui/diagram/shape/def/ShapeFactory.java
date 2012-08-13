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
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface ShapeFactory extends Shape 
{
	ModelElementType TYPE = new ModelElementType( ShapeFactory.class );
	
    // *** Property ***
    
    @Label( standard = "property" )
    @XmlBinding( path = "property" )
    @Required
    
    ValueProperty PROP_PROPERTY = new ValueProperty( TYPE, "Property" );
    
    Value<String> getProperty();
    void setProperty( String property );
    
    // *** ShapeFactoryCases ***
    
    @Type( base = ShapeFactoryCase.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "case", type = ShapeFactoryCase.class ) )
                             
    ListProperty PROP_SHAPE_FACTORY_CASES = new ListProperty( TYPE, "ShapeFactoryCases" );
    
    ModelElementList<ShapeFactoryCase> getShapeFactoryCases();
    	
}
