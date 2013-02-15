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
 * Tests for the greater than or equal operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class GreaterThanOrEqualOperatorTests extends OperatorTests
{
    private GreaterThanOrEqualOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "GreaterThanOrEqualOperatorTests" );
        
        for( int i = 1; i <= 15; i++ )
        {
            suite.addTest( new GreaterThanOrEqualOperatorTests( "testGreaterThanOrEqualOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testGreaterThanOrEqualOperator1()
    {
        test( "${ 3 >= 5 }", false );
    }
    
    public void testGreaterThanOrEqualOperator2()
    {
        test( "${ 5 >= 3 }", true );
    }
    
    public void testGreaterThanOrEqualOperator3()
    {
        test( "${ 3 >= 3 }", true );
    }
    
    public void testGreaterThanOrEqualOperator4()
    {
        test( "${ 3.2 >= 5 }", false );
    }
    
    public void testGreaterThanOrEqualOperator5()
    {
        test( "${ 5.3 >= 3 }", true );
    }
    
    public void testGreaterThanOrEqualOperator6()
    {
        test( "${ 3.2 >= 3.2 }", true );
    }

    public void testGreaterThanOrEqualOperator7()
    {
        test( "${ 3 ge 5 }", false );
    }
    
    public void testGreaterThanOrEqualOperator8()
    {
        test( "${ 5 ge 3 }", true );
    }
    
    public void testGreaterThanOrEqualOperator9()
    {
        test( "${ 3 ge 3 }", true );
    }
    
    public void testGreaterThanOrEqualOperator10()
    {
        test( "${ 3.2 ge 5 }", false );
    }
    
    public void testGreaterThanOrEqualOperator11()
    {
        test( "${ 5.3 ge 3 }", true );
    }
    
    public void testGreaterThanOrEqualOperator12()
    {
        test( "${ 3.2 ge 3.2 }", true );
    }

    public void testGreaterThanOrEqualOperator13()
    {
        test( "${ Integer3 >= 7 }", false );
    }

    public void testGreaterThanOrEqualOperator14()
    {
        test( "${ 7 >= Integer5 }", true );
    }
    
    public void testGreaterThanOrEqualOperator15()
    {
        test( "${ Integer3 >= Integer5 }", false );
    }

}

