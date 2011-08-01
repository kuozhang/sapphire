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
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
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

@Label( standard = "service port reference" )
@GenerateImpl

@Documentation
(
    content = "A service port reference declares a component dependency on the container for " +
              "resolving a service endpoint interface to a WSDL port. This is used by the container " +
              "for a Service.getPort( Class ) method call."
)

public interface IServicePortRef extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IServicePortRef.class );
    
    // *** Type ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "type" )
    @Required
    @MustExist
    @XmlBinding( path = "service-endpoint-interface" )
    
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    ReferenceValue<JavaTypeName,JavaType> getType();
    void setType( String value );
    void setType( JavaTypeName value );
    
    // *** Link ***
    
    @Label( standard = "link" )
    @XmlBinding( path = "port-component-link" )
    
    @Documentation
    (
        content = "Identifies the port component that should be resolved by this reference. " +
                  "[pbr/]" +
                  "The link must be the name of a port component in the same component archive or in another component archive " +
                  "in the same Java EE application. Alternatively, the link may be composed of a path specifying " +
                  "a component archive with the port component name appended " +
                  "and separated from the path by \"#\". The path should be relative the archive containing the referencing " +
                  "component. This allows multiple port components with the same name to be uniquely identified." +
                  "[pbr/]" +
                  "Specifying the link is optional for the component developer. If not specified in the component," +
                  "the deployer will be required to specify it at deployment time. The deployer can always override the " +
                  "link specified by the developer."
    )
    
    ValueProperty PROP_LINK = new ValueProperty( TYPE, "Link" );
    
    Value<String> getLink();
    void setLink( String value );
    
}
