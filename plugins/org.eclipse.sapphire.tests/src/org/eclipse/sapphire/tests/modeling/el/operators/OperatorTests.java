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

package org.eclipse.sapphire.tests.modeling.el.operators;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests for EL operators.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class OperatorTests extends TestExpr
{
    protected OperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "OperatorTests" );
        
        suite.addTest( AdditionOperatorTests.suite() );
        suite.addTest( SubtractionOperatorTests.suite() );
        suite.addTest( MultiplicationOperatorTests.suite() );
        suite.addTest( DivisionOperatorTests.suite() );
        suite.addTest( ModuloOperatorTests.suite() );
        suite.addTest( ArithmeticNegationOperatorTests.suite() );
        suite.addTest( EqualityOperatorTests.suite() );
        suite.addTest( InequalityOperatorTests.suite() );
        suite.addTest( LessThanOperatorTests.suite() );
        suite.addTest( LessThanOrEqualOperatorTests.suite() );
        suite.addTest( GreaterThanOperatorTests.suite() );
        suite.addTest( GreaterThanOrEqualOperatorTests.suite() );
        suite.addTest( LogicalConjunctionOperatorTests.suite() );
        suite.addTest( LogicalDisjunctionOperatorTests.suite() );
        suite.addTest( LogicalNegationOperatorTests.suite() );
        suite.addTest( EmptyOperatorTests.suite() );
        suite.addTest( ConditionalOperatorTests.suite() );
        suite.addTest( MembershipOperatorTests.suite() );
        
        return suite;
    }
    
    protected void test( final String expr,
                         final Object expected )
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            testForExpectedValue( new ModelElementFunctionContext( element ), expr, expected );
        }
        finally
        {
            element.dispose();
        }
    }

}

