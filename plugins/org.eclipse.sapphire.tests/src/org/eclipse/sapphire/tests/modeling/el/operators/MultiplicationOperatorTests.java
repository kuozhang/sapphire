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

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for the multiplication operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MultiplicationOperatorTests extends OperatorTests
{
    private MultiplicationOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "MultiplicationOperatorTests" );
        
        for( int i = 1; i <= 7; i++ )
        {
            suite.addTest( new MultiplicationOperatorTests( "testMultiplicationOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testMultiplicationOperator1()
    {
        test( "${ 3 * 5 }", new BigInteger( "15" ) );
    }
    
    public void testMultiplicationOperator2()
    {
        test( "${ 3 * 5 * 7 }", new BigInteger( "105" ) );
    }
    
    public void testMultiplicationOperator3()
    {
        test( "${ 3.2 * 5 }", new BigDecimal( "16.0" ) );
    }

    public void testMultiplicationOperator4()
    {
        test( "${ 3.2 * 6}", new BigDecimal( "19.2" ) );
    }

    public void testMultiplicationOperator5()
    {
        test( "${ Integer3 * 7 }", new BigInteger( "21" ) );
    }

    public void testMultiplicationOperator6()
    {
        test( "${ 7 * Integer5 }", new BigInteger( "35" ) );
    }
    
    public void testMultiplicationOperator7()
    {
        test( "${ Integer3 * Integer5 }", new Long( "15" ) );
    }

}

