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
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "web resource collection" )

@Documentation
(
    content = "Identifies a subset of web application resources and HTTP methods on those resources " +
              "to which a security constraint applies. If no HTTP methods are specified, then the " +
              "security constraint applies to all HTTP methods."
)

@GenerateImpl

public interface WebResourceCollection extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( WebResourceCollection.class );
    
    // *** Name ***
    
    @Label( standard = "name" )
    @Required
    @XmlBinding( path = "web-resource-name" )
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String value );
    
    // *** Description ***
    
    @Label( standard = "description" )
    @LongString
    @XmlBinding( path = "description" )
    
    ValueProperty PROP_DESCRIPTION = new ValueProperty( TYPE, "Description" );
    
    Value<String> getDescription();
    void setDescription( String value );
    
    // *** UrlPatterns ***
    
    @Type( base = UrlPattern.class )
    @Label( standard = "URL patterns" )
    @CountConstraint( min = 1 )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "url-pattern", type = UrlPattern.class ) )
    
    ListProperty PROP_URL_PATTERNS = new ListProperty( TYPE, "UrlPatterns" );
    
    ModelElementList<UrlPattern> getUrlPatterns();
    
    // *** HttpMethodTypes ***
    
    @Type( base = HttpMethodTypeWrapper.class )
    @Label( standard = "HTTP method types" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "http-method", type = HttpMethodTypeWrapper.class ) )

    ListProperty PROP_HTTP_METHOD_TYPES = new ListProperty( TYPE, "HttpMethodTypes" );
    
    ModelElementList<HttpMethodTypeWrapper> getHttpMethodTypes();

}
