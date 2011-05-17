/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.jee.web;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "dispatcher event" )
@GenerateImpl

public interface IDispatcherEventRef extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IDispatcherEventRef.class );
    
    // *** DispatcherEvent ***
    
    @Type( base = DispatcherEvent.class )
    @Label( standard = "dispatcher event" )
    @Required
    @NoDuplicates
    @XmlBinding( path = "" )
    
    ValueProperty PROP_DISPATCHER_EVENT = new ValueProperty( TYPE, "DispatcherEvent" );
    
    Value<DispatcherEvent> getDispatcherEvent();
    void setDispatcherEvent( String value );
    void setDispatcherEvent( DispatcherEvent value );

}
