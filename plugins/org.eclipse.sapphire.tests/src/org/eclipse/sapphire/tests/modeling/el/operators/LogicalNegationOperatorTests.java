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
 * Tests for the logical negation operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LogicalNegationOperatorTests extends OperatorTests
{
    private LogicalNegationOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "LogicalNegationOperatorTests" );
        
        for( int i = 1; i <= 4; i++ )
        {
            suite.addTest( new LogicalNegationOperatorTests( "testLogicalNegationOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testLogicalNegationOperator1()
    {
        test( "${ ! true }", false );
    }
    
    public void testLogicalNegationOperator2()
    {
        test( "${ not true }", false );
    }
    
    public void testLogicalNegationOperator3()
    {
        test( "${ ! false }", true );
    }
    
    public void testLogicalNegationOperator4()
    {
        test( "${ ! BooleanTrue }", false );
    }

}

