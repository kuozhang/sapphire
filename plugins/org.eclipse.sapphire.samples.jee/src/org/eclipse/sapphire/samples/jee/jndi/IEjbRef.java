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

package org.eclipse.sapphire.samples.jee.jndi;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "EJB reference" )

@Documentation
(
    content = "The EJB references provide means for a component to locate needed beans via JNDI. The deployer " +
              "ensures the availability of appropriate beans at runtime guided by the constraints specified by the " +
              "developer in an EJB reference."
)

public interface IEjbRef extends IEnvironmentRef
{
    ModelElementType TYPE = new ModelElementType( IEjbRef.class );
    
    // *** Name ***
    
    @XmlBinding( path = "ejb-ref-name" )
    
    @Documentation
    (
        content = "The name of an EJB reference is a JNDI name relative to the java:comp/env " +
                  "context. The name must be unique within this deployment component, but uniqueness " +
                  "across components on the same server is not required." +
                  "[pbr/]" + 
                  "It is recommended that the name is prefixed with \"ejb/\"."
    )

    ValueProperty PROP_NAME = new ValueProperty( TYPE, IEnvironmentRef.PROP_NAME );
    
    // *** Type ***
    
    enum EjbType
    {
        @Label( standard = "entity" )
        @EnumSerialization( primary = "Entity" )
        
        ENTITY,
        
        @Label( standard = "session" )
        @EnumSerialization( primary = "Session" )
        
        SESSION
    }

    @Type( base = EjbType.class )
    @Label( standard = "type" )
    @Required
    @XmlBinding( path = "ejb-ref-type" )
    
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    Value<EjbType> getType();
    void setType( String value );
    void setType( EjbType value );
    
    // *** HomeInterface ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "home interface" )
    @Required
    @MustExist
    @JavaTypeConstraint( kind = JavaTypeKind.INTERFACE, type = "javax.ejb.EJBHome" )
    @XmlBinding( path = "home" )
    
    ValueProperty PROP_HOME_INTERFACE = new ValueProperty( TYPE, "HomeInterface" );
    
    ReferenceValue<JavaTypeName,JavaType> getHomeInterface();
    void setHomeInterface( String value );
    void setHomeInterface( JavaTypeName value );
    
    // *** BeanInterface ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "bean interface" )
    @Required
    @MustExist
    @JavaTypeConstraint( kind = JavaTypeKind.INTERFACE, type = "javax.ejb.EJBObject" )
    @XmlBinding( path = "remote" )
    
    ValueProperty PROP_BEAN_INTERFACE = new ValueProperty( TYPE, "BeanInterface" );
    
    ReferenceValue<JavaTypeName,JavaType> getBeanInterface();
    void setBeanInterface( String value );
    void setBeanInterface( JavaTypeName value );
    
    // *** Link ***
    
    @Label( standard = "link" )
    @XmlBinding( path = "ejb-link" )
    
    @Documentation
    (
        content = "Identifies the EJB that should be resolved by this reference. " +
                  "[pbr/]" +
                  "The link must be the EJB name of an enterprise bean in the same EJB jar file or in another EJB jar " +
                  "file in the same Java EE application. Alternatively, the link may be composed of a path specifying " +
                  "the EJB jar containining the referenced enterprise bean with the EJB name of the target bean appended " +
                  "and separated from the path by \"#\". The path should be relative the archive containing the referencing " +
                  "component. This allows multiple enterprise beans with the same EJB name to be uniquely identified." +
                  "[pbr/]" +
                  "Specifying the link is optional for the component developer. If not specified in the component," +
                  "the deployer will be required to specify it at deployment time. The deployer can always override the " +
                  "link specified by the developer."
    )
    
    ValueProperty PROP_LINK = new ValueProperty( TYPE, "Link" );
    
    Value<String> getLink();
    void setLink( String value );
    
    // *** Description ***
    
    @Label( standard = "description" )
    @LongString
    @XmlBinding( path = "description" )
    
    ValueProperty PROP_DESCRIPTION = new ValueProperty( TYPE, "Description" );
    
    Value<String> getDescription();
    void setDescription( String value );
    
}
