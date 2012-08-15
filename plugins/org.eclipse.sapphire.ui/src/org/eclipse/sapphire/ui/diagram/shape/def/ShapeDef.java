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
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.ui.def.PartDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface ShapeDef extends PartDef
{
	ModelElementType TYPE = new ModelElementType( ShapeDef.class );
		
    // *** LayoutConstraint ***
    
    @Type
    ( 
        base = LayoutConstraintDef.class, 
        possible = 
        { 
            SequenceLayoutConstraint.class, 
            StackLayoutConstraintDef.class
        }
    )    
    @Label( standard = "layout constaint" )
    @XmlElementBinding
    ( 
        mappings = 
        {
            @XmlElementBinding.Mapping( element = "sequence-layout-constraint", type = SequenceLayoutConstraint.class ),
            @XmlElementBinding.Mapping( element = "stack-layout-constraint", type = StackLayoutConstraintDef.class )
        }
    )
    
    ElementProperty PROP_LAYOUT_CONSTRAINT = new ElementProperty( TYPE, "LayoutConstraint" );
    
    ModelElementHandle<LayoutConstraintDef> getLayoutConstraint();
    
}
