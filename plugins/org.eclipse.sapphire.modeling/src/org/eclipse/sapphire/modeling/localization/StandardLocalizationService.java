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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.sapphire.modeling.CapitalizationType;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class StandardLocalizationService

    extends LocalizationService
    
{
    private final Locale locale;
    private Map<String,String> sourceLangToTranslation;
    
    public StandardLocalizationService( final Locale locale )
    {
        this.locale = locale;
    }
    
    protected final synchronized void init()
    {
        if( this.sourceLangToTranslation == null )
        {
            final Map<String,String> keyToSource = new HashMap<String,String>();
            final Map<String,String> keyToTranslation = new HashMap<String,String>();
            
            load( Locale.ENGLISH, keyToSource );
            load( locale, keyToTranslation );
            
            this.sourceLangToTranslation = new HashMap<String,String>();
            
            for( Map.Entry<String,String> entry : keyToSource.entrySet() )
            {
                final String key = entry.getKey();
                final String source = entry.getValue();
                final String translation = keyToTranslation.get( key );
                
                if( translation != null )
                {
                    this.sourceLangToTranslation.put( source, translation );
                }
            }
        }
    }
    
    protected abstract void load( Locale locale, Map<String,String> hashToTranslation );
    
    @Override
    public String string( final String sourceLangString,
                          final CapitalizationType capitalizationType,
                          final boolean includeMnemonic )
    {
        init();
        
        String result = this.sourceLangToTranslation.get( sourceLangString );
        
        if( result == null )
        {
            result = sourceLangString;
        }
        
        result = transform( result, capitalizationType, includeMnemonic );
        
        return result;
    }
    
}
