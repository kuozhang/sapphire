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

package org.eclipse.sapphire.tests.modeling.el;

import junit.framework.TestCase;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class TestExpr

    extends TestCase
    
{
    protected TestExpr( final String name )
    {
        super( name );
    }
    
    protected static void testForExpectedValue( final FunctionContext context,
                                                final String expr,
                                                final Object expected )
    {
        final FunctionResult result = ExpressionLanguageParser.parse( expr ).evaluate( context );
        
        try
        {
            assertEquals( expected, result.value() );
        }
        finally
        {
            result.dispose();
        }
    }
    
    protected static void testForExpectedValue( final IModelElement element,
                                                final String expr,
                                                final Object expected )
    {
        final ModelElementFunctionContext context = new ModelElementFunctionContext( element );
        
        try
        {
            testForExpectedValue( context, expr, expected );
        }
        finally
        {
            context.dispose();
        }
    }
    
    protected static void testForExpectedError( final FunctionContext context,
                                                final String expr,
                                                final String expected )
    {
        final FunctionResult result = ExpressionLanguageParser.parse( expr ).evaluate( context );
        
        try
        {
            final Status status = result.status();
            assertEquals( Status.Severity.ERROR, status.severity() );
            assertEquals( expected, status.message() );
        }
        finally
        {
            result.dispose();
        }
    }
    
}

