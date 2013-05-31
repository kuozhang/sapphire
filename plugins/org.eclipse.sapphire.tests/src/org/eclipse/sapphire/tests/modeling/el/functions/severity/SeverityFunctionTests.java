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

package org.eclipse.sapphire.tests.modeling.el.functions.severity;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests Severity function.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SeverityFunctionTests extends TestExpr
{
    private SeverityFunctionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "SeverityFunctionTests" );

        suite.addTest( new SeverityFunctionTests( "testSeverityFunction" ) );
        
        return suite;
    }
    
    public void testSeverityFunction()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        final FunctionResult fr = ExpressionLanguageParser.parse( "${ IntegerValue.Validation.Severity }" ).evaluate( context );
        
        try
        {
            assertEquals( Status.Severity.OK, fr.value() );
            
            element.setIntegerValue( 3 );
            assertEquals( Status.Severity.OK, fr.value() );
            
            element.setIntegerValue( "abc" );
            assertEquals( Status.Severity.ERROR, fr.value() );
            
            element.setIntegerValue( 4 );
            assertEquals( Status.Severity.OK, fr.value() );
        }
        finally
        {
            fr.dispose();
        }
    }

}

