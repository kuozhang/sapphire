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
 * Function that returns one of two alternatives depending on a condition. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ConditionalFunction<T>

    extends Function<T>

{
    private final Function<?> condition;
    private final Function<?> positive;
    private final Function<?> negative;
    
    public ConditionalFunction( final Function<?> condition,
                                final Function<?> positive,
                                final Function<?> negative)
    {
        this.condition = condition;
        this.positive = positive;
        this.negative = negative;
        
        final Listener listener = new Listener()
        {
            @Override
            public void handleValueChanged()
            {
                refresh();
            }
        };
        
        this.condition.addListener( listener );
        this.positive.addListener( listener );
        this.negative.addListener( listener );
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    
    protected final T evaluate()
    {
        final Boolean conditionValue = cast( condition().value(), Boolean.class );
        
        if( conditionValue == true )
        {
            return (T) positive().value();
        }
        else
        {
            return (T) negative().value();
        }
    }
    
    public Function<?> condition()
    {
        return this.condition;
    }

    public Function<?> positive()
    {
        return this.positive;
    }

    public Function<?> negative()
    {
        return this.negative;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        this.condition.dispose();
        this.positive.dispose();
        this.negative.dispose();
    }

}
