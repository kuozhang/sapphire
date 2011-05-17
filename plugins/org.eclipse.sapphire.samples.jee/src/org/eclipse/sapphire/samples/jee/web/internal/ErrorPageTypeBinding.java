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
import org.eclipse.sapphire.samples.jee.web.ErrorPageType;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ErrorPageTypeBinding extends XmlValueBindingImpl
{
    private static final String EL_ERROR_CODE = "error-code";
    private static final String EL_EXCEPTION_TYPE = "exception-type";

    @Override
    public String read()
    {
        final XmlElement parent = xml();
        
        if( parent.getChildElement( EL_ERROR_CODE, false ) != null )
        {
            return ErrorPageType.HTTP_ERROR_CODE.name();
        }
        else if( parent.getChildElement( EL_EXCEPTION_TYPE, false ) != null )
        {
            return ErrorPageType.JAVA_EXCEPTION.name();
        }
        
        return null;
    }

    @Override
    public void write( final String value )
    {
        final XmlElement parent = xml();
        
        if( ErrorPageType.HTTP_ERROR_CODE.name().equals( value ) )
        {
            parent.removeChildNode( EL_EXCEPTION_TYPE );
            parent.getChildElement( EL_ERROR_CODE, true );
        }
        else if( ErrorPageType.JAVA_EXCEPTION.name().equals( value ) )
        {
            parent.removeChildNode( EL_ERROR_CODE );
            parent.getChildElement( EL_EXCEPTION_TYPE, true );
        }
        else
        {
            parent.removeChildNode( EL_ERROR_CODE );
            parent.removeChildNode( EL_EXCEPTION_TYPE );
        }
    }

    @Override
    public XmlNode getXmlNode()
    {
        final XmlElement parent = xml();
        
        XmlElement element = parent.getChildElement( EL_ERROR_CODE, false );
        
        if( element != null )
        {
            return element;
        }
        
        element = parent.getChildElement( EL_EXCEPTION_TYPE, false );
        
        if( element != null )
        {
            return element;
        }
        
        return null;
    }
    
}
