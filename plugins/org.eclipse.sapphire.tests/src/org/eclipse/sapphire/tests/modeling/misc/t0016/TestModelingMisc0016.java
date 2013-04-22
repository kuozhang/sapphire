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

package org.eclipse.sapphire.tests.modeling.misc.t0016;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests behavior of Element.enabled( ModelProperty ) method for overridden properties.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestModelingMisc0016 extends SapphireTestCase
{
    private TestModelingMisc0016( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestModelingMisc0016" );

        suite.addTest( new TestModelingMisc0016( "testEnablementOfOverriddenProperty" ) );
        
        return suite;
    }
    
    public void testEnablementOfOverriddenProperty() throws Exception
    {
        final BaseElement base = BaseElement.TYPE.instantiate();
        
        assertTrue( base.property( BaseElement.PROP_VALUE ).enabled() );
        
        final DerivedElement derived = DerivedElement.TYPE.instantiate();
        
        assertFalse( derived.property( BaseElement.PROP_VALUE ).enabled() );
        assertFalse( derived.property( DerivedElement.PROP_VALUE ).enabled() );
    }

}
