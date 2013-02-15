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
 * Tests for the empty operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EmptyOperatorTests extends OperatorTests
{
    private EmptyOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "EmptyOperatorTests" );
        
        for( int i = 1; i <= 4; i++ )
        {
            suite.addTest( new EmptyOperatorTests( "testEmptyOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testEmptyOperator1()
    {
        test( "${ empty null }", true );
    }

    public void testEmptyOperator2()
    {
        test( "${ empty 5 }", false );
    }
    
    public void testEmptyOperator3()
    {
        test( "${ empty 'abc' }", false );
    }
    
    public void testEmptyOperator4()
    {
        test( "${ empty '' }", true );
    }

}

