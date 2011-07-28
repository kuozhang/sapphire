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
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.jee.web.internal.LocaleEncodingMappingServices;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "locale encoding mapping" )

@Documentation
(
    content = "Locale encoding mappings tell the container how to set the character encoding when a servlet " +
              "specifies a particular locale in the ServletResponse.setLocale() call. If the web application " +
              "does not specify a locale encoding mapping, the mapping is container dependent"
)

@GenerateImpl

public interface ILocaleEncodingMapping extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( ILocaleEncodingMapping.class );
    
    // *** Locale ***
    
    @Label( standard = "locale" )
    @Required
    @Service( impl = LocaleEncodingMappingServices.LocalePossibleValuesService.class )
    @XmlBinding( path = "locale" )
    
    @Documentation
    (
        content = "Locale is either a language code as defined by ISO-639-1 or a language code " +
                  "followed by a country code as defined by ISO-3166." +
                  "[pbr/]" +
                  "Examples: ja, ja_JP"
    )
    
    ValueProperty PROP_LOCALE = new ValueProperty( TYPE, "Locale" );
    
    Value<String> getLocale();
    void setLocale( String value );
    
    // *** Encoding ***
    
    @Label( standard = "encoding" )
    @Required
    @XmlBinding( path = "encoding" )
    
    ValueProperty PROP_ENCODING = new ValueProperty( TYPE, "Encoding" );
    
    Value<String> getEncoding();
    void setEncoding( String value );

}
