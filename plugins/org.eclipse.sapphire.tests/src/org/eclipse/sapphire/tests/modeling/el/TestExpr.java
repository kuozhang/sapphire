package org.eclipse.sapphire.tests.modeling.el;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.IModelElement;
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
            final IStatus status = result.status();
            assertEquals( IStatus.ERROR, status.getSeverity() );
            assertEquals( expected, status.getMessage() );
        }
        finally
        {
            result.dispose();
        }
    }
    
}

