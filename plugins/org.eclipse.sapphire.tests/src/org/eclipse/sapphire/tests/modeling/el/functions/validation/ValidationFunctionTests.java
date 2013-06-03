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

package org.eclipse.sapphire.tests.modeling.el.functions.validation;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests Validation function.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ValidationFunctionTests extends TestExpr
{
    private ValidationFunctionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "ValidationFunctionTests" );

        suite.addTest( new ValidationFunctionTests( "testValidationFunction" ) );
        suite.addTest( new ValidationFunctionTests( "testValidationFunctionNull" ) );
        suite.addTest( new ValidationFunctionTests( "testValidationFunctionWrongType" ) );
        
        return suite;
    }
    
    public void testValidationFunction()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        final FunctionResult fr = ExpressionLanguageParser.parse( "${ IntegerValue.Validation }" ).evaluate( context );
        
        try
        {
            assertValidationOk( (Status) fr.value() );
            
            element.setIntegerValue( 3 );
            assertValidationOk( (Status) fr.value() );
            
            element.setIntegerValue( "abc" );
            assertValidationError( (Status) fr.value(), "\"abc\" is not a valid integer." );
            
            element.setIntegerValue( 4 );
            assertValidationOk( (Status) fr.value() );
        }
        finally
        {
            fr.dispose();
        }
    }

    public void testValidationFunctionNull()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        final FunctionResult fr = ExpressionLanguageParser.parse( "${ Validation( null ) }" ).evaluate( context );
        
        try
        {
            final Status st = fr.status();
            
            assertEquals( Status.Severity.ERROR, st.severity() );
            assertEquals( "Function Validation does not accept nulls in position 0.", st.message() );
        }
        finally
        {
            fr.dispose();
        }
    }

    public void testValidationFunctionWrongType()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        final FunctionResult fr = ExpressionLanguageParser.parse( "${ Validation( 'abc' ) }" ).evaluate( context );
        
        try
        {
            final Status st = fr.status();
            
            assertEquals( Status.Severity.ERROR, st.severity() );
            assertEquals( "Function Validation expects org.eclipse.sapphire.Property in position 0, but java.lang.String was found. A conversion was not possible.", st.message() );
        }
        finally
        {
            fr.dispose();
        }
    }

}
