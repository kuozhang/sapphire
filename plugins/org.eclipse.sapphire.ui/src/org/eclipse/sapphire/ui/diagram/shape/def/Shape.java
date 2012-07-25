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
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.ui.def.PartDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public interface Shape extends PartDef
{
	ModelElementType TYPE = new ModelElementType( Shape.class );
	
    // *** VisibleWhen ***
    
    @Type( base = Function.class )
    @XmlBinding( path = "visible-when" )
    @Label( standard = "visible when" )
    
    ValueProperty PROP_VISIBLE_WHEN = new ValueProperty(TYPE, "VisibleWhen");
    
    Value<Function> getVisibleWhen();
    void setVisibleWhen( String value );
    void setVisibleWhen( Function value );        
	
    // *** LayoutConstraint ***
    
    @Type
    ( 
        base = LayoutConstraint.class, 
        possible = 
        { 
            SequenceLayoutConstraint.class, 
            StackLayoutConstraint.class
        }
    )    
    @Label( standard = "layout constaint" )
    @XmlElementBinding
    ( 
        mappings = 
        {
            @XmlElementBinding.Mapping( element = "sequence-layout-constraint", type = SequenceLayoutConstraint.class ),
            @XmlElementBinding.Mapping( element = "stack-layout-constraint", type = StackLayoutConstraint.class )
        }
    )
    
    ElementProperty PROP_LAYOUT_CONSTRAINT = new ElementProperty( TYPE, "LayoutConstraint" );
    
    ModelElementHandle<LayoutConstraint> getLayoutConstraint();
    
}
