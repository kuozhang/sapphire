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
 * Tests for the logical disjunction operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LogicalDisjunctionOperatorTests extends OperatorTests
{
    private LogicalDisjunctionOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "LogicalDisjunctionOperatorTests" );
        
        for( int i = 1; i <= 11; i++ )
        {
            suite.addTest( new LogicalDisjunctionOperatorTests( "testLogicalDisjunctionOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testLogicalDisjunctionOperator1()
    {
        test( "${ true || true }", true );
    }
    
    public void testLogicalDisjunctionOperator2()
    {
        test( "${ true || false }", true );
    }
    
    public void testLogicalDisjunctionOperator3()
    {
        test( "${ false || true }", true );
    }
    
    public void testLogicalDisjunctionOperator4()
    {
        test( "${ false || false }", false );
    }
    
    public void testLogicalDisjunctionOperator5()
    {
        test( "${ true or true }", true );
    }
    
    public void testLogicalDisjunctionOperator6()
    {
        test( "${ true or false }", true );
    }
    
    public void testLogicalDisjunctionOperator7()
    {
        test( "${ false or true }", true );
    }
    
    public void testLogicalDisjunctionOperator8()
    {
        test( "${ false or false }", false );
    }
    
    public void testLogicalDisjunctionOperator9()
    {
        test( "${ BooleanTrue || true }", true );
    }

    public void testLogicalDisjunctionOperator10()
    {
        test( "${ true || BooleanFalse }", true );
    }
    
    public void testLogicalDisjunctionOperator11()
    {
        test( "${ BooleanTrue || BooleanFalse }", true );
    }

}

