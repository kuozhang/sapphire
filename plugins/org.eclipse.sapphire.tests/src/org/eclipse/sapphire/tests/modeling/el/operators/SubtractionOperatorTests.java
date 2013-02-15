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
 * Tests for the subtraction operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SubtractionOperatorTests extends OperatorTests
{
    private SubtractionOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "SubtractionOperatorTests" );
        
        for( int i = 1; i <= 6; i++ )
        {
            suite.addTest( new SubtractionOperatorTests( "testSubtractionOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testSubtractionOperator1()
    {
        test( "${ 3 - 5 }", new BigInteger( "-2" ) );
    }
    
    public void testSubtractionOperator2()
    {
        test( "${ 3 - 5 - 7 }", new BigInteger( "-9" ) );
    }
    
    public void testSubtractionOperator3()
    {
        test( "${ 3.2 - 5 }", new BigDecimal( "-1.8" ) );
    }

    public void testSubtractionOperator4()
    {
        test( "${ Integer3 - 7 }", new BigInteger( "-4" ) );
    }

    public void testSubtractionOperator5()
    {
        test( "${ 7 - Integer5 }", new BigInteger( "2" ) );
    }
    
    public void testSubtractionOperator6()
    {
        test( "${ Integer3 - Integer5 }", new Long( "-2" ) );
    }

}

