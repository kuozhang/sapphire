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

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "environment entry" )
@GenerateImpl

@Documentation
(
    content = "The environment entries provide means to parameterize a component. Unlike some other " +
              "means of parameterization, the deployer can change the values without modifying the " +
              "contents of the component archive." +
              "[pbr/]" +
              "All environment entries are placed by the server into the \"java:comp/env\" context. " +
              "This context is read-only and unique per component, so if two different components " +
              "define the same environment entry, the entries do not collide." +
              "[pbr/]" +
              "Java code can retrieve environment entry values using JNDI:" +
              "[pbr/]" +
              "Context ctxt = new InitialContext();[br/]" +
              "Integer pin = (Integer) ctxt.lookup( \"java:comp/env/pin\" );"
)

public interface IEnvironmentEntry extends IEnvironmentRef
{
    ModelElementType TYPE = new ModelElementType( IEnvironmentEntry.class );
    
    // *** Name ***
    
    @XmlBinding( path = "env-entry-name" )
    
    @Documentation
    (
        content = "The name of an environment entry is a JNDI name relative to the java:comp/env " +
                  "context. The name must be unique within this deployment component, but uniqueness " +
                  "across components on the same server is not required."
    )

    ValueProperty PROP_NAME = new ValueProperty( TYPE, IEnvironmentRef.PROP_NAME );
    
    // *** Type ***
    
    enum EntryType
    {
        @Label( standard = "boolean" )
        @EnumSerialization( primary = "java.lang.Boolean" )
        
        BOOLEAN,
        
        @Label( standard = "byte" )
        @EnumSerialization( primary = "java.lang.Byte" )
        
        BYTE,
        
        @Label( standard = "character" )
        @EnumSerialization( primary = "java.lang.Character" )
        
        CHARACTER,
        
        @Label( standard = "string" )
        @EnumSerialization( primary = "java.lang.String" )
        
        STRING,
        
        @Label( standard = "short" )
        @EnumSerialization( primary = "java.lang.Short" )
        
        SHORT,
        
        @Label( standard = "integer" )
        @EnumSerialization( primary = "java.lang.Integer" )
        
        INTEGER,
        
        @Label( standard = "long" )
        @EnumSerialization( primary = "java.lang.Long" )
        
        LONG,
        
        @Label( standard = "float" )
        @EnumSerialization( primary = "java.lang.Float" )
        
        FLOAT,
        
        @Label( standard = "double" )
        @EnumSerialization( primary = "java.lang.Double" )
        
        DOUBLE
    }
    
    @Type( base = EntryType.class )
    @Label( standard = "type" )
    @Required
    @XmlBinding( path = "env-entry-type" )
    
    @Documentation
    (
        content = "The Java type of the environment entry value that is expected by application code."
    )
    
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    Value<EntryType> getType();
    void setType( String value );
    void setType( EntryType value );
    
    // *** Value ***
    
    @Label( standard = "value" )
    @XmlBinding( path = "env-entry-value" )
    
    @Documentation
    (
        content = "The value of the environment entry in the form of a string that is valid for the " +
                  "constructor of specified type that takes a single string parameter or for " +
                  "java.lang.Character a single character." +
                  "[pbr/]" +
                  "The value is optional, but not specifying a value requires the deployer to provide one."
    )
    
    ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );
    
    Value<String> getValue();
    void setValue( String value );
    
    // *** Description ***
    
    @Label( standard = "description" )
    @LongString
    @XmlBinding( path = "description" )
    
    ValueProperty PROP_DESCRIPTION = new ValueProperty( TYPE, "Description" );
    
    Value<String> getDescription();
    void setDescription( String value );
    
}
