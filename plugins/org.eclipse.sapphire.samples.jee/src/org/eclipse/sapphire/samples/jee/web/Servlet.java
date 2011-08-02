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

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;
import org.eclipse.sapphire.samples.jee.DescribableExt;
import org.eclipse.sapphire.samples.jee.RunAsSecurityRole;
import org.eclipse.sapphire.samples.jee.Param;
import org.eclipse.sapphire.samples.jee.web.internal.ServletTypeBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "servlet" )
@GenerateImpl

public interface Servlet extends IModelElement, DescribableExt
{
    ModelElementType TYPE = new ModelElementType( Servlet.class );
    
    // *** Name ***
    
    @Label( standard = "name" )
    @Required
    @XmlBinding( path = "servlet-name" )
    
    @Documentation
    (
        content = "The logical name of the servlet. The name is used to map the servlet. Each servlet name should be unique " + 
                  "within the web application."
    )
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String value );
    
    // *** Type ***
    
    @Type( base = ServletType.class )
    @Label( standard = "type" )
    @DefaultValue( text = "CLASS" )
    @CustomXmlValueBinding( impl = ServletTypeBinding.class )
    
    @Documentation
    (
        content = "A servlet can be defined by via a class or via a JSP file. If JSP file is specified, it will be compiled " +
                  "to a servlet class on access."
    )
    
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    Value<ServletType> getType();
    void setType( String value );
    void setType( ServletType value );
    
    // *** Implementation ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "implementation class", full = "servlet implementation class" )
    @Required
    @MustExist
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "javax.servlet.GenericServlet" )
    @Enablement( expr = "${ Type == 'CLASS' }" )
    @XmlBinding( path = "servlet-class" )
    
    @Documentation
    (
        content = "The servlet implementation class."
    )
    
    ValueProperty PROP_IMPLEMENTATION = new ValueProperty( TYPE, "Implementation" );
    
    ReferenceValue<JavaTypeName,JavaType> getImplementation();
    void setImplementation( String value );
    void setImplementation( JavaTypeName value );

    // *** JspFile ***
    
    @Type( base = Path.class )
    @Label( standard = "JSP file" )
    @Required
    @MustExist
    @Enablement( expr = "${ Type == 'JSP' }" )
    
    @Documentation
    (
        content = "The JSP file that will be compiled into a servlet class."
    )
    
    // TODO: implement relative to webcontent
    
    ValueProperty PROP_JSP_FILE = new ValueProperty( TYPE, "JspFile" );
    
    Value<Path> getJspFile();
    void setJspFile( String value );
    void setJspFile( Path value );
    
    // *** InitParams ***
    
    @Type( base = Param.class )
    @Label( standard = "initialization parameters" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "init-param", type = Param.class ) )
    
    @Documentation
    (
        content = "The parameters provided to the servlet during initialization."
    )
    
    ListProperty PROP_INIT_PARAMS = new ListProperty( TYPE, "InitParams" );
    
    ModelElementList<Param> getInitParams();
    
    // *** LoadOnStartup ***
    
    @Type( base = Boolean.class )
    @Label( standard = "load on startup" )
    @DefaultValue( text = "false" )
    @XmlValueBinding( path = "load-on-startup", mapExistanceToValue = "true" )
    
    @Documentation
    (
        content = "Indicates that this servlet should be loaded (instantiated and have its init method called) on the " +
                  "startup of the web application."
    )
    
    ValueProperty PROP_LOAD_ON_STARTUP = new ValueProperty( TYPE, "LoadOnStartup" );
    
    Value<Boolean> getLoadOnStartup();
    void setLoadOnStartup( String value );
    void setLoadOnStartup( Boolean value );
    
    // *** StartupPriority ***
    
    @Type( base = Integer.class )
    @Label( standard = "startup priority" )
    @Enablement( expr = "${ LoadOnStartup }" )
    @XmlValueBinding( path = "load-on-startup", removeNodeOnSetIfNull = false )
    
    @Documentation
    (
        content = "Controls the order in which the servlet should be loaded. If the value is a negative integer or not set, the " +
                  "server is free to load the servlet whenever it chooses. if the value is a positive integer or 0, the " + 
                  "server must load and initialize the servlet as the application is deployed. The server must " +
                  "guarantee that servlets marked with lower integers are loaded before servlets marked with higher " +
                  "integers. The server may choose the order of loading of servlets with the same startup priority."
    )
    
    ValueProperty PROP_STARTUP_PRIORITY = new ValueProperty( TYPE, "StartupPriority" );
    
    Value<Integer> getStartupPriority();
    void setStartupPriority( String value );
    void setStartupPriority( Integer value );
    
    // *** RunAsRole ***
    
    @Type( base = RunAsSecurityRole.class )
    @Label( standard = "run-as role" )
    @XmlElementBinding( mappings = @XmlElementBinding.Mapping( element = "run-as", type = RunAsSecurityRole.class ) )
    
    ElementProperty PROP_RUN_AS_ROLE = new ElementProperty( TYPE, "RunAsRole" );
    
    ModelElementHandle<RunAsSecurityRole> getRunAsRole();
    
    // *** SecurityRoles ***
    
    @Type( base = SecurityRoleRef.class )
    @Label( standard = "security roles" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "security-role-ref", type = SecurityRoleRef.class ) )
    
    // TODO: Understand functional difference between run-as and security-role-ref
    
    ListProperty PROP_SECURITY_ROLES = new ListProperty( TYPE, "SecurityRoles" );
    
    ModelElementList<SecurityRoleRef> getSecurityRoles();
    
}
