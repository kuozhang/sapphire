/******************************************************************************
 * Copyright (c) 2013 Oracle
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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyEnablementEvent;
import org.eclipse.sapphire.modeling.util.NLS;

/**
 * Determines if a property is enabled. Can be used either with two arguments (element and property name) or with
 * a single property name argument. In the single argument form, the context element is used.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EnabledFunction extends Function
{
    public static EnabledFunction create( final List<Function> operands )
    {
        final EnabledFunction function = new EnabledFunction();
        function.init( operands );
        return function;
    }

    public static EnabledFunction create( final Function element,
                                          final Function property )
    {
        final EnabledFunction function = new EnabledFunction();
        function.init( element, property );
        return function;
    }

    public static EnabledFunction create( final Function element,
                                          final String property )
    {
        return create( element, Literal.create( property ) );
    }

    public static EnabledFunction create( final Function property )
    {
        final EnabledFunction function = new EnabledFunction();
        function.init( property );
        return function;
    }

    public static EnabledFunction create( final String property )
    {
        return create( Literal.create( property ) );
    }

    @Override
    public String name()
    {
        return "Enabled";
    }

    @Override
    public FunctionResult evaluate( final FunctionContext context )
    {
        return new FunctionResult( this, context )
        {
            private Property lastProperty;
            private Listener listener;
            
            @Override
            protected Object evaluate()
            {
                Property property = null;
                
                try
                {
                    Element element = null;
                    String propertyName = null;
                    
                    final int count = operands().size();
                    
                    if( count == 1 )
                    {
                        if( context instanceof ModelElementFunctionContext )
                        {
                            element = ( (ModelElementFunctionContext) context ).element();
                        }
                        else
                        {
                            throw new FunctionException( Resources.contextElementNotFound );
                        }
                        
                        propertyName = cast( operand( 0 ), String.class );
                    }
                    else if( count == 2 )
                    {
                        element = cast( operand( 0 ), Element.class );
                        propertyName = cast( operand( 1 ), String.class );
                    }
                    else
                    {
                        final String msg = NLS.bind( Resources.wrongNumberOfArguments, count );
                        throw new FunctionException( msg );
                    }
                    
                    if( element != null && propertyName.length() > 0 )
                    {
                        property = element.property( propertyName );
                        
                        if( property != null )
                        {
                            return property.enabled();
                        }
                    }
                    
                    return null;
                }
                finally
                {
                    if( this.lastProperty != property )
                    {
                        if( this.lastProperty != null )
                        {
                            this.lastProperty.detach( this.listener );
                        }
                        
                        if( property != null )
                        {
                            if( this.listener == null )
                            {
                                this.listener = new FilteredListener<PropertyEnablementEvent>()
                                {
                                    @Override
                                    protected void handleTypedEvent( final PropertyEnablementEvent event )
                                    {
                                        refresh();
                                    }
                                };
                            }
                            
                            property.attach( this.listener );
                        }
                        
                        this.lastProperty = property;
                    }
                }
            }
        };
    }
    
    private static final class Resources extends NLS
    {
        public static String wrongNumberOfArguments; 
        public static String contextElementNotFound;
        
        static
        {
            initializeMessages( EnabledFunction.class.getName(), Resources.class );
        }
    }
    
}
