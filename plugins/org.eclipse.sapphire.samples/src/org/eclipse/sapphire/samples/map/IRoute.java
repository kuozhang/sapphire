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

package org.eclipse.sapphire.samples.map;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.map.internal.DestinationReferenceService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IRoute extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( IRoute.class );
	
	// *** FromDestination ***
	
	@Reference( target = IDestination.class, service = DestinationReferenceService.class )
	@XmlBinding( path = "from-destination")
	@NonNullValue
	@Label(standard = "from destination")

	ValueProperty PROP_FROM_DESTINATION = new ValueProperty( TYPE, "FromDestination" );

    ReferenceValue<String,IDestination> getFromDestination();
    void setFromDestination( String name );
	
	// *** ToDestination ***
	
	@Reference( target = IDestination.class, service = DestinationReferenceService.class )
	@XmlBinding( path = "to-destination")
	@NonNullValue
	@Label(standard = "to destination")

	ValueProperty PROP_TO_DESTINATION = new ValueProperty( TYPE, "ToDestination" );

    ReferenceValue<String,IDestination> getToDestination();
    void setToDestination( String name );
    
    // *** Distance ***
    
    @XmlBinding( path = "distance" )
    @Label( standard = "distance" )
    @NonNullValue

    ValueProperty PROP_DISTANCE = new ValueProperty( TYPE, "Distance" );

    Value<String> getDistance();
    void setDistance( String distance );
    
}
