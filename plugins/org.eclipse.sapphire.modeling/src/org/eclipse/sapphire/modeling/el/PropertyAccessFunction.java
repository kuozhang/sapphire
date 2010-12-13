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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;

/**
 * An function that pulls a property from an element. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertyAccessFunction

    extends Function

{
    private final ModelPropertyListener listener;
    private IModelElement element;
    private ModelProperty property;
    
    public PropertyAccessFunction()
    {
        this.listener = new ModelPropertyListener()
        {
            @Override
            public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
            {
                refresh();
            }
        };
    }
    
    public static PropertyAccessFunction create( final FunctionContext context,
                                                 final Function element,
                                                 final Function property )
    {
        final PropertyAccessFunction literal = new PropertyAccessFunction();
        literal.init( context, element, property );
        return literal;
    }
    
    @Override
    protected Object evaluate()
    {
        final IModelElement el = cast( operand( 0 ).value(), IModelElement.class );
        final String pname = cast( operand( 1 ).value(), String.class );
        final ModelProperty p = el.getModelElementType().getProperty( pname );
        
        if( this.element != el || this.property != p )
        {
            if( this.element != null )
            {
                this.element.removeListener( this.listener, this.property.getName() );
            }
            
            this.element = el;
            this.property = p;
            
            this.element.addListener( this.listener, this.property.getName() );
        }
        
        Object res = this.element.read( this.property );
        
        if( res instanceof ModelElementHandle<?> )
        {
            res = ( (ModelElementHandle<?>) res ).element();
        }
        
        return res;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.element != null )
        {
            this.element.removeListener( this.listener, this.property.getName() );
        }
    }
    
}
