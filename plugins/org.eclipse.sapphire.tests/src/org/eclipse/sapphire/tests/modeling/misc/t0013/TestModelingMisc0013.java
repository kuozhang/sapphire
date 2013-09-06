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

package org.eclipse.sapphire.tests.modeling.misc.t0013;

import org.eclipse.sapphire.tests.SapphireTestCase;
import org.junit.Test;

/**
 * Tests use of @DependsOn where the property specifying the dependency resides in an implied element property
 * and the specified dependency path traverses through this implied element property.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestModelingMisc0013 extends SapphireTestCase
{
    @Test
    
    public void test() throws Exception
    {
        final TestRootElement root = TestRootElement.TYPE.instantiate();
        final TestChildElement child = root.getChild();
        
        assertNull( child.getDefaultIntegerValue().content() );
        assertNull( child.getIntegerValue().content() );
        
        child.setDefaultIntegerValue( 123 );
        
        assertEquals( Integer.valueOf( 123 ), child.getIntegerValue().content() );
    }

}
