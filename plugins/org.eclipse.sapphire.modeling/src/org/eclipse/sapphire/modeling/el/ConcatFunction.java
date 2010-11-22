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
 * String concatenation function. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ConcatFunction

    extends Function

{
    public static ConcatFunction create( final FunctionContext context,
                                         final Function a,
                                         final Function b )
    {
        final ConcatFunction function = new ConcatFunction();
        function.init( context, a, b );
        return function;
    }

    public static ConcatFunction create( final FunctionContext context,
                                         final String a,
                                         final String b )
    {
        return create( context, Literal.create( context, a ), Literal.create( context, b ) );
    }

    @Override
    protected String evaluate()
    {
        final String a = cast( operand( 0 ).value(), String.class );
        final String b = cast( operand( 1 ).value(), String.class );
        return ( a + b );
    }

}
