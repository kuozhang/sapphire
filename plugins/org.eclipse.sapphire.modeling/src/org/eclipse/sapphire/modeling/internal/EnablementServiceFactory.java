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

package org.eclipse.sapphire.modeling.internal;

import org.eclipse.sapphire.modeling.EnablementService;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.el.AndFunction;
import org.eclipse.sapphire.modeling.el.FailSafeFunction;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EnablementServiceFactory

    extends ModelPropertyServiceFactory
    
{
    @Override
    public boolean applicable( final IModelElement element,
                               final ModelProperty property,
                               final Class<? extends ModelPropertyService> service )
    {
        return true;
    }

    @Override
    public ModelPropertyService create( final IModelElement element,
                                        final ModelProperty property,
                                        final Class<? extends ModelPropertyService> service )
    {
        final FunctionContext context = new ModelElementFunctionContext( element );
        Function function = null;
        
        for( Enablement annotation : property.getAnnotations( Enablement.class ) )
        {
            final String expr = annotation.expr();
            Function f = null;
            
            if( expr != null )
            {
                try
                {
                    f = ExpressionLanguageParser.parse( context, expr );
                }
                catch( Exception e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                    f = null;
                }
            }
            
            if( f != null )
            {
                f = FailSafeFunction.create( context, f, Boolean.class );
            }
            
            if( function == null )
            {
                function = f;
            }
            else
            {
                function = AndFunction.create( context, function, f );
            }
        }
        
        if( function == null )
        {
            function = Literal.create( context, Boolean.TRUE );
        }
        else
        {
            function.addListener
            (
                new Function.Listener()
                {
                    @Override
                    public void handleValueChanged()
                    {
                        element.refresh( property );
                    }
                }
            );
        }
        
        final Function finalFunction = function;

        final EnablementService svc = new EnablementService()
        {
            @Override
            public boolean isEnabled()
            {
                Boolean enabled = null;
                
                if( finalFunction != null )
                {
                    enabled = (Boolean) finalFunction.value();
                }
                
                if( enabled == null )
                {
                    enabled = false;
                }
                
                return enabled;
            }
        };
        
        svc.init( element, property, new String[ 0 ] );
        
        return svc;
    }
    
}
