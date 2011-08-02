/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.modeling.misc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.sapphire.tests.modeling.misc.t0001.TestModelingMisc0001;
import org.eclipse.sapphire.tests.modeling.misc.t0002.TestModelingMisc0002;
import org.eclipse.sapphire.tests.modeling.misc.t0003.TestModelingMisc0003;
import org.eclipse.sapphire.tests.modeling.misc.t0004.TestModelingMisc0004;
import org.eclipse.sapphire.tests.modeling.misc.t0005.TestModelingMisc0005;
import org.eclipse.sapphire.tests.modeling.misc.t0006.TestModelingMisc0006;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SuiteModelingMisc

    extends TestCase
    
{
    private SuiteModelingMisc( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "SuiteModelingMisc" );

        suite.addTest( TestModelingMisc0001.suite() );
        suite.addTest( TestModelingMisc0002.suite() );
        suite.addTest( TestModelingMisc0003.suite() );
        suite.addTest( TestModelingMisc0004.suite() );
        suite.addTest( TestModelingMisc0005.suite() );
        suite.addTest( TestModelingMisc0006.suite() );
        
        return suite;
    }
    
}
