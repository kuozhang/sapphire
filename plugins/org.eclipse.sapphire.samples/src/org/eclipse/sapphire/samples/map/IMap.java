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
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl
@XmlRootBinding( elementName = "map")

public interface IMap extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IMap.class );
    
    // *** Destinations ***

    @Type( base = IDestination.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "destination", type = IDestination.class ) )
    
    ListProperty PROP_DESTINATIONS = new ListProperty( TYPE, "Destinations" );
    
    ModelElementList<IDestination> getDestinations();
    
    // *** Routes ***
    
    @Type( base = IRoute.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "route", type = IRoute.class ) )
    
    ListProperty PROP_ROUTES = new ListProperty( TYPE, "Routes" );
    
    ModelElementList<IRoute> getRoutes();
    
}
