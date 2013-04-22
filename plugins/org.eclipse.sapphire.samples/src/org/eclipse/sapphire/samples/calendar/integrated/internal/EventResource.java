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

package org.eclipse.sapphire.samples.calendar.integrated.internal;

import java.util.List;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.LayeredListBindingImpl;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;
import org.eclipse.sapphire.samples.calendar.integrated.IAttendee;
import org.eclipse.sapphire.samples.calendar.integrated.IEvent;
import org.eclipse.sapphire.samples.calendar.integrated.IEventAttachment;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EventResource extends Resource
{
    private final org.eclipse.sapphire.samples.calendar.IEvent base;
    
    public EventResource( final Resource parent,
                          final org.eclipse.sapphire.samples.calendar.IEvent base )
    {
        super( parent );
        
        this.base = base;
        
        final Listener listener = new FilteredListener<PropertyEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyEvent event )
            {
                final PropertyDef property = event.property().definition();
                final IEvent calevent = (IEvent) element();
                
                if( property == org.eclipse.sapphire.samples.calendar.IEvent.PROP_SUBJECT )
                {
                    calevent.getSubject().refresh();
                }
                else if( property == org.eclipse.sapphire.samples.calendar.IEvent.PROP_LOCATION )
                {
                    calevent.getLocation().refresh();
                }
                else if( property == org.eclipse.sapphire.samples.calendar.IEvent.PROP_NOTES )
                {
                    calevent.getNotes().refresh();
                }
                else if( property == org.eclipse.sapphire.samples.calendar.IEvent.PROP_START_TIME )
                {
                    calevent.getStartTime().refresh();
                }
                else if( property == org.eclipse.sapphire.samples.calendar.IEvent.PROP_END_TIME )
                {
                    calevent.getEndTime().refresh();
                }
                else if( property == org.eclipse.sapphire.samples.calendar.IEvent.PROP_ATTENDEES )
                {
                    calevent.getAttendees().refresh();
                }
                else if( property == org.eclipse.sapphire.samples.calendar.IEvent.PROP_ATTACHMENTS )
                {
                    calevent.getAttachments().refresh();
                }
            }
        };
        
        this.base.attach( listener );
    }
    
    public org.eclipse.sapphire.samples.calendar.IEvent getBase()
    {
        return this.base;
    }
    
    @Override
    protected BindingImpl createBinding( final PropertyDef property )
    {
        if( property == IEvent.PROP_SUBJECT )
        {
            return new ValueBindingImpl()
            {
                @Override
                public String read()
                {
                    return getBase().getSubject().text( false );
                }
                
                @Override
                public void write( final String value )
                {
                    getBase().setSubject( value );
                }
            };
        }
        else if( property == IEvent.PROP_LOCATION )
        {
            return new ValueBindingImpl()
            {
                @Override
                public String read()
                {
                    return getBase().getLocation().text( false );
                }
                
                @Override
                public void write( final String value )
                {
                    getBase().setLocation( value );
                }
            };
        }
        else if( property == IEvent.PROP_NOTES )
        {
            return new ValueBindingImpl()
            {
                @Override
                public String read()
                {
                    return getBase().getNotes().text( false );
                }
                
                @Override
                public void write( final String value )
                {
                    getBase().setNotes( value );
                }
            };
        }
        else if( property == IEvent.PROP_START_TIME )
        {
            return new ValueBindingImpl()
            {
                @Override
                public String read()
                {
                    return getBase().getStartTime().text( false );
                }
                
                @Override
                public void write( final String value )
                {
                    getBase().setStartTime( value );
                }
            };
        }
        else if( property == IEvent.PROP_END_TIME )
        {
            return new ValueBindingImpl()
            {
                @Override
                public String read()
                {
                    return getBase().getEndTime().text( false );
                }
                
                @Override
                public void write( final String value )
                {
                    getBase().setEndTime( value );
                }
            };
        }
        else if( property == IEvent.PROP_ATTENDEES )
        {
            final ListBindingImpl binding = new LayeredListBindingImpl()
            {
                private final ElementList<org.eclipse.sapphire.samples.calendar.IAttendee> base
                    = EventResource.this.base.getAttendees();
                
                @Override
                public ElementType type( final Resource resource )
                {
                    return IAttendee.TYPE;
                }

                @Override
                protected Resource resource( final Object obj )
                {
                    return new AttendeeResource( EventResource.this, (org.eclipse.sapphire.samples.calendar.IAttendee) obj );
                }
                
                @Override
                protected List<?> readUnderlyingList()
                {
                    return this.base;
                }

                @Override
                protected Object insertUnderlyingObject( final ElementType type,
                                                         final int position )
                {
                    return this.base.insert( org.eclipse.sapphire.samples.calendar.IAttendee.TYPE, position );
                }
                
                @Override
                public void move( final Resource resource, 
                                  final int position )
                {
                    this.base.move( ( (AttendeeResource) resource ).getBase(), position );
                }

                @Override
                public void remove( final Resource resource )
                {
                    this.base.remove( ( (AttendeeResource) resource ).getBase() );
                }
            };
            
            binding.init( element(), IEvent.PROP_ATTENDEES, null );
            
            return binding;
        }
        else if( property == IEvent.PROP_ATTACHMENTS )
        {
            final ListBindingImpl binding = new LayeredListBindingImpl()
            {
                private final ElementList<org.eclipse.sapphire.samples.calendar.IEventAttachment> base
                    = EventResource.this.base.getAttachments();

                @Override
                public ElementType type( final Resource resource )
                {
                    return IEventAttachment.TYPE;
                }

                @Override
                protected Resource resource( final Object obj )
                {
                    return new EventAttachmentResource( EventResource.this, (org.eclipse.sapphire.samples.calendar.IEventAttachment) obj );
                }
                
                @Override
                protected List<?> readUnderlyingList()
                {
                    return this.base;
                }

                @Override
                protected Object insertUnderlyingObject( final ElementType type,
                                                         final int position )
                {
                    return this.base.insert( org.eclipse.sapphire.samples.calendar.IEventAttachment.TYPE, position );
                }
                
                @Override
                public void move( final Resource resource, 
                                  final int position )
                {
                    this.base.move( ( (EventAttachmentResource) resource ).getBase(), position );
                }

                @Override
                public void remove( final Resource resource )
                {
                    this.base.remove( ( (EventAttachmentResource) resource ).getBase() );
                }
            };
            
            binding.init( element(), IEvent.PROP_ATTACHMENTS, null );
            
            return binding;
        }
        
        return null;
    }

    @Override
    public <A> A adapt( final Class<A> adapterType )
    {
        A res = super.adapt( adapterType );
        
        if( res == null )
        {
            res = this.base.adapt( adapterType );
        }
        
        return res;
    }

}
