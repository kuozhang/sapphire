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

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "resource environment reference" )
@GenerateImpl

@Documentation
(
    content = "A resource environment reference provides means for a component to access administered objects " +
    		  "in the component's environment."
)

public interface IResourceEnvironmentRef extends IEnvironmentRef
{
    ModelElementType TYPE = new ModelElementType( IResourceEnvironmentRef.class );
    
    // *** Name ***
    
    @XmlBinding( path = "res-ref-name" )
    
    @Documentation
    (
        content = "The name of a resource environment reference is a JNDI name relative to the java:comp/env " +
                  "context. The name must be unique within this deployment component, but uniqueness " +
                  "across components on the same server is not required."
    )

    ValueProperty PROP_NAME = new ValueProperty( TYPE, IEnvironmentRef.PROP_NAME );
    
    // *** Type ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "type" )
    @Required
    @MustExist
    @XmlBinding( path = "res-type" )
    
    @Documentation
    (
        content = "The type of the object expected by this resource environment reference."
    )

    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    ReferenceValue<JavaTypeName,JavaType> getType();
    void setType( String value );
    void setType( JavaTypeName value );
    
}
