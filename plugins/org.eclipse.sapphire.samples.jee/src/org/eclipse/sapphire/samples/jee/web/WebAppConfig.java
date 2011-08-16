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

import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;
import org.eclipse.sapphire.samples.jee.DescribableExt;
import org.eclipse.sapphire.samples.jee.Param;
import org.eclipse.sapphire.samples.jee.SecurityRole;
import org.eclipse.sapphire.samples.jee.environment.EnvironmentConsumer;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@XmlRootBinding( elementName = "web-app", namespace = "http://java.sun.com/xml/ns/j2ee", schemaLocation = "http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd" )
@GenerateImpl

// TODO: Tool message-destination element. How is different from message-destination-ref. Is the second form newer? Are both relevant?

public interface WebAppConfig extends DescribableExt, EnvironmentConsumer
{
    ModelElementType TYPE = new ModelElementType( WebAppConfig.class );
    
    // *** Distributable ***
    
    @Type( base = Boolean.class )
    @Label( standard = "distributable" )
    @XmlValueBinding( path = "distributable", mapExistanceToValue = "true" )
    
    // TODO: Documentation
    
    ValueProperty PROP_DISTRIBUTABLE = new ValueProperty( TYPE, "Distributable" );
    
    Value<Boolean> getDistributable();
    void setDistributable( String value );
    void setDistributable( Boolean value );
    
    // *** ContextParams ***
    
