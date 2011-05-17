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
import org.eclipse.sapphire.samples.jee.web.internal.ErrorPageTypeBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "error page" )

@Documentation
(
    content = "A web application can provide custom error pages to show when an HTTP error response is generated or when " +
              "a Java exception is encountered. " +
              "[pbr/]" +
              "Each error page definition maps one HTTP error response code or a Java " +
              "exception to a resource in the web application."
)

@GenerateImpl

public interface IErrorPage extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IErrorPage.class );
    
    // *** Type ***
    
    @Type( base = ErrorPageType.class )
    @Label( standard = "type" )
    @DefaultValue( text = "HTTP_ERROR_CODE" )
    @CustomXmlValueBinding( impl = ErrorPageTypeBinding.class )
    
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    Value<ErrorPageType> getType();
    void setType( String value );
    void setType( ErrorPageType value );
    
    // *** HttpErrorCode ***
    
    @Type( base = HttpErrorResponseCode.class )
    @Label( standard = "HTTP error code" )
    @Required
    @Enablement( expr = "${ Type == 'HTTP_ERROR_CODE' }" )
    @XmlBinding( path = "error-code" )
    
    ValueProperty PROP_HTTP_ERROR_CODE = new ValueProperty( TYPE, "HttpErrorCode" );
    
    Value<HttpErrorResponseCode> getHttpErrorCode();
    void setHttpErrorCode( String value );
    void setHttpErrorCode( HttpErrorResponseCode value );
    
    // *** JavaExceptionType ***
    
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "Java exception type" )
    @Required
    @MustExist
    @Enablement( expr = "${ Type == 'JAVA_EXCEPTION' }" )
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "java.lang.Throwable" )
    @XmlBinding( path = "exception-type" )
    
    ValueProperty PROP_JAVA_EXCEPTION_TYPE = new ValueProperty( TYPE, "JavaExceptionType" );
    
    ReferenceValue<JavaTypeName,JavaType> getJavaExceptionType();
    void setJavaExceptionType( String value );
    void setJavaExceptionType( JavaTypeName value );
    
    // *** Location ***
    
    @Type( base = Path.class )
    @Label( standard = "location" )
    @Required
    @XmlBinding( path = "location" )
    
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );
    
    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );

}
