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
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.samples.jee.web.internal.LeadingSlashValidationService;
import org.eclipse.sapphire.samples.jee.web.internal.WebContentRelativePathService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "service reference" )
@GenerateImpl

@Documentation
(
    content = "A service reference provides means for a component to locate a web service via JNDI."
)

// TODO: IServiceRef and IServicePortRef should extend IDescribable

public interface IServiceRef extends IEnvironmentRef
{
    ModelElementType TYPE = new ModelElementType( IServiceRef.class );
    
    // *** Name ***
    
    @XmlBinding( path = "service-ref-name" )
    
    @Documentation
    (
        content = "The name of a service reference is a JNDI name relative to the java:comp/env " +
                  "context. The name must be unique within this deployment component, but uniqueness " +
                  "across components on the same server is not required." +
                  "[pbr/]" + 
                  "It is recommended that the name is prefixed with \"service/\"."
    )

    ValueProperty PROP_NAME = new ValueProperty( TYPE, IEnvironmentRef.PROP_NAME );
    
    // *** Type ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "type" )
    @Required
    @MustExist
    @JavaTypeConstraint( kind = JavaTypeKind.INTERFACE, type = "javax.xml.rpc.Service" )
    @XmlBinding( path = "service-interface" )
    
    @Documentation
    (
        content = "The expected type of the JAX-RPC service interface. In most cases the type will be " +
                  "javax.xml.rpc.Service, but a JAX-RPC generated service interface may also be specified."
    )

    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    ReferenceValue<JavaTypeName,JavaType> getType();
    void setType( String value );
    void setType( JavaTypeName value );
    
    // *** WsdlFile ***
    
    @Type( base = Path.class )
    @Label( standard = "WSDL file" )
    @MustExist
    
    @Services
    (
        {
            @Service( impl = WebContentRelativePathService.class ), 
            @Service( impl = LeadingSlashValidationService.class )
        }
    )
    
    @XmlBinding( path = "wsdl-file" )
    
    ValueProperty PROP_WSDL_FILE = new ValueProperty( TYPE, "WsdlFile" );
    
    Value<Path> getWsdlFile();
    void setWsdlFile( String value );
    void setWsdlFile( Path value );
    
    // *** JaxRpcMappingFile ***
    
    @Type( base = Path.class )
    @Label( standard = "JAX-RPC mapping file" )
    @Enablement( expr = "${ WsdlFile != null }" )
    @MustExist
    
    @Services
    (
        {
            @Service( impl = WebContentRelativePathService.class ), 
            @Service( impl = LeadingSlashValidationService.class )
        }
    )
    
    @XmlBinding( path = "jaxrpc-mapping-file" )
    
    @Documentation
    (
        content = "The file that describes the JAX-RPC mapping between Java interfaces used by the application " +
                  "and the WSDL."
    )

    ValueProperty PROP_JAX_RPC_MAPPING_FILE = new ValueProperty( TYPE, "JaxRpcMappingFile" );
    
    Value<Path> getJaxRpcMappingFile();
    void setJaxRpcMappingFile( String value );
    void setJaxRpcMappingFile( Path value );
    
    // *** ServiceName ***
    
    @Label( standard = "service name" )
    @Enablement( expr = "${ WsdlFile != null }" )
    @XmlBinding( path = "service-qname" )
    
    @Documentation
    (
        content = "The name of the service that is being referenced. The name must correspond to a service declared " +
                  "in the WSDL."
    )

    ValueProperty PROP_SERVICE_NAME = new ValueProperty( TYPE, "ServiceName" );
    
    Value<String> getServiceName();
    void setServiceName( String value );
    
    // *** Ports ***
    
    @Type( base = IServicePortRef.class )
    @Label( standard = "service ports" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "port-component-ref", type = IServicePortRef.class ) )
    
    @Documentation
    (
        content = "A service port reference declares a component dependency on the container for " +
                  "resolving a service endpoint interface to a WSDL port. This is used by the container " +
                  "for a Service.getPort( Class ) method call."
    )

    ListProperty PROP_PORTS = new ListProperty( TYPE, "Ports" );
    
    ModelElementList<IServicePortRef> getPorts();
    
    // *** Handlers ***
    
    @Type( base = IServiceHandler.class )
    @Label( standard = "service handlers" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "handler", type = IServiceHandler.class ) )
    
    @Documentation
    (
        content = "A service handler processes SOAP message header traffic during a remote call to a web service."
    )

    ListProperty PROP_HANDLERS = new ListProperty( TYPE, "Handlers" );
    
    ModelElementList<IServiceHandler> getHandlers();

}