    @Type( base = Param.class )
    @Label( standard = "context parameters" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "context-param", type = Param.class ) )
    
    @Documentation
    (
        content = "The web application's servlet context initialization parameters."
    )
    
    ListProperty PROP_CONTEXT_PARAMS = new ListProperty( TYPE, "ContextParams" );
    
    ModelElementList<Param> getContextParams();
    
    // *** Filters ***
    
    @Type( base = Filter.class )
    @Label( standard = "filters" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "filter", type = Filter.class ) )
    
    @Documentation
    (
        content = "Filters intercept and modify requests and response from the server. Once defined, " +
                  "filters are mapped to either a servlet or a URL pattern."
    )
    
    ListProperty PROP_FILTERS = new ListProperty( TYPE, "Filters" );
    
    ModelElementList<Filter> getFilters();
    
    // *** FilterMappings ***
    
    @Type( base = FilterMapping.class )
    @Label( standard = "filter mappings" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "filter-mapping", type = FilterMapping.class ) )
    
    @Documentation
    (
        content = "The container uses the filter mappings to decide which filters to apply to a request and " +
                  "in what order. The container matches request URL to a servlet in the normal manner. To " +
                  "determine which filters to apply, it matches filter mapping declarations either on " +
                  "servlet name or on URL pattern for each filter mapping. The order in which " +
                  "filters are invoked is the order of filter mappings in the web application configuration."
    )
    
    ListProperty PROP_FILTER_MAPPINGS = new ListProperty( TYPE, "FilterMappings" );
    
    ModelElementList<FilterMapping> getFilterMappings();
    
    // *** Listeners ***
    
    @Type( base = Listener.class )
    @Label( standard = "listeners" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "listener", type = Listener.class ) )
    
    @Documentation
    (
        content = "Listeners are notified of web application and HTTP session lifecycle events. To listen " +
                  "to web application events, implement javax.servlet.ServletContextListener. To " +
                  "listen to HTTP session events, implement javax.servlet.http.HttpSessionListener."
    )
    
    ListProperty PROP_LISTENERS = new ListProperty( TYPE, "Listeners" );
    
    ModelElementList<Listener> getListeners();

    // *** Servlets ***
    
    @Type( base = Servlet.class )
    @Label( standard = "servlet" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "servlet", type = Servlet.class ) )
    
    // TODO: documentation
    
    ListProperty PROP_SERVLETS = new ListProperty( TYPE, "Servlets" );
    
    ModelElementList<Servlet> getServlets();
    
    // *** ServletMappings ***
    
    @Type( base = ServletMapping.class )
    @Label( standard = "servlet mappings" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "servlet-mapping", type = ServletMapping.class ) )
    
    ListProperty PROP_SERVLET_MAPPINGS = new ListProperty( TYPE, "ServletMappings" );
    
    ModelElementList<ServletMapping> getServletMappings();
    
    // *** SessionConfig ***
    
    @Type( base = SessionConfig.class )
    @Label( standard = "session configuration" )
    @XmlBinding( path = "session-config" )
    
    ImpliedElementProperty PROP_SESSION_CONFIG = new ImpliedElementProperty( TYPE, "SessionConfig" );
    
    SessionConfig getSessionConfig();
    
    // *** MimeTypeMappings ***
    
    @Type( base = MimeTypeMapping.class )
    @Label( standard = "MIME type mappings" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "mime-mapping", type = MimeTypeMapping.class ) )
    
    @Documentation
    (
        content = "MIME type mappings tell the container how to set the content type when serving files with unrecognized " +
                  "extensions."
    )

    ListProperty PROP_MIME_TYPE_MAPPINGS = new ListProperty( TYPE, "MimeTypeMappings" );
    
    ModelElementList<MimeTypeMapping> getMimeTypeMappings();
    
    // *** LocaleEncodingMappings ***
    
    @Type( base = LocaleEncodingMapping.class )
    @Label( standard = "locale encoding mappings" )
    @XmlListBinding( path = "locale-encoding-mapping-list", mappings = @XmlListBinding.Mapping( element = "locale-encoding-mapping", type = LocaleEncodingMapping.class ) )
    
    @Documentation
    (
        content = "Locale encoding mappings tell the container how to set the character encoding when a servlet " +
                  "specifies a particular locale in the ServletResponse.setLocale() call. If the web application " +
                  "does not specify a locale encoding mapping, the mapping is container dependent"
    )
    
    ListProperty PROP_LOCALE_ENCODING_MAPPINGS = new ListProperty( TYPE, "LocaleEncodingMappings" );
    
    ModelElementList<LocaleEncodingMapping> getLocaleEncodingMappings();
    
    // *** WelcomeFiles ***
    
    @Type( base = WelcomeFile.class )
    @Label( standard = "welcome files" )
    @XmlListBinding( path = "welcome-file-list", mappings = @XmlListBinding.Mapping( element = "welcome-file", type = WelcomeFile.class ) )
    
    @Documentation
    (
        content = "When a request for a folder path is received, the server uses the list of welcome file names " +
                  "(such as index.html) to determine the content of the response. Files are matched in order of specification. " +
                  "[pbr/]" +
                  "If not specified by the web application, the server is responsible for providing the default list. Depending " +
                  "on server implimentation, this list may be configurable."
    )

    ListProperty PROP_WELCOME_FILES = new ListProperty( TYPE, "WelcomeFiles" );
    
    ModelElementList<WelcomeFile> getWelcomeFiles();
    
    // *** ErrorPages ***
    
    @Type( base = ErrorPage.class )
    @Label( standard = "error pages" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "error-page", type = ErrorPage.class ) )
    
    @Documentation
    (
        content = "A web application can provide custom error pages to show when an HTTP error response is generated or when " +
                  "a Java exception is encountered. " +
                  "[pbr/]" +
                  "Each error page definition maps one HTTP error response code or a Java " +
                  "exception to a resource in the web application."
    )

    ListProperty PROP_ERROR_PAGES = new ListProperty( TYPE, "ErrorPages" );
    
    ModelElementList<ErrorPage> getErrorPages();

    // *** JspConfig ***
    
    @Type( base = JspConfig.class )
    @Label( standard = "JSP configuration" )
    @XmlBinding( path = "jsp-config" )
    
    @Documentation
    (
        content = "Provides global configuration information for the JSP files in the web application."
    )

    ImpliedElementProperty PROP_JSP_CONFIG = new ImpliedElementProperty( TYPE, "JspConfig" );
    
    JspConfig getJspConfig();
    
    // *** SecurityRoles ***
    
    @Type( base = SecurityRole.class )
    @Label( standard = "security roles" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "security-role", type = SecurityRole.class ) )
    
    ListProperty PROP_SECURITY_ROLES = new ListProperty( TYPE, "SecurityRoles" );
    
    ModelElementList<SecurityRole> getSecurityRoles();
    
    // *** SecurityConstraints ***
    
    @Type( base = SecurityConstraint.class )
    @Label( standard = "security constraints" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "security-constraint", type = SecurityConstraint.class ) )
    
    ListProperty PROP_SECURITY_CONSTRAINTS = new ListProperty( TYPE, "SecurityConstraints" );
    
    ModelElementList<SecurityConstraint> getSecurityConstraints();
    
    // *** AuthenticationConfig ***
    
    @Type( base = AuthenticationConfig.class )
    @Label( standard = "authentication configuration" )
    @XmlBinding( path = "login-config" )
    
    ImpliedElementProperty PROP_AUTHENTICATION_CONFIG = new ImpliedElementProperty( TYPE, "AuthenticationConfig" );
    
    AuthenticationConfig getAuthenticationConfig();
    
}
