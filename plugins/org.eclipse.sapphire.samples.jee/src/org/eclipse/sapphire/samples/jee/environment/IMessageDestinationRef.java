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
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
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

@Label( standard = "message destination reference" )
@GenerateImpl

@Documentation
(
    content = "A message destination reference provides means for a component to locate a message destination via JNDI."
)

public interface IMessageDestinationRef extends IEnvironmentRef
{
    ModelElementType TYPE = new ModelElementType( IMessageDestinationRef.class );
    
    // *** Name ***
    
    @XmlBinding( path = "message-destination-ref-name" )
    
    @Documentation
    (
        content = "The name of a message destination reference is a JNDI name relative to the java:comp/env " +
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
    @XmlBinding( path = "message-destination-type" )
    
    @Documentation
    (
        content = "The expected type of the message destination, such as javax.jms.Queue."
    )

    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    ReferenceValue<JavaTypeName,JavaType> getType();
    void setType( String value );
    void setType( JavaTypeName value );
    
    // *** Usage ***
    
    enum Usage
    {
        @Label( standard = "consumes" )
        @EnumSerialization( primary = "Consumes" )
        
        CONSUMES,
        
        @Label( standard = "produces" )
        @EnumSerialization( primary = "Produces" )
        
        PRODUCES,
        
        @Label( standard = "consumes and produces" )
        @EnumSerialization( primary = "ConsumesProduces" )
        
        CONSUMES_AND_PRODUCES
    }
    
    @Type( base = Usage.class )
    @Label( standard = "usage" )
    @Required
    @XmlBinding( path = "message-destination-usage" )
    
    @Documentation
    (
        content = "The nature of use of the referenced message destination by the component."
    )

    ValueProperty PROP_USAGE = new ValueProperty( TYPE, "Usage" );
    
    Value<Usage> getUsage();
    void setUsage( String value );
    void setUsage( Usage value );
    
    // *** Link ***
    
    @Label( standard = "link" )
    @XmlBinding( path = "message-destination-link" )
    
    @Documentation
    (
        content = "Identifies the message destination that should be resolved by this reference. " +
                  "[pbr/]" +
                  "The link must be the name of a message destination in the same component archive or in another component archive " +
                  "in the same Java EE application. Alternatively, the link may be composed of a path specifying " +
                  "a component archive with the message destination name appended " +
                  "and separated from the path by \"#\". The path should be relative the archive containing the referencing " +
                  "component. This allows multiple message destinations with the same name to be uniquely identified." +
                  "[pbr/]" +
                  "Specifying the link is optional for the component developer. If not specified in the component," +
                  "the deployer will be required to specify it at deployment time. The deployer can always override the " +
                  "link specified by the developer."
    )
    
    ValueProperty PROP_LINK = new ValueProperty( TYPE, "Link" );
    
    Value<String> getLink();
    void setLink( String value );
    
}
