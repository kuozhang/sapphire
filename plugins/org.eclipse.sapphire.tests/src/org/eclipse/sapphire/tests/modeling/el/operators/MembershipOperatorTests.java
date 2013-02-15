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
 * Tests for the membership operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MembershipOperatorTests extends OperatorTests
{
    private MembershipOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "MembershipOperatorTests" );
        
        for( int i = 1; i <= 9; i++ )
        {
            suite.addTest( new MembershipOperatorTests( "testMembershipOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testMembershipOperator1()
    {
        test( "${ 'x' IN List( 'x', 'y', 'z' ) }", true );
    }

    public void testMembershipOperator2()
    {
        test( "${ 'y' IN List( 'x', 'y', 'z' ) }", true );
    }

    public void testMembershipOperator3()
    {
        test( "${ 'z' IN List( 'x', 'y', 'z' ) }", true );
    }

    public void testMembershipOperator4()
    {
        test( "${ 'a' IN List( 'x', 'y', 'z' ) }", false );
    }

    public void testMembershipOperator5()
    {
        test( "${ 'y' IN 'x,y,z' }", true );
    }

    public void testMembershipOperator6()
    {
        test( "${ 'a' IN 'x,y,z' }", false );
    }

    public void testMembershipOperator7()
    {
        test( "${ null IN null }", false );
    }

    public void testMembershipOperator8()
    {
        test( "${ 'x' IN null }", false );
    }

    public void testMembershipOperator9()
    {
        test( "${ null IN List( 'x' ) }", false );
    }
    
}

