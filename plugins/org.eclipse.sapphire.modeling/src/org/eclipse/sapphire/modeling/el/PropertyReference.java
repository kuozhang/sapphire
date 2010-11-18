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

package org.eclipse.sapphire.modeling.el;

/**
 * An function that pulls a property from the context. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertyReference

    extends Function

{
    private String property;
    private FunctionContext.Listener listener;
    
    public PropertyReference( final String property )
    {
        this.property = property;
    }
    
    public static PropertyReference create( final FunctionContext context,
                                            final String property )
    {
        final PropertyReference literal = new PropertyReference( property );
        literal.init( context );
        return literal;
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        this.listener = new FunctionContext.Listener()
        {
            @Override
            public void handlePropertyChanged( final String property )
            {
                if( PropertyReference.this.property.equals( property ) )
                {
                    refresh();
                }
            }
        };
        
        context().addListener( this.listener );
    }

    @Override
    protected Object evaluate()
    {
        return context().property( this.property );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        context().removeListener( this.listener );
    }
    
}
