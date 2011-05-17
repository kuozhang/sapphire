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
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;
import org.eclipse.sapphire.samples.jee.web.internal.FilterMappingTypeBinding;
import org.eclipse.sapphire.samples.jee.web.internal.FilterReferenceService;
import org.eclipse.sapphire.samples.jee.web.internal.ServletReferenceService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "filter mapping" )
@GenerateImpl

public interface IFilterMapping extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IFilterMapping.class );
    
    // *** Filter ***
    
    @Reference( target = IFilter.class )
    @Service( impl = FilterReferenceService.class )
    @Label( standard = "filter" )
    @Required
    @MustExist
    @PossibleValues( property = "/Filters/Name" )
    @XmlBinding( path = "filter-name" )
    
    @Documentation
    (
        content = "The name of a defined filter."
    )
    
    ValueProperty PROP_FILTER = new ValueProperty( TYPE, "Filter" );
    
    ReferenceValue<String,IFilter> getFilter();
    void setFilter( String value );
    
    // *** Type ***
    
    @Type( base = FilterMappingType.class )
    @Label( standard = "type" )
    @DefaultValue( text = "SERVLET" )
    @CustomXmlValueBinding( impl = FilterMappingTypeBinding.class )
    
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );
    
    Value<FilterMappingType> getType();
    void setType( String value );
    void setType( FilterMappingType value );
    
    // *** Servlet ***
    
    @Reference( target = IServlet.class )
    @Service( impl = ServletReferenceService.class )
    @Label( standard = "servlet" )
    @Required
    @MustExist
    @PossibleValues( property = "/Servlets/Name" )
    @Enablement( expr = "${ Type == 'SERVLET' }" )
    @XmlValueBinding( path = "servlet-name", removeNodeOnSetIfNull = false )
    
    // TODO: documentation
    
    ValueProperty PROP_SERVLET = new ValueProperty( TYPE, "Servlet" );
    
    ReferenceValue<String,IServlet> getServlet();
    void setServlet( String value );
    
    // *** UrlPattern ***
    
    @Label( standard = "URL pattern" )
    @Required
    @Enablement( expr = "${ Type == 'URL_PATTERN' }" )
    @XmlValueBinding( path = "url-pattern", removeNodeOnSetIfNull = false )
    
    // TODO: documentation
    
    ValueProperty PROP_URL_PATTERN = new ValueProperty( TYPE, "UrlPattern" );
    
    Value<String> getUrlPattern();
    void setUrlPattern( String value );

    // *** DispatcherEvents ***
    
    @Type( base = IDispatcherEventRef.class )
    @Label( standard = "dispatcher events" )
    @CountConstraint( max = 4 )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "dispatcher", type = IDispatcherEventRef.class ) )
    
    @Documentation
    (
        content = "Control which dispatcher events the filter should map to. There are four dispatcher events: " +
                  "forward, request, include and error.[br/][br/]" +
                  
                  "If forward event is referenced, the filter will be applied under RequestDispatcher.forward() " +
                  "calls.[br/][br/]" +
                  
                  "If request event is referenced, the filter will be applied under ordinary client calls to the " +
                  "path or a servlet.[br/][br/]" +
                  
                  "If include event is referenced, the filter will be applied under RequestDispatcher.include() " +
                  "calls.[br/][br/]" +
                  
                  "If error event is referenced, the filter will be applied under the error page mechanism.[br/][br/]" +
                  
                  "The absence of any dispatcher event references indicates a default of applying the filter only " +
                  "under ordinary client calls to the path or a servlet."
    )
    
    ListProperty PROP_DISPATCHER_EVENTS = new ListProperty( TYPE, "DispatcherEvents" );
    
    ModelElementList<IDispatcherEventRef> getDispatcherEvents();
    
}
