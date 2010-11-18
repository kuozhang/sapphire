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
 * A function with two operands. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class BinaryFunction<T>

    extends Function<T>

{
    private final Function<?> operand1;
    private final Function<?> operand2;
    
    public BinaryFunction( final Function<?> operand1,
                           final Function<?> operand2 )
    {
        this.operand1 = operand1;
        this.operand2 = operand2;
        
        final Listener listener = new Listener()
        {
            @Override
            public void handleValueChanged()
            {
                refresh();
            }
        };
        
        this.operand1.addListener( listener );
        this.operand2.addListener( listener );
    }
    
    @Override
    protected final T evaluate()
    {
        return evaluate( operand1().value(), operand2().value() );
    }
    
    protected abstract T evaluate( Object a, Object b );

    public Function<?> operand1()
    {
        return this.operand1;
    }

    public Function<?> operand2()
    {
        return this.operand2;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        this.operand1.dispose();
        this.operand2.dispose();
    }

}
