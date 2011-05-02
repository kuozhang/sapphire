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

import org.eclipse.sapphire.tests.modeling.misc.t0001.TestMisc0001;
import org.eclipse.sapphire.tests.modeling.misc.t0002.TestMisc0002;
import org.eclipse.sapphire.tests.modeling.misc.t0003.Misc0003Test;
import org.eclipse.sapphire.tests.modeling.misc.t0004.Misc0004Test;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MiscTestSuite

    extends TestCase
    
{
    private MiscTestSuite( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Misc" );

        suite.addTest( TestMisc0001.suite() );
        suite.addTest( TestMisc0002.suite() );
        suite.addTest( Misc0003Test.suite() );
        suite.addTest( Misc0004Test.suite() );
        
        return suite;
    }
    
}
