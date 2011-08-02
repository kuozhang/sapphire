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
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.jee.web.internal.LeadingSlashValidationService;
import org.eclipse.sapphire.samples.jee.web.internal.WebContentRelativePathService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "authentication configuration" )

@Documentation
(
    content = "Configures the manner in which users authenticate with the web application prior to gaining access to " +
              "secured resources."
)

@GenerateImpl

public interface AuthenticationConfig extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( AuthenticationConfig.class );
    
    // *** AuthenticationMethod ***
    
    @Label( standard = "authentication method" )
    @PossibleValues( values = { "BASIC", "DIGEST", "FORM", "CLIENT-CERT" }, invalidValueSeverity = Status.Severity.OK )
    @XmlBinding( path = "auth-method" )
    
    @Documentation
    (
        content = "Specifies the authentication mechanism for the web application. As a prerequisite to " +
                  "gaining access to any web resources which are protected by a security constraint, a user " + 
                  "must have authenticated using the configured mechanism." +
                  "[pbr/]" +
                  "Authentication Methods" +
                  "[pbr/]" +
                  "[ol]" +
                  "[li]BASIC[/li]" +
                  "[li]DIGEST[/li]" +
                  "[li]FORM[/li]" +
                  "[li]CLIENT-CERT[/li]" +
                  "[li]Vendor-specific authentication scheme.[/li]" +
                  "[/ol]"
    )
    
    ValueProperty PROP_AUTHENTICATION_METHOD = new ValueProperty( TYPE, "AuthenticationMethod" );
    
    Value<String> getAuthenticationMethod();
    void setAuthenticationMethod( String value );
    
    // *** RealmName ***
    
    @Label( standard = "realm name" )
    @Enablement( expr = "${ AuthenticationMethod == 'BASIC' }" )
    @Required
    @XmlBinding( path = "realm-name" )
    
    @Documentation
    (
        content = "Specifies the realm name to use in HTTP Basic authentication."
    )
    
    ValueProperty PROP_REALM_NAME = new ValueProperty( TYPE, "RealmName" );
    
    Value<String> getRealmName();
    void setRealmName( String value );
    
    // *** FormLoginPage ***
    
    @Type( base = Path.class )
    @Label( standard = "form login page" )
    @Enablement( expr = "${ AuthenticationMethod == 'FORM' }")
    @Required
    @MustExist
    
    @Services
    (
        {
            @Service( impl = WebContentRelativePathService.class ), 
            @Service( impl = LeadingSlashValidationService.class )
        }
    )
    
    @XmlBinding( path = "form-login-config/form-login-page" )
    
    @Documentation
    (
        content = "Specifies the location in the web application where the page that can be used " +
                  "for login is located. The path should begin with a leading slash ('/') and is " +
                  "interpreted related to the root of the web application."
    )
    
    ValueProperty PROP_FORM_LOGIN_PAGE = new ValueProperty( TYPE, "FormLoginPage" );
    
    Value<Path> getFormLoginPage();
    void setFormLoginPage( String value );
    void setFormLoginPage( Path value );

    // *** FormErrorPage ***
    
    @Type( base = Path.class )
    @Label( standard = "form error page" )
    @Enablement( expr = "${ AuthenticationMethod == 'FORM' }")
    @Required
    @MustExist
    
    @Services
    (
        {
            @Service( impl = WebContentRelativePathService.class ), 
            @Service( impl = LeadingSlashValidationService.class )
        }
    )
    
    @XmlBinding( path = "form-login-config/form-error-page" )
    
    @Documentation
    (
        content = "Specifies the location in the web application where the error page that is displayed " +
                  "when login is not successful is located. The path should begin with a leading slash ('/') and is " +
                  "interpreted related to the root of the web application."
    )
    
    ValueProperty PROP_FORM_ERROR_PAGE = new ValueProperty( TYPE, "FormErrorPage" );
    
    Value<Path> getFormErrorPage();
    void setFormErrorPage( String value );
    void setFormErrorPage( Path value );

}
