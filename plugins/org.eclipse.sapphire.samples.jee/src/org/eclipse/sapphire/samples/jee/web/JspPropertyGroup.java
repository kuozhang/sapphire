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
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.samples.jee.DescribableExt;
import org.eclipse.sapphire.samples.jee.internal.InvertingBooleanXmlValueBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "JSP property group" )
@GenerateImpl

public interface JspPropertyGroup extends IModelElement, DescribableExt
{
    ModelElementType TYPE = new ModelElementType( JspPropertyGroup.class );
    
    // *** UrlPatterns ***
    
    @Type( base = UrlPattern.class )
    @Label( standard = "URL patterns" )
    @CountConstraint( min = 1 )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "url-pattern", type = UrlPattern.class ) )
    
    @Documentation
    (
        content = "The set of URL patterns define the group of JSP pages that these properties should " +
                  "affect. A URL pattern must follow the rules specified in Section 11.2 of the Servlet API " +
                  "Specification. This pattern is assumed to be in URL-decoded form and must not contain " +
                  "CR (#xD) or LF (#xA). The container must preserve all other characters including " +
                  "whitespace."
    )
    
    ListProperty PROP_URL_PATTERNS = new ListProperty( TYPE, "UrlPatterns" );
    
    ModelElementList<UrlPattern> getUrlPatterns();
    
    // *** PageEncoding ***
    
    @Label( standard = "page encoding" )
    @XmlBinding( path = "page-encoding" )

    @Documentation
    (
        content = "The valid values are those of pageEncoding directive. It is a translation-time " +
                  "error to name different encodings in the pageEncoding attribute of the page directive " +
                  "of a JSP page and in a JSP configuration element matching the page. It is also a " +
                  "translation-time error to name different encodings in the prolog or text declaration " +
                  "of a document in XML syntax and in a JSP configuration element matching the document. " +
                  "It is legal to name the same encoding through multiple mechanisms."
    )
    
    // TODO: Research default value and possible values.
    
    ValueProperty PROP_PAGE_ENCODING = new ValueProperty( TYPE, "PageEncoding" );
    
    Value<String> getPageEncoding();
    void setPageEncoding( String value );
    
    // *** ExpressionsLanguageEnabled ***
    
    @Type( base = Boolean.class )
    @Label( standard = "expression language enabled" )
    @DefaultValue( text = "true" )
    @CustomXmlValueBinding( impl = InvertingBooleanXmlValueBinding.class, params = "el-ignored" )

    @Documentation
    (
        content = "Specifies whether expressions language (EL) evaluation is enabled for the matched group of JSP pages. " +
                  "By default, the EL evaluation is enabled for web applications using Servlet 2.4 or greater spec, " +
                  "and disabled otherwise."
    )
    
    // TODO: Default varies based on spec level
    
    ValueProperty PROP_EXPRESSIONS_LANGUAGE_ENABLED = new ValueProperty( TYPE, "ExpressionsLanguageEnabled" );
    
    Value<Boolean> getExpressionsLanguageEnabled();
    void setExpressionsLanguageEnabled( String value );
    void setExpressionsLanguageEnabled( Boolean value );
    
    // *** ScriptingEnabled ***
    
    @Type( base = Boolean.class )
    @Label( standard = "scripting enabled" )
    @DefaultValue( text = "true" )
    @CustomXmlValueBinding( impl = InvertingBooleanXmlValueBinding.class, params = "scripting-invalid" )
    
    @Documentation
    (
        content = "Specifies whether scripting is enabled for the matched group of JSP pages."
    )
    
    ValueProperty PROP_SCRIPTING_ENABLED = new ValueProperty( TYPE, "ScriptingEnabled" );
    
    Value<Boolean> getScriptingEnabled();
    void setScriptingEnabled( String value );
    void setScriptingEnabled( Boolean value );
    
    // *** XmlContent ***
    
    @Type( base = Boolean.class )
    @Label( standard = "XML content" )
    @DefaultValue( text = "false" )
    @XmlBinding( path = "is-xml" )
    
    @Documentation
    (
        content = "If true, denotes that the matched group of resources are JSP documents, and thus must be " +
                  "interpreted as XML documents." +
                  "[pbr/]" +
                  "If false, the resources are assumed to not be JSP documents, unless there is another property " +
                  "group that indicates otherwise."
    )
    
    // TODO: Determine the actual default value
    
    ValueProperty PROP_XML_CONTENT = new ValueProperty( TYPE, "XmlContent" );
    
    Value<Boolean> getXmlContent();
    void setXmlContent( String value );
    void setXmlContent( Boolean value );
    
    // *** Preludes ***
    
    @Type( base = JspInclude.class )
    @Label( standard = "preludes" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "include-prelude", type = JspInclude.class ) )
    
    @Documentation
    (
        content = "Preludes are treated as an automatic include (similar to the include directive) at the beginning of " +
                  "each matched JSP page. Each prelude must specify a path (relative to the root of the web application) " +
                  "where to find the resource to be included."
    )
    
    ListProperty PROP_PRELUDES = new ListProperty( TYPE, "Preludes" );
    
    ModelElementList<JspInclude> getPreludes();
    
    // *** Codas ***
    
    @Type( base = JspInclude.class )
    @Label( standard = "codas" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "include-coda", type = JspInclude.class ) )
    
    @Documentation
    (
        content = "Codas are treated as an automatic include (similar to the include directive) at the end of " +
                  "each matched JSP page. Each coda must specify a path (relative to the root of the web application) " +
                  "where to find the resource to be included."
    )
    
    ListProperty PROP_CODAS = new ListProperty( TYPE, "Codas" );
    
    ModelElementList<JspInclude> getCodas();
    
}
