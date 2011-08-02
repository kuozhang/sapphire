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
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.samples.jee.web.internal.SecurityConstraintServices;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "security constraint" )

@Documentation
(
    content = "Associates a security constraint with a collection of web resources."
)

@GenerateImpl

public interface SecurityConstraint extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( SecurityConstraint.class );
    
    // *** Name ***
    
    @Label( standard = "name" )
    @XmlBinding( path = "display-name" )
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String value );
    
    // *** WebResourceCollections ***
    
    @Type( base = WebResourceCollection.class )
    @Label( standard = "web resource collections" )
    @CountConstraint( min = 1 )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "web-resource-collection", type = WebResourceCollection.class ) )
    
    ListProperty PROP_WEB_RESOURCE_COLLECTIONS = new ListProperty( TYPE, "WebResourceCollections" );
    
    ModelElementList<WebResourceCollection> getWebResourceCollections();
    
    // *** TransportGuarantee ***
    
    enum TransportGuarantee
    {
        NONE,
        INTEGRAL,
        CONFIDENTIAL
    }
    
    @Type( base = TransportGuarantee.class )
    @Label( standard = "transport guarantee" )
    @DefaultValue( text = "none" )
    @XmlBinding( path = "user-data-constraint/transport-guarantee" )
    
    @Documentation
    (
        content = "Specifies expectation regarding communication between the client and the server." +
                  "[pbr/]" +
                  "[b]None[/b] - The data does not require any transport guarantees." +
                  "[pbr/]" +
                  "[b]Integral[/b] - The data must be sent in such a way that it cannot be changed in transit." +
                  "[pbr/]" +
                  "[b]Confidential[/b] - The data must be sent in the manner that prevents other entities " +
                  "from observing the contents of the transmission." +
                  "[pbr/]" +
                  "In most cases, specifying integral or confidential transport guarantee will " +
                  "indicate that the use of SSL is required."
    )
    
    ValueProperty PROP_TRANSPORT_GUARANTEE = new ValueProperty( TYPE, "TransportGuarantee" );
    
    Value<TransportGuarantee> getTransportGuarantee();
    void setTransportGuarantee( String value );
    void setTransportGuarantee( TransportGuarantee value );
    
    // *** RolesSpecificationMethod ***
    
    enum RolesSpecificationMethod
    {
        @Label( standard = "no access" )
        @EnumSerialization( primary = "none" )
        
        NONE,

        @Label( standard = "any user" )
        @EnumSerialization( primary = "any" )
        
        ANY,
        
        @Label( standard = "listed roles" )
        @EnumSerialization( primary = "listed" )
        
        LISTED
    }
    
    @Type( base = RolesSpecificationMethod.class )
    @Label( standard = "roles specification method" )
    @DefaultValue( text = "none" )
    @CustomXmlValueBinding( impl = SecurityConstraintServices.RolesSpecificationMethodBinding.class )
    
    @Documentation
    (
        content = "Access to web resource collections can be restricted based on roles. The specific roles " +
                  "that are allowed access can be listed explicitly, any user can be allowed access or access " +
                  "can be forbidden to all users."
    )
    
    ValueProperty PROP_ROLES_SPECIFICATION_METHOD = new ValueProperty( TYPE, "RolesSpecificationMethod" );
    
    Value<RolesSpecificationMethod> getRolesSpecificationMethod();
    void setRolesSpecificationMethod( String value );
    void setRolesSpecificationMethod( RolesSpecificationMethod value );
    
    // *** Roles ***
    
    @Type( base = SecurityRoleRef2.class )
    @Label( standard = "roles" )
    @Enablement( expr = "${ RolesSpecificationMethod == 'listed' }" )
    @CountConstraint( min = 1 )
    @CustomXmlListBinding( impl = SecurityConstraintServices.RolesBinding.class )
    
    @Documentation
    (
        content = "Access to web resource collections can be restricted based on roles. The specific roles " +
                  "that are allowed access can be listed explicitly, any user can be allowed access or access " +
                  "can be forbidden to all users."
    )
    
    ListProperty PROP_ROLES = new ListProperty( TYPE, "Roles" );
    
    ModelElementList<SecurityRoleRef2> getRoles();

}
