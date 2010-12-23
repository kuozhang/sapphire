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

package org.eclipse.sapphire.ui.build.internal;

import static org.eclipse.sapphire.ui.build.internal.DomUtil.doc;
import static org.eclipse.sapphire.ui.build.internal.DomUtil.elements;
import static org.eclipse.sapphire.ui.build.internal.DomUtil.text;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.sapphire.modeling.localization.LocalizationUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StringResourcesExtractor
{
    private static final Set<String> LOCALIZABLE_ELEMENTS = new HashSet<String>();
    
    static
    {
        LOCALIZABLE_ELEMENTS.add( "page-header-text" );
        LOCALIZABLE_ELEMENTS.add( "initial-selection" );
        LOCALIZABLE_ELEMENTS.add( "label" );
        LOCALIZABLE_ELEMENTS.add( "conditional" );
        LOCALIZABLE_ELEMENTS.add( "description" );
        LOCALIZABLE_ELEMENTS.add( "null-value-label" );
    }
    
    public static String extract( final Reader input )
    
        throws Exception
        
    {
        // Gather string resources from the input..
        
        final Document doc = doc( input );
        
        if( doc == null )
        {
            return null;
        }
        
        final Element root = doc.getDocumentElement();
        
        if( root == null )
        {
            return null;
        }
        
        final Set<String> strings = new HashSet<String>();
        
        gather( root, strings );
        
        if( strings.isEmpty() )
        {
            return null;
        }
        
        // Build a lookup table with synthesized resource keys.
        
        final Properties resources = new Properties();
        
        for( String string : strings )
        {
            final String digest = LocalizationUtil.createStringDigest( string );
            resources.put( digest, string );
        }
        
        // Serialize the resources file content and return it to caller.
        
        final ByteArrayOutputStream resourcesFileContentBytes = new ByteArrayOutputStream();
        resources.store( resourcesFileContentBytes, null );

        final String resourcesFileContent = new String( resourcesFileContentBytes.toByteArray() );

        return resourcesFileContent;
    }

    private static void gather( final Element element,
                                final Set<String> strings )
    {
        if( LOCALIZABLE_ELEMENTS.contains( element.getLocalName() ) )
        {
            String text = text( element );
            
            if( text != null )
            {
                text = text.trim();
                
                if( text.length() != 0 )
                {
                    strings.add( text );
                }
            }
        }
        
        for( Element child : elements( element ) )
        {
            gather( child, strings );
        }
    }
    
}
