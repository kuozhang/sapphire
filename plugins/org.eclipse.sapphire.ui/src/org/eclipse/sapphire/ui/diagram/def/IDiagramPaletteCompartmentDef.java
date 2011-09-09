/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.diagram.def.internal.PaletteCompartmentIdService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramPaletteCompartmentDef extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( IDiagramPaletteCompartmentDef.class );
	
	// *** Id ***

    @XmlBinding( path = "id" )
    @Label( standard = "ID" )
    @Required
    @Service( impl = PaletteCompartmentIdService.class )
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );
    
    Value<String> getId();
    void setId( String value );
		
    // *** Label ***

    @Label( standard = "label" )
    @XmlBinding( path = "label" )
    @Required
    
    ValueProperty PROP_LABEL = new ValueProperty( TYPE, "Label" );
    
    Value<String> getLabel();
    void setLabel( String label );
	
}
