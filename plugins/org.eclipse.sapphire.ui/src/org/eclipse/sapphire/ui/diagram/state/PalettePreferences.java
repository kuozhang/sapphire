/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.state;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface PalettePreferences extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( PalettePreferences.class );

    // *** DockLocation ***

    @Type( base = Integer.class )
    @DefaultValue( text = "0" )
    @XmlBinding( path = "dock-location" )

    ValueProperty PROP_DOCK_LOCATION = new ValueProperty( TYPE, "DockLocation" );
    
    Value<Integer> getDockLocation();
    void setDockLocation( String value );
    void setDockLocation( Integer value );
    
    // *** PaletteState ***
    
    @Type( base = Integer.class )
    @DefaultValue( text = "4" )
    @XmlBinding( path = "palette-state" )

    ValueProperty PROP_PALETTE_STATE = new ValueProperty( TYPE, "PaletteState" );
    
    Value<Integer> getPaletteState();
    void setPaletteState( String value );
    void setPaletteState( Integer value );
	
    // *** PaletteWidth ***
    
    @Type( base = Integer.class )
    @DefaultValue( text = "0" )
    @XmlBinding( path = "palette-width" )

    ValueProperty PROP_PALETTE_WIDTH = new ValueProperty( TYPE, "PaletteWidth" );
    
    Value<Integer> getPaletteWidth();
    void setPaletteWidth( String value );
    void setPaletteWidth( Integer value );
        
}
