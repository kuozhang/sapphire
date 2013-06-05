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

package org.eclipse.sapphire.tests.modeling.el.functions.fragment;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests Fragment function.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FragmentFunctionTests extends TestExpr
{
    private FragmentFunctionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "FragmentFunctionTests" );

        suite.addTest( new FragmentFunctionTests( "testFragmentFunction" ) );
        
        return suite;
    }
    
    public void testFragmentFunction()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        FunctionResult fr = ExpressionLanguageParser.parse( "${ Value.Fragment( 0, 3 ) }" ).evaluate( context );
        
        try
        {
            assertEquals( "", fr.value() );
            
            element.setValue( "ab" );
            assertEquals( "ab", fr.value() );

            element.setValue( "abcdefg" );
            assertEquals( "abc", fr.value() );
        }
        finally
        {
            fr.dispose();
        }

        fr = ExpressionLanguageParser.parse( "${ Value.Fragment( 3, 6 ) }" ).evaluate( context );
        
        try
        {
            element.setValue( null );
            assertEquals( "", fr.value() );
            
            element.setValue( "ab" );
            assertEquals( "", fr.value() );

            element.setValue( "abcdefg" );
            assertEquals( "def", fr.value() );
        }
        finally
        {
            fr.dispose();
        }

        fr = ExpressionLanguageParser.parse( "${ Value.Fragment( 6, 3 ) }" ).evaluate( context );
        
        try
        {
            element.setValue( null );
            assertEquals( "", fr.value() );
            
            element.setValue( "ab" );
            assertEquals( "", fr.value() );

            element.setValue( "abcdefg" );
            assertEquals( "", fr.value() );
        }
        finally
        {
            fr.dispose();
        }

        fr = ExpressionLanguageParser.parse( "${ Value.Fragment( -3, 3 ) }" ).evaluate( context );
        
        try
        {
            element.setValue( null );
            assertEquals( "", fr.value() );
            
            element.setValue( "ab" );
            assertEquals( "ab", fr.value() );

            element.setValue( "abcdefg" );
            assertEquals( "abc", fr.value() );
        }
        finally
        {
            fr.dispose();
        }
    }

}
