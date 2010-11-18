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
 * Function that ensures that the returned value is of specified type and prevents function
 * exceptions from propagating.  
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class FailSafeFunction<T>

    extends Function<T>

{
    private final Function<?> operand;
    private final Class<T> expectedValueType;
    
    public FailSafeFunction( final Function<?> operand,
                             final Class<T> expectedValueType )
    {
        this.operand = operand;
        this.expectedValueType = expectedValueType;
        
        final Listener listener = new Listener()
        {
            @Override
            public void handleValueChanged()
            {
                refresh();
            }
        };
        
        this.operand.addListener( listener );
    }
    
    @Override
    protected final T evaluate()
    {
        try
        {
            return cast( operand().value(), this.expectedValueType );
        }
        catch( FunctionException e )
        {
            return handleFunctionException( e );
        }
    }
    
    protected T handleFunctionException( final FunctionException e )
    {
        return null;
    }
    
    public Function<?> operand()
    {
        return this.operand;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        this.operand.dispose();
    }

}
