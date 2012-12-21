/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.modeling.el.t0018;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests FailSafeFunction function with an enum value property and string expected type.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestExpr0018 extends TestExpr
{
    private TestExpr0018( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestExpr0018" );

        suite.addTest( new TestExpr0018( "test" ) );
        
        return suite;
    }
    
    public void test()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final FunctionContext context = new ModelElementFunctionContext( element );
        
        element.setType( "int" );
        element.setTypeWithDefault( "int" );
        
        testForExpectedString( context, "${ Type }", "int" );
        testForExpectedString( context, "${ TypeWithDefault }", "int" );

        testForExpectedString( context, "${ Type == null ? '<type>' : Type }", "int" );
        testForExpectedString( context, "${ TypeWithDefault == null ? '<type>' : TypeWithDefault }", "int" );
        
        element.setType( (String) null );
        element.setTypeWithDefault( (String) null );
        
        testForExpectedString( context, "${ Type == null ? '<type>' : Type }", "<type>" );
        testForExpectedString( context, "${ TypeWithDefault == null ? '<type>' : TypeWithDefault }", "INTEGER" );
    }

}

