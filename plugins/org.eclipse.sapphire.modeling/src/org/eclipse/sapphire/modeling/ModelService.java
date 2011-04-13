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

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class ModelService
{
    private IModelElement element;
    
    protected final void init( final IModelElement element )
    {
        this.element = element;
    }
    
    public final IModelElement element()
    {
        return this.element;
    }
    
    public final <T> T nearest( final Class<T> particleType )
    {
        return this.element.nearest( particleType );
    }

    public final <A> A adapt( final Class<A> adapterType )
    {
        return this.element.adapt( adapterType );
    }
    
}
