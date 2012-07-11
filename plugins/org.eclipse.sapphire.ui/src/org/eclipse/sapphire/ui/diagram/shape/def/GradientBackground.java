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
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public interface GradientBackground extends Background 
{
	ModelElementType TYPE = new ModelElementType( GradientBackground.class );
	
	// *** GradientSegments ***
	
    @Type( base = GradientSegment.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "gradient-segment", type = GradientSegment.class ) )
                             
    ListProperty PROP_GRADIENT_SEGMENTS = new ListProperty( TYPE, "GradientSegments" );
    
    ModelElementList<GradientSegment> getGradientSegments();
    
}
