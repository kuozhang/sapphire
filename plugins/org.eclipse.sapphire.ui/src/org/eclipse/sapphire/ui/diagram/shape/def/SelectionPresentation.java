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

package org.eclipse.sapphire.ui.diagram.shape.def;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@Label( standard = "selection presentation" )

public interface SelectionPresentation extends LinePresentation 
{
	ElementType TYPE = new ElementType( SelectionPresentation.class );
	
	// *** Color ***
    
    @DefaultValue( text = "#FFA500" ) 
    
    ValueProperty PROP_COLOR = new ValueProperty( TYPE, LinePresentation.PROP_COLOR );

	// *** Weight ***
    
    @DefaultValue( text = "1" )
    
    ValueProperty PROP_WEIGHT = new ValueProperty( TYPE, LinePresentation.PROP_WEIGHT );

	// *** Style ***
    
    @DefaultValue( text = "dash" )
    
    ValueProperty PROP_STYLE = new ValueProperty( TYPE, LinePresentation.PROP_STYLE );
    
    // *** Inset ***
    
    @Type( base = Integer.class )
    @Label( standard = "inset" )
    @DefaultValue( text = "0" )
    @NumericRange( min = "0" )
    @XmlBinding( path = "inset" )
    
    ValueProperty PROP_INSET = new ValueProperty( TYPE, "Inset" );
    
    Value<Integer> getInset();
    void setInset( String value );
    void setInset( Integer value );        
    
}
