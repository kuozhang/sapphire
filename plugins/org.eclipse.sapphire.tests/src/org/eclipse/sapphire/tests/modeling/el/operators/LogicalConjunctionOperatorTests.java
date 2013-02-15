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

/**
 * Tests for the logical conjunction operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LogicalConjunctionOperatorTests extends OperatorTests
{
    private LogicalConjunctionOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "LogicalConjunctionOperatorTests" );
        
        for( int i = 1; i <= 11; i++ )
        {
            suite.addTest( new LogicalConjunctionOperatorTests( "testLogicalConjunctionOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testLogicalConjunctionOperator1()
    {
        test( "${ true && true }", true );
    }
    
    public void testLogicalConjunctionOperator2()
    {
        test( "${ true && false }", false );
    }
    
    public void testLogicalConjunctionOperator3()
    {
        test( "${ false && true }", false );
    }
    
    public void testLogicalConjunctionOperator4()
    {
        test( "${ false && false }", false );
    }
    
    public void testLogicalConjunctionOperator5()
    {
        test( "${ true and true }", true );
    }
    
    public void testLogicalConjunctionOperator6()
    {
        test( "${ true and false }", false );
    }
    
    public void testLogicalConjunctionOperator7()
    {
        test( "${ false and true }", false );
    }
    
    public void testLogicalConjunctionOperator8()
    {
        test( "${ false and false }", false );
    }

    public void testLogicalConjunctionOperator9()
    {
        test( "${ BooleanTrue && true }", true );
    }

    public void testLogicalConjunctionOperator10()
    {
        test( "${ true && BooleanFalse }", false );
    }
    
    public void testLogicalConjunctionOperator11()
    {
        test( "${ BooleanTrue && BooleanFalse }", false );
    }

}

