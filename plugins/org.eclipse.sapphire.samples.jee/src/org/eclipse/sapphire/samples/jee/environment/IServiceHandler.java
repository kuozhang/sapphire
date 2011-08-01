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
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.samples.jee.IDescribableExt;
import org.eclipse.sapphire.samples.jee.IParam;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "service handler" )
@GenerateImpl

@Documentation
(
    content = "A service handler processes SOAP message header traffic during a remote call to a web service."
)

public interface IServiceHandler extends IDescribableExt
{
    ModelElementType TYPE = new ModelElementType( IServiceHandler.class );
    
    // *** Name ***
    
    @Label( standard = "name" )
    @Required
    @NoDuplicates // actually, must be unique within the module
    @XmlBinding( path = "handler-name" )
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String value );
    
    // *** Implementation ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "implementation" )
    @Required
    @MustExist
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS )
    @XmlBinding( path = "handler-class" )
    
    ValueProperty PROP_IMPLEMENTATION = new ValueProperty( TYPE, "Implementation" );
    
    ReferenceValue<JavaTypeName,JavaType> getImplementation();
    void setImplementation( String value );
    void setImplementation( JavaTypeName value );
    
    // *** InitParams ***
    
    @Type( base = IParam.class )
    @Label( standard = "initialization parameters" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "init-param", type = IParam.class ) )
    
    @Documentation
    (
        content = "The parameters provided to the service handler implementation during initialization."
    )
    
    ListProperty PROP_INIT_PARAMS = new ListProperty( TYPE, "InitParams" );
    
    ModelElementList<IParam> getInitParams();

    // *** SoapHeaders ***
    
    @Type( base = ISoapHeaderWrapper.class )
    @Label( standard = "SOAP headers" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "soap-header", type = ISoapHeaderWrapper.class ) )
    
    @Documentation
    (
        content = "The SOAP headers that will be processed by the handler."
    )
    
    ListProperty PROP_SOAP_HEADERS = new ListProperty( TYPE, "SoapHeaders" );
    
    ModelElementList<ISoapHeaderWrapper> getSoapHeaders();
    
    // *** SoapRoles ***
    
    @Type( base = ISoapRoleWrapper.class )
    @Label( standard = "SOAP roles" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "soap-role", type = ISoapRoleWrapper.class ) )
    
    @Documentation
    (
        content = "The SOAP actors that the handler will play as a role."
    )
    
    ListProperty PROP_SOAP_ROLES = new ListProperty( TYPE, "SoapRoles" );
    
    ModelElementList<ISoapRoleWrapper> getSoapRoles();
    
    // *** Ports ***
    
    @Type( base = IServicePortNameWrapper.class )
    @Label( standard = "ports" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "port-name", type = IServicePortNameWrapper.class ) )
    
    @Documentation
    (
        content = "The ports that the handler should be associated with. If no ports are specified, the handler " +
                  "is assumed to be associated with all ports of the service."
    )
    
    ListProperty PROP_PORTS = new ListProperty( TYPE, "Ports" );
    
    ModelElementList<IServicePortNameWrapper> getPorts();
    
}
