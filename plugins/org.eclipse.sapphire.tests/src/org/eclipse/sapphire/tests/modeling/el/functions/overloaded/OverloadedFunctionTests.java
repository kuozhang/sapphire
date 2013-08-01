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

package org.eclipse.sapphire.tests.modeling.el.functions.overloaded;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests resolution of overloaded functions.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class OverloadedFunctionTests extends TestExpr
{
    private OverloadedFunctionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "OverloadedFunctionTests" );

        suite.addTest( new OverloadedFunctionTests( "testOverloadedFunctions" ) );
        
        return suite;
    }
    
    public void testOverloadedFunctions()
    {
        testForExpectedValue( "${ SapphireTests:OverloadedFunction( 123 ) }", "BigInteger" );
        testForExpectedValue( "${ SapphireTests:OverloadedFunction( 123.456 ) }", "Number" );
        testForExpectedValue( "${ SapphireTests:OverloadedFunction( '2013-07-25' ) }", "Date" );
        testForExpectedValue( "${ SapphireTests:OverloadedFunction( true ) }", "Wildcard" );
        testForExpectedValue( "${ SapphireTests:OverloadedFunction( 123, true ) }", "Wildcard" );
        testForExpectedValue( "${ SapphireTests:OverloadedFunction( 123.456, true ) }", "Wildcard" );
        testForExpectedValue( "${ SapphireTests:OverloadedFunction( '2013-07-25', true ) }", "Wildcard" );
        testForExpectedValue( "${ SapphireTests:OverloadedFunction() }", "Wildcard" );
    }

}
