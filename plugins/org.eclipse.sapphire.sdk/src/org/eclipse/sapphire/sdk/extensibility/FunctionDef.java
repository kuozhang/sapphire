/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.sdk.extensibility;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "function" )
@GenerateImpl

public interface FunctionDef extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( FunctionDef.class );
    
    // *** Name ***
    
    @Label( standard = "name" )
    @Required
    @XmlBinding( path = "name" )
    
    @Documentation( content = "The name by which function will be referenced in the expression language. Functions " +
                              "can be placed in a namespace by using \"[namespace]:[name]\" syntax. Hierarchical " +
                              "namespaces are also alowed. Each segment in the function name must follow Java identifier rules." )
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String value );
    
    // *** OperandCounts ***
    
    @Label( standard = "operand count" )
    @GenerateImpl
    
    interface OperandCount extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( OperandCount.class );
        
        // *** Count ***
        
        @Type( base = Integer.class )
        @Label( standard = "operand count" )
        @Required
        @NoDuplicates
        @NumericRange( min = "0" )
        @XmlBinding( path = "" )
        
        ValueProperty PROP_COUNT = new ValueProperty( TYPE, "Count" );
        
        Value<Integer> getCount();
        void setCount( String value );
        void setCount( Integer value );
    }
    
    @Type( base = OperandCount.class )
    @Label( standard = "operand counts" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "operand-count", type = OperandCount.class ) )
    
    @Documentation
    (
        content = "A function can be restricted to apply only to a specified number of operands. If not restricted, the function will " +
                  "apply for any number of operands. The function is responsible for throwing a FunctionExeption during evaluation " +
                  "if a problem with the operands is detected."
    )
    
    ListProperty PROP_OPERAND_COUNTS = new ListProperty( TYPE, "OperandCounts" );
    
    ModelElementList<OperandCount> getOperandCounts();
    
    // *** Description ***
    
    @LongString
    @Label( standard = "description" )
    @Localizable
    @Whitespace( collapse = true )
    @XmlValueBinding( path = "description" )
    
    @Documentation( content = "Provides detailed information about the function. The " +
                              "description should be in the form of properly capitalized and punctuated sentences." )
    
    ValueProperty PROP_DESCRIPTION = new ValueProperty( TYPE, "Description" );
    
    Value<String> getDescription();
    void setDescription( String value );
    
    // *** ImplClass ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "implementation class" )
    @Required
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "org.eclipse.sapphire.modeling.el.Function" )
    @MustExist
    @XmlBinding( path = "impl" )
    
    @Documentation( content = "The function implementation. Must extend Function." )

    ValueProperty PROP_IMPL_CLASS = new ValueProperty( TYPE, "ImplClass" );
    
    ReferenceValue<JavaTypeName,JavaType> getImplClass();
    void setImplClass( String value );
    void setImplClass( JavaTypeName value );
    
}
