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

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramNodeImageDef 

    extends IDiagramDimension 

{
    ModelElementType TYPE = new ModelElementType( IDiagramNodeImageDef.class );

    // *** Id ***

    @Type( base = Function.class )
    @Label( standard = "ID" )
    @Required
    @XmlBinding( path = "id" )
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );
    
    Value<Function> getId();
    void setId( String value );
    void setId( Function value );
    
    // *** Placement ***
    
    @Type( base = ImagePlacement.class )
    @Label( standard = "placement")
    @Localizable
    @XmlBinding( path = "placement" )
    @DefaultValue( text = "top" )
    
    ValueProperty PROP_PLACEMENT = new ValueProperty( TYPE, "Placement" );
    
    Value<ImagePlacement> getPlacement();
    void setPlacement( String value );
    void setPlacement( ImagePlacement value );
    
    // *** PossibleImages ***
    
    @Type( base = IDiagramImageChoice.class )
    @CountConstraint( min = 1 )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "possible", type = IDiagramImageChoice.class ) )
                             
    ListProperty PROP_POSSIBLE_IMAGES = new ListProperty( TYPE, "PossibleImages" );
    
    ModelElementList<IDiagramImageChoice> getPossibleImages();
            
}
