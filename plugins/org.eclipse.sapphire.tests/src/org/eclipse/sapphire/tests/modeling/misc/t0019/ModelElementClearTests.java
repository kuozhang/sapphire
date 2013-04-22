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

package org.eclipse.sapphire.tests.modeling.misc.t0019;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests the IModelElement.clear() methods.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ModelElementClearTests extends SapphireTestCase
{
    private ModelElementClearTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "ModelElementClearTests" );

        suite.addTest( new ModelElementClearTests( "testValuePropertyClear" ) );
        suite.addTest( new ModelElementClearTests( "testTransientPropertyClear" ) );
        suite.addTest( new ModelElementClearTests( "testElementPropertyClear" ) );
        suite.addTest( new ModelElementClearTests( "testImpliedElementPropertyClear" ) );
        suite.addTest( new ModelElementClearTests( "testListPropertyClear" ) );
        suite.addTest( new ModelElementClearTests( "testAllPropertiesClear" ) );
        
        return suite;
    }
    
    public void testValuePropertyClear()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.setStringValueProperty( "abc" );
            element.setIntegerValueProperty( 123 );
            element.getListProperty().insert();
            
            assertEquals( "abc", element.getStringValueProperty().content() );
            assertEquals( Integer.valueOf( 123 ), element.getIntegerValueProperty().content() );
            assertEquals( 1, element.getListProperty().size() );
            
            element.property( TestElement.PROP_STRING_VALUE_PROPERTY ).clear();
            element.property( TestElement.PROP_INTEGER_VALUE_PROPERTY ).clear();
            
            assertNull( element.getStringValueProperty().content() );
            assertNull( element.getIntegerValueProperty().content() );
            assertEquals( 1, element.getListProperty().size() );
        }
        finally
        {
            element.dispose();
        }
    }

    public void testTransientPropertyClear()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.setTransientProperty( this );
            element.getListProperty().insert();
            
            assertEquals( this, element.getTransientProperty().content() );
            assertEquals( 1, element.getListProperty().size() );
            
            element.property( TestElement.PROP_TRANSIENT_PROPERTY ).clear();
            
            assertNull( element.getTransientProperty().content() );
            assertEquals( 1, element.getListProperty().size() );
        }
        finally
        {
            element.dispose();
        }
    }

    public void testElementPropertyClear()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getElementProperty().content( true ).setValueProperty( "abc" );
            element.getListProperty().insert();
            
            assertEquals( "abc", element.getElementProperty().content().getValueProperty().content() );
            assertEquals( 1, element.getListProperty().size() );
            
            element.property( TestElement.PROP_ELEMENT_PROPERTY ).clear();
            
            assertNull( element.getElementProperty().content() );
            assertEquals( 1, element.getListProperty().size() );
        }
        finally
        {
            element.dispose();
        }
    }

    public void testImpliedElementPropertyClear()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getImpliedElementProperty().setValueProperty( "abc" );
            element.getListProperty().insert();
            
            assertEquals( "abc", element.getImpliedElementProperty().getValueProperty().content() );
            assertEquals( 1, element.getListProperty().size() );
            
            element.property( TestElement.PROP_IMPLIED_ELEMENT_PROPERTY ).clear();
            
            assertNull( element.getImpliedElementProperty().getValueProperty().content() );
            assertEquals( 1, element.getListProperty().size() );
        }
        finally
        {
            element.dispose();
        }
    }

    public void testListPropertyClear()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getListProperty().insert();
            element.getListProperty().insert();
            element.setStringValueProperty( "abc" );
            
            assertEquals( 2, element.getListProperty().size() );
            assertEquals( "abc", element.getStringValueProperty().content() );
            
            element.property( TestElement.PROP_LIST_PROPERTY ).clear();
            
            assertEquals( 0, element.getListProperty().size() );
            assertEquals( "abc", element.getStringValueProperty().content() );
        }
        finally
        {
            element.dispose();
        }
    }

    public void testAllPropertiesClear()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.setStringValueProperty( "abc" );
            element.setIntegerValueProperty( 123 );
            element.setTransientProperty( this );
            element.getElementProperty().content( true ).setValueProperty( "abc" );
            element.getImpliedElementProperty().setValueProperty( "abc" );
            element.getListProperty().insert();
            element.getListProperty().insert();
            
            assertEquals( "abc", element.getStringValueProperty().content() );
            assertEquals( Integer.valueOf( 123 ), element.getIntegerValueProperty().content() );
            assertEquals( this, element.getTransientProperty().content() );
            assertEquals( "abc", element.getElementProperty().content().getValueProperty().content() );
            assertEquals( "abc", element.getImpliedElementProperty().getValueProperty().content() );
            assertEquals( 2, element.getListProperty().size() );
            
            element.clear();
            
            assertNull( element.getStringValueProperty().content() );
            assertNull( element.getIntegerValueProperty().content() );
            assertNull( element.getTransientProperty().content() );
            assertNull( element.getElementProperty().content() );
            assertNull( element.getImpliedElementProperty().getValueProperty().content() );
            assertEquals( 0, element.getListProperty().size() );
        }
        finally
        {
            element.dispose();
        }
    }

}
