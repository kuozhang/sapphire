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

package org.eclipse.sapphire.sdk.build.internal;

import static org.eclipse.sapphire.sdk.build.internal.DomUtil.doc;
import static org.eclipse.sapphire.sdk.build.internal.DomUtil.elements;
import static org.eclipse.sapphire.sdk.build.internal.DomUtil.text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Localizable;
import org.eclipse.sapphire.modeling.localization.LocalizationUtil;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.sdk.ISapphireExtensionDef;
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
    
    public static boolean check( final File file )
    {
        final String fileName = file.getName();
        
        if( fileName.endsWith( ".sdef" ) )
        {
            return true;
        }
        else if( fileName.equals( "sapphire-extension.xml" ) && file.getParentFile().getName().equals( "META-INF" ) )
        {
            return true;
        }
        
        return false;
    }
    
    public static String extract( final File file )
    
        throws Exception
        
    {
        // Gather string resources from the input..
        
        final Set<String> strings = new HashSet<String>();
        
        if( file.getName().endsWith( ".sdef" ) )
        {
            final Document doc = doc( file );
            
            if( doc == null )
            {
                return null;
            }
            
            final Element root = doc.getDocumentElement();
            
            if( root == null )
            {
                return null;
            }
            
            gather( root, strings );
        }
        else
        {
            final IModelElement root = ISapphireExtensionDef.TYPE.instantiate( new RootXmlResource( new XmlResourceStore( file ) ) );
            
            try
            {
                gather( root, strings );
            }
            finally
            {
                root.dispose();
            }
        }
        
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
    
    private static void gather( final IModelElement element,
                                final Set<String> strings )
    {
        for( ModelProperty property : element.getModelElementType().getProperties() )
        {
            if( property instanceof ValueProperty )
            {
                if( property.hasAnnotation( Localizable.class ) )
                {
                    final String value = element.read( (ValueProperty) property ).getText( false );
                    
                    if( value != null )
                    {
                        strings.add( value );
                    }
                }
            }
            else if( property instanceof ListProperty )
            {
                for( IModelElement child : element.read( (ListProperty) property ) )
                {
                    gather( child, strings );
                }
            }
            else if( property instanceof ImpliedElementProperty )
            {
                gather( element.read( (ImpliedElementProperty) property ), strings );
            }
            else if( property instanceof ElementProperty )
            {
                final IModelElement child = element.read( (ElementProperty) property ).element();
                
                if( child != null )
                {
                    gather( child, strings );
                }
            }
        }
    }
    
}
