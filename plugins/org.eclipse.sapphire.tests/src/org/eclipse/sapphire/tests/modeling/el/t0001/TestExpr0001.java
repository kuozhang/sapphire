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

package org.eclipse.sapphire.tests.modeling.el.t0001;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestExpr0001

    extends TestCase
    
{
    private TestExpr0001( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestExpr0001" );

        suite.addTest( new TestExpr0001( "testParentFunction" ) );
        suite.addTest( new TestExpr0001( "testRootFunction" ) );
        
        return suite;
    }
    
    public void testParentFunction()
    {
        final ITestExpr0001ModelElement r = ITestExpr0001ModelElement.TYPE.instantiate();
        final ITestExpr0001ModelElement r_e = r.getElement().element( true );
        final ITestExpr0001ModelElement r_l = r.getList().addNewElement();
        final ITestExpr0001ModelElement r_e_l = r_e.getList().addNewElement();
        final ITestExpr0001ModelElement r_l_e = r_l.getElement().element( true );

        test( "${ Parent() }", r, r_e );
        test( "${ Parent() }", r, r_l );
        
        test( "${ Parent() }", r_e, r_e_l );
        test( "${ Parent() }", r_l, r_l_e );

        test( "${ Parent().Parent() }", r, r_e_l );
        test( "${ Parent().Parent() }", r, r_l_e );
    }

    public void testRootFunction()
    {
        final ITestExpr0001ModelElement r = ITestExpr0001ModelElement.TYPE.instantiate( new RootXmlResource() );
        final ITestExpr0001ModelElement r_e = r.getElement().element( true );
        final ITestExpr0001ModelElement r_l = r.getList().addNewElement();
        final ITestExpr0001ModelElement r_e_l = r_e.getList().addNewElement();
        final ITestExpr0001ModelElement r_l_e = r_l.getElement().element( true );

        test( "${ Root() }", r, r_e );
        test( "${ Root() }", r, r_l );
        
        test( "${ Root() }", r, r_e_l );
        test( "${ Root() }", r, r_l_e );

        test( "${ Parent().Root() }", r, r_e_l );
        test( "${ Parent().Root() }", r, r_l_e );

        test( "${ Root().Root() }", r, r_e_l );
        test( "${ Root().Root() }", r, r_l_e );
    }
    
    private void test( final String expr,
                       final Object expected,
                       final IModelElement element )
    {
        final ModelElementFunctionContext context = new ModelElementFunctionContext( element );
        final FunctionResult result = ExpressionLanguageParser.parse( expr ).evaluate( context );
        
        try
        {
            assertSame( expected, result.value() );
        }
        finally
        {
            result.dispose();
            context.dispose();
        }
    }
    
}

