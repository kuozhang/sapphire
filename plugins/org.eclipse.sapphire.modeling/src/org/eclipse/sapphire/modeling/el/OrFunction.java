/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.el;

import java.util.List;

/**
 * Logical OR function. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class OrFunction extends Function
{
    public static Function create( final Function... operands )
    {
        if( operands.length == 0 )
        {
            return null;
        }
        else if( operands.length == 1 )
        {
            return operands[ 0 ];
        }
        else
        {
            final OrFunction function = new OrFunction();
            function.init( operands );
            return function;
        }
    }
    
    public static Function create( final List<Function> operands )
    {
        if( operands.isEmpty() )
        {
            return null;
        }
        else if( operands.size() == 1 )
        {
            return operands.get( 0 );
        }
        else
        {
            final OrFunction function = new OrFunction();
            function.init( operands );
            return function;
        }
    }

    @Override
    public String name()
    {
        return "||";
    }

    @Override
    public boolean operator()
    {
        return true;
    }

    @Override
    public int precedence()
    {
        return 6;
    }

    @Override
    public FunctionResult evaluate( final FunctionContext context )
    {
        return new FunctionResult( this, context )
        {
            @Override
            protected Object evaluate()
            {
                boolean result = false;
                
                for( FunctionResult operand : operands() )
                {
                    result = result || cast( operand.value(), Boolean.class );
                }
                
                return result;
            }
        };
    }
    
}
