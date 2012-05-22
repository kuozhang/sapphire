/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.modeling.events.t0001;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests delivery of value property events.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestModelingEvents0001 extends SapphireTestCase
{
    private TestModelingEvents0001( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestModelingEvents0001" );

        suite.addTest( new TestModelingEvents0001( "testValuePropertyEvents" ) );
        
        return suite;
    }
    
    public void testValuePropertyEvents() throws Exception
    {
        final RootElement root = RootElement.TYPE.instantiate();
        
        final List<Event> events = new ArrayList<Event>();
        
        final Listener listener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                events.add( event );
            }
        };
        
        root.attach( listener );
        
        final Status initialPropertyValidation = root.getRequiredStringValue().validation();
        final Status initialElementValidation = root.validation();
        
        assertValidationError( initialPropertyValidation, "Required string value must be specified." );
        assertValidationError( initialElementValidation, "Required string value must be specified." );
        assertEquals( 0, events.size() );
        
        root.setRequiredStringValue( "abc" );
        
        assertEquals( 3, events.size() );
        assertPropertyContentEvent( events.get( 0 ), root, RootElement.PROP_REQUIRED_STRING_VALUE );
        assertPropertyValidationEvent( events.get( 1 ), root, RootElement.PROP_REQUIRED_STRING_VALUE, initialPropertyValidation, Status.createOkStatus() );
        assertElementValidationEvent( events.get( 2 ), root, initialPropertyValidation, Status.createOkStatus() );
        
        events.clear();
        
        root.detach( listener );
        
        root.setRequiredStringValue( null );
        
        assertEquals( 0, events.size() );
        
        root.attach( listener );
        
        root.setRequiredStringValue( "abc" );
        root.setRequiredStringValue( "xyz" );
        
        assertEquals( 4, events.size() );
        assertPropertyContentEvent( events.get( 0 ), root, RootElement.PROP_REQUIRED_STRING_VALUE );
        assertPropertyValidationEvent( events.get( 1 ), root, RootElement.PROP_REQUIRED_STRING_VALUE, initialPropertyValidation, Status.createOkStatus() );
        assertElementValidationEvent( events.get( 2 ), root, initialPropertyValidation, Status.createOkStatus() );
        assertPropertyContentEvent( events.get( 3 ), root, RootElement.PROP_REQUIRED_STRING_VALUE );
        
        events.clear();
        
        root.setEnablement( false );
        root.setEnablement( true );
        
        assertEquals( 4, events.size() );
        assertPropertyContentEvent( events.get( 0 ), root, RootElement.PROP_ENABLEMENT );
        assertPropertyEnablementEvent( events.get( 1 ), root, RootElement.PROP_REQUIRED_STRING_VALUE, true, false );
        assertPropertyContentEvent( events.get( 2 ), root, RootElement.PROP_ENABLEMENT );
        assertPropertyEnablementEvent( events.get( 3 ), root, RootElement.PROP_REQUIRED_STRING_VALUE, false, true );
        
        events.clear();
        
        root.dispose();
        
        assertEquals( 1, events.size() );
        assertElementDisposeEvent( events.get( 0 ), root );
    }

}
