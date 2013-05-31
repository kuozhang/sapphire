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

package org.eclipse.sapphire.tests.modeling.el.functions.text;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests Text function.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TextFunctionTests extends TestExpr
{
    private TextFunctionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TextFunctionTests" );

        suite.addTest( new TextFunctionTests( "testTextFunction" ) );
        
        return suite;
    }
    
    public void testTextFunction()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        FunctionResult fr = ExpressionLanguageParser.parse( "${ IntegerValue.Text }" ).evaluate( context );
        
        try
        {
            assertNull( fr.value() );
            
            element.setIntegerValue( 3 );
            assertEquals( "3", fr.value() );

            element.setIntegerValue( "abc" );
            assertEquals( "abc", fr.value() );
        }
        finally
        {
            fr.dispose();
        }

        fr = ExpressionLanguageParser.parse( "${ IntegerValueWithDefault.Text }" ).evaluate( context );
        
        try
        {
            assertEquals( "1", fr.value() );
            
            element.setIntegerValueWithDefault( 3 );
            assertEquals( "3", fr.value() );

            element.setIntegerValueWithDefault( "abc" );
            assertEquals( "abc", fr.value() );
        }
        finally
        {
            fr.dispose();
        }
    }

}

