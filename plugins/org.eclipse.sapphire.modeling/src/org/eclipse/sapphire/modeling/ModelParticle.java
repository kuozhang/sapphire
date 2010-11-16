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

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class ModelParticle

    implements IModelParticle
    
{
    private final IModelParticle parent;
    private final Resource resource;
    
    public ModelParticle( final IModelParticle parent,
                          final Resource resource )
    {
        this.parent = parent;
        this.resource = resource;
    }
    
    public final Resource resource()
    {
        return this.resource;
    }
    
    public final IModelParticle root()
    {
        if( this.parent == null )
        {
            return this;
        }
        
        return this.parent.root();
    }
    
    public IModelParticle parent()
    {
        return this.parent;
    }
    
    @SuppressWarnings( "unchecked" )
    public final <T> T nearest( final Class<T> particleType )
    {
        if( particleType.isAssignableFrom( getClass() ) )
        {
            return (T) this;
        }
        else
        {
            if( this.parent != null )
            {
                return this.parent.nearest( particleType );
            }
            else
            {
                return null;
            }
        }
    }
    
    public <A> A adapt( final Class<A> adapterType )
    {
        A adapter = null;
        
        if( this.resource != null )
        {
            adapter = this.resource.adapt( adapterType );
        }

        if( adapter == null && this.parent != null )
        {
            adapter = this.parent.adapt( adapterType );
        }
        
        return adapter;
    }

}
