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

package org.eclipse.sapphire.tests.modeling.misc.t0018;

import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests runtime compilation of element type implementation classes.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestModelingMisc0018 extends SapphireTestCase
{
    private TestModelingMisc0018( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestModelingMisc0018" );

        suite.addTest( new TestModelingMisc0018( "testRuntimeTypeCompilation" ) );
        
        return suite;
    }
    
    public void testRuntimeTypeCompilation() throws Exception
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.setStringValue( "abc" );
            assertEquals( "abc", element.getStringValue().getContent() );
            
            element.setIntegerValue( 1 );
            assertEquals( Integer.valueOf( 1 ), element.getIntegerValue().getContent() );
            
            element.setIntegerValue( "2" );
            assertEquals( Integer.valueOf( 2 ), element.getIntegerValue().getContent() );
            
            element.setJavaTypeReferenceValue( List.class.getName() );
            assertSame( List.class, element.getJavaTypeReferenceValue().resolve().artifact() );
            
            element.setJavaTypeReferenceValue( new JavaTypeName( Map.class.getName() ) );
            assertSame( Map.class, element.getJavaTypeReferenceValue().resolve().artifact() );
            
            element.setTransient( System.out );
            assertSame( System.out, element.getTransient().content() );
            
            final ModelElementList<TestChildElement> list = element.getList();
            
            list.insert().setStringValue( "foo" );
            list.insert().setStringValue( "bar" );
            assertEquals( 2, list.size() );
            assertEquals( "foo", list.get( 0 ).getStringValue().getContent() );
            assertEquals( "bar", list.get( 1 ).getStringValue().getContent() );
            
            element.getElement().element( true ).setStringValue( "foo" );
            assertEquals( "foo", element.getElement().element().getStringValue().getContent() );
            
            element.getImpliedElement().setStringValue( "bar" );
            assertEquals( "bar", element.getImpliedElement().getStringValue().getContent() );
            
            element.method1();
            assertEquals( 2, element.method2( 1, "abc", new String[] { "foo", "bar" }, null ).length );
        }
        finally
        {
            element.dispose();
        }
    }
    
}
