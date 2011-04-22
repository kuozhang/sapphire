/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [bugzilla 329114] rewrite context help binding feature
 ******************************************************************************/

package org.eclipse.sapphire.ui.def.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.ui.def.IDefinitionReference;
import org.eclipse.sapphire.ui.def.IImportDirective;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationDef;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.SapphireUiDefFactory;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireUiDefMethods
{
    public static List<ISapphireUiDef> getImportedDefinitions( final ISapphireUiDef def )
    {
        final List<ISapphireUiDef> result = new ArrayList<ISapphireUiDef>();
        
        for( IImportDirective importDirective : def.getImportDirectives() )
        {
            final String bundleId = importDirective.getBundle().getText();
            
            if( bundleId != null )
            {
                for( IDefinitionReference ref : importDirective.getDefinitions() )
                {
                    final String path = ref.getPath().getText();
                    
                    if( path != null )
                    {
                        final ISapphireUiDef referencedDefinition = SapphireUiDefFactory.load( bundleId, path );
                        
                        if( referencedDefinition != null )
                        {
                            result.add( referencedDefinition );
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    public static ISapphirePartDef getPartDef( final ISapphireUiDef rootdef,
                                               final String id,
                                               final boolean searchImportedDefinitions,
                                               final Class<?> expectedType )
    {
        if( id != null )
        {
            for( ISapphirePartDef def : rootdef.getPartDefs() )
            {
                if( id.equals( def.getId().getText() ) )
                {
                    if( expectedType != null && ! expectedType.isAssignableFrom( def.getClass() ) )
                    {
                        final String msg = Resources.bind( Resources.doesNotImplement, id, expectedType.getName() );
                        SapphireUiFrameworkPlugin.logError( msg );
                        
                        return null;
                    }
                    
                    return def;
                }
            }
            
            if( searchImportedDefinitions )
            {
                for( ISapphireUiDef importedDefinition : rootdef.getImportedDefinitions() )
                {
                    final ISapphirePartDef def = importedDefinition.getPartDef( id, true, expectedType );
                    
                    if( def != null )
                    {
                        return def;
                    }
                }
            }
        }
        
        return null;
    }

    public static ISapphireDocumentationDef getDocumentationDef(final ISapphireUiDef rootdef, final String id,
                                                            final boolean searchImportedDefinitions) {
        if (id != null) {
            for (ISapphireDocumentationDef def : rootdef.getDocumentationDefs()) {
                if (id.equals(def.getId().getText())) {
                    return def;
                }
            }

            if (searchImportedDefinitions) {
                for (ISapphireUiDef importedDefinition : rootdef.getImportedDefinitions()) {
                    final ISapphireDocumentationDef def = importedDefinition.getDocumentationDef(id, true);

                    if (def != null) {
                        return def;
                    }
                }
            }
        }

        return null;
    }

    public static Class<?> resolveClass( final ISapphireUiDef def,
                                         final String className )
    {
        for( IImportDirective directive : def.getImportDirectives() )
        {
            final Class<?> cl = directive.resolveClass( className );
            
            if( cl != null )
            {
                return cl;
            }
        }
        
        return null;
    }
    
    public static ModelProperty resolveProperty( final ISapphireUiDef def,
                                                 final String qualifiedPropertyName )
    {
        ModelProperty property = null;
        
        if( qualifiedPropertyName != null )
        {
            final int dot = qualifiedPropertyName.indexOf( '.' );
            final String className = qualifiedPropertyName.substring( 0, dot );
            final String propertyName = qualifiedPropertyName.substring( dot + 1 );
            final Class<?> cl = resolveClass( def, className );
            
            if( cl == null )
            {
                return null;
            }
            
            try
            {
                final Field field = cl.getField( propertyName );
                property = (ModelProperty) field.get( null ); 
            }
            catch( Throwable e )
            {
                System.err.println( "Failed to resolve property: " + qualifiedPropertyName );
                e.printStackTrace();
                return null;
            }
        }
        
        return property;
    }
    
    public static ImageData resolveImage( final ISapphireUiDef def,
                                          final String imagePath )
    {
        ImageData img = null;
        
        for( IImportDirective directive : def.getImportDirectives() )
        {
            img = directive.resolveImage( imagePath );
            
            if( img != null )
            {
                break;
            }
        }
        
        return img;
    }
    
    private static final class Resources extends NLS
    {
        public static String doesNotImplement;
    
        static
        {
            initializeMessages( SapphireUiDefMethods.class.getName(), Resources.class );
        }
    }
    
}
