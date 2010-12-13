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

package org.eclipse.sapphire.modeling;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class ResourceStore
{
    private Map<Locale,Map<String,String>> localizedResources = null;
    
    public <A> A adapt( final Class<A> adapterType )
    {
        return null;
    }

    /**
     * @throws ResourceStoreException  
     */
    
    public void save() 
    
        throws ResourceStoreException
        
    {
        // The default implementation doesn't do anything.
    }

    public void validateEdit() 
        
        throws ValidateEditException
        
    {
        validateSave();
    }
    
    public void validateSave()
    
        throws ValidateEditException
        
    {
        // The default implementation doesn't do anything.
    }
    
    public boolean isOutOfDate()
    {
        return false;
    }
    
    protected Map<String,String> loadLocalizedResources( final Locale locale )
    {
        return null;
    }
    
    public final Map<String,String> getLocalizedResources( final Locale locale )
    {
        if( this.localizedResources == null )
        {
            this.localizedResources = new HashMap<Locale,Map<String,String>>();
        }
    
        if( ! this.localizedResources.containsKey( locale ) )
        {
            Map<String,String> localizedResources = loadLocalizedResources( locale );
            
            if( localizedResources == null )
            {
                if( locale.getVariant().length() > 0 )
                {
                    localizedResources = getLocalizedResources( new Locale( locale.getLanguage(), locale.getCountry() ) );
                }
                else if( locale.getCountry().length() > 0 )
                {
                    localizedResources = getLocalizedResources( new Locale( locale.getLanguage() ) );
                }
            }
            
            this.localizedResources.put( locale, localizedResources );
        }
        
        return this.localizedResources.get( locale );
    }
    
}
