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

package org.eclipse.sapphire.samples.jee.environment;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface EnvironmentConsumer extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( EnvironmentConsumer.class );
    
    // *** EnvironmentRefs ***
    
    @Type
    (
        base = EnvironmentRef.class,
        possible = 
        {
            EnvironmentEntry.class, 
            EjbRemoteRef.class,
            EjbLocalRef.class,
            ResourceRef.class,
            ResourceEnvironmentRef.class,
            MessageDestinationRef.class,
            ServiceRef.class
        }
    )
    
    @Label( standard = "environment references" )
    
    @XmlListBinding
    (
        mappings = 
        {
            @XmlListBinding.Mapping( element = "env-entry", type = EnvironmentEntry.class ),
            @XmlListBinding.Mapping( element = "ejb-ref", type = EjbRemoteRef.class ),
            @XmlListBinding.Mapping( element = "ejb-local-ref", type = EjbLocalRef.class ),
            @XmlListBinding.Mapping( element = "resource-ref", type = ResourceRef.class ),
            @XmlListBinding.Mapping( element = "resource-env-ref", type = ResourceEnvironmentRef.class ),
            @XmlListBinding.Mapping( element = "message-destination-ref", type = MessageDestinationRef.class ),
            @XmlListBinding.Mapping( element = "service-ref", type = ServiceRef.class )
        }
    )
    
    ListProperty PROP_ENVIRONMENT_REFS = new ListProperty( TYPE, "EnvironmentRefs" );
    
    ModelElementList<EnvironmentRef> getEnvironmentRefs();
    
}
