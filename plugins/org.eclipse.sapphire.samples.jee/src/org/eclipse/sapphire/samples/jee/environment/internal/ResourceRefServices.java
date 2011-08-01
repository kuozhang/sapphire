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

package org.eclipse.sapphire.samples.jee.environment.internal;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ResourceRefServices
{
    public static final class SharedBinding extends XmlValueBindingImpl
    {
        private static final String EL_RES_SHARING_SCOPE = "res-sharing-scope";
        private static final String SHAREABLE = "Shareable";
        private static final String UNSHAREABLE = "Unshareable";

        @Override
        public String read()
        {
            final XmlElement el = xml().getChildElement( EL_RES_SHARING_SCOPE, false );
            
            if( el != null )
            {
                final String text = el.getText().trim();
                
                if( text.equalsIgnoreCase( SHAREABLE ) )
                {
                    return String.valueOf( true );
                }
                else if( text.equalsIgnoreCase( UNSHAREABLE ) )
                {
                    return String.valueOf( false );
                }
            }

            return null;
        }

        @Override
        public void write( final String value )
        {
            final XmlElement xml = xml();
            String text = null;
            
            if( value != null )
            {
                if( value.equalsIgnoreCase( "true" ) )
                {
                    text = SHAREABLE;
                }
                else if( value.equalsIgnoreCase( "false" ) )
                {
                    text = UNSHAREABLE;
                }
                else
                {
                    text = value;
                }
            }
            
            xml.setChildNodeText( EL_RES_SHARING_SCOPE, text, true );
        }

        @Override
        public XmlNode getXmlNode()
        {
            return xml().getChildElement( EL_RES_SHARING_SCOPE, false );
        }
    }
    
}
