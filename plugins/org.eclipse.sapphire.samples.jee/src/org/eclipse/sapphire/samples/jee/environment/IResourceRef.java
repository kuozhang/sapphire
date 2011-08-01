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
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.jee.environment.internal.ResourceRefServices;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "resource reference" )
@GenerateImpl

@Documentation
(
    content = "A resource reference provides means for a component to access objects that are created on demand by " +
    		  "a resource factory. A resource can be a database connection, a message queue, etc."
)

public interface IResourceRef extends IEnvironmentRef
{
    ModelElementType TYPE = new ModelElementType( IResourceRef.class );
    
    // *** Name ***
    
    @XmlBinding( path = "res-ref-name" )
    
    @Documentation
    (
        content = "The name of a resource reference is a JNDI name relative to the java:comp/env " +
                  "context. The name must be unique within this deployment component, but uniqueness " +
                  "across components on the same server is not required." +
                  "[pbr/]" +
                  "It is recommended, but not required, to place the resource factories in a context " +
                  "that describes the resource type:" +
                  "[pbr/]" +
                  "jdbc/ for a JDBC javax.swl.DataSource factory[br/]" +
                  "jms/ for a JMS javax.jms.QueueConnectionFactory or javax.jms.TopicConnectionFactory[br/]" +
                  "mail/ for a JavaMail javax.mail.Session factory[br/]" +
                  "url/ for a java.net.URL factory"
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
        content = "The type of the object expected by this resource reference. The type is used to " +
        		  "locate the appropriate resource factory."
    )

    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    ReferenceValue<JavaTypeName,JavaType> getType();
    void setType( String value );
    void setType( JavaTypeName value );
    
    // *** AuthenticationMethod ***
    
    enum AuthenticationMethod
    {
        @Label( standard = "application" )
        @EnumSerialization( primary = "Application" )
        
        APPLICATION,
        
        @Label( standard = "container" )
        @EnumSerialization( primary = "Container" )
        
        CONTAINER
    }
    
    @Type( base = AuthenticationMethod.class )
    @Label( standard = "authentication method" )
    @Required
    @XmlBinding( path = "res-auth" )
    
    @Documentation
    (
        content = "The component can sign on programmatically to the resource manager (application method) or " +
                  "the container can sign on to the resource manager on the behalf of the component. In the latter " +
                  "case, the container uses information that is supplied by the deployer."
    )

    ValueProperty PROP_AUTHENTICATION_METHOD = new ValueProperty( TYPE, "AuthenticationMethod" );
    
    Value<AuthenticationMethod> getAuthenticationMethod();
    void setAuthenticationMethod( String value );
    void setAuthenticationMethod( AuthenticationMethod value );
    
    // *** Shared ***
    
    @Type( base = Boolean.class )
    @Label( standard = "shared" )
    @DefaultValue( text = "true" )
    @CustomXmlValueBinding( impl = ResourceRefServices.SharedBinding.class )
    
    @Documentation
    (
        content = "An object retrieved by a resource reference can be shared by all consumers in a container. " +
                  "This can be advantageous when creating a new resource is expensive, but the nature of the " +
                  "resource and its usage will determine whether sharing is possible."
    )
    
    ValueProperty PROP_SHARED = new ValueProperty( TYPE, "Shared" );
    
    Value<Boolean> getShared();
    void setShared( String value );
    void setShared( Boolean value );
    
}
