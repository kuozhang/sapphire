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

import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for the empty operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ConditionalOperatorTests extends OperatorTests
{
    private ConditionalOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "ConditionalOperatorTests" );
        
        for( int i = 1; i <= 3; i++ )
        {
            suite.addTest( new ConditionalOperatorTests( "testConditionalOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testConditionalOperator1()
    {
        test( "${ true ? 3 : 5 }", new BigInteger( "3" ) );
    }
    
    public void testConditionalOperator2()
    {
        test( "${ false ? 3 : 5 }", new BigInteger( "5" ) );
    }
    
    /**
     * Tests conditional operator in circumstances when evaluating unused branch would result in errors.
     */

    public void testConditionalOperator3()
    {
        test( "${ EmptyList.Size == 1 ? EmptyList[ 0 ] : 'x' }", "x" );
    }
    
}

