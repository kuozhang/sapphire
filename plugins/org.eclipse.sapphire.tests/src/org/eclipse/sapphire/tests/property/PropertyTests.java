/******************************************************************************
 * Copyright (c) 2014 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.property;

import org.eclipse.sapphire.tests.SapphireTestCase;
import org.junit.Test;

/**
 * Tests the Property class.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertyTests extends SapphireTestCase
{
    @Test
    
    public void HoldsElement() throws Exception
    {
        final TestElement a = TestElement.TYPE.instantiate();
        final TestElement aa = a.getElement().content( true );
        
        assertTrue( a.getElement().holds( aa ) );
        assertFalse( a.getValue().holds( aa ) );
        assertFalse( a.getTransient().holds( aa ) );
        assertFalse( a.getList().holds( aa ) );
        
        final TestElement aaa = aa.getList().insert();

        assertTrue( a.getElement().holds( aaa ) );
        assertFalse( a.getValue().holds( aaa ) );
        assertFalse( a.getTransient().holds( aaa ) );
        assertFalse( a.getList().holds( aaa ) );
        
        assertTrue( aa.getList().holds( aaa ) );
        assertFalse( aa.getValue().holds( aaa ) );
        assertFalse( aa.getTransient().holds( aaa ) );
        assertFalse( aa.getElement().holds( aaa ) );
    }

    @Test
    
    public void HoldsProperty() throws Exception
    {
        final TestElement a = TestElement.TYPE.instantiate();
        final TestElement aa = a.getElement().content( true );
        
        assertTrue( a.getElement().holds( aa.getValue() ) );
        assertTrue( a.getElement().holds( aa.getTransient() ) );
        assertTrue( a.getElement().holds( aa.getElement() ) );
        assertTrue( a.getElement().holds( aa.getList() ) );
        
        assertFalse( a.getValue().holds( aa.getValue() ) );
        assertFalse( a.getTransient().holds( aa.getValue() ) );
        assertFalse( a.getList().holds( aa.getValue() ) );
        
        final TestElement aaa = aa.getList().insert();

        assertTrue( a.getElement().holds( aaa.getValue() ) );
        assertTrue( a.getElement().holds( aaa.getTransient() ) );
        assertTrue( a.getElement().holds( aaa.getElement() ) );
        assertTrue( a.getElement().holds( aaa.getList() ) );
        
        assertFalse( a.getValue().holds( aaa.getValue() ) );
        assertFalse( a.getTransient().holds( aaa.getValue() ) );
        assertFalse( a.getList().holds( aaa.getValue() ) );
        
        assertTrue( aa.getList().holds( aaa.getValue() ) );
        assertTrue( aa.getList().holds( aaa.getTransient() ) );
        assertTrue( aa.getList().holds( aaa.getElement() ) );
        assertTrue( aa.getList().holds( aaa.getList() ) );
        
        assertFalse( aa.getValue().holds( aaa.getValue() ) );
        assertFalse( aa.getTransient().holds( aaa.getValue() ) );
        assertFalse( aa.getElement().holds( aaa.getValue() ) );
    }

}
