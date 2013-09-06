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

package org.eclipse.sapphire.tests.index;

import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Index;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.tests.EventLog;
import org.eclipse.sapphire.tests.SapphireTestCase;
import org.eclipse.sapphire.tests.index.TestElement.ListEntry;
import org.eclipse.sapphire.tests.index.TestElement.ListEntry.NestedListEntry;

/**
 * Tests the index feature.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class IndexTests extends SapphireTestCase
{
    private IndexTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( IndexTests.class.getSimpleName() );

        suite.addTest( new IndexTests( "testSingleIndex" ) );
        suite.addTest( new IndexTests( "testMultipleIndexes" ) );
        suite.addTest( new IndexTests( "testIndexEvents" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_ValueProperty_Null" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_ValueProperty_Foreign_1" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_ValueProperty_Foreign_2" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_ValueProperty_Disposed" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_String_Null" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_String_Unknown" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_String_Path" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_String_List" ) );
        suite.addTest( new IndexTests( "testException_ElementList_Index_String_Disposed" ) );
        suite.addTest( new IndexTests( "testException_Index_Element_Null" ) );
        suite.addTest( new IndexTests( "testException_Index_Element_Disposed" ) );
        suite.addTest( new IndexTests( "testException_Index_Elements_Null" ) );
        suite.addTest( new IndexTests( "testException_Index_Elements_Disposed" ) );
        suite.addTest( new IndexTests( "testException_Index_Attach_Null" ) );
        suite.addTest( new IndexTests( "testException_Index_Attach_Disposed" ) );
        suite.addTest( new IndexTests( "testException_Index_Detach_Null" ) );
        
        return suite;
    }
    
    public void testSingleIndex()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final ElementList<ListEntry> list = element.getList();
            
            for( int i = 0; i < 100; i++ )
            {
                list.insert().setStringValue( String.valueOf( i ) );
            }
            
            final Index<ListEntry> index = list.index( ListEntry.PROP_STRING_VALUE );
            
            assertSame( list, index.list() );
            assertSame( ListEntry.PROP_STRING_VALUE, index.property() );
            assertSame( index, list.index( ListEntry.PROP_STRING_VALUE ) );
            
            testIndexLookup( index, "20", 1 );
            testIndexLookup( index, "97", 1 );
            testIndexLookup( index, "137", 0 );

            for( int i = 100; i < 200; i++ )
            {
                list.insert().setStringValue( String.valueOf( i ) );
            }
            
            testIndexLookup( index, "137", 1 );

            for( int i = 100; i < 200; i++ )
            {
                list.insert().setStringValue( String.valueOf( i ) );
            }
            
            testIndexLookup( index, "20", 1 );
            testIndexLookup( index, "97", 1 );
            testIndexLookup( index, "137", 2 );
            testIndexLookup( index, "182", 2 );
            testIndexLookup( index, "213", 0 );
        }
        finally
        {
            element.dispose();
        }
    }
    
    public void testMultipleIndexes()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final ElementList<ListEntry> list = element.getList();
            
            for( int i = 0; i < 100; i++ )
            {
                final ListEntry entry = list.insert();
                
                entry.setStringValue( "s" + String.valueOf( i ) );
                entry.setIntegerValue( i );
            }
            
            final Index<ListEntry> stringValueIndex = list.index( ListEntry.PROP_STRING_VALUE );
            
            assertSame( list, stringValueIndex.list() );
            assertSame( ListEntry.PROP_STRING_VALUE, stringValueIndex.property() );
            assertSame( stringValueIndex, list.index( ListEntry.PROP_STRING_VALUE ) );

            final Index<ListEntry> integerValueIndex = list.index( ListEntry.PROP_INTEGER_VALUE );
            
            assertSame( list, integerValueIndex.list() );
            assertSame( ListEntry.PROP_INTEGER_VALUE, integerValueIndex.property() );
            assertSame( integerValueIndex, list.index( ListEntry.PROP_INTEGER_VALUE ) );
            
            assertSame( stringValueIndex.element( "s97" ), integerValueIndex.element( "97" ) );

            for( int i = 100; i < 200; i++ )
            {
                list.insert().setStringValue( String.valueOf( i ) );
            }
            
            assertSame( stringValueIndex.element( "s137" ), integerValueIndex.element( "137" ) );
        }
        finally
        {
            element.dispose();
        }
    }
    
    public void testIndexEvents()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final ElementList<ListEntry> list = element.getList();
            
            for( int i = 0; i < 100; i++ )
            {
                final ListEntry entry = list.insert();
                
                entry.setStringValue( String.valueOf( i ) );
                entry.setIntegerValue( i );
            }
            
            final Index<ListEntry> index = list.index( ListEntry.PROP_STRING_VALUE );
            final EventLog log = new EventLog();
            
            index.attach( log );
            
            // Initialize the index by accessing it.
            
            index.element( "1" );
            
            assertEquals( 0, log.size() );
            
            // Remove item from the list.
            
            list.remove( 0 );
            
            assertEquals( 1, log.size() );
            log.clear();
            
            // Change an indexed value.
            
            list.get( 10 ).setStringValue( "aa" );
            
            assertEquals( 1, log.size() );
            log.clear();
            
            // Change an unrelated value.
            
            list.get( 10 ).setIntegerValue( 9999 );
            
            assertEquals( 0, log.size() );
            
            // Move list items.
            
            list.moveUp( list.get( 10 ) );
            
            assertEquals( 0, log.size() );
            
            list.moveDown( list.get( 20 ) );
            
            assertEquals( 0, log.size() );
            
            // Detach the listener.
            
            index.detach( log );
            list.remove( 0 );
            
            assertEquals( 0, log.size() );
        }
        finally
        {
            element.dispose();
        }
    }

    /**
     * Test {@link ElementList#index(ValueProperty)} with a null.
     */
    
    public void testException_ElementList_Index_ValueProperty_Null()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( (ValueProperty) null );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }

    /**
     * Test {@link ElementList#index(ValueProperty)} with a foreign property.
     */
    
    public void testException_ElementList_Index_ValueProperty_Foreign_1()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( TestElement.PROP_VALUE );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    /**
     * Test {@link ElementList#index(ValueProperty)} with a foreign property.
     */
    
    public void testException_ElementList_Index_ValueProperty_Foreign_2()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( NestedListEntry.PROP_VALUE );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    /**
     * Test {@link ElementList#index(ValueProperty)} when the list is disposed.
     */
    
    public void testException_ElementList_Index_ValueProperty_Disposed()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final ElementList<ListEntry> list;
        
        try
        {
            list = element.getList();
        }
        finally
        {
            element.dispose();
        }

        try
        {
            list.index( ListEntry.PROP_STRING_VALUE );
            
            fail( "Exception expected." );
        }
        catch( IllegalStateException e )
        {
            // expected
        }
    }
    
    /**
     * Test {@link ElementList#index(String)} with a null.
     */
    
    public void testException_ElementList_Index_String_Null()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( (String) null );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    /**
     * Test {@link ElementList#index(String)} with an unknown property.
     */
    
    public void testException_ElementList_Index_String_Unknown()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( "Value" );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }

    /**
     * Test {@link ElementList#index(String)} with a path.
     */
    
    public void testException_ElementList_Index_String_Path()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( "List/Value" );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    /**
     * Test {@link ElementList#index(String)} with a list property.
     */
    
    public void testException_ElementList_Index_String_List()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( "List" );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    /**
     * Test {@link ElementList#index(String)} when the list is disposed.
     */
    
    public void testException_ElementList_Index_String_Disposed()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final ElementList<ListEntry> list;
        
        try
        {
            list = element.getList();
        }
        finally
        {
            element.dispose();
        }

        try
        {
            list.index( ListEntry.PROP_STRING_VALUE );
            
            fail( "Exception expected." );
        }
        catch( IllegalStateException e )
        {
            // expected
        }
    }
    
    /**
     * Test {@link Index#element(String)} with a null.
     */
    
    public void testException_Index_Element_Null()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( ListEntry.PROP_STRING_VALUE ).element( null );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    /**
     * Test {@link Index#element(String)} when the list is disposed.
     */
    
    public void testException_Index_Element_Disposed()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final Index<ListEntry> index;
        
        try
        {
            index = element.getList().index( ListEntry.PROP_STRING_VALUE );
        }
        finally
        {
            element.dispose();
        }

        try
        {
            index.element( "a" );
            
            fail( "Exception expected." );
        }
        catch( IllegalStateException e )
        {
            // expected
        }
    }
    
    /**
     * Test {@link Index#elements(String)} with a null.
     */
    
    public void testException_Index_Elements_Null()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( ListEntry.PROP_STRING_VALUE ).elements( null );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    /**
     * Test {@link Index#elements(String)} when the list is disposed.
     */
    
    public void testException_Index_Elements_Disposed()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final Index<ListEntry> index;
        
        try
        {
            index = element.getList().index( ListEntry.PROP_STRING_VALUE );
        }
        finally
        {
            element.dispose();
        }

        try
        {
            index.elements( "a" );
            
            fail( "Exception expected." );
        }
        catch( IllegalStateException e )
        {
            // expected
        }
    }
    
    /**
     * Test {@link Index#attach(Listener)} with a null.
     */
    
    public void testException_Index_Attach_Null()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( ListEntry.PROP_STRING_VALUE ).attach( null );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    /**
     * Test {@link Index#attach(Listener)} when the list is disposed.
     */
    
    public void testException_Index_Attach_Disposed()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        final Index<ListEntry> index;
        
        try
        {
            index = element.getList().index( ListEntry.PROP_STRING_VALUE );
        }
        finally
        {
            element.dispose();
        }

        try
        {
            index.attach( new EventLog() );
            
            fail( "Exception expected." );
        }
        catch( IllegalStateException e )
        {
            // expected
        }
    }
    
    /**
     * Test {@link Index#detach(Listener)} with a null.
     */
    
    public void testException_Index_Detach_Null()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            element.getList().index( ListEntry.PROP_STRING_VALUE ).detach( null );
            
            fail( "Exception expected." );
        }
        catch( IllegalArgumentException e )
        {
            // expected
        }
        finally
        {
            element.dispose();
        }
    }
    
    private void testIndexLookup( final Index<ListEntry> index, final String key, final int count )
    {
        if( count == 0 )
        {
            assertNull( index.element( key ) );
            
            final Set<ListEntry> elements = index.elements( key );
            assertNotNull( elements );
            assertEquals( 0, elements.size() );
        }
        else
        {
            final ListEntry element = index.element( key );
            assertNotNull( element );
            assertEquals( key, element.property( index.property() ).text() );

            final Set<ListEntry> elements = index.elements( key );
            assertNotNull( elements );
            assertEquals( count, elements.size() );
            
            for( final ListEntry entry : elements )
            {
                assertEquals( key, entry.property( index.property() ).text() );
            }
        }
    }

}
