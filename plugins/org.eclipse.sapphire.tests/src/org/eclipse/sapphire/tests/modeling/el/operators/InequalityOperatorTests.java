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
 * Tests for the inequality operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class InequalityOperatorTests extends OperatorTests
{
    private InequalityOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "InequalityOperatorTests" );
        
        for( int i = 1; i <= 15; i++ )
        {
            suite.addTest( new InequalityOperatorTests( "testInequalityOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testInequalityOperator1()
    {
        test( "${ 3 != 3 }", false );
    }

    public void testInequalityOperator2()
    {
        test( "${ 3 != 5 }", true );
    }

    public void testInequalityOperator3()
    {
        test( "${ 3.2 != 3.2 }", false );
    }

    public void testInequalityOperator4()
    {
        test( "${ 3.2 != 5 }", true );
    }
    
    public void testInequalityOperator5()
    {
        test( "${ 'abc' != 'abc' }", false );
    }

    public void testInequalityOperator6()
    {
        test( "${ 'abc' != 'xyz' }", true );
    }
    
    public void testInequalityOperator7()
    {
        test( "${ 3 ne 3 }", false );
    }

    public void testInequalityOperator8()
    {
        test( "${ 3 ne 5 }", true );
    }

    public void testInequalityOperator9()
    {
        test( "${ 3.2 ne 3.2 }", false );
    }

    public void testInequalityOperator10()
    {
        test( "${ 3.2 ne 5 }", true );
    }
    
    public void testInequalityOperator11()
    {
        test( "${ 'abc' ne 'abc' }", false );
    }

    public void testInequalityOperator12()
    {
        test( "${ 'abc' ne 'xyz' }", true );
    }

    public void testInequalityOperator13()
    {
        test( "${ Integer3 != 7 }", true );
    }

    public void testInequalityOperator14()
    {
        test( "${ 7 != Integer5 }", true );
    }
    
    public void testInequalityOperator15()
    {
        test( "${ Integer3 != Integer5 }", true );
    }

}

