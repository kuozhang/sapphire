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

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.IPropertiesViewContributorDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public interface IDiagramConnectionBindingDef 

    extends IModelElement, IPropertiesViewContributorDef
    
{
    ModelElementType TYPE = new ModelElementType( IDiagramConnectionBindingDef.class );
    
    // *** ConnectionId ***
    
    @Label( standard = "connection ID" )
    @XmlBinding( path = "connection-id" )
    @Required
    
    ValueProperty PROP_CONNECTION_ID = new ValueProperty( TYPE, "ConnectionId" );
    
    Value<String> getConnectionId();
    void setConnectionId( String value );

    // *** Property ***
    
    @Label( standard = "property" )
    @Required
    @XmlBinding( path = "property" )
    
    ValueProperty PROP_PROPERTY = new ValueProperty( TYPE, "Property" );
    
    Value<String> getProperty();
    void setProperty( String property );
    
    // *** InstanceId ***
    
    @Type( base = Function.class )
    @Label( standard = "instance ID" )
    @XmlBinding( path = "instance-id" )
    
    ValueProperty PROP_INSTANCE_ID = new ValueProperty( TYPE, "InstanceId" );
    
    Value<Function> getInstanceId();
    void setInstanceId( String value );
    void setInstanceId( Function value );
    
    // *** Label ***
    
    @Type( base = IDiagramLabelDef.class )
    @XmlBinding( path = "label" )
    
    ElementProperty PROP_LABEL = new ElementProperty( TYPE, "Label" );
    
    ModelElementHandle<IDiagramLabelDef> getLabel();

}
