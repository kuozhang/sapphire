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
 * Tests for the less than operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LessThanOperatorTests extends OperatorTests
{
    private LessThanOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "LessThanOperatorTests" );
        
        for( int i = 1; i <= 15; i++ )
        {
            suite.addTest( new LessThanOperatorTests( "testLessThanOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testLessThanOperator1()
    {
        test( "${ 3 < 5 }", true );
    }
    
    public void testLessThanOperator2()
    {
        test( "${ 5 < 3 }", false );
    }
    
    public void testLessThanOperator3()
    {
        test( "${ 3 < 3 }", false );
    }
    
    public void testLessThanOperator4()
    {
        test( "${ 3.2 < 5 }", true );
    }
    
    public void testLessThanOperator5()
    {
        test( "${ 5.3 < 3 }", false );
    }
    
    public void testLessThanOperator6()
    {
        test( "${ 3.2 < 3.2 }", false );
    }

    public void testLessThanOperator7()
    {
        test( "${ 3 lt 5 }", true );
    }
    
    public void testLessThanOperator8()
    {
        test( "${ 5 lt 3 }", false );
    }
    
    public void testLessThanOperator9()
    {
        test( "${ 3 lt 3 }", false );
    }
    
    public void testLessThanOperator10()
    {
        test( "${ 3.2 lt 5 }", true );
    }
    
    public void testLessThanOperator11()
    {
        test( "${ 5.3 lt 3 }", false );
    }
    
    public void testLessThanOperator12()
    {
        test( "${ 3.2 lt 3.2 }", false );
    }

    public void testLessThanOperator13()
    {
        test( "${ Integer3 < 7 }", true );
    }

    public void testLessThanOperator14()
    {
        test( "${ 7 < Integer5 }", false );
    }
    
    public void testLessThanOperator15()
    {
        test( "${ Integer3 < Integer5 }", true );
    }

}

