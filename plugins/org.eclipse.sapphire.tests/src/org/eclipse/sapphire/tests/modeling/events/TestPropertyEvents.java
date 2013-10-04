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

package org.eclipse.sapphire.tests.modeling.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.tests.SapphireTestCase;
import org.junit.Test;

/**
 * Tests delivery of property events.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestPropertyEvents extends SapphireTestCase
{
    private List<Event> monitor( final TestElement element )
    {
        final List<Event> events = new ArrayList<Event>();
        
        final Listener listener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                events.add( event );
            }
        };
        
        element.attach( listener );
        
        for( Property property : element.properties() )
        {
            property.attach( listener );
        }
        
        return events;
    }
    
    @Test
    
    public void testEventsValuePropertyPlain() throws Exception
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final List<Event> events = monitor( element );
            
            assertEquals( 0, events.size() );
            
            element.getValuePlain().text();
            element.setValuePlain( "a" );
            
            assertEquals( 1, events.size() );
            assertPropertyContentEvent( events.get( 0 ), element.getValuePlain() );
            
            events.clear();
            
            element.getValuePlain().validation();
            element.setValuePlain( "b" );
            
            assertEquals( 1, events.size() );
            assertPropertyContentEvent( events.get( 0 ), element.getValuePlain() );
            
            events.clear();

            element.getValuePlain().enabled();
            element.setValuePlain( "c" );
            
            assertEquals( 1, events.size() );
            assertPropertyContentEvent( events.get( 0 ), element.getValuePlain() );
        }
        finally
        {
            element.dispose();
        }
    }
    
    @Test

    public void testEventsValuePropertyConstrained() throws Exception
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final List<Event> events = monitor( element );
            
            assertEquals( 0, events.size() );
            
            element.getValueConstrained().text();
            element.setValueConstrained( "a" );
            
            assertEquals( 1, events.size() );
            assertPropertyContentEvent( events.get( 0 ), element.getValueConstrained() );
            
            events.clear();
            
            element.getValueConstrained().validation();
            element.setValueConstrained( "b" );
            
            assertEquals( 1, events.size() );
            assertPropertyContentEvent( events.get( 0 ), element.getValueConstrained() );
            
            events.clear();
            
            element.setValueConstrained( null );
            
            assertEquals( 2, events.size() );
            assertPropertyContentEvent( events.get( 0 ), element.getValueConstrained() );
            assertPropertyValidationEvent( events.get( 1 ), element.getValueConstrained(), Status.createOkStatus(), Status.createErrorStatus( "Value constrained must be specified" ) );
            
            events.clear();

            element.getValueConstrained().enabled();
            element.setValueConstrained( "c" );
            
            assertEquals( 2, events.size() );
            assertPropertyContentEvent( events.get( 0 ), element.getValueConstrained() );
            assertPropertyValidationEvent( events.get( 1 ), element.getValueConstrained(), Status.createErrorStatus( "Value constrained must be specified" ), Status.createOkStatus() );
            
            events.clear();
            
            element.setEnablement( false );
            
            assertEquals( 2, events.size() );
            assertPropertyContentEvent( events.get( 0 ), element.getEnablement() );
            assertPropertyEnablementEvent( events.get( 1 ), element.getValueConstrained(), true, false );
        }
        finally
        {
            element.dispose();
        }
    }

}
