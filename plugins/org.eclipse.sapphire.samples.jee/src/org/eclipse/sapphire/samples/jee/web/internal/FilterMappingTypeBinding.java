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

package org.eclipse.sapphire.samples.jee.web.internal;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.samples.jee.web.FilterMappingType;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FilterMappingTypeBinding extends XmlValueBindingImpl
{
    private static final String EL_SERVLET_NAME = "servlet-name";
    private static final String EL_URL_PATTERN = "url-pattern";

    @Override
    public String read()
    {
        final XmlElement parent = xml();
        
        if( parent.getChildElement( EL_SERVLET_NAME, false ) != null )
        {
            return FilterMappingType.SERVLET.name();
        }
        else if( parent.getChildElement( EL_URL_PATTERN, false ) != null )
        {
            return FilterMappingType.URL_PATTERN.name();
        }
        
        return null;
    }

    @Override
    public void write( final String value )
    {
        final XmlElement parent = xml();
        
        if( FilterMappingType.SERVLET.name().equals( value ) )
        {
            parent.removeChildNode( EL_URL_PATTERN );
            parent.getChildElement( EL_SERVLET_NAME, true );
        }
        else if( FilterMappingType.URL_PATTERN.name().equals( value ) )
        {
            parent.removeChildNode( EL_SERVLET_NAME );
            parent.getChildElement( EL_URL_PATTERN, true );
        }
        else
        {
            parent.removeChildNode( EL_SERVLET_NAME );
            parent.removeChildNode( EL_URL_PATTERN );
        }
    }

    @Override
    public XmlNode getXmlNode()
    {
        final XmlElement parent = xml();
        
        XmlElement element = parent.getChildElement( EL_SERVLET_NAME, false );
        
        if( element != null )
        {
            return element;
        }
        
        element = parent.getChildElement( EL_URL_PATTERN, false );
        
        if( element != null )
        {
            return element;
        }
        
        return null;
    }
    
}
