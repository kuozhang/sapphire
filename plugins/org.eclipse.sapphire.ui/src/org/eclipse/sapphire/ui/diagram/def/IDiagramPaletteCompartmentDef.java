/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramPaletteCompartmentDef 

	extends IModelElement 
	
{
	ModelElementType TYPE = new ModelElementType( IDiagramPaletteCompartmentDef.class );
	
	// *** CompartmentId ***

    @Type( base = PaletteCompartmentId.class )
    @XmlBinding( path = "id" )    
    
    ValueProperty PROP_COMPARTMENT_ID = new ValueProperty( TYPE, "CompartmentId" );
    
    Value<PaletteCompartmentId> getCompartmentId();
    void setCompartmentId( String value );
    void setCompartmentId( PaletteCompartmentId value );    
		
    // *** CompartmentLabel ***

    @Label( standard = "label" )
    @XmlBinding( path = "label" )
    
    ValueProperty PROP_COMPARTMENT_LABEL = new ValueProperty( TYPE, "CompartmentLabel" );
    
    Value<String> getCompartmentLabel();
    void setCompartmentLabel( String label );
	
}
