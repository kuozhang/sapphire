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

package org.eclipse.sapphire.tests.binding.list;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.tests.Counter;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;

/**
 * Tests for LayeredListPropertyBinding.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LayeredListPropertyBindingTests extends TestExpr
{
    private LayeredListPropertyBindingTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "LayeredListPropertyBindingTests" );

        suite.addTest( new LayeredListPropertyBindingTests( "testReadUnderlyingListCount" ) );
        
        return suite;
    }
    
    public void testReadUnderlyingListCount()
    {
        final TestResource resource = new TestResource();
        final Counter counter = resource.getReadUnderlyingListCounter();
        final TestElement element = TestElement.TYPE.instantiate( resource );
        final ElementList<Element> list = element.getList();
        
        list.iterator(); // prime
        counter.reset();
        
        list.refresh();
        assertEquals( 1, counter.value() );
        counter.reset();
        
        list.insert();
        assertEquals( 1, counter.value() );
        counter.reset();

        list.remove( 0 );
        assertEquals( 1, counter.value() );
        counter.reset();
        
        final Element a = list.insert();
        final Element b = list.insert();
        assertEquals( 2, counter.value() );
        counter.reset();
        
        list.moveDown( a );
        assertEquals( 1, counter.value() );
        counter.reset();
        
        list.moveUp( a );
        assertEquals( 1, counter.value() );
        counter.reset();
        
        list.move( b, 0 );
        assertEquals( 1, counter.value() );
        counter.reset();
    }

}
