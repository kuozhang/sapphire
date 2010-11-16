/******************************************************************************
 * Copyright (c) 2010 Oracle
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

public abstract class Resource
{
    private static final Locale ROOT_LOCALE = new Locale( "" );

    private final Resource parent;
    private IModelElement element;
    private final Map<ModelProperty,BindingImpl> bindings = new HashMap<ModelProperty,BindingImpl>();
    private CorruptedResourceExceptionInterceptor corruptedResourceExceptionInterceptor;
    private Map<String,String> defaultResourcesReverseLookup = null;
    private Map<Locale,Map<String,String>> localizedResources = null;
    
    public Resource( final Resource parent )
    {
        this.parent = parent;
    }
    
    public void init( final IModelElement element )
    {
        if( this.element != null )
        {
            throw new IllegalStateException();
        }
        
        this.element = element;
    }

    public Resource parent()
    {
        return this.parent;
    }
    
    public Resource root()
    {
        if( this.parent == null )
        {
            return this;
        }
        else
        {
            return this.parent.root();
        }
    }
    
    public final IModelElement element()
    {
        return this.element;
    }
    
    public final ValueBindingImpl binding( final ValueProperty property )
    {
        return (ValueBindingImpl) binding( (ModelProperty) property );
    }
    
    public final ElementBindingImpl binding( final ElementProperty property )
    {
        return (ElementBindingImpl) binding( (ModelProperty) property );
    }
    
    public final ListBindingImpl binding( final ListProperty property )
    {
        return (ListBindingImpl) binding( (ModelProperty) property );
    }
    
    public final BindingImpl binding( final ModelProperty property )
    {
        BindingImpl binding = this.bindings.get( property );
        
        if( binding == null )
        {
            binding = createBinding( property );
            
            if( binding == null )
            {
                throw new IllegalArgumentException();
            }
            
            this.bindings.put( property, binding );
        }
        
        return binding;
    }
    
    protected abstract BindingImpl createBinding( final ModelProperty property );
    
    /**
     * @throws ResourceStoreException  
     */
    
    public void save() 
    
        throws ResourceStoreException
        
    {
        final Resource root = root();
        
        if( this != root )
        {
            root.save();
        }
    }
    
    public <A> A adapt( final Class<A> adapterType )
    {
        A adapter = null;
        
        if( this.parent != null )
        {
            adapter = this.parent.adapt( adapterType );
        }
        
        return adapter;
    }
    
    public boolean isOutOfDate()
    {
        final Resource root = root();
        
        if( this != root )
        {
            return root.isOutOfDate();
        }
        
        return false;
    }
    
    public final String getLocalizedText( final String text,
                                          final Locale locale )
    {
        if( this.defaultResourcesReverseLookup == null )
        {
            this.defaultResourcesReverseLookup = new HashMap<String,String>();
        }
        
        String key = this.defaultResourcesReverseLookup.get( text );
        
        if( key == null )
        {
            final Map<String,String> defaultResources = getLocalizedResources( ROOT_LOCALE );
            
            if( defaultResources != null )
            {
                for( Map.Entry<String,String> entry : defaultResources.entrySet() )
                {
                    if( text.equals( entry.getValue() ) )
                    {
                        key = entry.getKey();
                        this.defaultResourcesReverseLookup.put( text, key );
                    }
                }
            }
        }
        
        if( key != null )
        {
            final Map<String,String> resourcesForLocale = getLocalizedResources( locale );
            
            if( resourcesForLocale != null )
            {
                final String localizedText = resourcesForLocale.get( key );
                
                if( localizedText != null )
                {
                    return localizedText;
                }
            }
        }
        
        return text;
    }
    
    protected Map<String,String> loadLocalizedResources( final Locale locale )
    {
        final Resource root = root();
        
        if( this != root )
        {
            return root.loadLocalizedResources( locale );
        }
        
        return null;
    }
    
    private Map<String,String> getLocalizedResources( final Locale locale )
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

    public final void setCorruptedResourceExceptionInterceptor( final CorruptedResourceExceptionInterceptor interceptor )
    {
        this.corruptedResourceExceptionInterceptor = interceptor;
    }
    
    protected final boolean validateCorruptedResourceRecovery()
    {
        if( this.corruptedResourceExceptionInterceptor != null )
        {
            return this.corruptedResourceExceptionInterceptor.shouldAttemptRepair();
        }
        
        return false;
    }

}
