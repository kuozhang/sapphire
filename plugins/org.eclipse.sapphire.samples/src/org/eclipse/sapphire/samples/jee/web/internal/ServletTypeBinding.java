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
import org.eclipse.sapphire.samples.jee.web.ServletType;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ServletTypeBinding extends XmlValueBindingImpl
{
    private static final String EL_SERVLET_CLASS = "servlet-class";
    private static final String EL_JSP_FILE = "jsp-file";

    @Override
    public String read()
    {
        final XmlElement parent = xml();
        
        if( parent.getChildElement( EL_SERVLET_CLASS, false ) != null )
        {
            return ServletType.CLASS.name();
        }
        else if( parent.getChildElement( EL_JSP_FILE, false ) != null )
        {
            return ServletType.JSP.name();
        }
        
        return null;
    }

    @Override
    public void write( final String value )
    {
        final XmlElement parent = xml();
        
        if( ServletType.CLASS.name().equals( value ) )
        {
            parent.removeChildNode( EL_JSP_FILE );
            parent.getChildElement( EL_SERVLET_CLASS, true );
        }
        else if( ServletType.JSP.name().equals( value ) )
        {
            parent.removeChildNode( EL_SERVLET_CLASS );
            parent.getChildElement( EL_JSP_FILE, true );
        }
        else
        {
            parent.removeChildNode( EL_SERVLET_CLASS );
            parent.removeChildNode( EL_JSP_FILE );
        }
    }

    @Override
    public XmlNode getXmlNode()
    {
        final XmlElement parent = xml();
        
        XmlElement element = parent.getChildElement( EL_SERVLET_CLASS, false );
        
        if( element != null )
        {
            return element;
        }
        
        element = parent.getChildElement( EL_JSP_FILE, false );
        
        if( element != null )
        {
            return element;
        }
        
        return null;
    }
    
}
