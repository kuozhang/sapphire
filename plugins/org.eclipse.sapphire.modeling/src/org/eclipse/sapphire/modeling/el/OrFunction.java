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
 * Logical OR function. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class OrFunction

    extends BinaryFunction<Boolean>

{
    public OrFunction( final Function<?> operand1,
                       final Function<?> operand2 )
    {
        super( operand1, operand2 );
    }
    
    @Override
    protected Boolean evaluate( final Object a,
                                final Object b )
    {
        final Boolean x = cast( a, Boolean.class );
        final Boolean y = cast( b, Boolean.class );
        return ( x == null || y == null ? null : ( x || y ) );
    }

}
