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
 * Tests for the arithmetic negation operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ArithmeticNegationOperatorTests extends OperatorTests
{
    private ArithmeticNegationOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "ArithmeticNegationOperatorTests" );
        
        for( int i = 1; i <= 3; i++ )
        {
            suite.addTest( new ArithmeticNegationOperatorTests( "testArithmeticNegationOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testArithmeticNegationOperator1()
    {
        test( "${ -5 }", new BigInteger( "-5" ) );
    }

    public void testArithmeticNegationOperator2()
    {
        test( "${ -5.23 }", new BigDecimal( "-5.23" ) );
    }

    public void testArithmeticNegationOperator3()
    {
        test( "${ -Integer3 }", new Integer( "-3" ) );
    }

}

