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
 * Logical NOT function. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class NotFunction

    extends UnaryFunction<Boolean>

{
    public NotFunction( final Function<?> operand )
    {
        super( operand );
    }
    
    @Override
    protected Boolean evaluate( final Object a )
    {
        final Boolean x = cast( a, Boolean.class );
        return ( x == null ? null : ! x );
    }

}
