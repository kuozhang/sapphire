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

package org.eclipse.sapphire.modeling.localization;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ResourceBundleLocalizationService

    extends StandardLocalizationService
    
{
    private final ClassLoader classLoader;
    private final String bundleName;
    
    public ResourceBundleLocalizationService( final ClassLoader classLoader,
                                              final String bundleName,
                                              final Locale locale )
    {
        super( locale );
        
        this.classLoader = classLoader;
        this.bundleName = bundleName;
    }
    
    @Override
    protected void load( final Locale locale,
                         final Map<String,String> hashToTranslation )
    {
        try
        {
            final ResourceBundle resourceBundle 
                = ResourceBundle.getBundle( this.bundleName, locale, this.classLoader );
            
            for( Enumeration<String> keys = resourceBundle.getKeys(); keys.hasMoreElements(); )
            {
                final String k = keys.nextElement();
                hashToTranslation.put( k, resourceBundle.getString( k ) );
            }
        }
        catch( MissingResourceException e )
        {
            // Intentionally ignoring.
        }
    }

}
