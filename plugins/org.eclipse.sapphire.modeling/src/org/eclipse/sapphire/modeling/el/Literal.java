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

package org.eclipse.sapphire.modeling.el;

/**
 * An function that always evaluates to the same value. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class Literal

    extends Function

{
    private Object value;
    
    public Literal( final Object value )
    {
        this.value = value;
    }
    
    public static Literal create( final FunctionContext context,
                                  final Object obj )
    {
        final Literal literal = new Literal( obj );
        literal.init( context );
        return literal;
    }

    @Override
    protected Object evaluate()
    {
        return this.value;
    }
}
