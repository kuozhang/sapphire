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
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;
import org.eclipse.sapphire.samples.jee.web.internal.ServletReferenceService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "servlet mapping" )

@Documentation
(
    content = "Defines a mapping between a servlet and a URL pattern."
)

@GenerateImpl

public interface IServletMapping extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IServletMapping.class );
    
    // *** Servlet ***
    
    @Reference( target = IServlet.class )
    @Service( impl = ServletReferenceService.class )
    @Label( standard = "servlet" )
    @Required
    @MustExist
    @PossibleValues( property = "/Servlets/Name" )
    @XmlValueBinding( path = "servlet-name" )
    
    ValueProperty PROP_SERVLET = new ValueProperty( TYPE, "Servlet" );
    
    ReferenceValue<String,IServlet> getServlet();
    void setServlet( String value );
    
    // *** UrlPattern ***
    
    @Label( standard = "URL pattern" )
    @Required
    @XmlValueBinding( path = "url-pattern" )
    
    ValueProperty PROP_URL_PATTERN = new ValueProperty( TYPE, "UrlPattern" );
    
    Value<String> getUrlPattern();
    void setUrlPattern( String value );
    
}
