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
 * A function with one operand. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class UnaryFunction<T>

    extends Function<T>

{
    private final Function<?> operand;
    
    public UnaryFunction( final Function<?> operand )
    {
        this.operand = operand;
        
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
        return evaluate( operand().value() );
    }

    protected abstract T evaluate( Object operand );

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
