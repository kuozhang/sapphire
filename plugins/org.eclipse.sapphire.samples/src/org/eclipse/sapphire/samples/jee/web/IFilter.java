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
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.samples.jee.IDescribable;
import org.eclipse.sapphire.samples.jee.IParam;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface IFilter extends IModelElement, IDescribable
{
    ModelElementType TYPE = new ModelElementType( IFilter.class );
    
    // *** Name ***
    
    @Label( standard = "name" )
    @Required
    @XmlBinding( path = "filter-name" )
    
    @Documentation
    (
        content = "The logical name of the filter. The name is used to map the filter. Each filter name should be unique " + 
                  "within the web application."
    )
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String value );
    
    // *** Implementation ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "implementation class", full = "filter implementation class" )
    @Required
    @MustExist
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "javax.servlet.Filter" )
    @XmlBinding( path = "filter-class" )
    
    @Documentation
    (
        content = "The filter implementation class."
    )
    
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
        content = "The parameters provided to the filter implementation during initialization."
    )
    
    ListProperty PROP_INIT_PARAMS = new ListProperty( TYPE, "InitParams" );
    
    ModelElementList<IParam> getInitParams();
    
}
