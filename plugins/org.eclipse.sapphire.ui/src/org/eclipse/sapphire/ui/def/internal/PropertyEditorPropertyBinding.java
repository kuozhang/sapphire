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

package org.eclipse.sapphire.ui.def.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode.Event;
import org.eclipse.sapphire.modeling.xml.XmlNode.EventType;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertyEditorPropertyBinding

    extends XmlValueBindingImpl

{
    private static final String EL_HINT = "hint"; //$NON-NLS-1$
    private static final String EL_PROPERTY = "property"; //$NON-NLS-1$
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element, property, params );
        
        final XmlElement root = xml();
        
        final XmlElement.Listener listener = new XmlElement.Listener()
        {
            @Override
            public void handle( final Event event )
            {
                final EventType type = event.getType();
                
                if( type == EventType.PRE_CHILD_ELEMENT_ADD )
                {
                    XmlElement propElement = root.getChildElement( EL_PROPERTY, false );
                    
                    if( propElement == null )
                    {
                        final String propName = root.getText();
                        
                        if( propName.length() > 0 )
                        {
                            root.setText( null );
                            propElement = root.getChildElement( EL_PROPERTY, true );
                            propElement.setText( propName );
                        }
                    }
                }
                else if( type == EventType.POST_CHILD_ELEMENT_REMOVE )
                {
                    final int childElementsCount = root.getChildElements().size();
                    
                    if( childElementsCount == 1 )
                    {
                        final XmlElement propElement = root.getChildElement( EL_PROPERTY, false );
                        
                        if( propElement != null )
                        {
                            final String propName = propElement.getText();
                            
                            propElement.remove();
                            
                            if( propName.length() > 0 )
                            {
                                root.setText( propName );
                            }
                        }
                    }
                }
            }
        };
        
        root.addListener( listener );
    }

    @Override
    public String read()
    {
        String value = null;
        final XmlElement el = xml( false );
        
        if( el != null )
        {
            final XmlElement propElement = el.getChildElement( EL_PROPERTY, false );
            
            if( propElement != null )
            {
                value = propElement.getText();
            }
            else
            {
                value = el.getText();
            }
        }
        
        return value;
    }

    @Override
    public void write( final String value )
    {
        final XmlElement el = xml( true );
        
        if( el.getChildElement( EL_PROPERTY, false ) != null ||
            el.getChildElement( EL_HINT, false ) != null )
        {
            el.setChildNodeText( EL_PROPERTY, value, true );
        }
        else
        {
            el.setText( value );
        }
    }

}
