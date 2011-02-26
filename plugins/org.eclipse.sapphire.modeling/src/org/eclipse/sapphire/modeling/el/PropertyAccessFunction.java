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

import java.util.Map;

import org.eclipse.osgi.util.NLS;
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
    public static PropertyAccessFunction create( final Function element,
                                                 final Function property )
    {
        final PropertyAccessFunction literal = new PropertyAccessFunction();
        literal.init( element, property );
        return literal;
    }
    
    @Override
    public FunctionResult evaluate( final FunctionContext context )
    {
        return new FunctionResult( this, context )
        {
            private ModelPropertyListener listener;
            private IModelElement element;
            private ModelProperty property;
            
            @Override
            protected void init()
            {
                super.init();
                
                this.listener = new ModelPropertyListener()
                {
                    @Override
                    public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
                    {
                        refresh();
                    }
                };
            }

            @Override
            protected Object evaluate()
            {
                final Object object = operand( 0 ).value();
                final String pname = cast( operand( 1 ).value(), String.class );
                
                if( object == null )
                {
                    throw new FunctionException( Resources.cannotReadPropertiesFromNull );
                }
                else
                {
                    if( object instanceof IModelElement )
                    {
                        final IModelElement el = (IModelElement) object;
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
                    else if( object instanceof Map )
                    {
                        for( final Map.Entry<?,?> entry : ( (Map<?,?>) object ).entrySet() )
                        {
                            final String key = (String) entry.getKey();
                            
                            if( key.equalsIgnoreCase( pname ) )
                            {
                                return entry.getValue();
                            }
                        }
                        
                        throw new FunctionException( NLS.bind( Resources.undefinedPropertyMessage, pname ) );
                    }
                    else
                    {
                        throw new FunctionException( NLS.bind( Resources.cannotReadProperties, object.getClass().getName() ) );
                    }
                }
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
        };
    }
    
    private static final class Resources extends NLS
    {
        public static String undefinedPropertyMessage;
        public static String cannotReadProperties;
        public static String cannotReadPropertiesFromNull;
        
        static
        {
            initializeMessages( PropertyAccessFunction.class.getName(), Resources.class );
        }
    }
    
}
