/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.el.internal;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionException;
import org.eclipse.sapphire.modeling.el.FunctionResult;

/**
 * Determines the size of a collection, a map or an array.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SizeFunction extends Function
{
    @Override
    public String name()
    {
        return "Size";
    }
    
    @Override
    public final FunctionResult evaluate( final FunctionContext context )
    {
        return new FunctionResult( this, context )
        {
            private Object operand;
            private Listener listener;
            
            @Override
            protected Object evaluate()
            {
                final Object operand = operand( 0 );
                
                if( this.operand != operand )
                {
                    if( this.operand instanceof ElementList && this.listener != null )
                    {
                        ( (ElementList<?>) this.operand ).detach( this.listener );
                    }
                    
                    this.operand = operand;
                    
                    if( this.operand instanceof ElementList )
                    {
                        if( this.listener == null )
                        {
                            this.listener = new FilteredListener<PropertyContentEvent>()
                            {
                                @Override
                                protected void handleTypedEvent( final PropertyContentEvent event )
                                {
                                    refresh();
                                }
                            };
                        }
                        
                        ( (ElementList<?>) this.operand ).attach( this.listener );
                    }
                }
                
                if( this.operand == null )
                {
                    throw new FunctionException( "null" );
                }
                
                if( this.operand instanceof Collection )
                {
                    return ( (Collection<?>) this.operand ).size();
                }
                else if( this.operand instanceof Map<?,?> )
                {
                    return ( (Map<?,?>) this.operand ).size();
                }
                else if( this.operand.getClass().isArray() )
                {
                    return Array.getLength( this.operand );
                }
                else
                {
                    throw new FunctionException( "wrong type" );
                }
            }
            
            @Override
            public void dispose()
            {
                super.dispose();
                
                if( this.operand instanceof ElementList )
                {
                    ( (ElementList<?>) this.operand ).detach( this.listener );
                }
                
                this.operand = null;
                this.listener = null;
            }
        };
    }

}
