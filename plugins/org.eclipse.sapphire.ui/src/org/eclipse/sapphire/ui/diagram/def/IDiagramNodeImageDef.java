/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.LineStyle;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramNodeImageDef 

	extends ISapphirePartDef 

{
	ModelElementType TYPE = new ModelElementType( IDiagramNodeImageDef.class );

	// *** Value ***

    @Type( base = Function.class )
    @Label( standard = "value" )
    @Localizable
    @XmlBinding( path = "value" )
    
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );
    
    Value<Function> getValue();
    void setValue( String value );
    void setValue( Function value );
    
    // *** ImagePlacement ***
    
    @Type( base = ImagePlacement.class )
    @Label( standard = "image placement")
    @Localizable
    @XmlBinding( path = "image-placement" )
    @DefaultValue( text = "top" )
    
    ValueProperty PROP_IMAGE_PLACEMENT = new ValueProperty( TYPE, "ImagePlacement" );
    
    Value<ImagePlacement> getImagePlacement();
    void setImagePlacement( String value );
    void setImagePlacement( ImagePlacement value ) ;
    		
}
